#ifndef CG_ENGINE_RENDERER_SHADER_PROGRAM_H
#define CG_ENGINE_RENDERER_SHADER_PROGRAM_H

#include "core/Matrix.h"

#include <fstream>
#include <sstream>

#define GL_GLEXT_PROTOTYPES
#include <GL/gl.h>
#include <string>
#include <unordered_map>

namespace engine
{

class ShaderProgram
{
public:
  ShaderProgram (const std::string &vertShaderPath,
                 const std::string &fragShaderPath);
  ~ShaderProgram ();

  ShaderProgram (const ShaderProgram &) = delete;
  ShaderProgram &operator= (const ShaderProgram &) = delete;

  ShaderProgram (ShaderProgram &&other) noexcept;
  ShaderProgram &operator= (ShaderProgram &&other) noexcept;

  void use   () const;
  void unuse () const;

  void setUniform1i (const std::string &name, int value) const;
  void setUniform1f (const std::string &name, float value) const;

  void setUniformVec3 (const std::string &name,
                       const core::Vector3 &vec) const;
  void setUniformMat4 (const std::string &name,
                       const core::Matrix4 &matrix) const;

  GLuint
  getId () const
  {
    return _id;
  }

private:
  GLint getUniformLocation (const std::string &name) const;

  GLuint _id = 0;
  mutable std::unordered_map<std::string, GLint> _uniformLocationCache;
};

} // namespace engine

#endif // CG_ENGINE_RENDERER_SHADER_PROGRAM_H