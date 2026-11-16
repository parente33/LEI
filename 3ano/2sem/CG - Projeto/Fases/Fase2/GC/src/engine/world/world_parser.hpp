#pragma once

#include <filesystem>
#include <string>

#include "world.hpp"


struct WorldParseError
{
    std::string message;
};

bool parse_world_xml(const std::filesystem::path &xml_path,
    World &out_world, WorldParseError &out_err) noexcept;