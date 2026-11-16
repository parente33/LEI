#ifndef CG_CORE_MATRIX_H
#define CG_CORE_MATRIX_H

#include "core/Vector.h"

namespace core
{

class Matrix4
{
public:
  float m[16] = { 0 };

  Matrix4 () = default;

  static Matrix4 identity ();
  static Matrix4 translation (const Vector3 &v);
  static Matrix4 rotation (float angle, const Vector3 &axis);
  static Matrix4 scale (const Vector3 &v);

  static Matrix4 lookAt (const Vector3 &eye, const Vector3 &center,
                         const Vector3 &up);
  static Matrix4 perspective (float fovDeg, float aspect, float nearZ,
                              float farZ);

  Matrix4 operator* (const Matrix4 &o) const;
  Vector3 operator* (const Vector3 &v) const;

  const float *
  ptr () const
  {
    return m;
  }
};

} // namespace core

#endif // CG_CORE_MATRIX_H