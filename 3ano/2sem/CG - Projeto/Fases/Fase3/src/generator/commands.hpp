#pragma once

#include <string_view>

#include "mesh.hpp"


enum class BuildResult : unsigned char
{
    Ok     = 0,
    Usage  = 1,
    Fail   = 2
};

struct Command
{
    const char   *name;
    const char   *usage;
    const char   *help;
    BuildResult  (*build)(int argc, char *argv[], Mesh &out_mesh,
        const char *&out_path) noexcept;
};

const Command *find_command(std::string_view name) noexcept;

void print_global_usage(std::ostream &os);
void print_command_usage(std::ostream &os, const Command &cmd);