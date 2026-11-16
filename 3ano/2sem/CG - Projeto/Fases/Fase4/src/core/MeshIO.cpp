#include "core/MeshIO.h"

#include <fstream>
#include <iostream>

namespace core
{

int
write_3d (const std::string &filepath, const Mesh &mesh)
{
  std::ofstream out (filepath);
  if (!out)
    {
      std::cerr << "Failed to open file for writing: " << filepath << "\n";
      return 1;
    }

  out << "VERTICES " << mesh.vertices.size () << "\n";
  for (const auto &v : mesh.vertices)
    {
      out << v.position.x << " " << v.position.y << " " << v.position.z << " "
          << v.normal.x   << " " << v.normal.y   << " " << v.normal.z   << " "
          << v.texcoord.x << " " << v.texcoord.y << "\n";
    }

  out << "INDICES " << mesh.indices.size () << "\n";
  for (size_t i = 0; i < mesh.indices.size (); i += 3)
    {
      out << mesh.indices[i]     << " "
          << mesh.indices[i + 1] << " "
          << mesh.indices[i + 2] << "\n";
    }

  return 0;
}

int
read_3d (const std::string &filepath, Mesh &out_mesh)
{
  std::ifstream in (filepath);
  if (!in)
    {
      std::cerr << "Failed to open file for reading: " << filepath << "\n";
      return 1;
    }

  out_mesh.clear ();
  std::string token;

  if (!(in >> token) || token != "VERTICES")
    {
      return 1;
    }

  size_t v_count;
  if (!(in >> v_count))
    {
      return 1;
    }

  out_mesh.vertices.resize (v_count);
  for (size_t i = 0; i < v_count; ++i)
    {
      in >> out_mesh.vertices[i].position.x
         >> out_mesh.vertices[i].position.y
         >> out_mesh.vertices[i].position.z

         >> out_mesh.vertices[i].normal.x
         >> out_mesh.vertices[i].normal.y
         >> out_mesh.vertices[i].normal.z

         >> out_mesh.vertices[i].texcoord.x
         >> out_mesh.vertices[i].texcoord.y;
    }

  if (!(in >> token) || token != "INDICES")
    {
      return 1;
    }

  size_t i_count;
  if (!(in >> i_count))
    {
      return 1;
    }

  out_mesh.indices.resize (i_count);
  for (size_t i = 0; i < i_count; ++i)
    {
      in >> out_mesh.indices[i];
    }

  return 0;
}

} // namespace core