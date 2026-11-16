#include "BoxGenerator.h"

namespace generator
{

BoxGenerator::BoxGenerator (float length, int divisions)
    : length (length), div (divisions)
{
}

core::Mesh
BoxGenerator::generate () const
{
  core::Mesh m;
  const auto d = static_cast<std::size_t> (div);
  m.reserve ((d + 1) * (d + 1) * 6, d * d * 6 * 6);

  const float h = length * 0.5f;

  const core::Vector3 X { length, 0.f, 0.f };
  const core::Vector3 Y { 0.f, length, 0.f };
  const core::Vector3 Z { 0.f, 0.f, length };

  const core::Vector3 nX { -length, 0.f, 0.f };
  const core::Vector3 nZ { 0.f, 0.f, -length };

  // +Y (top)
  emit_patch(m, { -h, +h, +h }, X, nZ, div, div);

  // -Y (bottom)
  emit_patch(m, { -h, -h, -h }, X, Z,  div, div);

  // +Z (front)
  emit_patch(m, { -h, -h, +h }, X, Y,  div, div);

  // -Z (back)
  emit_patch(m, { +h, -h, -h }, nX, Y, div, div);

  // +X (right)
  emit_patch(m, { +h, -h, +h }, nZ, Y, div, div);

  // -X (left)
  emit_patch(m, { -h, -h, -h }, Z, Y,  div, div);

  return m;
}

} // namespace generator