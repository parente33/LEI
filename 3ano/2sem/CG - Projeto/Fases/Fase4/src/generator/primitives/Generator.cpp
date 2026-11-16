#include "Generator.h"

namespace generator
{

void
PrimitiveGenerator::emit_grid_indices (core::Mesh &m, std::uint32_t base,
                                       std::uint32_t stride, int udiv,
                                       int vdiv)
{
  for (int j = 0; j < vdiv; ++j)
    {
      const auto r0 = base + static_cast<std::uint32_t> (j) * stride;
      const auto r1 = r0 + stride;

      for (int i = 0; i < udiv; ++i)
        {
          const auto a = r0 + static_cast<std::uint32_t> (i);
          const auto b = a + 1u;

          const auto c = r1 + static_cast<std::uint32_t> (i);
          const auto d = c + 1u;

          m.add_triangle (a, b, d);
          m.add_triangle (a, d, c);
        }
    }
}

void
PrimitiveGenerator::emit_grid_indices_reversed (core::Mesh &m,
                                                std::uint32_t base,
                                                std::uint32_t stride, int udiv,
                                                int vdiv)
{
  for (int j = 0; j < vdiv; ++j)
    {
      const auto r0 = base + static_cast<std::uint32_t> (j) * stride;
      const auto r1 = r0 + stride;

      for (int i = 0; i < udiv; ++i)
        {
          const auto a = r0 + static_cast<std::uint32_t> (i);
          const auto b = a + 1u;

          const auto c = r1 + static_cast<std::uint32_t> (i);
          const auto d = c + 1u;

          m.add_triangle (a, d, b);
          m.add_triangle (a, c, d);
        }
    }
}

std::uint32_t
PrimitiveGenerator::emit_patch (core::Mesh &m, const core::Vector3 &origin,
                                const core::Vector3 &u, const core::Vector3 &v,
                                int udiv, int vdiv)
{
  const auto base   = static_cast<std::uint32_t> (m.vertices.size ());
  const auto stride = static_cast<std::uint32_t> (udiv + 1);

  const core::Vector3 n = core::Vector3::cross (u, v).normalize ();

  const float inv_u = 1.f / static_cast<float> (udiv);
  const float inv_v = 1.f / static_cast<float> (vdiv);

  for (int j = 0; j <= vdiv; ++j)
    {
      const float tv = static_cast<float> (j) * inv_v;

      for (int i = 0; i <= udiv; ++i)
        {
          const float tu = static_cast<float> (i) * inv_u;
          const core::Vector3 p = origin + u * tu + v * tv;

          m.add_vertex (p, n, { tu, tv });
        }
    }

  emit_grid_indices (m, base, stride, udiv, vdiv);
  return base;
}

} // namespace generator