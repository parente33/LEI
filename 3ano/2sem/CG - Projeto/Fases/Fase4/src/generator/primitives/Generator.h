#ifndef CG_GENERATOR_PRIMITIVES_GENERATOR_H
#define CG_GENERATOR_PRIMITIVES_GENERATOR_H

#include "core/Mesh.h"

namespace generator
{

class PrimitiveGenerator
{
public:
  virtual ~PrimitiveGenerator () = default;
  virtual core::Mesh generate () const = 0;

protected:
  static void emit_grid_indices (core::Mesh &m, std::uint32_t base,
                                 std::uint32_t stride, int udiv, int vdiv);

  static void emit_grid_indices_reversed (core::Mesh &m, std::uint32_t base,
                                          std::uint32_t stride, int udiv,
                                          int vdiv);

  static std::uint32_t emit_patch (core::Mesh &m, const core::Vector3 &origin,
                                   const core::Vector3 &u,
                                   const core::Vector3 &v, int udiv, int vdiv);
};

} // namespace generator

#endif // CG_GENERATOR_PRIMITIVES_GENERATOR_H