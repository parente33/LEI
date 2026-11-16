#pragma once

#include <filesystem>
#include <string>

#include "world.hpp"
#include "resource_cache.hpp"
#include "world_parser.hpp"
#include "world_resolve.hpp"


struct Scene
{
    World          world;
    ResourceCache  cache;
};

inline bool load_scene(const std::filesystem::path &xml, Scene &out, WorldParseError &err) noexcept
{
    if (!parse_world_xml(xml, out.world, err)) return false;

    std::string rerr;
    if (!resolve_world(out.world, out.cache, rerr)) {
        err.message = "resolve: " + rerr;
        return false;
    }

    return true;
}