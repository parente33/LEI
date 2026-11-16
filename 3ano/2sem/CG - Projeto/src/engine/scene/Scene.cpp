#include "Scene.h"

namespace engine
{

Scene::Scene ()
{
  _root = std::make_unique<GroupNode> ();
}

GroupNode *
Scene::getRoot () const
{
  return _root.get ();
}

void
Scene::setRoot (std::unique_ptr<GroupNode> root)
{
  _root = std::move (root);
}

void
Scene::update (float dt)
{
  if (_root)
    {
      _root->update (dt);
    }
}

} // namespace engine