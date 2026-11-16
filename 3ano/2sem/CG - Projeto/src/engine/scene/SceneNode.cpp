#include "SceneNode.h"

namespace engine
{

void
GroupNode::addTransform (std::unique_ptr<Transform> transform)
{
  _transforms.push_back (std::move (transform));
}

core::Matrix4
GroupNode::getLocalTransform (float time) const
{
  core::Matrix4 mat = core::Matrix4::identity ();
  for (const auto &t : _transforms)
    {
      t->apply (mat, time);
    }

  return mat;
}

void
GroupNode::addChild (std::unique_ptr<SceneNode> child)
{
  _children.push_back (std::move (child));
}

const std::vector<std::unique_ptr<SceneNode>> &
GroupNode::getChildren () const
{
  return _children;
}

void
GroupNode::update (float dt)
{
  for (auto &child : _children)
    {
      child->update (dt);
    }
}

ModelNode::ModelNode (const std::string &modelPath) : _modelPath (modelPath)
{
}

const std::string &
ModelNode::getModelPath () const
{
  return _modelPath;
}

void ModelNode::setTexturePath(const std::string &path) {
  _texturePath = path;
}

const std::string &ModelNode::getTexturePath() const {
  return _texturePath;
}

void ModelNode::setColor(const ColorConfig &color) {
  _color = color;
}

const ColorConfig &ModelNode::getColor() const {
  return _color;
}

} // namespace engine