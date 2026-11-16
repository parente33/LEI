#include "renderer_im.hpp"

#include "resource_cache.hpp"
#include "mesh.hpp"

#include <GL/gl.h>
#include <GL/glu.h>


namespace {

void draw_axes(float size)
{
    glLineWidth(2.0f);
    glBegin(GL_LINES);

    glColor3f(1.f, 0.f, 0.f);
    glVertex3f(-size, 0.f, 0.f);
    glVertex3f( size, 0.f, 0.f);

    glColor3f(0.f, 1.f, 0.f);
    glVertex3f(0.f, -size, 0.f);
    glVertex3f(0.f,  size, 0.f);

    glColor3f(0.f, 0.f, 1.f);
    glVertex3f(0.f, 0.f, -size);
    glVertex3f(0.f, 0.f,  size);

    glEnd();
    glColor3f(1.f, 1.f, 1.f);
}

void apply_transforms(const std::vector<Transform> &transforms)
{
    for (const Transform &t : transforms) {
        switch (t.type) {
            case TransformType::Translate:
                glTranslatef(t.translate.x, t.translate.y, t.translate.z);
                break;
            case TransformType::Rotate:
                glRotatef(t.rotate.angle, t.rotate.x, t.rotate.y, t.rotate.z);
                break;
            case TransformType::Scale:
                glScalef(t.scale.x, t.scale.y, t.scale.z);
                break;
        }
    }
}

} /* namespace */


bool RendererIM::init()
{
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LESS);

    glClearColor(0.1f, 0.1f, 0.1f, 1.f);

    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
    glFrontFace(GL_CCW);

    glPolygonMode(GL_FRONT, GL_LINE);
    glLineWidth(1.f);

    return true;
}

void RendererIM::resize(int w, int h)
{
    w_ = (w > 0) ? w : 1;
    h_ = (h > 0) ? h : 1;
    glViewport(0, 0, w_, h_);
}

void RendererIM::apply_camera(const Camera &cam)
{
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(cam.fovDeg,
        static_cast<float>(w_) / static_cast<float>(h_),
        cam.nearZ, cam.farZ);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    gluLookAt(cam.pos.x, cam.pos.y, cam.pos.z,
        cam.lookAt.x, cam.lookAt.y, cam.lookAt.z,
        cam.up.x, cam.up.y, cam.up.z);
}

void RendererIM::draw_mesh(const Mesh &m)
{
    glBegin(GL_TRIANGLES);

    for (std::uint32_t idx : m.indices) {
        const Vec3 &p = m.vertices[idx].position;
        glVertex3f(p.x, p.y, p.z);
    }

    glEnd();
}

void RendererIM::draw_node(const World &w, const ResourceCache &rc, NodeId id)
{
    const Node &n = world_node(w, id);

    glPushMatrix();

    apply_transforms(n.transforms);

    for (const Model &m : n.models) {
        if (const Mesh *mesh = rc.mesh(m.mesh)) draw_mesh(*mesh);
    }

    for (NodeId c : n.children)
        draw_node(w, rc, c);

    glPopMatrix();
}

void RendererIM::render(const RenderFrame &frame)
{
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    apply_camera(frame.world.camera);
    glColor3f(1.f, 1.f, 1.f);

    if (frame.world.root != kInvalidNodeId)
        draw_node(frame.world, frame.resources, frame.world.root);

    draw_axes(100);
}