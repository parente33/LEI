#include "ShaderProgram.h"

namespace engine
{

ShaderProgram::ShaderProgram (const std::string &vertShaderPath,
                              const std::string &fragShaderPath)
{
  // shader files
  std::ifstream vShaderFile, fShaderFile;

  vShaderFile.open (vertShaderPath);
  fShaderFile.open (fragShaderPath);

  std::stringstream vShaderStream, fShaderStream;
  vShaderStream << vShaderFile.rdbuf ();
  fShaderStream << fShaderFile.rdbuf ();

  vShaderFile.close ();
  fShaderFile.close ();

  // shader code
  std::string vertCode, fragCode;

  vertCode = vShaderStream.str ();
  fragCode = fShaderStream.str ();

  const char *vertShaderCode = vertCode.c_str ();
  const char *fragShaderCode = fragCode.c_str ();

  // shaders
  GLuint vertexShader = glCreateShader (GL_VERTEX_SHADER);
  glShaderSource  (vertexShader, 1, &vertShaderCode, NULL);
  glCompileShader (vertexShader);

  GLuint fragmentShader = glCreateShader (GL_FRAGMENT_SHADER);
  glShaderSource  (fragmentShader, 1, &fragShaderCode, NULL);
  glCompileShader (fragmentShader);

  _id = glCreateProgram ();

  glAttachShader (_id, vertexShader);
  glAttachShader (_id, fragmentShader);

  glLinkProgram (_id);

  glDeleteShader (vertexShader);
  glDeleteShader (fragmentShader);
}

ShaderProgram::~ShaderProgram ()
{
  if (_id)
    {
      glDeleteProgram (_id);
    }
}

ShaderProgram::ShaderProgram (ShaderProgram &&other) noexcept
{
  _id = other._id;
  _uniformLocationCache = std::move (other._uniformLocationCache);
  other._id = 0;
}

ShaderProgram &
ShaderProgram::operator= (ShaderProgram &&other) noexcept
{
  if (this != &other)
    {
      if (_id)
        {
          glDeleteProgram (_id);
        }

      _id = other._id;
      _uniformLocationCache = std::move (other._uniformLocationCache);
      other._id = 0;
    }

  return *this;
}

void
ShaderProgram::use () const
{
  glUseProgram (_id);
}

void
ShaderProgram::unuse () const
{
  glUseProgram (0);
}

GLint
ShaderProgram::getUniformLocation (const std::string &name) const
{
  auto it = _uniformLocationCache.find (name);
  if (it != _uniformLocationCache.end ())
    {
      return it->second;
    }

  GLint location = glGetUniformLocation (_id, name.c_str ());
  _uniformLocationCache[name] = location;

  return location;
}

void
ShaderProgram::setUniform1i (const std::string &name, int value) const
{
  glUniform1i (getUniformLocation (name), value);
}

void
ShaderProgram::setUniform1f (const std::string &name, float value) const
{
  glUniform1f (getUniformLocation (name), value);
}

void
ShaderProgram::setUniformVec3 (const std::string &name,
                               const core::Vector3 &vec) const
{
  glUniform3f (getUniformLocation (name), vec.x, vec.y, vec.z);
}

void
ShaderProgram::setUniformMat4 (const std::string &name,
                               const core::Matrix4 &matrix) const
{
  glUniformMatrix4fv (getUniformLocation (name), 1, GL_FALSE,
                      matrix.ptr ());
}

} // namespace engine