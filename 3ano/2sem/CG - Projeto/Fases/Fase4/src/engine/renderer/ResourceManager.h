#ifndef CG_ENGINE_RENDERER_RESOURCE_MANAGER_H
#define CG_ENGINE_RENDERER_RESOURCE_MANAGER_H

#include "MeshBuffer.h"
#include "core/Mesh.h"

#define GL_GLEXT_PROTOTYPES
#include <GL/gl.h>

#include <string>
#include <unordered_map>

namespace engine
{

class ResourceManager
{
public:
  ResourceManager  () = default;
  ~ResourceManager ();

  const MeshBuffer *getMesh (const std::string &path);
  GLuint getTexture (const std::string &path);

private:
  MeshBuffer uploadToGPU (const class core::Mesh &m);

  std::unordered_map<std::string, MeshBuffer> _meshCache;
  std::unordered_map<std::string, GLuint>     _textureCache;
};

} // namespace engine

#endif // CG_ENGINE_RENDERER_RESOURCE_MANAGER_H