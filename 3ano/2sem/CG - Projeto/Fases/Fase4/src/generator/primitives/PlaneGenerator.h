#ifndef CG_GENERATOR_PRIMITIVES_PLANE_GENERATOR_H
#define CG_GENERATOR_PRIMITIVES_PLANE_GENERATOR_H

#include "Generator.h"

namespace generator
{

class PlaneGenerator : public PrimitiveGenerator
{
public:
  PlaneGenerator (float length, int divisions);
  core::Mesh generate () const override;

private:
  float length;
  int   div;
};

} // namespace generator

#endif // CG_GENERATOR_PRIMITIVES_PLANE_GENERATOR_H