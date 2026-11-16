#ifndef CG_GENERATOR_PRIMITIVES_CONE_GENERATOR_H
#define CG_GENERATOR_PRIMITIVES_CONE_GENERATOR_H

#include "Generator.h"

namespace generator
{

class ConeGenerator : public PrimitiveGenerator
{
public:
  ConeGenerator (float radius, float height, int slices, int stacks);
  core::Mesh generate () const override;

private:
  float radius;
  float height;
  int   slices;
  int   stacks;
};

} // namespace generator

#endif // CG_GENERATOR_PRIMITIVES_CONE_GENERATOR_H