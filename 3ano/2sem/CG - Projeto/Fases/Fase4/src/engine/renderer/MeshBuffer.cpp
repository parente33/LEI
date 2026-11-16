#include "MeshBuffer.h"

namespace engine
{

MeshBuffer::~MeshBuffer ()
{
  if (vao)
    {
      glDeleteVertexArrays (1, &vao);
    }

  if (vbo)
    {
      glDeleteBuffers (1, &vbo);
    }

  if (ebo)
    {
      glDeleteBuffers (1, &ebo);
    }
}

MeshBuffer::MeshBuffer (MeshBuffer &&other) noexcept
{
  vao = other.vao;
  vbo = other.vbo;
  ebo = other.ebo;
  indexCount = other.indexCount;
  boundingSphereRadius = other.boundingSphereRadius;
  aabbMin = other.aabbMin;
  aabbMax = other.aabbMax;

  other.vao = 0;
  other.vbo = 0;
  other.ebo = 0;
  other.indexCount = 0;
  other.boundingSphereRadius = 0.0f;
  other.aabbMin = { 0.0f, 0.0f, 0.0f };
  other.aabbMax = { 0.0f, 0.0f, 0.0f };
}

MeshBuffer &
MeshBuffer::operator= (MeshBuffer &&other) noexcept
{
  if (this != &other)
    {
      if (vao)
        {
          glDeleteVertexArrays (1, &vao);
        }

      if (vbo)
        {
          glDeleteBuffers (1, &vbo);
        }

      if (ebo)
        {
          glDeleteBuffers (1, &ebo);
        }

      vao = other.vao;
      vbo = other.vbo;
      ebo = other.ebo;
      indexCount = other.indexCount;
      boundingSphereRadius = other.boundingSphereRadius;
      aabbMin = other.aabbMin;
      aabbMax = other.aabbMax;

      other.vao = 0;
      other.vbo = 0;
      other.ebo = 0;
      other.indexCount = 0;
      other.boundingSphereRadius = 0.0f;
      other.aabbMin = { 0.0f, 0.0f, 0.0f };
      other.aabbMax = { 0.0f, 0.0f, 0.0f };
    }

  return *this;
}

void
MeshBuffer::bind () const
{
  glBindVertexArray (vao);
}

void
MeshBuffer::unbind () const
{
  glBindVertexArray (0);
}

} // namespace engine