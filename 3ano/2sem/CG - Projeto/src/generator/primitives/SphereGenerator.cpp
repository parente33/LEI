#include "SphereGenerator.h"

#include <cmath>

namespace generator
{

SphereGenerator::SphereGenerator (float radius, int slices, int stacks)
    : radius (radius), slices (slices), stacks (stacks)
{
}

core::Mesh
SphereGenerator::generate () const
{
  core::Mesh m;

  const auto rows = static_cast<std::size_t> (stacks - 1);
  const auto cols = static_cast<std::size_t> (slices + 1);

  m.reserve (2 + rows * cols, 6 * static_cast<std::size_t> (slices) * rows);

  const float PI = 3.14159265359f;

  const float dT = 2.f * PI / static_cast<float> (slices);
  const float dP = PI / static_cast<float> (stacks);

  /* top poles */
  const auto i_top_base = static_cast<std::uint32_t> (m.vertices.size ());
  for (int s = 0; s < slices; ++s)
    {
      float u = (static_cast<float>(s) + 0.5f) / static_cast<float>(slices);
      m.add_vertex ({ 0.f, radius, 0.f }, { 0.f, 1.f, 0.f }, { u, 0.f });
    }

  /* rings k=1..stacks-1 */
  const auto ring_base = static_cast<std::uint32_t> (m.vertices.size ());

  for (int k = 1; k < stacks; ++k)
    {
      const float phi = static_cast<float> (k) * dP;

      const float cp = std::cos (phi);
      const float sp = std::sin (phi);

      for (int s = 0; s <= slices; ++s)
        {
          const float theta = static_cast<float> (s) * dT;

          const float ct = std::cos (theta);
          const float st = std::sin (theta);

          const core::Vector3 n{ sp * ct, cp, sp * st };
          const core::Vector3 p = n * radius;

          const float u = static_cast<float> (s) / static_cast<float> (slices);
          const float v = static_cast<float> (k) / static_cast<float> (stacks);

          m.add_vertex (p, n, { u, v });
        }
    }

  /* bottom poles */
  const auto i_bot_base = static_cast<std::uint32_t> (m.vertices.size ());
  for (int s = 0; s < slices; ++s)
    {
      float u = (static_cast<float>(s) + 0.5f) / static_cast<float>(slices);
      m.add_vertex ({ 0.f, -radius, 0.f }, { 0.f, -1.f, 0.f }, { u, 1.f });
    }

  const auto stride = static_cast<std::uint32_t> (slices + 1);

  /* top cap */
  for (int s = 0; s < slices; ++s)
    {
      const auto a = ring_base + static_cast<std::uint32_t> (s);
      m.add_triangle (i_top_base + s, a + 1u, a);
    }

  /* middle bands */
  if (stacks > 2)
    {
      emit_grid_indices (m, ring_base, stride, slices, stacks - 2);
    }

  /* bottom cap */
  const auto last
      = ring_base + static_cast<std::uint32_t> (stacks - 2) * stride;
  for (int s = 0; s < slices; ++s)
    {
      const auto a = last + static_cast<std::uint32_t> (s);
      m.add_triangle (i_bot_base + s, a, a + 1u);
    }

  return m;
}

} // namespace generator