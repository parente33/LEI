#ifndef CG_CORE_VECTOR_H
#define CG_CORE_VECTOR_H

#include <cmath>

namespace core
{

class Vector2
{
public:
  float x = 0.0f;
  float y = 0.0f;

  Vector2 () = default;
  Vector2 (float x, float y) : x (x), y (y) {}
};

class Vector3
{
public:
  float x = 0.0f;
  float y = 0.0f;
  float z = 0.0f;

  Vector3 () = default;
  Vector3 (float x, float y, float z) : x (x), y (y), z (z) {}

  Vector3
  operator+ (const Vector3 &o) const
  {
    return { x + o.x, y + o.y, z + o.z };
  }

  Vector3
  operator- (const Vector3 &o) const
  {
    return { x - o.x, y - o.y, z - o.z };
  }

  Vector3
  operator* (float s) const
  {
    return { x * s, y * s, z * s };
  }

  Vector3
  operator/ (float s) const
  {
    return { x / s, y / s, z / s };
  }

  Vector3 &
  operator+= (const Vector3 &o)
  {
    x += o.x;
    y += o.y;
    z += o.z;

    return *this;
  }

  Vector3 &
  operator-= (const Vector3 &o)
  {
    x -= o.x;
    y -= o.y;
    z -= o.z;

    return *this;
  }

  float
  length () const
  {
    return std::sqrt (x * x + y * y + z * z);
  }

  Vector3
  normalize () const
  {
    float l = length ();
    return (l > 0.0f) ? (*this / l) : *this;
  }

  static float
  dot (const Vector3 &a, const Vector3 &b)
  {
    return a.x * b.x + a.y * b.y + a.z * b.z;
  }

  static Vector3
  cross (const Vector3 &a, const Vector3 &b)
  {
    return { a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z,
             a.x * b.y - a.y * b.x };
  }
};

inline Vector3
operator* (float s, const Vector3 &v)
{
  return v * s;
}

} // namespace core

#endif // CG_CORE_VECTOR_H