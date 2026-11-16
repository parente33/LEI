#include <iostream>

#include "commands.hpp"
#include "mesh_io.hpp"


int main(int argc, char *argv[])
{
    if (argc < 2) {
        print_global_usage(std::cerr);
        return 1;
    }

    const Command *cmd = find_command(argv[1]);
    if (!cmd) {
        print_global_usage(std::cerr);
        return 1;
    }

    Mesh mesh;
    const char *out_path = nullptr;

    const BuildResult r = cmd->build(argc - 2, argv + 2, mesh, out_path);
    if (r == BuildResult::Usage) {
        print_command_usage(std::cerr, *cmd);
        return 1;
    }

    if (r != BuildResult::Ok) {
        std::cerr << "command failed\n";
        return 1;
    }

    if (!write_3d(out_path, mesh)) {
        std::cerr << "failed to write to '" << out_path << "'\n";
        return 1;
    }

    return 0;
}