#ifndef CG_ENGINE_SCENE_SCENE_NODE_H
#define CG_ENGINE_SCENE_SCENE_NODE_H

#include "Transform.h"
#include "core/Matrix.h"

#include <memory>
#include <string>
#include <vector>

namespace engine
{

struct ColorConfig
{
  core::Vector3 diffuse  { 200.f / 255.f, 200.f / 255.f, 200.f / 255.f };
  core::Vector3 ambient  { 50.f  / 255.f, 50.f  / 255.f, 50.f  / 255.f };
  core::Vector3 specular { 0.f, 0.f, 0.f };
  core::Vector3 emissive { 0.f, 0.f, 0.f };

  float shininess = 0.f;
};

class SceneNode
{
public:
  virtual ~SceneNode () = default;

  virtual core::Matrix4 getLocalTransform (float time) const
  {
    return core::Matrix4::identity ();
  }

  virtual void
  update (float dt)
  {
  }

  core::Vector3 worldCenter { 0.f, 0.f, 0.f };
  float         worldRadius = 0.f;
};

class GroupNode : public SceneNode
{
public:
  void addChild (std::unique_ptr<SceneNode> child);
  const std::vector<std::unique_ptr<SceneNode>> &getChildren () const;

  void addTransform (std::unique_ptr<Transform> transform);
  core::Matrix4 getLocalTransform (float time) const override;

  const std::vector<std::unique_ptr<Transform>> &
  getTransforms () const
  {
    return _transforms;
  }

  void update (float dt) override;

private:
  std::vector<std::unique_ptr<SceneNode>> _children;
  std::vector<std::unique_ptr<Transform>> _transforms;
};

class ModelNode : public SceneNode
{
public:
  ModelNode (const std::string &modelPath);
  const std::string &getModelPath () const;

  void setTexturePath (const std::string &path);
  const std::string &getTexturePath () const;

  void setColor (const ColorConfig &color);
  const ColorConfig &getColor () const;

private:
  std::string _modelPath;
  std::string _texturePath;
  ColorConfig _color;
};

} // namespace engine

#endif // CG_ENGINE_SCENE_SCENE_NODE_H