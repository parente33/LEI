#ifndef CG_GENERATOR_PRIMITIVES_BOX_GENERATOR_H
#define CG_GENERATOR_PRIMITIVES_BOX_GENERATOR_H

#include "Generator.h"

namespace generator
{

class BoxGenerator : public PrimitiveGenerator
{
public:
  BoxGenerator (float length, int divisions);
  core::Mesh generate () const override;

private:
  float length;
  int   div;
};

} // namespace generator

#endif // CG_GENERATOR_PRIMITIVES_BOX_GENERATOR_H