#pragma once

#include "mesh.hpp"


Mesh make_plane(float length, int divisions);
Mesh make_box(float length, int divisions);
Mesh make_sphere(float radius, int slices, int stacks);
Mesh make_cone(float radius, float height, int slices, int stacks);