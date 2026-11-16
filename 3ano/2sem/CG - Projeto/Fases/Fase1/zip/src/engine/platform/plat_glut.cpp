#include "plat_glut.hpp"

#include <cstdlib>
#include <GL/glut.h>


static State  *g_state = nullptr;
static int     g_lastMs = 0;

static void cb_display(void)
{
    if (!g_state) return;

    const int now  = glutGet(GLUT_ELAPSED_TIME);
    const int prev = g_lastMs;
    g_lastMs = now;

    const float t  = now * 0.001f;
    const float dt = (prev == 0) ? 0.f : (now - prev) * 0.001f;

    g_state->draw(dt, t);

    glutSwapBuffers();
}

static void cb_idle(void)
{
    glutPostRedisplay();
}

static void cb_reshape(int w, int h)
{
    if (g_state) g_state->resize(w, h);
}

int GlutPlatform::run(int &argc, char *argv[], const GlutConfig &cfg, State &state)
{
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA | GLUT_DEPTH);
    glutInitWindowSize(cfg.width, cfg.height);
    glutCreateWindow(cfg.title);

    g_state = &state;
    g_lastMs = 0;

    if (!g_state->init_after_gl()) return 1;
    g_state->resize(cfg.width, cfg.height);

    glutDisplayFunc(cb_display);
    glutIdleFunc(cb_idle);
    glutReshapeFunc(cb_reshape);

    glutMainLoop();

    return 0;
}