#include "commands.hpp"

#include <ostream>
#include <cstdlib>

#include "primitives.hpp"


namespace {

/* --- build functions --- */

int build_plane (const ArgVal *a, Mesh &out) noexcept
{
    out = make_plane(a[0].f, a[1].i);
    return 0;
}

int build_box (const ArgVal *a, Mesh &out) noexcept
{
    out = make_box(a[0].f, a[1].i);
    return 0;
}

int build_sphere (const ArgVal *a, Mesh &out) noexcept
{
    out = make_sphere(a[0].f, a[1].i, a[2].i);
    return 0;
}

int build_cone (const ArgVal *a, Mesh &out) noexcept
{
    out = make_cone(a[0].f, a[1].f, a[2].i, a[3].i);
    return 0;
}

/* --- arg specs --- */

constexpr ArgSpec kPlaneArgs[] = {
    { ArgType::Float, "length" }, { ArgType::Int,   "divisions" },
};

constexpr ArgSpec kBoxArgs[] = {
    { ArgType::Float, "length" }, { ArgType::Int,   "divisions" },
};

constexpr ArgSpec kSphereArgs[] = {
    { ArgType::Float, "radius" }, { ArgType::Int,   "slices" },
    { ArgType::Int,   "stacks" },
};

constexpr ArgSpec kConeArgs[] = {
    { ArgType::Float, "radius" }, { ArgType::Float, "height" },
    { ArgType::Int,   "slices" }, { ArgType::Int,   "stacks" },
};

/* --- command table --- */

constexpr Command kCommands[] = {
    {
        "plane",
        "Square in the XZ plane, centered at the origin.",
        kPlaneArgs, std::size(kPlaneArgs),
        &build_plane
    },
    {
        "box",
        "Box centered at the origin, subdivided per face.",
        kBoxArgs, std::size(kBoxArgs),
        &build_box
    },
    {
        "sphere",
        "Sphere centered at the origin.",
        kSphereArgs, std::size(kSphereArgs),
        &build_sphere
    },
    {
        "cone",
        "Cone with base on the XZ plane.",
        kConeArgs, std::size(kConeArgs),
        &build_cone
    },
};

} /* namespace */


const Command *find_command (std::string_view name) noexcept
{
    for (const auto &cmd : kCommands)
        if (name == cmd.name) return &cmd;

    return nullptr;
}

void print_global_usage (std::ostream &os)
{
    os << "Usage:\n"
       << "  generator <command> [args...] <output.3d>\n\n"
       << "Commands:\n";

    for (const auto &cmd : kCommands)
    {
        os << "  generator " << cmd.name;

        for (std::size_t i = 0; i < cmd.arg_count; ++i)
            os << " <" << cmd.args[i].name << ">";

        os << " <output.3d>\n"
           << "      " << cmd.help << "\n\n";
    }
}

void print_command_usage (std::ostream &os, const Command &cmd)
{
    os << "Usage:\n"
       << "  generator " << cmd.name;

    for (std::size_t i = 0; i < cmd.arg_count; ++i)
        os << " <" << cmd.args[i].name << ">";

    os << " <output.3d>\n"
       << "  " << cmd.help << "\n";
}