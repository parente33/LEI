#include "Transform.h"

#include <algorithm>
#include <cmath>

namespace engine
{

/* --- Static Transformations --- */

StaticTranslate::StaticTranslate (const core::Vector3 &offset)
    : _offset (offset)
{
}

StaticRotate::StaticRotate (float angleDeg, const core::Vector3 &axis)
    : _angleDeg (angleDeg), _axis (axis)
{
}

StaticScale::StaticScale (const core::Vector3 &scale) : _scale (scale)
{
}

void
StaticTranslate::apply (core::Matrix4 &currentMatrix, float time) const
{
  currentMatrix = currentMatrix * core::Matrix4::translation (_offset);
}

void
StaticRotate::apply (core::Matrix4 &currentMatrix, float time) const
{
  currentMatrix = currentMatrix * core::Matrix4::rotation (_angleDeg, _axis);
}

void
StaticScale::apply (core::Matrix4 &currentMatrix, float time) const
{
  currentMatrix = currentMatrix * core::Matrix4::scale (_scale);
}

/* --- Animated Transformations --- */

AnimatedTranslate::AnimatedTranslate (float totalTime, bool align,
                                      const std::vector<core::Vector3> &points)
    : _totalTime (totalTime), _align (align), _points (points)
{
  buildArcLengthTable ();
}

AnimatedRotate::AnimatedRotate (float totalTime, const core::Vector3 &axis)
    : _totalTime (totalTime), _axis (axis)
{
}

std::vector<core::Vector3>
AnimatedTranslate::getCurvePath () const
{
  std::vector<core::Vector3> curvePoints;
  if (_points.empty ())
    {
      return curvePoints;
    }

  int segments = 100;
  for (int i = 0; i < segments; ++i)
    {
      float t = static_cast<float> (i) / static_cast<float> (segments);
      core::Vector3 pos, deriv;

      getRawCurvePoint (t, pos, deriv);
      curvePoints.push_back (pos);
    }

  return curvePoints;
}

void
AnimatedTranslate::buildArcLengthTable ()
{
  if (_points.empty ())
    {
      return;
    }

  int samples = 100 * _points.size ();
  _arcLengths.reserve (samples + 1);
  _arcLengths.push_back (0.0f);

  core::Vector3 lastPos, deriv;
  getRawCurvePoint (0.0f, lastPos, deriv);

  float length = 0.0f;
  for (int i = 1; i <= samples; ++i)
    {
      float t = static_cast<float> (i) / static_cast<float> (samples);
      core::Vector3 pos;

      getRawCurvePoint (t, pos, deriv);

      length += (pos - lastPos).length ();
      _arcLengths.push_back (length);

      lastPos = pos;
    }

  _totalLength = length;
}

float
AnimatedTranslate::mapTimeToT (float t) const
{
  if (_arcLengths.empty () || _totalLength == 0.0f)
    {
      return t;
    }

  float targetLength = t * _totalLength;
  auto it = std::lower_bound (_arcLengths.begin (), _arcLengths.end (),
                              targetLength);

  if (it == _arcLengths.end ())
    {
      return 1.0f;
    }

  if (it == _arcLengths.begin ())
    {
      return 0.0f;
    }

  int index = std::distance (_arcLengths.begin (), it);

  float lengthAfter  = *it;
  float lengthBefore = *(it - 1);

  float segmentFraction = 0.0f;
  if (lengthAfter != lengthBefore)
    {
      segmentFraction
          = (targetLength - lengthBefore) / (lengthAfter - lengthBefore);
    }

  float rawTBefore = static_cast<float> (index - 1)
                     / static_cast<float> (_arcLengths.size () - 1);

  float rawTAfter = static_cast<float> (index)
                    / static_cast<float> (_arcLengths.size () - 1);

  return rawTBefore + segmentFraction * (rawTAfter - rawTBefore);
}

void
AnimatedTranslate::getRawCurvePoint (float t, core::Vector3 &pos,
                                     core::Vector3 &deriv) const
{
  if (_points.empty ())
    {
      return;
    }

  int pointCount   = _points.size ();
  float timeScaled = t * pointCount;

  int index = static_cast<int> (std::floor (timeScaled));
  if (index >= pointCount)
    {
      index = pointCount - 1;
    }

  float timeLocal = timeScaled - index;

  int indices[4] = { (index - 1 + pointCount) % pointCount, index % pointCount,
                     (index + 1) % pointCount, (index + 2) % pointCount };

  float posWeights[4] = { 0 };
  float derWeights[4] = { 0 };

  float localT  = timeLocal;
  float localT2 = localT  * localT;
  float localT3 = localT2 * localT;

  posWeights[0] = -0.5f * localT3        + localT2 - 0.5f * localT;
  posWeights[1] =  1.5f * localT3 - 2.5f * localT2 + 1.0f;
  posWeights[2] = -1.5f * localT3 + 2.0f * localT2 + 0.5f * localT;
  posWeights[3] =  0.5f * localT3 - 0.5f * localT2;

  derWeights[0] = -1.5f * localT2 + 2.0f * localT - 0.5f;
  derWeights[1] =  4.5f * localT2 - 5.0f * localT;
  derWeights[2] = -4.5f * localT2 + 4.0f * localT + 0.5f;
  derWeights[3] =  1.5f * localT2 - 1.0f * localT;

  pos   = core::Vector3 (0, 0, 0);
  deriv = core::Vector3 (0, 0, 0);

  for (int i = 0; i < 4; ++i)
    {
      const core::Vector3 &pt = _points[indices[i]];

      pos   += pt * posWeights[i];
      deriv += pt * derWeights[i];
    }
}

void
AnimatedTranslate::apply (core::Matrix4 &currentMatrix, float time) const
{
  if (_points.empty ())
    {
      return;
    }

  float t = std::fmod (time / _totalTime, 1.0f);
  if (t < 0)
    {
      t += 1.0f;
    }

  float rawT = mapTimeToT (t);

  core::Vector3 currentPos;
  core::Vector3 currentDeriv;
  getRawCurvePoint (rawT, currentPos, currentDeriv);

  currentMatrix = currentMatrix * core::Matrix4::translation (currentPos);

  if (_align && currentDeriv.length () > 0.0001f)
    {
      core::Vector3 worldUp = core::Vector3 (0.0f, 1.0f, 0.0f);
      core::Vector3 der     = currentDeriv.normalize ();

      core::Vector3 left = core::Vector3::cross (worldUp, der);
      if (left.length () < 0.0001f)
        {
          // Fallback if looking straight up or down
          left = core::Vector3::cross (core::Vector3 (1.0f, 0.0f, 0.0f), der);
        }

      left = left.normalize ();
      core::Vector3 up = core::Vector3::cross (der, left).normalize ();

      core::Matrix4 alignMatrix = core::Matrix4::identity ();

      // Build rotation matrix from basis vectors
      alignMatrix.m[0] = der.x;
      alignMatrix.m[1] = der.y;
      alignMatrix.m[2] = der.z;

      alignMatrix.m[4] = left.x;
      alignMatrix.m[5] = left.y;
      alignMatrix.m[6] = left.z;

      alignMatrix.m[8]  = up.x;
      alignMatrix.m[9]  = up.y;
      alignMatrix.m[10] = up.z;

      currentMatrix = currentMatrix * alignMatrix;
    }
}

void
AnimatedRotate::apply (core::Matrix4 &currentMatrix, float time) const
{
  float t = std::fmod (time / _totalTime, 1.0f);
  if (t < 0)
    {
      t += 1.0f;
    }

  float angleDeg = t * 360.0f;
  currentMatrix = currentMatrix * core::Matrix4::rotation (angleDeg, _axis);
}

} // namespace engine