#include "primitives.hpp"
#include "math.hpp"


namespace {

/*
 * Emits indices for a (vdiv+1) × (udiv+1) vertex grid.
 *   index(row, col) = base + row * stride + col
 *   Winding: CCW from +cross(u, v).
 */
void emit_grid_indices(Mesh &m, std::uint32_t base,
    std::uint32_t stride, int udiv, int vdiv)
{
    for (int j = 0; j < vdiv; ++j)
    {
        const auto r0 = base + static_cast<std::uint32_t>(j) * stride;
        const auto r1 = r0 + stride;

        for (int i = 0; i < udiv; ++i)
        {
            const auto a = r0 + static_cast<std::uint32_t>(i);
            const auto b = a  + 1u;
            const auto c = r1 + static_cast<std::uint32_t>(i);
            const auto d = c  + 1u;

            m.add_triangle(a, b, d);
            m.add_triangle(a, d, c);
        }
    }
}

/*
 * Emits a planar quad patch: P(s,t) = origin + u*s + v*t
 *   Normal = normalize(cross(u, v)), constant.
 *   UV = (s, t).
 */
std::uint32_t emit_patch(Mesh &m, const Vec3 &origin,
    const Vec3 &u, const Vec3 &v, int udiv, int vdiv)
{
    const auto base   = static_cast<std::uint32_t>(m.vertices.size());
    const auto stride = static_cast<std::uint32_t>(udiv + 1);

    const Vec3  n     = normalize(cross(u, v));
    const float inv_u = 1.f / static_cast<float>(udiv);
    const float inv_v = 1.f / static_cast<float>(vdiv);

    for (int j = 0; j <= vdiv; ++j)
    {
        const float tv = static_cast<float>(j) * inv_v;

        for (int i = 0; i <= udiv; ++i)
        {
            const float tu = static_cast<float>(i) * inv_u;
            const Vec3  p  = origin + u * tu + v * tv;

            m.vertices.push_back({ p, n, { tu, tv } });
        }
    }

    emit_grid_indices(m, base, stride, udiv, vdiv);
    return base;
}

} /* namespace */


Mesh make_plane(float length, int div)
{
    Mesh m;
    const auto d = static_cast<std::size_t>(div);
    m.reserve((d+1)*(d+1), d*d*6);

    const float h = length * 0.5f;
    emit_patch(m,
        { -h,      0.f, +h      },  /* origin */
        {  length, 0.f,  0.f    },  /* u -> +X */
        {  0.f,    0.f, -length },  /* v -> -Z => normal = +Y */
        div, div);

    return m;
}

Mesh make_box(float length, int div)
{
    Mesh m;
    const auto d = static_cast<std::size_t>(div);
    m.reserve((d+1)*(d+1)*6, d*d*6*6);

    const float h = length * 0.5f;
    const Vec3 X  {  length, 0.f,    0.f    };
    const Vec3 Y  {  0.f,    length, 0.f    };
    const Vec3 Z  {  0.f,    0.f,    length };
    const Vec3 nX { -length, 0.f,    0.f    };
    const Vec3 nZ {  0.f,    0.f,   -length };

    emit_patch(m, { -h, +h, +h }, X,  nZ, div, div);  /* +Y top    */
    emit_patch(m, { -h, -h, -h }, X,  Z,  div, div);  /* -Y bottom */
    emit_patch(m, { -h, -h, +h }, X,  Y,  div, div);  /* +Z front  */
    emit_patch(m, { +h, -h, -h }, nX, Y,  div, div);  /* -Z back   */
    emit_patch(m, { +h, -h, +h }, nZ, Y,  div, div);  /* +X right  */
    emit_patch(m, { -h, -h, -h }, Z,  Y,  div, div);  /* -X left   */

    return m;
}

Mesh make_sphere(float radius, int slices, int stacks)
{
    Mesh m;

    const auto rows = static_cast<std::size_t>(stacks - 1);
    const auto cols = static_cast<std::size_t>(slices + 1);
    m.reserve(2 + rows * cols, 6 * static_cast<std::size_t>(slices) * rows);

    const float dT = 2.f * PI / static_cast<float>(slices);
    const float dP = PI / static_cast<float>(stacks);

    /* top pole */
    const auto i_top = static_cast<std::uint32_t>(m.vertices.size());
    m.vertices.push_back(
        {{ 0.f, radius, 0.f }, { 0.f, 1.f, 0.f }, { 0.5f, 0.f }});

    /* rings k=1..stacks-1 */
    const auto ring_base = static_cast<std::uint32_t>(m.vertices.size());

    for (int k = 1; k < stacks; ++k)
    {
        const float phi = static_cast<float>(k) * dP;
        const float cp  = std::cos(phi);
        const float sp  = std::sin(phi);

        for (int s = 0; s <= slices; ++s)
        {
            const float theta = static_cast<float>(s) * dT;
            const float ct    = std::cos(theta);
            const float st    = std::sin(theta);

            const Vec3 n { sp * ct, cp, sp * st };
            const Vec3 p = n * radius;

            const float u = static_cast<float>(s) / static_cast<float>(slices);
            const float v = static_cast<float>(k) / static_cast<float>(stacks);

            m.vertices.push_back({ p, n, { u, v } });
        }
    }

    /* bottom pole */
    const auto i_bot = static_cast<std::uint32_t>(m.vertices.size());
    m.vertices.push_back(
        {{ 0.f, -radius, 0.f }, { 0.f, -1.f, 0.f }, { 0.5f, 1.f }});

    const auto stride = static_cast<std::uint32_t>(slices + 1);

    /* top cap */
    for (int s = 0; s < slices; ++s) {
        const auto a = ring_base + static_cast<std::uint32_t>(s);
        m.add_triangle(i_top, a + 1u, a);
    }

    /* middle bands */
    if (stacks > 2)
        emit_grid_indices(m, ring_base, stride, slices, stacks - 2);

    /* bottom cap */
    const auto last =
        ring_base + static_cast<std::uint32_t>(stacks - 2) * stride;
    for (int s = 0; s < slices; ++s) {
        const auto a = last + static_cast<std::uint32_t>(s);
        m.add_triangle(i_bot, a, a + 1u);
    }

    return m;
}

Mesh make_cone(float radius, float height, int slices, int stacks)
{
    Mesh m;

    const auto sl = static_cast<std::size_t>(slices);
    const auto st = static_cast<std::size_t>(stacks);

    /* side + apex + base ring + center */
    m.reserve((sl+1)*st + 1 + (sl+1) + 1, sl*st*6 + sl*3);

    const float dT    = 2.f * PI / static_cast<float>(slices);
    const float slant = std::sqrt(radius*radius + height*height);
    const float ny    = radius / slant;  /* side normal Y component */
    const float nr    = height / slant;  /* side normal radial component */

    /* side surface */
    /*
     * Grid: row = stack (0 = base, stacks-1 = near apex), col = slice.
     * Matches sphere convention: outer = row (height), inner = col (around).
     */
    const auto side_base = static_cast<std::uint32_t>(m.vertices.size());
    const auto side_stride = static_cast<std::uint32_t>(slices + 1);

    for (int k = 0; k < stacks; ++k)
    {
        const float t = static_cast<float>(k) / static_cast<float>(stacks);
        const float y = t * height;
        const float r = (1.f - t) * radius;
        const float v = t;

        for (int s = 0; s <= slices; ++s)
        {
            const float theta = static_cast<float>(s) * dT;
            const float ct    = std::cos(theta);
            const float st    = std::sin(theta);
            const float u     = static_cast<float>(s) / static_cast<float>(slices);

            const Vec3 pos { r * ct, y, r * st };
            const Vec3 n   { nr * ct, ny, nr * st };

            m.vertices.push_back({ pos, n, { u, v } });
        }
    }

    if (stacks > 1)
        emit_grid_indices(m, side_base, side_stride, slices, stacks - 1);

    /* apex: connect last row to tip */
    const auto apex = static_cast<std::uint32_t>(m.vertices.size());
    m.vertices.push_back({{ 0.f, height, 0.f }, { 0.f, 1.f, 0.f }, { 0.5f, 1.f }});

    const auto top_row =
        side_base + static_cast<std::uint32_t>(stacks - 1) * side_stride;
    for (int s = 0; s < slices; ++s) {
        const auto a = top_row + static_cast<std::uint32_t>(s);
        m.add_triangle(a, a + 1u, apex);
    }

    /* base disk */
    const Vec3 base_n { 0.f, -1.f, 0.f };
    const auto base_ring = static_cast<std::uint32_t>(m.vertices.size());

    for (int s = 0; s <= slices; ++s) {
        const float theta = static_cast<float>(s) * dT;
        const float ct    = std::cos(theta);
        const float st    = std::sin(theta);
        const float u     = static_cast<float>(s) / static_cast<float>(slices);

        m.vertices.push_back({{ radius * ct, 0.f, radius * st }, base_n, { u, 0.f }});
    }

    const auto center = static_cast<std::uint32_t>(m.vertices.size());
    m.vertices.push_back({{ 0.f, 0.f, 0.f }, base_n, { 0.5f, 0.5f }});

    for (int s = 0; s < slices; ++s) {
        const auto a = base_ring + static_cast<std::uint32_t>(s);
        m.add_triangle(center, a, a + 1u);
    }

    return m;
}