#include "Camera.h"
#include <cmath>

namespace engine
{

OrbitalCamera::OrbitalCamera (const core::Vector3 &center, float radius,
                                float alpha, float beta, float fov,
                                float nearZ, float farZ)
    : _center (center), _radius (radius), _alpha (alpha), _beta (beta),
      _fov (fov), _nearZ (nearZ), _farZ (farZ)
{
}

OrbitalCamera
OrbitalCamera::fromConfig (const CameraConfig &config)
{
  core::Vector3 diff = config.pos - config.lookAt;
  float radius = diff.length ();

  float alpha = 0.0f;
  float beta  = 0.0f;

  if (radius > 0.0001f)
    {
      beta  = std::asin  (diff.y / radius);
      alpha = std::atan2 (diff.x, diff.z);
    }

  return OrbitalCamera (config.lookAt, radius, alpha, beta, config.fovDeg,
                         config.nearZ, config.farZ);
}

core::Matrix4
OrbitalCamera::getViewMatrix () const
{
  float camX = _center.x + _radius * std::cos (_beta) * std::sin (_alpha);
  float camY = _center.y + _radius * std::sin (_beta);
  float camZ = _center.z + _radius * std::cos (_beta) * std::cos (_alpha);

  core::Vector3 eye (camX, camY, camZ);
  core::Vector3 up  (0.0f, 1.0f, 0.0f);

  return core::Matrix4::lookAt (eye, _center, up);
}

core::Matrix4
OrbitalCamera::getProjectionMatrix (float aspect) const
{
  return core::Matrix4::perspective (_fov, aspect, _nearZ, _farZ);
}

void
OrbitalCamera::handleMove (float dx, float dy)
{
  _alpha -= dx * 0.01f;
  _beta  += dy * 0.01f;

  float maxBeta = (M_PI / 2.0f) - 0.01f;
  if (_beta > maxBeta)
    {
      _beta = maxBeta;
    }

  if (_beta < -maxBeta)
    {
      _beta = -maxBeta;
    }
}

void
OrbitalCamera::handleZoom (float dy)
{
  _radius -= dy * 5.0f;
  if (_radius < 1.0f)
    {
      _radius = 1.0f;
    }
}

} // namespace engine