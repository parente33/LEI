#include "mesh_io.hpp"

#include <fstream>
#include <cstdint>
#include <string>

#include "mesh.hpp"


namespace
{

    struct Header
    {
        std::uint32_t  vertex_count;
        std::uint32_t  index_count;
    };

    inline bool write_bytes(std::ofstream &os,
        const void *p, std::size_t n) noexcept
    {
        os.write(static_cast<const char*>(p), static_cast<std::streamsize>(n));
        return os.good();
    }

    inline bool read_bytes(std::ifstream &is, void *p,
        std::size_t n) noexcept
    {
        is.read(static_cast<char*>(p), static_cast<std::streamsize>(n));
        return is.good();
    }

} /* namespace */


bool write_3d(std::string path, const Mesh &mesh) noexcept
{
    std::ofstream os(path, std::ios::binary);
    if (!os) return false;

    const Header h
    {
        static_cast<std::uint32_t>(mesh.vertices.size()),
        static_cast<std::uint32_t>(mesh.indices.size())
    };

    if (!write_bytes(os, &h, sizeof h)) return false;

    const std::size_t vtx_bytes =
        mesh.vertices.size() * sizeof(Vertex);

    if (vtx_bytes &&
        !write_bytes(os, mesh.vertices.data(), vtx_bytes)) return false;

    const std::size_t idx_bytes =
        mesh.indices.size() * sizeof(std::uint32_t);

    if (idx_bytes &&
        !write_bytes(os, mesh.indices.data(), idx_bytes)) return false;

    return true;
}


bool read_3d(std::string path, Mesh &out) noexcept
{
    out.vertices.clear();
    out.indices.clear();

    std::ifstream is(path, std::ios::binary);
    if (!is) return false;

    Header h {};
    if (!read_bytes(is, &h, sizeof h)) return false;

    out.vertices.resize(h.vertex_count);

    const std::size_t vtx_bytes =
        std::size_t(h.vertex_count) * sizeof(Vertex);

    if (vtx_bytes &&
        !read_bytes(is, out.vertices.data(), vtx_bytes)) return false;

    out.indices.resize(h.index_count);

    const std::size_t idx_bytes =
        std::size_t(h.index_count) * sizeof(std::uint32_t);

    if (idx_bytes &&
        !read_bytes(is, out.indices.data(), idx_bytes)) return false;

    return true;
}