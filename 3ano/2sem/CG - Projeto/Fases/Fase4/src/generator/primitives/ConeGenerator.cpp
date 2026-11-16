#include "ConeGenerator.h"

#include <cmath>

namespace generator
{

ConeGenerator::ConeGenerator (float radius, float height, int slices,
                              int stacks)
    : radius (radius), height (height), slices (slices), stacks (stacks)
{
}

core::Mesh
ConeGenerator::generate () const
{
  core::Mesh m;

  const auto sl = static_cast<std::size_t> (slices);
  const auto st = static_cast<std::size_t> (stacks);

  /* side + apex + base ring + center */
  m.reserve ((sl + 1) * st + 1 + (sl + 1) + 1, sl * st * 6 + sl * 3);

  const float PI = 3.14159265359f;

  const float dT    = 2.f * PI / static_cast<float> (slices);
  const float slant = std::sqrt (radius * radius + height * height);

  const float ny = radius / slant;  /* side normal Y      component */
  const float nr = height / slant;  /* side normal radial component */

  const auto side_base   = static_cast<std::uint32_t> (m.vertices.size ());
  const auto side_stride = static_cast<std::uint32_t> (slices + 1);

  for (int k = 0; k < stacks; ++k)
    {
      const float t = static_cast<float> (k) / static_cast<float> (stacks);
      const float y = t * height;
      const float r = (1.f - t) * radius;
      const float v = t;

      for (int s = 0; s <= slices; ++s)
        {
          const float theta = static_cast<float> (s) * dT;
          const float ct = std::cos (theta);
          const float st = std::sin (theta);

          const float u = static_cast<float> (s) / static_cast<float> (slices);

          const core::Vector3 pos {  r * ct,  y,  r * st };
          const core::Vector3 n   { nr * ct, ny, nr * st };

          m.add_vertex (pos, n, { u, v });
        }
    }

  if (stacks > 1)
    {
      emit_grid_indices_reversed (m, side_base, side_stride, slices,
                                  stacks - 1);
    }

  /* apex: connect last row to tip */
  const auto apex_base = static_cast<std::uint32_t> (m.vertices.size ());
  for (int s = 0; s < slices; ++s)
    {
      float u = (static_cast<float>(s) + 0.5f) / static_cast<float>(slices);
      m.add_vertex ({ 0.f, height, 0.f }, { 0.f, 1.f, 0.f }, { u, 1.f });
    }

  const auto top_row
      = side_base + static_cast<std::uint32_t> (stacks - 1) * side_stride;
  for (int s = 0; s < slices; ++s)
    {
      const auto a = top_row + static_cast<std::uint32_t> (s);
      m.add_triangle (a + 1u, a, apex_base + s);
    }

  /* base disk */
  const core::Vector3 base_n{ 0.f, -1.f, 0.f };
  const auto base_ring = static_cast<std::uint32_t> (m.vertices.size ());

  for (int s = 0; s <= slices; ++s)
    {
      const float theta = static_cast<float> (s) * dT;
      const float ct = std::cos (theta);
      const float st = std::sin (theta);
      const float u = static_cast<float> (s) / static_cast<float> (slices);

      m.add_vertex ({ radius * ct, 0.f, radius * st }, base_n, { u, 0.f });
    }

  const auto center_base = static_cast<std::uint32_t> (m.vertices.size ());
  for (int s = 0; s < slices; ++s)
    {
      float u = (static_cast<float>(s) + 0.5f) / static_cast<float>(slices);
      m.add_vertex ({ 0.f, 0.f, 0.f }, base_n, { u, 1.f });
    }

  for (int s = 0; s < slices; ++s)
    {
      const auto a = base_ring + static_cast<std::uint32_t> (s);
      m.add_triangle (center_base + s, a, a + 1u);
    }

  return m;
}

} // namespace generator