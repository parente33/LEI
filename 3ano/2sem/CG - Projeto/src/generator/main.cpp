#include "Commands.h"
#include "core/MeshIO.h"

#include <iostream>

int
main (int argc, char *argv[])
{
  core::Mesh  mesh;
  const char *out_path = nullptr;

  if (generator::execute (argc, argv, mesh, out_path) != 0)
    {
      /* std::cerr << "Failed to execute generator" << "\n"; */
      return 1;
    }

  if (core::write_3d (out_path, mesh) != 0)
    {
      std::cerr << "Failed to write mesh to " << out_path << "\n";
      return 1;
    }

  return 0;
}