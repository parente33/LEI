#ifndef CG_ENGINE_CAMERA_FRUSTUM_H
#define CG_ENGINE_CAMERA_FRUSTUM_H

#include "core/Matrix.h"
#include "core/Vector.h"

namespace engine
{

class Frustum
{
public:
  Frustum () = default;

  void update         (const core::Matrix4 &viewProj);
  bool isSphereInside (const core::Vector3 &center, float radius) const;

private:
  struct Plane
  {
    core::Vector3 normal;
    float distance;
  };

  Plane _planes[6];
};

} // namespace engine

#endif // CG_ENGINE_CAMERA_FRUSTUM_H