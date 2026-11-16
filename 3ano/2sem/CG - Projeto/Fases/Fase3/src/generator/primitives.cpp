#include "primitives.hpp"

#include <algorithm>

#include "math.hpp"


namespace {

    /*
     * Emits indices for an existing (vdiv+1) x (udiv+1) vertex grid.
     * Layout must be:
     *      index(row, col) = base + row * stride + col
     * Winding:
     *      CCW when viewed from the side where cross(u, v) points.
     * */
    inline void emit_grid_indices(Mesh &m, std::uint32_t base,
        std::uint32_t stride, int udiv, int vdiv)
    {
        for (int j = 0; j < vdiv; ++j)
        {
            const std::uint32_t row0 = base + std::uint32_t(j) * stride;
            const std::uint32_t row1 = row0 + stride;

            for (int i = 0; i < udiv; ++i)
            {
                const std::uint32_t i0 = row0 + std::uint32_t(i);  /* (j,   i)   */
                const std::uint32_t i1 = i0 + 1u;                  /* (j,   i+1) */
                const std::uint32_t i2 = row1 + std::uint32_t(i);  /* (j+1, i)   */
                const std::uint32_t i3 = i2 + 1u;                  /* (j+1, i+1) */

                m.add_triangle(i0, i1, i3);
                m.add_triangle(i0, i3, i2);
            }
        }
    }

    /*
     * Emits a linear patch:
     *      P(s,t) = origin + u*s + v*t
     * where s,t in [0,1], subdivided into udiv x vdiv quads.
     *
     * Appends vertices and indices, returns base vertex index of the patch.
     * Winding matches emit_grid_indices() (CCW from +cross(u,v)).
     * */
    inline std::uint32_t emit_linear_patch(Mesh &m, const Vec3 &origin,
        const Vec3 &u, const Vec3 &v, int udiv, int vdiv)
    {
        const std::uint32_t base = static_cast<std::uint32_t>(m.vertices.size());
        const std::uint32_t stride = static_cast<std::uint32_t>(udiv + 1);

        const int rows = vdiv + 1;
        const int cols = udiv + 1;

        m.vertices.resize(m.vertices.size() + std::size_t(rows) * std::size_t(cols));

        const float inv_u = 1.0f / float(udiv);
        const float inv_v = 1.0f / float(vdiv);

        for (int j = 0; j <= vdiv; ++j)
        {
            const float tv = float(j) * inv_v;
            const Vec3 row_o = add(origin, mul(v, tv));

            for (int i = 0; i <= udiv; ++i)
            {
                const float tu = float(i) * inv_u;
                const Vec3 p = add(row_o, mul(u, tu));

                const std::size_t k =
                    std::size_t(base) + std::size_t(j) * std::size_t(stride) + std::size_t(i);

                m.vertices[k].position = p;
            }
        }

        emit_grid_indices(m, base, stride, udiv, vdiv);
        return base;
    }

} /* namespace */


Mesh make_plane(float length, int divisions)
{
    Mesh m;

    const std::size_t d = static_cast<std::size_t>(divisions);
    const std::size_t vtx_count = (d + 1u) * (d + 1u);
    const std::size_t idx_count = d * d * 6u;  /* 2 tris per cell => 6 indices */
    m.reserve(vtx_count, idx_count);

    const float h = length * 0.5f;

    /* Plane in XZ at Y=0, centered at origin.
     * Choose (u, v) so cross(u, v) points +Y => front face from above.
     * */
    const Vec3 origin { -h, 0.0f, +h };
    const Vec3 u { length, 0.0f, 0.0f };   /* +X */
    const Vec3 v { 0.0f, 0.0f, -length };  /* -Z => cross(u,v)=+Y */

    emit_linear_patch(m, origin, u, v, divisions, divisions);
    return m;
}

Mesh make_box(float length, int divisions)
{
    Mesh m;

    const std::size_t d = static_cast<std::size_t>(divisions);
    const std::size_t face_vtx = (d + 1u) * (d + 1u);
    const std::size_t face_idx = d * d * 6u;

    m.reserve(face_vtx * 6u, face_idx * 6u);

    const float h = length * 0.5f;

    const Vec3 X { length, 0.0f, 0.0f };
    const Vec3 Y { 0.0f, length, 0.0f };
    const Vec3 Z { 0.0f, 0.0f, length };
    const Vec3 nX { -length, 0.0f, 0.0f };
    const Vec3 nZ {  0.0f, 0.0f, -length };

    /* +Y (top): outward +Y */
    emit_linear_patch(m, Vec3 { -h, +h, +h }, X,  nZ, divisions, divisions);

    /* -Y (bottom): outward -Y */
    emit_linear_patch(m, Vec3 { -h, -h, -h }, X,  Z,  divisions, divisions);

    /* +Z (front): outward +Z */
    emit_linear_patch(m, Vec3 { -h, -h, +h }, X,  Y,  divisions, divisions);

    /* -Z (back): outward -Z */
    emit_linear_patch(m, Vec3 { +h, -h, -h }, nX, Y,  divisions, divisions);

    /* +X (right): outward +X */
    emit_linear_patch(m, Vec3 { +h, -h, +h }, nZ, Y,  divisions, divisions);

    /* -X (left): outward -X */
    emit_linear_patch(m, Vec3 { -h, -h, -h }, Z,  Y,  divisions, divisions);

    return m;
}

Mesh make_sphere(float radius, int slices, int stacks)
{
    Mesh m;

    const std::size_t ring_rows  = std::size_t(stacks - 1);
    const std::size_t ring_cols  = std::size_t(slices + 1);
    const std::size_t vtx_count  = 2u + ring_rows * ring_cols;
    const std::size_t idx_count  =
        6u * std::size_t(slices) * std::size_t(stacks - 1);

    m.reserve(vtx_count, idx_count);

    const float dTheta = 2.0f * PI / float(slices);
    const float dPhi   = PI / float(stacks);

    auto point = [&](float phi, float theta) -> Vec3 {
        const float y = radius * std::cos(phi);
        const float r = radius * std::sin(phi);
        return { r * std::cos(theta), y, r * std::sin(theta) };
    };

    const std::uint32_t i_top = (std::uint32_t)m.vertices.size();
    m.vertices.push_back(Vertex { Vec3 { 0.0f, +radius, 0.0f } });

    const std::uint32_t ring_base = (std::uint32_t)m.vertices.size();

    /* rings: k = 1..stacks-1, each has slices+1 vertices */
    for (int k = 1; k <= stacks - 1; ++k)
    {
        const float phi = float(k) * dPhi;
        for (int s = 0; s <= slices; ++s)
        {
            const float theta = float(s) * dTheta;
            m.vertices.push_back(Vertex { point(phi, theta) });
        }
    }

    const std::uint32_t i_bot = (std::uint32_t)m.vertices.size();
    m.vertices.push_back(Vertex { Vec3 { 0.0f, -radius, 0.0f } });

    const std::uint32_t stride = (std::uint32_t)(slices + 1);

    /* top cap: triangles are CCW when viewed from outside (+Y at top)*/
    for (int s = 0; s < slices; ++s)
    {
        const std::uint32_t p0 = ring_base + (std::uint32_t)s;
        const std::uint32_t p1 = p0 + 1u;
        m.add_triangle(i_top, p1, p0);
    }

    /* Middle bands: between rings, reuse the canonical grid indexer */
    if (stacks > 2)
    {
        emit_grid_indices(m, ring_base, stride, slices, stacks - 2);
    }

    /* bottom cap: triangles CCW when viewed from outside (-Y at bottom) */
    const std::uint32_t last_row_base =
        ring_base + (std::uint32_t)(stacks - 2) * stride;
    for (int s = 0; s < slices; ++s)
    {
        const std::uint32_t p0 = last_row_base + (std::uint32_t)s;
        const std::uint32_t p1 = p0 + 1u;
        m.add_triangle(i_bot, p0, p1);
    }

    return m;
}

Mesh make_cone(float radius, float height, int slices, int stacks)
{
    Mesh m;

    const std::size_t sl = (std::size_t)slices;
    const std::size_t st = (std::size_t)stacks;

    const std::size_t vtx_count = (sl + 1u) * st + 1u + (sl + 1u) + 1u;
    const std::size_t idx_count = sl * st * 6u;

    m.reserve(vtx_count, idx_count);

    const float dTheta = 2.0f * PI / float(slices);

    auto ring_point = [&](float r, float y, float theta) -> Vec3 {
        return { r * std::cos(theta), y, r * std::sin(theta) };
    };

    /* side */

    const std::uint32_t side_base = (std::uint32_t)m.vertices.size();
    const std::uint32_t stride = (std::uint32_t)stacks;

    for (int s = 0; s <= slices; ++s)
    {
        const float theta = float(s) * dTheta;

        for (int k = 0; k < stacks; ++k)
        {
            const float t = float(k) / float(stacks);  /* [0 .. (stacks-1)/stacks] */
            const float y = t * height;
            const float r = (1.0f - t) * radius;

            m.vertices.push_back(Vertex{ ring_point(r, y, theta) });
        }
    }

    if (stacks > 1) emit_grid_indices(m, side_base, stride, stacks - 1, slices);

    /* apex */
    const std::uint32_t apex = (std::uint32_t)m.vertices.size();
    m.vertices.push_back(Vertex{ Vec3{ 0.0f, height, 0.0f } });

    /* connect last side column (k = stacks-1) to apex (1 tri per slice) */
    for (int s = 0; s < slices; ++s)
    {
        const std::uint32_t p0 = side_base + (std::uint32_t)s * stride + (stride - 1u);
        const std::uint32_t p1 = p0 + stride; /* next theta row, same top column */
        m.add_triangle(p0, apex, p1);         /* outward */
    }

    /* base */

    const std::uint32_t base_ring = (std::uint32_t)m.vertices.size();

    for (int s = 0; s <= slices; ++s)
    {
        const float theta = float(s) * dTheta;
        m.vertices.push_back(Vertex{ ring_point(radius, 0.0f, theta) });
    }

    const std::uint32_t center = (std::uint32_t)m.vertices.size();
    m.vertices.push_back(Vertex{ Vec3{ 0.0f, 0.0f, 0.0f } });

    for (int s = 0; s < slices; ++s)
    {
        const std::uint32_t p0 = base_ring + (std::uint32_t)s;
        const std::uint32_t p1 = p0 + 1u;
        m.add_triangle(center, p0, p1); /* outward = -Y */
    }

    return m;
}