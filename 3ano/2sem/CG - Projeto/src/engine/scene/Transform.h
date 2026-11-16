#ifndef CG_ENGINE_SCENE_TRANSFORM_H
#define CG_ENGINE_SCENE_TRANSFORM_H

#include "core/Matrix.h"
#include "core/Vector.h"

#include <vector>

namespace engine
{

class ShaderProgram;

class Transform
{
public:
  virtual ~Transform () = default;
  virtual void apply (core::Matrix4 &currentMatrix, float time) const = 0;
};

class StaticTranslate : public Transform
{
public:
  StaticTranslate (const core::Vector3 &offset);
  void apply (core::Matrix4 &currentMatrix, float time) const override;

private:
  core::Vector3 _offset;
};

class StaticRotate : public Transform
{
public:
  StaticRotate (float angleDeg, const core::Vector3 &axis);
  void apply (core::Matrix4 &currentMatrix, float time) const override;

private:
  float _angleDeg;
  core::Vector3 _axis;
};

class StaticScale : public Transform
{
public:
  StaticScale (const core::Vector3 &scale);
  void apply (core::Matrix4 &currentMatrix, float time) const override;

private:
  core::Vector3 _scale;
};

class AnimatedTranslate : public Transform
{
public:
  AnimatedTranslate (float totalTime, bool align,
                     const std::vector<core::Vector3> &points);

  void apply (core::Matrix4 &currentMatrix, float time) const override;
  std::vector<core::Vector3> getCurvePath () const;

private:
  void getRawCurvePoint (float t, core::Vector3 &pos,
                         core::Vector3 &deriv) const;

  void buildArcLengthTable ();
  float mapTimeToT (float t) const;

  float _totalTime;
  bool  _align;

  std::vector<core::Vector3> _points;
  std::vector<float>         _arcLengths;

  float _totalLength = 0.0f;
};

class AnimatedRotate : public Transform
{
public:
  AnimatedRotate (float totalTime, const core::Vector3 &axis);
  void apply (core::Matrix4 &currentMatrix, float time) const override;

private:
  float _totalTime;
  core::Vector3 _axis;
};

} // namespace engine

#endif // CG_ENGINE_SCENE_TRANSFORM_H