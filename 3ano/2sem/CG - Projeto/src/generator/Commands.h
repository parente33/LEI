#ifndef CG_GENERATOR_COMMANDS_H
#define CG_GENERATOR_COMMANDS_H

#include "core/Mesh.h"

#include <cstddef>
#include <iosfwd>
#include <string_view>

namespace generator
{

enum class ArgType : unsigned char
{
  Float,
  Int,
  String
};

struct ArgSpec
{
  ArgType     type;
  const char *name;
};

union ArgVal
{
  float f;
  int   i;

  const char *s;
};

struct Command
{
  const char *name;
  const char *help;

  const ArgSpec *args;
  std::size_t    arg_count;

  void (*build) (const ArgVal *args, core::Mesh &out_mesh) noexcept;
};

int execute (int argc, char *argv[], core::Mesh &out_mesh,
             const char *&out_path) noexcept;

} // namespace generator

#endif // CG_GENERATOR_COMMANDS_H