#include "Frustum.h"

namespace engine
{

void
Frustum::update (const core::Matrix4 &viewProj)
{
  // Left
  _planes[0].normal.x = viewProj.m[3]  + viewProj.m[0];
  _planes[0].normal.y = viewProj.m[7]  + viewProj.m[4];
  _planes[0].normal.z = viewProj.m[11] + viewProj.m[8];
  _planes[0].distance = viewProj.m[15] + viewProj.m[12];

  // Right
  _planes[1].normal.x = viewProj.m[3]  - viewProj.m[0];
  _planes[1].normal.y = viewProj.m[7]  - viewProj.m[4];
  _planes[1].normal.z = viewProj.m[11] - viewProj.m[8];
  _planes[1].distance = viewProj.m[15] - viewProj.m[12];

  // Bottom
  _planes[2].normal.x = viewProj.m[3]  + viewProj.m[1];
  _planes[2].normal.y = viewProj.m[7]  + viewProj.m[5];
  _planes[2].normal.z = viewProj.m[11] + viewProj.m[9];
  _planes[2].distance = viewProj.m[15] + viewProj.m[13];

  // Top
  _planes[3].normal.x = viewProj.m[3]  - viewProj.m[1];
  _planes[3].normal.y = viewProj.m[7]  - viewProj.m[5];
  _planes[3].normal.z = viewProj.m[11] - viewProj.m[9];
  _planes[3].distance = viewProj.m[15] - viewProj.m[13];

  // Near
  _planes[4].normal.x = viewProj.m[3]  + viewProj.m[2];
  _planes[4].normal.y = viewProj.m[7]  + viewProj.m[6];
  _planes[4].normal.z = viewProj.m[11] + viewProj.m[10];
  _planes[4].distance = viewProj.m[15] + viewProj.m[14];

  // Far
  _planes[5].normal.x = viewProj.m[3]  - viewProj.m[2];
  _planes[5].normal.y = viewProj.m[7]  - viewProj.m[6];
  _planes[5].normal.z = viewProj.m[11] - viewProj.m[10];
  _planes[5].distance = viewProj.m[15] - viewProj.m[14];

  for (int i = 0; i < 6; ++i)
    {
      float length = _planes[i].normal.length ();

      _planes[i].normal    = _planes[i].normal / length;
      _planes[i].distance /= length;
    }
}

bool
Frustum::isSphereInside (const core::Vector3 &center, float radius) const
{
  for (int i = 0; i < 6; ++i)
    {
      if (core::Vector3::dot (_planes[i].normal, center) + _planes[i].distance
          < -radius)
        {
          return false;
        }
    }

  return true;
}

} // namespace engine