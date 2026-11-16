#include "BezierPatch.h"

namespace generator
{

BezierPatch::BezierPatch (const core::Vector3 points[16])
{
    for (int i = 0; i < 4; ++i)
      {
        for (int j = 0; j < 4; ++j)
          {
            control_points[i][j] = points[i * 4 + j];
          }
      }
}

core::Vector3
BezierPatch::evaluate (float u, float v, BezierEvaluationMode mode) const
{
    if (mode == BezierEvaluationMode::Bernstein)
      {
        return evaluateBernstein(u, v);
      }

    return evaluateDeCasteljau(u, v);
}

core::Vector3
BezierPatch::evaluateNormal (float u, float v, BezierEvaluationMode mode) const
{
    if (mode == BezierEvaluationMode::Bernstein)
      {
        return evaluateNormalBernstein(u, v);
      }

    return evaluateNormalDeCasteljau(u, v);
}

// --- Bernstein Implementation ---

std::array<float, 4>
BezierPatch::bernstein3 (float t)
{
    const float one_minus_t = 1.0f - t;
    return {
        one_minus_t * one_minus_t * one_minus_t,
        one_minus_t * one_minus_t * t * 3.0f,
        one_minus_t * t * t * 3.0f,
        t * t * t
    };
}

std::array<float, 3>
BezierPatch::bernstein2 (float t)
{
    const float one_minus_t = 1.0f - t;
    return {
        one_minus_t * one_minus_t,
        one_minus_t * t * 2.0f,
        t * t
    };
}

core::Vector3
BezierPatch::evaluateBernstein (float u, float v) const
{
    core::Vector3 result(0.0f, 0.0f, 0.0f);

    auto bu = bernstein3(u);
    auto bv = bernstein3(v);

    for (int i = 0; i < 4; ++i)
      {
        for (int j = 0; j < 4; ++j)
          {
            float weight = bu[i] * bv[j];
            result += control_points[i][j] * weight;
          }
      }

    return result;
}

core::Vector3
BezierPatch::evaluateDerivativeU (float u, float v) const
{
    core::Vector3 result(0.0f, 0.0f, 0.0f);

    auto bv_deg3 = bernstein3(v);
    auto bu_deg2 = bernstein2(u);

    for (int j = 0; j < 4; ++j)
      {
        for (int i = 0; i < 3; ++i)
          {
            core::Vector3 diff = control_points[i + 1][j] - control_points[i][j];
            float weight = bu_deg2[i] * bv_deg3[j];
            result += diff * weight;
          }
      }

    return result * 3.0f;
}

core::Vector3
BezierPatch::evaluateDerivativeV (float u, float v) const
{
    core::Vector3 result(0.0f, 0.0f, 0.0f);

    auto bu_deg3 = bernstein3(u);
    auto bv_deg2 = bernstein2(v);

    for (int i = 0; i < 4; ++i)
      {
        for (int j = 0; j < 3; ++j)
          {
            core::Vector3 diff = control_points[i][j + 1] - control_points[i][j];
            float weight = bu_deg3[i] * bv_deg2[j];
            result += diff * weight;
          }
      }

    return result * 3.0f;
}

core::Vector3
BezierPatch::evaluateNormalBernstein (float u, float v) const
{
    core::Vector3 du = evaluateDerivativeU(u, v);
    core::Vector3 dv = evaluateDerivativeV(u, v);

    return core::Vector3::cross(dv, du).normalize();
}

// --- De Casteljau Implementation ---

core::Vector3
BezierPatch::deCasteljau1D (const core::Vector3 *points, int numPoints, float t)
{
    core::Vector3 tmp[4];
    for (int i = 0; i < numPoints; ++i)
      {
        tmp[i] = points[i];
      }

    for (int k = 1; k < numPoints; ++k)
      {
        for (int i = 0; i < numPoints - k; ++i)
          {
            tmp[i] = tmp[i] * (1.0f - t) + tmp[i + 1] * t;
          }
      }

    return tmp[0];
}

core::Vector3
BezierPatch::evaluateDeCasteljau (float u, float v) const
{
    core::Vector3 pV[4];
    for (int i = 0; i < 4; ++i)
      {
        pV[i] = deCasteljau1D(control_points[i].data(), 4, v);
      }

    return deCasteljau1D(pV, 4, u);
}

core::Vector3
BezierPatch::evaluateNormalDeCasteljau (float u, float v) const
{
    core::Vector3 dU[4];
    for (int j = 0; j < 4; ++j)
      {
        core::Vector3 derivU[3];
        for (int i = 0; i < 3; ++i)
          {
            derivU[i] = (control_points[i + 1][j] - control_points[i][j]) * 3.0f;
          }

        dU[j] = deCasteljau1D(derivU, 3, u);
      }

    core::Vector3 du = deCasteljau1D(dU, 4, v);

    core::Vector3 dV[4];
    for (int i = 0; i < 4; ++i)
      {
        core::Vector3 derivV[3];
        for (int j = 0; j < 3; ++j)
          {
            derivV[j] = (control_points[i][j + 1] - control_points[i][j]) * 3.0f;
          }

        dV[i] = deCasteljau1D(derivV, 3, v);
      }

    core::Vector3 dv = deCasteljau1D(dV, 4, u);

    return core::Vector3::cross(dv, du).normalize();
}

} // namespace generator
