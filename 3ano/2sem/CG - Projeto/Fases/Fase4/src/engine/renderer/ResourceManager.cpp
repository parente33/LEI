#include "ResourceManager.h"
#include "core/Mesh.h"
#include "core/MeshIO.h"

#include "stb_image.h"

#ifndef GL_TEXTURE_MAX_ANISOTROPY_EXT
#define GL_TEXTURE_MAX_ANISOTROPY_EXT     0x84FE
#define GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT 0x84FF
#endif

#include <algorithm>
#include <filesystem>

namespace engine
{

ResourceManager::~ResourceManager ()
{
  for (auto const &pair : _textureCache)
    {
      if (pair.second)
        {
          glDeleteTextures (1, &pair.second);
        }
    }
}

const MeshBuffer *
ResourceManager::getMesh (const std::string &path)
{
  std::filesystem::path p (path);
  std::string k = p.string ();

  auto it = _meshCache.find (k);
  if (it != _meshCache.end ())
    {
      return &it->second;
    }

  core::Mesh m;
  if (core::read_3d (k, m) != 0)
    {
      return nullptr;
    }

  MeshBuffer buffer = uploadToGPU (m);
  auto       result = _meshCache.emplace (k, std::move (buffer));
  return &result.first->second;
}

MeshBuffer
ResourceManager::uploadToGPU (const core::Mesh &m)
{
  MeshBuffer gm;

  if (m.vertices.empty ())
    {
      return gm;
    }

  glGenVertexArrays (1, &gm.vao);
  glBindVertexArray (gm.vao);

  glGenBuffers (1, &gm.vbo);
  glBindBuffer (GL_ARRAY_BUFFER, gm.vbo);
  glBufferData (
      GL_ARRAY_BUFFER,
      static_cast<GLsizeiptr> (m.vertices.size () * sizeof (core::Vertex)),
      m.vertices.data (), GL_STATIC_DRAW);

  /* position: attrib 0, offset 0 */
  glEnableVertexAttribArray (0);
  glVertexAttribPointer (
      0, 3, GL_FLOAT, GL_FALSE, sizeof (core::Vertex),
      reinterpret_cast<const void *> (offsetof (core::Vertex, position)));

  /* normal: attrib 1, offset 12 */
  glEnableVertexAttribArray (1);
  glVertexAttribPointer (
      1, 3, GL_FLOAT, GL_FALSE, sizeof (core::Vertex),
      reinterpret_cast<const void *> (offsetof (core::Vertex, normal)));

  /* texcoord: attrib 2, offset 24 */
  glEnableVertexAttribArray (2);
  glVertexAttribPointer (
      2, 2, GL_FLOAT, GL_FALSE, sizeof (core::Vertex),
      reinterpret_cast<const void *> (offsetof (core::Vertex, texcoord)));

  glGenBuffers (1, &gm.ebo);
  glBindBuffer (GL_ELEMENT_ARRAY_BUFFER, gm.ebo);
  glBufferData (
      GL_ELEMENT_ARRAY_BUFFER,
      static_cast<GLsizeiptr> (m.indices.size () * sizeof (std::uint32_t)),
      m.indices.data (), GL_STATIC_DRAW);

  gm.indexCount = static_cast<GLsizei> (m.indices.size ());

  float maxRadiusSq = 0.0f;
  gm.aabbMin = m.vertices[0].position;
  gm.aabbMax = m.vertices[0].position;

  for (const auto &v : m.vertices)
    {
      float distSq = v.position.x * v.position.x + v.position.y * v.position.y
                     + v.position.z * v.position.z;

      maxRadiusSq = std::max (maxRadiusSq, distSq);

      gm.aabbMin.x = std::min (gm.aabbMin.x, v.position.x);
      gm.aabbMin.y = std::min (gm.aabbMin.y, v.position.y);
      gm.aabbMin.z = std::min (gm.aabbMin.z, v.position.z);

      gm.aabbMax.x = std::max (gm.aabbMax.x, v.position.x);
      gm.aabbMax.y = std::max (gm.aabbMax.y, v.position.y);
      gm.aabbMax.z = std::max (gm.aabbMax.z, v.position.z);
    }

  gm.boundingSphereRadius = std::sqrt (maxRadiusSq);

  glBindVertexArray (0);

  return gm;
}

GLuint
ResourceManager::getTexture (const std::string &path)
{
  std::filesystem::path p (path);
  std::string k = p.string ();

  auto it = _textureCache.find (k);
  if (it != _textureCache.end ())
    {
      return it->second;
    }

  int width, height, channels;
  /* stbi_set_flip_vertically_on_load (true); */
  unsigned char *data = stbi_load (k.c_str (), &width, &height, &channels, STBI_rgb);
  if (!data)
    {
      return 0;
    }

  GLuint texID;
  glGenTextures (1, &texID);
  glBindTexture (GL_TEXTURE_2D, texID);

  GLenum format = GL_RGB;
  glTexImage2D (GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB,
                GL_UNSIGNED_BYTE, data);
  glGenerateMipmap (GL_TEXTURE_2D);

  glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
                   GL_LINEAR_MIPMAP_LINEAR);
  glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

  stbi_image_free (data);
  _textureCache[k] = texID;

  return texID;
}

} // namespace engine