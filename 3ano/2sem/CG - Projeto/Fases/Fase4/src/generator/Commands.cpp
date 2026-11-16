#include "Commands.h"
#include "primitives/BoxGenerator.h"
#include "primitives/ConeGenerator.h"
#include "primitives/PatchGenerator.h"
#include "primitives/PlaneGenerator.h"
#include "primitives/SphereGenerator.h"

#include <cstdlib>
#include <iostream>
#include <ostream>

namespace generator
{

namespace
{

/* --- build functions --- */

void
build_plane (const ArgVal *a, core::Mesh &out) noexcept
{
  PlaneGenerator gen (a[0].f, a[1].i);
  out = gen.generate ();
}

void
build_box (const ArgVal *a, core::Mesh &out) noexcept
{
  BoxGenerator gen (a[0].f, a[1].i);
  out = gen.generate ();
}

void
build_sphere (const ArgVal *a, core::Mesh &out) noexcept
{
  SphereGenerator gen (a[0].f, a[1].i, a[2].i);
  out = gen.generate ();
}

void
build_cone (const ArgVal *a, core::Mesh &out) noexcept
{
  ConeGenerator gen (a[0].f, a[1].f, a[2].i, a[3].i);
  out = gen.generate ();
}

void
build_patch (const ArgVal *a, core::Mesh &out) noexcept
{
  PatchGenerator gen (a[0].s, a[1].i);
  out = gen.generate ();
}

/* --- arg specs --- */

constexpr ArgSpec kPlaneArgs[] = {
  { ArgType::Float, "length"    },
  { ArgType::Int,   "divisions" },
};

constexpr ArgSpec kBoxArgs[] = {
  { ArgType::Float, "length"    },
  { ArgType::Int,   "divisions" },
};

constexpr ArgSpec kSphereArgs[] = {
  { ArgType::Float, "radius"  },
  { ArgType::Int,   "slices"  },
  { ArgType::Int,   "stacks"  },
};

constexpr ArgSpec kConeArgs[] = {
  { ArgType::Float, "radius"  },
  { ArgType::Float, "height"  },
  { ArgType::Int,   "slices"  },
  { ArgType::Int,   "stacks"  },
};

constexpr ArgSpec kPatchArgs[] = {
  { ArgType::String, "file.patch"   },
  { ArgType::Int,    "tessellation" },
};

/* --- command table --- */

constexpr Command kCommands[] = {
  {
    "plane", "Square in the XZ plane, centered at the origin.",
    kPlaneArgs, std::size (kPlaneArgs), &build_plane
  },
  {
    "box", "Box centered at the origin, subdivided per face.",
    kBoxArgs, std::size (kBoxArgs), &build_box
  },
  {
    "sphere", "Sphere centered at the origin.",
    kSphereArgs, std::size (kSphereArgs), &build_sphere
  },
  {
    "cone", "Cone with base on the XZ plane.",
    kConeArgs, std::size (kConeArgs), &build_cone
  },
  {
    "patch", "Bezier patches from a file.",
    kPatchArgs, std::size (kPatchArgs), &build_patch
  },
};

} /* namespace */

const Command *
find_command (std::string_view name) noexcept
{
  for (const auto &cmd : kCommands)
    {
      if (name == cmd.name)
        {
          return &cmd;
        }
    }

  return nullptr;
}

void
print_command_usage (std::ostream &os, const Command &cmd)
{
  os << "Usage:\n"
     << "  generator " << cmd.name;

  for (std::size_t i = 0; i < cmd.arg_count; ++i)
    {
      os << " <" << cmd.args[i].name << ">";
    }

  os << " <output.3d>\n"
     << "  " << cmd.help << "\n";
}

void
print_global_usage (std::ostream &os)
{
  os << "Usage:\n"
     << "  generator <command> [args...] <output.3d>\n\n"
     << "Commands:\n";

  for (const auto &cmd : kCommands)
    {
      os << "  generator " << cmd.name;

      for (std::size_t i = 0; i < cmd.arg_count; ++i)
        {
          os << " <" << cmd.args[i].name << ">";
        }

      os << " <output.3d>\n"
         << "      " << cmd.help << "\n\n";
    }
}

int
execute (int argc, char *argv[], core::Mesh &out_mesh, const char *&out_path) noexcept
{
  if (argc < 2)
    {
      print_global_usage (std::cerr);
      return 1;
    }

  const Command *cmd = find_command (argv[1]);
  if (!cmd)
    {
      print_global_usage (std::cerr);
      return 1;
    }

  const int expected = static_cast<int> (cmd->arg_count) + 1;
  const int provided = argc - 2;

  if (provided != expected)
    {
      print_command_usage (std::cerr, *cmd);
      return 1;
    }

  constexpr std::size_t kMaxArgs = 8;
  ArgVal vals[kMaxArgs]{};

  for (std::size_t i = 0; i < cmd->arg_count; ++i)
    {
      const char *s = argv[2 + i];

      if (cmd->args[i].type == ArgType::Float)
        {
          vals[i].f = std::strtof (s, nullptr);
        }
      else if (cmd->args[i].type == ArgType::Int)
        {
          vals[i].i = static_cast<int> (std::strtol (s, nullptr, 10));
        }
      else
        {
          vals[i].s = s;
        }
    }

  out_path = argv[2 + cmd->arg_count];
  cmd->build (vals, out_mesh);

  return 0;
}

} // namespace generator