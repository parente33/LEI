#include "PatchReader.h"

#include <fstream>
#include <iostream>

namespace generator
{

bool
PatchReader::read (const std::string &filepath)
{
  std::ifstream in (filepath);
  if (!in)
    {
      std::cerr << "failed to open patch file: " << filepath << "\n";
      return false;
    }

  int num_patches;
  if (!(in >> num_patches))
    {
      return false;
    }

  char comma;

  patches.resize (num_patches);
  for (int p = 0; p < num_patches; ++p)
    {
      for (int j = 0; j < 16; ++j)
        {
          in >> patches[p].indices[j];
          if (j < 15)
            {
              in >> comma;
            }
        }
    }

  int num_points;
  if (!(in >> num_points))
    {
      return false;
    }

  control_points.resize (num_points);
  for (int i = 0; i < num_points; ++i)
    {
      in >> control_points[i].x;
      in >> comma;
      in >> control_points[i].y;
      in >> comma;
      in >> control_points[i].z;
    }

  return true;
}

} // namespace generator