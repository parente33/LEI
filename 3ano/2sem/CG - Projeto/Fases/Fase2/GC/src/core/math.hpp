#pragma once

#include <cmath>


constexpr float PI = 3.14159265358979323846f;

struct Vec2
{
    float u, v;
};

struct Vec3
{
    float x, y, z;

    Vec3 operator+(const Vec3 &o) const { return { x + o.x, y + o.y, z + o.z }; }
    Vec3 operator-(const Vec3 &o) const { return { x - o.x, y - o.y, z - o.z }; }
    Vec3 operator*(float s) const { return { x * s, y * s, z * s }; }
    Vec3 operator/(float s) const { return { x / s, y / s, z / s }; }
};

inline Vec3  operator*(float s, const Vec3 &v) { return v * s; }
inline float dot(const Vec3 &a, const Vec3 &b) { return a.x*b.x + a.y*b.y + a.z*b.z; }
inline float length(const Vec3 &v) { return std::sqrt(dot(v, v)); }

inline Vec3 cross(const Vec3 &a, const Vec3 &b)
{ return { a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x }; }

inline Vec3 normalize(const Vec3 &v)
{
    float len = length(v);
    return (len > 0.f) ? v / len : Vec3{0.f, 0.f, 0.f};
}