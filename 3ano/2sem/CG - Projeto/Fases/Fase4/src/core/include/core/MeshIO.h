#ifndef CG_CORE_MESH_IO_H
#define CG_CORE_MESH_IO_H

#include "core/Mesh.h"

#include <string>

namespace core
{

int write_3d (const std::string &filepath, const Mesh &mesh);
int read_3d  (const std::string &filepath, Mesh &out_mesh);

} // namespace core

#endif // CG_CORE_MESH_IO_H