#ifndef CG_GENERATOR_PRIMITIVES_SPHERE_GENERATOR_H
#define CG_GENERATOR_PRIMITIVES_SPHERE_GENERATOR_H

#include "Generator.h"

namespace generator
{

class SphereGenerator : public PrimitiveGenerator
{
public:
  SphereGenerator (float radius, int slices, int stacks);
  core::Mesh generate () const override;

private:
  float radius;
  int   slices;
  int   stacks;
};

} // namespace generator

#endif // CG_GENERATOR_PRIMITIVES_SPHERE_GENERATOR_H