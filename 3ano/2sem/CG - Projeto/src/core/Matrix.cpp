#include "core/Matrix.h"

namespace core
{

Matrix4
Matrix4::identity ()
{
  Matrix4 mat;

  mat.m[0] = 1;
  mat.m[5] = 1;
  mat.m[10] = 1;
  mat.m[15] = 1;

  return mat;
}

Matrix4
Matrix4::translation (const Vector3 &v)
{
  Matrix4 mat = identity ();

  mat.m[12] = v.x;
  mat.m[13] = v.y;
  mat.m[14] = v.z;

  return mat;
}

Matrix4
Matrix4::rotation (float angle, const Vector3 &axis)
{
  Matrix4 mat = identity ();
  Vector3 a = axis.normalize ();

  float rad = angle * (M_PI / 180.0f);
  float c = std::cos (rad);
  float s = std::sin (rad);
  float ic = 1.0f - c;

  mat.m[0] = a.x * a.x * ic + c;
  mat.m[1] = a.x * a.y * ic + a.z * s;
  mat.m[2] = a.x * a.z * ic - a.y * s;

  mat.m[4] = a.x * a.y * ic - a.z * s;
  mat.m[5] = a.y * a.y * ic + c;
  mat.m[6] = a.y * a.z * ic + a.x * s;

  mat.m[8] = a.x * a.z * ic + a.y * s;
  mat.m[9] = a.y * a.z * ic - a.x * s;
  mat.m[10] = a.z * a.z * ic + c;

  return mat;
}

Matrix4
Matrix4::scale (const Vector3 &v)
{
  Matrix4 mat = identity ();

  mat.m[0] = v.x;
  mat.m[5] = v.y;
  mat.m[10] = v.z;

  return mat;
}

Matrix4
Matrix4::operator* (const Matrix4 &o) const
{
  Matrix4 res;

  for (int c = 0; c < 4; ++c)
    {
      for (int r = 0; r < 4; ++r)
        {
          res.m[c * 4 + r] = m[0 * 4 + r] * o.m[c * 4 + 0]
                             + m[1 * 4 + r] * o.m[c * 4 + 1]
                             + m[2 * 4 + r] * o.m[c * 4 + 2]
                             + m[3 * 4 + r] * o.m[c * 4 + 3];
        }
    }

  return res;
}

Vector3
Matrix4::operator* (const Vector3 &v) const
{
  float x = v.x * m[0] + v.y * m[4] + v.z * m[8] + m[12];
  float y = v.x * m[1] + v.y * m[5] + v.z * m[9] + m[13];
  float z = v.x * m[2] + v.y * m[6] + v.z * m[10] + m[14];

  return Vector3 (x, y, z);
}

Matrix4
Matrix4::lookAt (const Vector3 &eye, const Vector3 &center, const Vector3 &up)
{
  Vector3 f = (center - eye).normalize ();
  Vector3 s = Vector3::cross (f, up).normalize ();
  Vector3 u = Vector3::cross (s, f);

  Matrix4 mat = identity ();

  mat.m[0] = s.x;
  mat.m[4] = s.y;
  mat.m[8] = s.z;
  mat.m[12] = -Vector3::dot (s, eye);

  mat.m[1] = u.x;
  mat.m[5] = u.y;
  mat.m[9] = u.z;
  mat.m[13] = -Vector3::dot (u, eye);

  mat.m[2] = -f.x;
  mat.m[6] = -f.y;
  mat.m[10] = -f.z;
  mat.m[14] = Vector3::dot (f, eye);

  return mat;
}

Matrix4
Matrix4::perspective (float fovDeg, float aspect, float nearZ, float farZ)
{
  Matrix4 mat;

  float tanHalfFov = std::tan (fovDeg * 0.5f * (M_PI / 180.0f));

  mat.m[0] = 1.0f / (aspect * tanHalfFov);
  mat.m[5] = 1.0f / tanHalfFov;
  mat.m[10] = -(farZ + nearZ) / (farZ - nearZ);
  mat.m[11] = -1.0f;
  mat.m[14] = -(2.0f * farZ * nearZ) / (farZ - nearZ);

  return mat;
}

} // namespace core