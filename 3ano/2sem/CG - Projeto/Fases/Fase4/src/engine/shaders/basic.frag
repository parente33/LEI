#version 330 core

out vec4 FragColor;

in vec3 FragPos;
in vec3 FragNormal;
in vec2 TexCoords;
in vec3 ViewPos;

struct Light
{
  int type;  // 0 = point, 1 = directional, 2 = spotlight

  vec3 pos;
  vec3 dir;
  vec3 color;

  float cutoff;
  float constant;
  float linear;
  float quadratic;
};

uniform int   u_numLights;
uniform Light u_lights[8];

uniform vec3 u_matDiffuse;
uniform vec3 u_matAmbient;
uniform vec3 u_matSpecular;
uniform vec3 u_matEmissive;
uniform float u_matShininess;

uniform int       u_hasTexture;
uniform sampler2D u_texture;

uniform int u_useLighting;

vec3
CalcDirectionalLight (Light light, vec3 normal, vec3 viewDir, vec3 baseColor)
{
  vec3 lightDir = normalize (light.dir);

  // diffuse shading
  float diff = max (dot (normal, lightDir), 0.0);

  // specular shading (Blinn-Phong)
  vec3 halfwayDir = normalize (lightDir + viewDir);
  float spec = 0.0;
  if (u_matShininess > 0.0 && diff > 0.0)
    {
      spec = pow (max (dot (normal, halfwayDir), 0.0), u_matShininess);
    }

  vec3 diffuse  = diff * baseColor     * light.color;
  vec3 specular = spec * u_matSpecular * light.color;

  return (diffuse + specular);
}

vec3
CalcPointLight (Light light, vec3 normal, vec3 fragPos, vec3 viewDir, vec3 baseColor)
{
  vec3 lightDir = normalize (light.pos - fragPos);

  // diffuse shading
  float diff = max (dot (normal, lightDir), 0.0);

  // specular shading (Blinn-Phong)
  vec3 halfwayDir = normalize (lightDir + viewDir);
  float spec = 0.0;
  if (u_matShininess > 0.0 && diff > 0.0)
    {
      spec = pow (max (dot (normal, halfwayDir), 0.0), u_matShininess);
    }

  // attenuation
  float distance    = length(light.pos - fragPos);
  float attenuation = 1.0 / (light.constant + light.linear * distance
                                              + light.quadratic * (distance * distance));

  vec3 diffuse  = diff * baseColor     * light.color;
  vec3 specular = spec * u_matSpecular * light.color;

  return (diffuse + specular) * attenuation;
}

vec3
CalcSpotLight(Light light, vec3 normal, vec3 fragPos, vec3 viewDir, vec3 baseColor)
{
    vec3 lightDir = normalize (light.pos - fragPos);

    // diffuse shading
    float diff = max (dot (normal, lightDir), 0.0);

    // specular shading (Blinn-Phong)
    vec3 halfwayDir = normalize (lightDir + viewDir);
    float spec = 0.0;
    if (u_matShininess > 0.0 && diff > 0.0)
      {
        spec = pow (max (dot (normal, halfwayDir), 0.0), u_matShininess);
      }

    // attenuation
    float distance    = length (light.pos - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance
                                              + light.quadratic * (distance * distance));

    // spotlight intensity with smooth fade (soft edges)
    float theta     = dot (lightDir, normalize (-light.dir));
    float epsilon   = cos (radians (light.cutoff)) - cos (radians (light.cutoff + 5.0));
    float intensity = clamp ((theta - cos (radians (light.cutoff + 5.0))) / epsilon, 0.0, 1.0);

    vec3 diffuse  = diff * baseColor     * light.color;
    vec3 specular = spec * u_matSpecular * light.color;

    return (diffuse + specular) * attenuation * intensity;
}

void
main (void)
{
  vec3 baseColor;

  if (u_hasTexture == 1)
    {
      vec4 texColor = texture (u_texture, TexCoords);
      baseColor = texColor.rgb;
    }
  else
    {
      baseColor = u_matDiffuse;
    }

  if (u_useLighting == 1)
  {
    vec3 norm    = normalize (FragNormal);
    vec3 viewDir = normalize (ViewPos - FragPos);

    vec3 result = u_matEmissive + u_matAmbient * baseColor;

    for (int i = 0; i < u_numLights && i < 8; i++)
    {
      if (u_lights[i].type == 1)
        {
          result += CalcDirectionalLight (u_lights[i], norm, viewDir, baseColor);
        }
      else if (u_lights[i].type == 0)
        {
          result += CalcPointLight (u_lights[i], norm, FragPos, viewDir, baseColor);
        }
      else if (u_lights[i].type == 2)
        {
          result += CalcSpotLight (u_lights[i], norm, FragPos, viewDir, baseColor);
        }
    }

    FragColor = vec4 (clamp (result, 0.0, 1.0), 1.0);
  }
  else
  {
    FragColor = vec4 (baseColor, 1.0);
  }
}