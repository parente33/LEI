#include <iostream>
#include <cstdlib>

#include "commands.hpp"
#include "mesh_io.hpp"


int main (int argc, char *argv[])
{
    if (argc < 2)
    {
        print_global_usage(std::cerr);
        return 1;
    }

    const Command *cmd = find_command(argv[1]);
    if (!cmd)
    {
        print_global_usage(std::cerr);
        return 1;
    }

    const int expected = static_cast<int>(cmd->arg_count) + 1;
    const int provided = argc - 2;

    if (provided != expected)
    {
        print_command_usage(std::cerr, *cmd);
        return 1;
    }

    constexpr std::size_t kMaxArgs = 8;
    ArgVal vals[kMaxArgs] {};

    for (std::size_t i = 0; i < cmd->arg_count; ++i)
    {
        const char *s = argv[2 + i];

        if (cmd->args[i].type == ArgType::Float)
            vals[i].f = std::strtof(s, nullptr);
        else
            vals[i].i = static_cast<int>(std::strtol(s, nullptr, 10));
    }

    const char *out_path = argv[2 + cmd->arg_count];

    Mesh mesh;
    int r = cmd->build(vals, mesh);

    if (r != 0)
    {
        std::cerr << "command failed\n";
        return 1;
    }

    if (!write_3d(out_path, mesh))
    {
        std::cerr << "failed to write '" << out_path << "'\n";
        return 1;
    }

    return 0;
}