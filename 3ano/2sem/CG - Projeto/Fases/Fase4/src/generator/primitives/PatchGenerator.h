#ifndef CG_GENERATOR_PRIMITIVES_PATCH_GENERATOR_H
#define CG_GENERATOR_PRIMITIVES_PATCH_GENERATOR_H

#include "Generator.h"
#include "PatchReader.h"

#include <string>

namespace generator
{

class PatchGenerator : public PrimitiveGenerator
{
public:
  PatchGenerator (const std::string &filepath, int tessellation);
  core::Mesh generate () const override;

private:
  std::string filepath;
  int         tessellation;
};

} // namespace generator

#endif // CG_GENERATOR_PRIMITIVES_PATCH_GENERATOR_H