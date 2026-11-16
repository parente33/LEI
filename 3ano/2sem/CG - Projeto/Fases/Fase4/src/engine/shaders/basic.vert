#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoords;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 FragPos;
out vec3 FragNormal;
out vec2 TexCoords;
out vec3 ViewPos;

void
main (void)
{
  vec4 worldPos = model * vec4(aPos, 1.0);
  FragPos = vec3(worldPos);

  mat3 rot         = transpose(mat3(view));
  vec3 translation = vec3(view[3]);
  ViewPos = -rot * translation;

  gl_Position = projection * view * worldPos;
  FragNormal  = mat3 (transpose (inverse (model))) * aNormal;
  TexCoords   = aTexCoords;
}