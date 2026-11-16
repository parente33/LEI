#include "commands.hpp"

#include <ostream>
#include <cstdlib>
#include <cstring>

#include "primitives.hpp"


namespace {

float to_f(const char *s) noexcept
{
    return std::strtof(s, nullptr);
}

int to_i(const char *s) noexcept
{
    return static_cast<int>(std::strtol(s, nullptr, 10));
}

BuildResult build_plane(int argc, char *argv[], Mesh &out_mesh,
    const char *&out_path) noexcept
{
    if (argc != 3) return BuildResult::Usage;

    const float length = to_f(argv[0]);
    const int divisions = to_i(argv[1]);

    out_path = argv[2];
    out_mesh = make_plane(length, divisions);

    return BuildResult::Ok;
}

BuildResult build_box(int argc, char *argv[], Mesh &out_mesh,
    const char *&out_path) noexcept
{
    if (argc != 3) return BuildResult::Usage;

    const float length = to_f(argv[0]);
    const int divisions = to_i(argv[1]);

    out_path = argv[2];
    out_mesh = make_box(length, divisions);

    return BuildResult::Ok;
}

BuildResult build_sphere(int argc, char *argv[], Mesh &out_mesh,
    const char *&out_path) noexcept
{
    if (argc != 4) return BuildResult::Usage;

    const float radius = to_f(argv[0]);
    const int slices = to_i(argv[1]);
    const int stacks = to_i(argv[2]);

    out_path = argv[3];
    out_mesh = make_sphere(radius, slices, stacks);

    return BuildResult::Ok;
}

BuildResult build_cone(int argc, char *argv[], Mesh &out_mesh,
    const char *&out_path) noexcept
{
    if (argc != 5) return BuildResult::Usage;

    const float radius = to_f(argv[0]);
    const float height = to_f(argv[1]);
    const int slices = to_i(argv[2]);
    const int stacks = to_i(argv[3]);

    out_path = argv[4];
    out_mesh = make_cone(radius, height, slices, stacks);

    return BuildResult::Ok;
}

constexpr Command kCommands[] = {
    {
        "plane",
        "plane <length> <divisions> <output.3d>",
        "Square in the XZ plane, centered at the origin, subdivided in X and Z.",
        &build_plane
    },
    {
        "box",
        "box <length> <divisions> <output.3d>",
        "Box centered at the origin, subdivided per face.",
        &build_box
    },
    {
        "sphere",
        "sphere <radius> <slices> <stacks> <output.3d>",
        "Sphere centered at the origin.",
        &build_sphere
    },
    {
        "cone",
        "cone <radius> <height> <slices> <stacks> <output.3d>",
        "Cone with base on the XZ plane.",
        &build_cone
    },
};

constexpr std::size_t kCommandCount = sizeof(kCommands) / sizeof(kCommands[0]);

} /* namespace */


const Command *find_command(std::string_view name) noexcept
{
    for (std::size_t i = 0; i < kCommandCount; ++i) {
        if (name == kCommands[i].name) return &kCommands[i];
    }

    return nullptr;
}

void print_global_usage(std::ostream &os)
{
    os << "Usage:\n"
       << "  generator <command> [args...]\n\n"
       << "Commands:\n";

    for (std::size_t i = 0; i < kCommandCount; ++i) {
        os << "  generator " << kCommands[i].usage << "\n"
           << "      " << kCommands[i].help << "\n\n";
    }
}

void print_command_usage(std::ostream &os, const Command &cmd)
{
    os << "Usage:\n"
       << "  generator " << cmd.usage << "\n"
       << "  " << cmd.help << "\n";
}