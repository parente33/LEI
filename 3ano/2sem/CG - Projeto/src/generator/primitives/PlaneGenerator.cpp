#include "PlaneGenerator.h"

namespace generator
{

PlaneGenerator::PlaneGenerator (float length, int divisions)
    : length (length), div (divisions)
{
}

core::Mesh
PlaneGenerator::generate () const
{
  core::Mesh m;
  const auto d = static_cast<std::size_t> (div);
  m.reserve ((d + 1) * (d + 1), d * d * 6);

  const float h = length * 0.5f;
  emit_patch (m,
              { -h, 0.f, +h },
              { length, 0.f, 0.f },
              { 0.f, 0.f, -length },
              div, div);

  return m;
}

} // namespace generator