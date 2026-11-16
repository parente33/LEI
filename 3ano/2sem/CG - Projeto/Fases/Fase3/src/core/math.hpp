#pragma once

#include <cmath>


constexpr float PI = 3.14159265358979323846f;

struct Vec3
{
    float x, y, z;
};

inline Vec3 add(const Vec3 &a, const Vec3 &b)
{
    return { a.x + b.x, a.y + b.y, a.z + b.z };
}

inline Vec3 mul(const Vec3 &v, float s)
{
    return { v.x * s, v.y * s, v.z * s };
}