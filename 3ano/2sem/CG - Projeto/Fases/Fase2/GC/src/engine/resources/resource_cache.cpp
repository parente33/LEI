#include "resource_cache.hpp"

#include "mesh_io.hpp"


MeshId ResourceCache::load_mesh(const std::filesystem::path &path, ResourceError &err) noexcept
{
    const std::string k = key(path);

    if (auto it = meshMap_.find(k); it != meshMap_.end()) {
        return it->second;
    }

    auto m = std::make_unique<Mesh>();
    if (!read_3d(k, *m)) {
        err.message = "failed to read .3d: " + k;
        return kInvalidMeshId;
    }

    MeshId id = static_cast<MeshId>(meshes_.size());
    meshes_.push_back(std::move(m));
    meshMap_.emplace(k, id);

    return id;
}

const Mesh *ResourceCache::mesh(MeshId id) const noexcept
{
    if (id == kInvalidMeshId) return nullptr;

    const auto i = static_cast<std::size_t>(id);
    if (i >= meshes_.size()) return nullptr;

    return meshes_[i].get();
}