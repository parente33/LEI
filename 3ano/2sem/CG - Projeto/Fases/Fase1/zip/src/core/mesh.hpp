#pragma once

#include <vector>
#include <cstdint>
#include <cstddef>

#include "math.hpp"


struct Vertex
{
    Vec3 position;

    /*
     * normal;
     * uv;
     * */
};

struct Mesh
{
    std::vector<Vertex>         vertices;
    std::vector<std::uint32_t>  indices;

    bool empty() const noexcept { return vertices.empty() || indices.empty(); }

    std::size_t vertex_count() const noexcept { return vertices.size(); }

    std::size_t index_count() const noexcept { return indices.size(); }

    std::size_t triangle_count() const noexcept { return indices.size() / 3; }

    void reserve(std::size_t vtx_count, std::size_t idx_count)
    {
        vertices.reserve(vtx_count);
        indices.reserve(idx_count);
    }

    void add_vertex(const Vec3 &p) { vertices.push_back(Vertex{p}); }

    void add_triangle(std::uint32_t i0, std::uint32_t i1, std::uint32_t i2)
    {
        indices.push_back(i0);
        indices.push_back(i1);
        indices.push_back(i2);
    }
};