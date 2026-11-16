#pragma once

#include <string>

#include "world.hpp"
#include "resource_cache.hpp"


inline bool resolve_world(World &w, ResourceCache &cache, std::string &errMsg) noexcept
{
    ResourceError rerr;
    for (Node &n : w.nodes)
    {
        for (Model &m : n.models)
        {
            if (m.mesh == kInvalidMeshId) {
                m.mesh = cache.load_mesh(m.meshFile, rerr);

                if (m.mesh == kInvalidMeshId) {
                    errMsg = rerr.message;
                    return false;
                }
            }
        }
    }

    return true;
}