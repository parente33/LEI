#include "Renderer.h"
#include "scene/Transform.h"

#define GL_GLEXT_PROTOTYPES
#include <GL/gl.h>

namespace engine
{

Renderer::Renderer (ResourceManager &resourceManager, ShaderProgram &shader)
    : _resourceManager (resourceManager), _shader (shader)
{
}

Renderer::~Renderer ()
{
  if (_debugVao)
    {
      glDeleteVertexArrays (1, &_debugVao);
    }

  if (_debugVbo)
    {
      glDeleteBuffers (1, &_debugVbo);
    }
}

void
Renderer::init ()
{
  glEnable (GL_DEPTH_TEST);
  glDepthFunc (GL_LESS);

  glClearColor (0.2f, 0.2f, 0.2f, 1.0f);

  glEnable (GL_CULL_FACE);
  glCullFace (GL_BACK);
  glFrontFace (GL_CCW);

  glEnable (GL_BLEND);
  glEnable (GL_RESCALE_NORMAL);

  glPolygonMode (GL_FRONT_AND_BACK, GL_FILL);

  glGenVertexArrays (1, &_debugVao);
  glBindVertexArray (_debugVao);

  glGenBuffers (1, &_debugVbo);
  glBindBuffer (GL_ARRAY_BUFFER, _debugVbo);

  glEnableVertexAttribArray (0);
  glVertexAttribPointer (0, 3, GL_FLOAT, GL_FALSE, sizeof (core::Vector3),
                         (void *)0);

  glBindVertexArray (0);
}

void
Renderer::drawDebugCurve (const std::vector<core::Vector3> &points,
                          const core::Matrix4 &matrix)
{
  if (points.empty () || _debugVao == 0)
    {
      return;
    }

  _shader.setUniform1i   ("u_useLighting", 0);
  _shader.setUniformMat4 ("model", matrix);

  glBindVertexArray (_debugVao);
  glBindBuffer (GL_ARRAY_BUFFER, _debugVbo);

  glBufferData (GL_ARRAY_BUFFER, points.size () * sizeof (core::Vector3),
                points.data (), GL_DYNAMIC_DRAW);

  glDrawArrays (GL_LINE_LOOP, 0, points.size ());

  glBindVertexArray (0);
  _shader.setUniform1i ("u_useLighting", _useLighting ? 1 : 0);
}

void
Renderer::drawScene (const Scene &scene, const Camera &camera, float time,
                     bool showCurves)
{
  glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

  _shader.use ();

  float aspect = static_cast<float> (scene.window.width)
                 / static_cast<float> (
                     scene.window.height > 0 ? scene.window.height : 1);

  core::Matrix4 view       = camera.getViewMatrix ();
  core::Matrix4 projection = camera.getProjectionMatrix (aspect);

  _shader.setUniformMat4 ("view", view);
  _shader.setUniformMat4 ("projection", projection);

  _shader.setUniform1i ("u_useLighting", _useLighting ? 1 : 0);
  _shader.setUniform1i ("u_texture", 0);

  glActiveTexture (GL_TEXTURE0);

  int numLights = scene.lights.size ();
  _shader.setUniform1i ("u_numLights", numLights);
  for (int i = 0; i < numLights; ++i)
    {
      std::string base = "u_lights[" + std::to_string (i) + "].";
      _shader.setUniform1i (base + "type",
                            static_cast<int> (scene.lights[i].type));

      _shader.setUniformVec3 (base + "pos", scene.lights[i].pos);
      _shader.setUniformVec3 (base + "dir", scene.lights[i].dir);
      _shader.setUniformVec3 (base + "color", scene.lights[i].color);

      _shader.setUniform1f (base + "cutoff", scene.lights[i].cutoff);
      _shader.setUniform1f (base + "constant", scene.lights[i].constant);
      _shader.setUniform1f (base + "linear", scene.lights[i].linear);
      _shader.setUniform1f (base + "quadratic", scene.lights[i].quadratic);
    }

  _frustum.update (projection * view);

  const GroupNode *root = scene.getRoot ();
  if (root)
    {
      core::Matrix4 identity = core::Matrix4::identity ();

      // update all bounding volumes hierarchically
      updateBoundingVolumes (const_cast<GroupNode *> (root), identity, time);
      drawNode (root, identity, time, showCurves);
    }

  _shader.unuse ();
}

void
Renderer::updateBoundingVolumes (SceneNode *node,
                                 const core::Matrix4 &parentMatrix, float time)
{
  if (!node)
    {
      return;
    }

  core::Matrix4 localTransform = node->getLocalTransform (time);
  core::Matrix4 worldTransform = parentMatrix * localTransform;

  if (ModelNode *modelNode = dynamic_cast<ModelNode *> (node))
    {
      const MeshBuffer *mesh
          = _resourceManager.getMesh (modelNode->getModelPath ());
      if (mesh)
        {
          modelNode->worldCenter
              = worldTransform * core::Vector3 (0.f, 0.f, 0.f);

          float scaleX
              = std::sqrt (worldTransform.m[0] * worldTransform.m[0]
                           + worldTransform.m[1] * worldTransform.m[1]
                           + worldTransform.m[2] * worldTransform.m[2]);

          modelNode->worldRadius = mesh->boundingSphereRadius * scaleX;
        }
    }
  else if (GroupNode *groupNode = dynamic_cast<GroupNode *> (node))
    {
      float maxDistanceSq = 0.0f;
      core::Vector3 groupCenter
          = worldTransform * core::Vector3 (0.f, 0.f, 0.f);

      for (const auto &child : groupNode->getChildren ())
        {
          updateBoundingVolumes (child.get (), worldTransform, time);

          // expands group's bounding sphere to enclose child's bounding sphere
          float dist = (child->worldCenter - groupCenter).length ();
          float requiredRadius = dist + child->worldRadius;
          if (requiredRadius * requiredRadius > maxDistanceSq)
            {
              maxDistanceSq = requiredRadius * requiredRadius;
            }
        }

      groupNode->worldCenter = groupCenter;
      groupNode->worldRadius = std::sqrt (maxDistanceSq);
    }
}

void
Renderer::drawNode (const SceneNode *node, const core::Matrix4 &parentMatrix,
                    float time, bool showCurves)
{
  if (!node)
    {
      return;
    }

  if (_useFrustumCulling
      && !_frustum.isSphereInside (node->worldCenter, node->worldRadius))
    {
      return;  // cull
    }

  core::Matrix4 localTransform = node->getLocalTransform (time);
  core::Matrix4 worldTransform = parentMatrix * localTransform;

  if (const ModelNode *modelNode = dynamic_cast<const ModelNode *> (node))
    {
      const MeshBuffer *mesh
          = _resourceManager.getMesh (modelNode->getModelPath ());
      if (mesh)
        {
          _shader.setUniformMat4 ("model", worldTransform);

          const ColorConfig &color = modelNode->getColor ();
          _shader.setUniformVec3 ("u_matDiffuse",   color.diffuse);
          _shader.setUniformVec3 ("u_matAmbient",   color.ambient);
          _shader.setUniformVec3 ("u_matSpecular",  color.specular);
          _shader.setUniformVec3 ("u_matEmissive",  color.emissive);
          _shader.setUniform1f   ("u_matShininess", color.shininess);

          GLuint texID
              = _resourceManager.getTexture (modelNode->getTexturePath ());
          if (texID > 0)
            {
              _shader.setUniform1i ("u_hasTexture", 1);
              glBindTexture (GL_TEXTURE_2D, texID);
            }
          else
            {
              _shader.setUniform1i ("u_hasTexture", 0);
            }

          mesh->bind ();
          glDrawElements (GL_TRIANGLES, mesh->indexCount, GL_UNSIGNED_INT,
                          nullptr);
        }
    }
  else if (const GroupNode *groupNode = dynamic_cast<const GroupNode *> (node))
    {
      if (showCurves)
        {
          core::Matrix4 debugMatrix = parentMatrix;
          for (const auto &transform : groupNode->getTransforms ())
            {
              if (const AnimatedTranslate *at
                  = dynamic_cast<const AnimatedTranslate *> (transform.get ()))
                {
                  drawDebugCurve (at->getCurvePath (), debugMatrix);
                }

              transform->apply (debugMatrix, time);
            }
        }

      for (const auto &child : groupNode->getChildren ())
        {
          drawNode (child.get (), worldTransform, time, showCurves);
        }
    }
}

} // namespace engine