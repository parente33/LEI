#ifndef CG_CORE_MESH_H
#define CG_CORE_MESH_H

#include "core/Vector.h"

#include <cstdint>
#include <vector>

namespace core
{

struct Vertex
{
  Vector3 position;
  Vector3 normal;
  Vector2 texcoord;
};

class Mesh
{
public:
  std::vector<Vertex>        vertices;
  std::vector<std::uint32_t> indices;

  Mesh () = default;

  void
  reserve (std::size_t v_count, std::size_t i_count)
  {
    vertices.reserve (v_count);
    indices.reserve  (i_count);
  }

  void
  add_vertex (const Vector3 &pos, const Vector3 &norm, const Vector2 &tex)
  {
    vertices.push_back ({ pos, norm, tex });
  }

  void
  add_triangle (std::uint32_t i0, std::uint32_t i1, std::uint32_t i2)
  {
    indices.push_back (i0);
    indices.push_back (i1);
    indices.push_back (i2);
  }

  void
  clear ()
  {
    vertices.clear ();
    indices.clear  ();
  }
};

} // namespace core

#endif // CG_CORE_MESH_H