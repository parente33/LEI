#ifndef CG_ENGINE_APPLICATION_H
#define CG_ENGINE_APPLICATION_H

#include "camera/Camera.h"
#include "renderer/Renderer.h"
#include "renderer/ResourceManager.h"
#include "renderer/ShaderProgram.h"
#include "scene/Scene.h"

#include <cstdio>
#include <filesystem>
#include <memory>
#include <string>

struct GLFWwindow;

namespace engine
{

class Application
{
public:
  Application  ();
  ~Application ();

  int init (const std::filesystem::path &xmlPath);
  int run ();

private:
  void initGL ();
  void resize (int w, int h);
  void draw ();
  void handleKey (int key, int action);

  static void cbKey (GLFWwindow *win, int key, int scancode, int action,
                     int mods);

  static void cbResize (GLFWwindow *win, int w, int h);

  GLFWwindow *_win = nullptr;
  int _width  = 1;
  int _height = 1;

  Scene _scene;

  std::unique_ptr<ResourceManager> _resourceManager;
  std::unique_ptr<ShaderProgram> _shader;
  std::unique_ptr<Renderer> _renderer;
  std::unique_ptr<Camera> _camera;

  bool _wireframe  = false;
  bool _showCurves = false;
};

} // namespace engine

#endif // CG_ENGINE_APPLICATION_H