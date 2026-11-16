#ifndef CG_ENGINE_CAMERA_H
#define CG_ENGINE_CAMERA_H

#include "core/Matrix.h"
#include "core/Vector.h"
#include "scene/Scene.h"

namespace engine
{

class Camera
{
public:
  virtual ~Camera () = default;

  virtual core::Matrix4 getViewMatrix       () const = 0;
  virtual core::Matrix4 getProjectionMatrix (float aspect) const = 0;

  virtual void
  update (float dt)
  {
  }
};

class OrbitalCamera : public Camera
{
public:
  OrbitalCamera (const core::Vector3 &center, float radius, float alpha,
                  float beta, float fov, float nearZ, float farZ);

  static OrbitalCamera fromConfig (const CameraConfig &config);

  core::Matrix4 getViewMatrix       () const override;
  core::Matrix4 getProjectionMatrix (float aspect) const override;

  void handleMove  (float dx, float dy);
  void handleZoom (float dy);

private:
  core::Vector3 _center;
  float _radius;
  float _alpha;
  float _beta;

  float _fov;
  float _nearZ;
  float _farZ;
};

} // namespace engine

#endif // CG_ENGINE_CAMERA_H