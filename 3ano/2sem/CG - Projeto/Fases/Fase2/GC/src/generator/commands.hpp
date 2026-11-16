#pragma once

#include <cstddef>
#include <iosfwd>
#include <string_view>

#include "mesh.hpp"


enum class ArgType : unsigned char
{
    Float,
    Int
};

struct ArgSpec
{
    ArgType      type;
    const char  *name;
};

union ArgVal
{
    float f;
    int   i;
};

struct Command
{
    const char     *name;
    const char     *help;

    const ArgSpec  *args;
    std::size_t     arg_count;

    int             (*build)(const ArgVal *args, Mesh &out_mesh) noexcept;
};

const Command *find_command (std::string_view name) noexcept;

void print_global_usage (std::ostream &os);
void print_command_usage (std::ostream &os, const Command &cmd);