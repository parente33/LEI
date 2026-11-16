#pragma once

#include <vector>
#include <cstdint>
#include <cstddef>

#include "math.hpp"


struct Vertex
{
    Vec3  position;
    Vec3  normal;
    Vec2  texcoord;
};

struct Mesh
{
    std::vector<Vertex>         vertices;
    std::vector<std::uint32_t>  indices;

    void reserve(std::size_t vtx, std::size_t idx)
    {
        vertices.reserve(vtx);
        indices.reserve(idx);
    }

    void add_triangle(std::uint32_t i0, std::uint32_t i1, std::uint32_t i2)
    {
        indices.push_back(i0);
        indices.push_back(i1);
        indices.push_back(i2);
    }
};