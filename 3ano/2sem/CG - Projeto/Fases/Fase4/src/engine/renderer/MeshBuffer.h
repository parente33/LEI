#ifndef CG_ENGINE_RENDERER_MESH_BUFFER_H
#define CG_ENGINE_RENDERER_MESH_BUFFER_H

#include "core/Vector.h"

#define GL_GLEXT_PROTOTYPES
#include <GL/gl.h>

namespace engine
{

class MeshBuffer
{
public:
  MeshBuffer  () = default;
  ~MeshBuffer ();

  MeshBuffer (const MeshBuffer &) = delete;
  MeshBuffer &operator= (const MeshBuffer &) = delete;

  MeshBuffer (MeshBuffer &&other) noexcept;
  MeshBuffer &operator= (MeshBuffer &&other) noexcept;

  void bind   () const;
  void unbind () const;

  GLuint vao = 0;
  GLuint vbo = 0;
  GLuint ebo = 0;
  GLsizei indexCount = 0;

  float boundingSphereRadius = 0.0f;

  core::Vector3 aabbMin { 0.0f, 0.0f, 0.0f };
  core::Vector3 aabbMax { 0.0f, 0.0f, 0.0f };
};

} // namespace engine

#endif // CG_ENGINE_RENDERER_MESH_BUFFER_H