#ifndef CG_GENERATOR_PRIMITIVES_BEZIER_PATCH_H
#define CG_GENERATOR_PRIMITIVES_BEZIER_PATCH_H

#include "core/Vector.h"
#include <array>

namespace generator
{

enum class BezierEvaluationMode {
    Bernstein,
    DeCasteljau
};

class BezierPatch
{
public:
    BezierPatch (const core::Vector3 points[16]);

    core::Vector3 evaluate (float u, float v,
                            BezierEvaluationMode mode
                            = BezierEvaluationMode::DeCasteljau) const;

    core::Vector3 evaluateNormal (float u, float v,
                                  BezierEvaluationMode mode
                                  = BezierEvaluationMode::DeCasteljau) const;

  private:
    std::array<std::array<core::Vector3, 4>, 4> control_points;

    // Bernstein implementation
    core::Vector3 evaluateBernstein       (float u, float v) const;
    core::Vector3 evaluateNormalBernstein (float u, float v) const;

    static std::array<float, 4> bernstein3 (float t);
    static std::array<float, 3> bernstein2 (float t);

    core::Vector3 evaluateDerivativeU (float u, float v) const;
    core::Vector3 evaluateDerivativeV (float u, float v) const;

    // De Casteljau implementation
    core::Vector3 evaluateDeCasteljau       (float u, float v) const;
    core::Vector3 evaluateNormalDeCasteljau (float u, float v) const;

    static core::Vector3 deCasteljau1D (const core::Vector3 *points,
                                        int numPoints, float t);
};

} // namespace generator

#endif // CG_GENERATOR_PRIMITIVES_BEZIER_PATCH_H