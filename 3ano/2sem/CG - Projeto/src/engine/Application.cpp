#include "Application.h"
#include "SceneParser.h"

#include <iostream>

#define GL_GLEXT_PROTOTYPES
#include <GL/gl.h>
#include <GLFW/glfw3.h>

namespace engine
{

Application::Application () = default;

Application::~Application ()
{
  _renderer.reset ();
  _shader.reset ();
  _resourceManager.reset ();

  if (_win)
    {
      glfwDestroyWindow (_win);
    }

  glfwTerminate ();
}

int
Application::init (const std::filesystem::path &xmlPath)
{
  if (parse_scene_xml (xmlPath, _scene) != 0)
    {
      std::cerr << "parse_scene_xml failed\n";
      return 1;
    }

  if (!glfwInit ())
    {
      std::cerr << "glfwInit failed\n";
      return 1;
    }

  glfwWindowHint (GLFW_CONTEXT_VERSION_MAJOR, 3);
  glfwWindowHint (GLFW_CONTEXT_VERSION_MINOR, 3);
  glfwWindowHint (GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

  _width  = _scene.window.width;
  _height = _scene.window.height;

  _win = glfwCreateWindow (_width, _height, "Engine", nullptr, nullptr);
  if (!_win)
    {
      std::cerr << "glfwCreateWindow failed\n";
      return 1;
    }

  glfwMakeContextCurrent   (_win);
  glfwSetWindowUserPointer (_win, this);

  glfwSetKeyCallback             (_win, cbKey);
  glfwSetFramebufferSizeCallback (_win, cbResize);

  initGL ();
  resize (_width, _height);

  _camera = std::make_unique<OrbitalCamera> (
      OrbitalCamera::fromConfig (_scene.camera));

  return 0;
}

void
Application::initGL ()
{
  _resourceManager = std::make_unique<ResourceManager> ();
  _shader = std::make_unique<ShaderProgram> ("src/engine/shaders/basic.vert",
                                             "src/engine/shaders/basic.frag");

  _renderer = std::make_unique<Renderer> (*_resourceManager, *_shader);
  _renderer->init ();
}

void
Application::resize (int w, int h)
{
  _width  = (w > 0) ? w : 1;
  _height = (h > 0) ? h : 1;

  glViewport (0, 0, _width, _height);
}

void
Application::draw ()
{
  float time = static_cast<float> (glfwGetTime ());

  _scene.update (time);
  _renderer->drawScene (_scene, *_camera, time, _showCurves);
}

int
Application::run ()
{
  while (!glfwWindowShouldClose (_win))
    {
      draw ();
      glfwSwapBuffers (_win);
      glfwPollEvents ();
    }

  return 0;
}

void
Application::handleKey (int key, int action)
{
  if (action != GLFW_PRESS && action != GLFW_REPEAT)
    {
      return;
    }

  if (key == GLFW_KEY_ESCAPE)
    {
      glfwSetWindowShouldClose (_win, GLFW_TRUE);
      return;
    }

  if (OrbitalCamera *expCam = dynamic_cast<OrbitalCamera *> (_camera.get ()))
    {
      switch (key)
        {
        case GLFW_KEY_A:
        case GLFW_KEY_LEFT:
          expCam->handleMove (-5.0f, 0.0f);
          break;

        case GLFW_KEY_D:
        case GLFW_KEY_RIGHT:
          expCam->handleMove (5.0f, 0.0f);
          break;

        case GLFW_KEY_W:
        case GLFW_KEY_UP:
          expCam->handleMove (0.0f, 5.0f);
          break;

        case GLFW_KEY_S:
        case GLFW_KEY_DOWN:
          expCam->handleMove (0.0f, -5.0f);
          break;

        case GLFW_KEY_KP_ADD:
          expCam->handleZoom (1.0f);
          break;

        case GLFW_KEY_KP_SUBTRACT:
          expCam->handleZoom (-1.0f);
          break;
        }
    }

  /* handle render modes */
  if (action == GLFW_PRESS)
    {
      if (key == GLFW_KEY_M)
        {
          _wireframe = !_wireframe;
          glPolygonMode (GL_FRONT_AND_BACK, _wireframe ? GL_LINE : GL_FILL);
        }
      else if (key == GLFW_KEY_L)
        {
          if (_renderer)
            {
              _renderer->toggleLighting ();
            }
        }
      else if (key == GLFW_KEY_C)
        {
          _showCurves = !_showCurves;
        }
      else if (key == GLFW_KEY_F)
        {
          if (_renderer)
            {
              _renderer->toggleFrustumCulling ();
            }
        }
    }
}

void
Application::cbKey (GLFWwindow *win, int key, int scancode, int action,
                    int mods)
{
  Application *app
      = static_cast<Application *> (glfwGetWindowUserPointer (win));
  if (app)
    {
      app->handleKey (key, action);
    }
}

void
Application::cbResize (GLFWwindow *win, int w, int h)
{
  Application *app
      = static_cast<Application *> (glfwGetWindowUserPointer (win));
  if (app)
    {
      app->resize (w, h);
    }
}

} // namespace engine