#pragma once

#include <filesystem>
#include <string>
#include <unordered_map>
#include <vector>
#include <memory>

#include "mesh.hpp"
#include "world.hpp"


struct ResourceError
{
    std::string message;
};

class ResourceCache
{
    public:
        MeshId load_mesh(const std::filesystem::path &path, ResourceError &err) noexcept;
        const Mesh *mesh(MeshId id) const noexcept;

    private:
        static std::string key(std::filesystem::path p) {
            return p.lexically_normal().generic_string();
        }

        std::unordered_map<std::string, MeshId>  meshMap_;
        std::vector<std::unique_ptr<Mesh>>       meshes_;
};