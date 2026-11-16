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
    int  width = 512;
    int  height = 512;
};

struct Camera
{
    Vec3   pos { 0.f, 0.f, 0.f };
    Vec3   lookAt { 0.f, 0.f, 0.f };
    Vec3   up { 0.f, 1.f, 0.f };

    float  fovDeg = 60.f;
    float  nearZ = 1.f;
    float  farZ = 1000.f;
};

enum class TransformType : unsigned char
{
    Translate,
    Rotate,
    Scale,
};

struct Transform
{
    TransformType type;

    union {
        struct { float x, y, z; } translate;
        struct { float angle, x, y, z; } rotate;
        struct { float x, y, z; } scale;
    };

    static Transform make_translate(float x, float y, float z)
    {
        Transform t {};
        t.type = TransformType::Translate;
        t.translate = { x, y, z };
        return t;
    }

    static Transform make_rotate(float angle, float x, float y, float z)
    {
        Transform t {};
        t.type = TransformType::Rotate;
        t.rotate = { angle, x, y, z };
        return t;
    }

    static Transform make_scale(float x, float y, float z)
    {
        Transform t {};
        t.type = TransformType::Scale;
        t.scale = { x, y, z };
        return t;
    }
};

struct Model
{
    std::filesystem::path  meshFile;
    MeshId                 mesh = kInvalidMeshId;

    /* texture, color/material */
};

struct Node
{
    std::vector<Transform>  transforms;
    std::vector<Model>      models;
    std::vector<NodeId>     children;
};

struct World
{
    WindowConfig  window;
    Camera        camera;

    std::vector<Node>  nodes;
    NodeId             root = kInvalidNodeId;

    std::filesystem::path sourceXml;

    /* lights */
};

inline NodeId world_make_node(World &w)
{
    w.nodes.push_back(Node {});
    return static_cast<NodeId>(w.nodes.size() - 1);
}

inline Node &world_node(World &w, NodeId id)
{
    return w.nodes[static_cast<std::size_t>(id)];
}

inline const Node &world_node(const World &w, NodeId id)
{
    return w.nodes[static_cast<std::size_t>(id)];
}