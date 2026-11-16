#include "PatchGenerator.h"
#include "BezierPatch.h"

namespace generator
{

PatchGenerator::PatchGenerator (const std::string &filepath, int tessellation)
    : filepath (filepath), tessellation (tessellation)
{
}

core::Mesh
PatchGenerator::generate () const
{
  PatchReader reader;
  if (!reader.read (filepath))
    {
      return {};
    }

  core::Mesh m;
  const int num_patches = static_cast<int> (reader.patches.size ());
  const int L           = tessellation;

  m.reserve (num_patches * (L + 1) * (L + 1), num_patches * L * L * 6);

  for (int p = 0; p < num_patches; ++p)
    {
      core::Vector3 P[16];
      for (int i = 0; i < 16; ++i)
        {
          P[i] = reader.control_points[reader.patches[p].indices[i]];
        }

      BezierPatch patch(P);

      std::uint32_t start_idx
          = static_cast<std::uint32_t> (m.vertices.size ());

      for (int j = 0; j <= L; ++j)
        {
          float v = static_cast<float> (j) / L;
          for (int i = 0; i <= L; ++i)
            {
              float u = static_cast<float> (i) / L;

              core::Vector3 pos  = patch.evaluate       (u, v, BezierEvaluationMode::Bernstein);
              core::Vector3 norm = patch.evaluateNormal (u, v, BezierEvaluationMode::Bernstein);

              m.add_vertex (pos, norm, { u, v });
            }
        }

      emit_grid_indices_reversed (m, start_idx,
                                  static_cast<std::uint32_t> (L + 1), L, L);
    }

  return m;
}

} // namespace generator