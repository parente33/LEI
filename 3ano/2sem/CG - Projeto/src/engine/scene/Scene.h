#ifndef CG_ENGINE_SCENE_H
#define CG_ENGINE_SCENE_H

#include "SceneNode.h"
#include "core/Vector.h"

#include <memory>

namespace engine
{

struct LightConfig
{
  enum class Type
  {
    Point,
    Directional,
    Spotlight
  };

  Type type;

  core::Vector3 pos   { 0.f,  0.f, 0.f };
  core::Vector3 dir   { 0.f, -1.f, 0.f };
  core::Vector3 color { 1.f,  1.f, 1.f };

  float cutoff    = 45.f;
  float constant  = 1.0f;
  float linear    = 0.09f;
  float quadratic = 0.032f;
};

struct WindowConfig
{
  int width  = 512;
  int height = 512;
};

struct CameraConfig
{
  core::Vector3 pos    { 3.f, 3.f, 3.f };
  core::Vector3 lookAt { 0.f, 0.f, 0.f };
  core::Vector3 up     { 0.f, 1.f, 0.f };

  float fovDeg = 60.f;
  float nearZ  = 1.f;
  float farZ   = 1000.f;
};

class Scene
{
public:
  Scene ();

  GroupNode *getRoot () const;
  void setRoot (std::unique_ptr<GroupNode> root);

  void update (float dt);

  WindowConfig window;
  CameraConfig camera;

  std::vector<LightConfig> lights;

private:
  std::unique_ptr<GroupNode> _root;
};

} // namespace engine

#endif // CG_ENGINE_SCENE_H