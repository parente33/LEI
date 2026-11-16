#ifndef CG_GENERATOR_PATCH_READER_H
#define CG_GENERATOR_PATCH_READER_H

#include "core/Vector.h"

#include <string>
#include <vector>

namespace generator
{

struct PatchData
{
  int indices[16];
};

class PatchReader
{
public:
  std::vector<PatchData>     patches;
  std::vector<core::Vector3> control_points;

  bool read (const std::string &filepath);
};

} // namespace generator

#endif // CG_GENERATOR_PATCH_READER_H