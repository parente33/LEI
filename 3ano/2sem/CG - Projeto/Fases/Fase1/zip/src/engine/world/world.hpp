#pragma once

#include <cstdint>
#include <filesystem>
#include <vector>

#include "math.hpp"


using NodeId = std::uint32_t;
using MeshId = std::uint32_t;

inline constexpr NodeId kInvalidNodeId = 0xFFFF'FFFFu;
inline constexpr MeshId kInvalidMeshId = 0xFFFF'FFFFu;


struct WindowConfig
{
    int  width  = 512;
    int  height = 512;
};

struct Camera
{
    Vec3  pos { 0.f, 0.f, 0.f };
    Vec3  lookAt { 0.f, 0.f, 0.f };
    Vec3  up { 0.f, 1.f, 0.f };

    float  fovDeg = 60.f;
    float  nearZ  = 1.f;
    float  farZ   = 1000.f;
};

struct Model
{
    std::filesystem::path  meshFile;
    MeshId                 mesh = kInvalidMeshId;

    /* texture/material refs */
};

struct Node
{
    std::vector<Model>   models;
    std::vector<NodeId>  children;

    /* ordered transforms */
};

struct World
{
    WindowConfig  window;
    Camera        camera;

    std::vector<Node>  nodes;
    NodeId             root = kInvalidNodeId;

    /* useful for relative paths */
    std::filesystem::path sourceXml;

    /* lights */
};

inline NodeId world_make_node(World &w)
{
    w.nodes.push_back(Node {});
    return static_cast<NodeId>(w.nodes.size() - 1);
}

inline Node& world_node(World &w, NodeId id)
{
    return w.nodes[static_cast<std::size_t>(id)];
}

inline const Node &world_node(const World &w, NodeId id)
{
    return w.nodes[(std::size_t)id];
}