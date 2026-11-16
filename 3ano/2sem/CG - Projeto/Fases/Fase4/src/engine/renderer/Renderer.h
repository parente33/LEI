#ifndef CG_ENGINE_RENDERER_RENDERER_H
#define CG_ENGINE_RENDERER_RENDERER_H

#include "ResourceManager.h"
#include "ShaderProgram.h"
#include "camera/Camera.h"
#include "camera/Frustum.h"
#include "core/Matrix.h"
#include "scene/Scene.h"

namespace engine
{

class Renderer
{
public:
  Renderer  (ResourceManager &resourceManager, ShaderProgram &shader);
  ~Renderer ();

  void init ();
  void drawScene (const Scene &scene, const Camera &camera, float time,
                  bool showCurves);
  void
  toggleLighting ()
  {
    _useLighting = !_useLighting;
  }

  void
  toggleFrustumCulling ()
  {
    _useFrustumCulling = !_useFrustumCulling;
  }

private:
  void updateBoundingVolumes (SceneNode *node, const core::Matrix4 &parentMatrix, float time);
  void drawNode (const SceneNode *node, const core::Matrix4 &parentMatrix,
                 float time, bool showCurves);

  ResourceManager &_resourceManager;
  ShaderProgram   &_shader;

  Frustum _frustum;

  bool _useLighting       = true;
  bool _useFrustumCulling = true;

  unsigned int _debugVao = 0;
  unsigned int _debugVbo = 0;
  void drawDebugCurve (const std::vector<core::Vector3> &points, const core::Matrix4 &matrix);
};

} // namespace engine

#endif // CG_ENGINE_RENDERER_RENDERER_H