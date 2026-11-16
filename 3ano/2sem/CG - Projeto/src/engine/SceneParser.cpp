#include "SceneParser.h"

#include "tinyxml/tinyxml2.h"
#include <iostream>
#include <cstring>

using namespace tinyxml2;

namespace engine
{

namespace
{

int
require_elem (XMLElement *e, const char *what)
{
  if (e)
    {
      return 0;
    }

  std::cerr << "failed to find " << what << " element\n";
  return 1;
}

int
read_int_attr (XMLElement *e, const char *attr, int &out, const char *what)
{
  if (e->QueryIntAttribute (attr, &out) == XML_SUCCESS)
    {
      return 0;
    }

  std::cerr << "failed to parse " << what << " " << attr << " attribute\n";
  return 1;
}

int
read_float_attr (XMLElement *e, const char *attr, float &out, const char *what)
{
  if (e->QueryFloatAttribute (attr, &out) == XML_SUCCESS)
    {
      return 0;
    }

  std::cerr << "failed to parse " << what << " " << attr << " attribute\n";
  return 1;
}

float
opt_float (XMLElement *e, const char *attr, float fallback)
{
  float v = fallback;
  if (e->QueryFloatAttribute (attr, &v) != XML_SUCCESS)
    {
      std::cerr << "failed to parse " << attr << " attribute\n";
      return fallback;
    }

  return v;
}

int
read_vec3_xyz (XMLElement *e, core::Vector3 &out)
{
  float x = 0.f, y = 0.f, z = 0.f;
  if (
    e->QueryFloatAttribute ("x", &x) != XML_SUCCESS ||
    e->QueryFloatAttribute ("y", &y) != XML_SUCCESS ||
    e->QueryFloatAttribute ("z", &z) != XML_SUCCESS
  )
    {
      return 1;
    }

  out = core::Vector3 (x, y, z);
  return 0;
}

int
parse_window (XMLElement *win, WindowConfig &out)
{
  if (require_elem (win, "<window>") != 0)
    {
      return 1;
    }

  int w = 0, h = 0;

  if (read_int_attr (win, "width", w, "<window>") != 0)
    {
      return 1;
    }

  if (read_int_attr (win, "height", h, "<window>") != 0)
    {
      return 1;
    }

  out.width  = w;
  out.height = h;

  return 0;
}

int
parse_camera (XMLElement *cam, CameraConfig &out)
{
  if (require_elem(cam, "<camera>") != 0)
    {
      return 1;
    }

  XMLElement *pos = cam->FirstChildElement ("position");
  XMLElement *la  = cam->FirstChildElement ("lookAt");

  if (require_elem (pos, "<camera>/<position>") != 0)
    {
      return 1;
    }

  if (require_elem (la, "<camera>/<lookAt>") != 0)
    {
      return 1;
    }

  if (read_vec3_xyz (pos, out.pos) != 0)
    {
      std::cerr << "failed to parse <camera>/<position> element\n";
      return 1;
    }

  if (read_vec3_xyz (la, out.lookAt) != 0)
    {
      std::cerr << "failed to parse <camera>/<lookAt> element\n";
      return 1;
    }

  if (XMLElement *up = cam->FirstChildElement ("up"))
    {
      core::Vector3 tmp;
      if (read_vec3_xyz (up, tmp) != 0)
        {
          std::cerr << "failed to parse <camera>/<up> element\n";
          return 1;
        }

      out.up = tmp;
    }

  if (XMLElement *proj = cam->FirstChildElement ("projection"))
    {
      float fov = out.fovDeg, nz = out.nearZ, fz = out.farZ;

      if (read_float_attr (proj, "fov", fov, "<camera>/<projection>") != 0)
        {
          return 1;
        }

      if (read_float_attr (proj, "near", nz, "<camera>/<projection>") != 0)
        {
          return 1;
        }

      if (read_float_attr (proj, "far", fz, "<camera>/<projection>") != 0)
        {
          return 1;
        }

      out.fovDeg = fov;
      out.nearZ  = nz;
      out.farZ   = fz;
    }

  return 0;
}

int
parse_lights (XMLElement *lightsElem, Scene &outScene)
{
  if (require_elem (lightsElem, "<lights>") != 0)
    {
      return 1;
    }

  for (XMLElement *l = lightsElem->FirstChildElement ("light"); l;
       l = l->NextSiblingElement ("light"))
    {
      const char *typeStr = l->Attribute ("type");
      if (!typeStr)
        {
          std::cerr << "missing attribute 'type' in <light>\n";
          continue;
        }

      LightConfig light;
      std::string type (typeStr);

      if (type == "point")
        {
          light.type = LightConfig::Type::Point;
        }
      else if (type == "directional")
        {
          light.type = LightConfig::Type::Directional;
        }
      else if (type == "spotlight" || type == "spot")
        {
          light.type = LightConfig::Type::Spotlight;
        }
      else
        {
          std::cerr << "unknown light type: " << type << '\n';
          continue;
        }

      if (light.type == LightConfig::Type::Point
          || light.type == LightConfig::Type::Spotlight)
        {
          light.pos.x = opt_float (l, "posX", opt_float (l, "posx", 0.f));
          light.pos.y = opt_float (l, "posY", opt_float (l, "posy", 0.f));
          light.pos.z = opt_float (l, "posZ", opt_float (l, "posz", 0.f));
        }

      if (light.type == LightConfig::Type::Directional
          || light.type == LightConfig::Type::Spotlight)
        {
          light.dir.x = opt_float (l, "dirX", opt_float (l, "dirx", 0.f));
          light.dir.y = opt_float (l, "dirY", opt_float (l, "diry", -1.f));
          light.dir.z = opt_float (l, "dirZ", opt_float (l, "dirz", 0.f));
        }

      if (light.type == LightConfig::Type::Spotlight)
        {
          light.cutoff = opt_float (l, "cutoff", 45.f);
        }

      outScene.lights.push_back (light);
    }

  return 0;
}

int
parse_transform (XMLElement *xfElem, GroupNode *node)
{
  if (require_elem(xfElem, "<transform> ") != 0)
    {
      return 0;
    }

  bool hasTranslate = false;
  bool hasRotate    = false;
  bool hasScale     = false;

  for (XMLElement *child = xfElem->FirstChildElement (); child;
       child = child->NextSiblingElement ())
    {
      const char *tag = child->Name ();

      if (std::strcmp (tag, "translate") == 0)
        {
          if (hasTranslate)
            {
              std::cerr << "duplicate translate element in transform block is not allowed\n";
              return 1;
            }

          hasTranslate = true;

          /* float time = opt_float (child, "time", 0.f); */
          float time = 0.f;
          child->QueryFloatAttribute ("time", &time);
          if (time > 0.f)
            {
              bool align = 1;
              if (child->Attribute ("align")
                  && std::string (child->Attribute ("align")) == "true")
                {
                  align = 0;
                }

              std::vector<core::Vector3> points;
              for (XMLElement *pt = child->FirstChildElement ("point"); pt;
                   pt = pt->NextSiblingElement ("point"))
                {
                  core::Vector3 p;
                  if (read_vec3_xyz (pt, p) != 0)
                    {
                      std::cerr << "failed to parse <translate>/<point> element\n";
                      return 1;
                    }

                  points.push_back (p);
                }

              try
                {
                  node->addTransform (
                      std::make_unique<AnimatedTranslate> (time, align, points));
                }
              catch (const std::invalid_argument &e)
                {
                  std::cerr << "failed to parse <translate>: " << e.what () << '\n';
                  return 1;
                }
            }
          else
            {
              node->addTransform (std::make_unique<StaticTranslate> (
                  core::Vector3 (opt_float (child, "x", 0.f),
                                 opt_float (child, "y", 0.f),
                                 opt_float (child, "z", 0.f))));
            }
        }
      else if (std::strcmp (tag, "rotate") == 0)
        {
          if (hasRotate)
            {
              std::cerr << "duplicate rotate element in transform block is not allowed\n";
              return 1;
            }

          hasRotate = true;

          /* float time = opt_float (child, "time", 0.f); */
          float time = 0.f;
          child->QueryFloatAttribute ("time", &time);
          core::Vector3 axis (opt_float (child, "x", 0.f),
                              opt_float (child, "y", 0.f),
                              opt_float (child, "z", 0.f));

          if (time > 0.f)
            {
              node->addTransform (
                  std::make_unique<AnimatedRotate> (time, axis));
            }
          else
            {
              float angle = opt_float (child, "angle", 0.f);
              node->addTransform (
                  std::make_unique<StaticRotate> (angle, axis));
            }
        }
      else if (std::strcmp (tag, "scale") == 0)
        {
          if (hasScale)
            {
              std::cerr << "duplicate scale element in transform block is not allowed\n";
              return 1;
            }

          hasScale = true;

          node->addTransform (std::make_unique<StaticScale> (core::Vector3 (
              opt_float (child, "x", 1.f), opt_float (child, "y", 1.f),
              opt_float (child, "z", 1.f))));
        }
    }

  return 0;
}

int
parse_models (XMLElement *modelsElem, const std::filesystem::path &base_dir,
              GroupNode *node)
{
  if (require_elem (modelsElem, "<models>") != 0)
    {
      return 1;
    }

  for (XMLElement *m = modelsElem->FirstChildElement ("model"); m;
       m = m->NextSiblingElement ("model"))
    {
      const char *file = m->Attribute ("file");
      if (!file || !*file)
        {
          std::cerr << "missing required attribute 'file' in <model>\n";
          return 1;
        }

      std::string fullPath = (base_dir / file).lexically_normal ().string ();
      auto modelNode = std::make_unique<ModelNode> (fullPath);

      if (XMLElement *texElem = m->FirstChildElement ("texture"))
        {
          const char *texFile = texElem->Attribute ("file");
          if (texFile && *texFile)
            {
              std::string fullTexPath
                  = (base_dir / texFile).lexically_normal ().string ();
              modelNode->setTexturePath (fullTexPath);
            }
        }

      if (XMLElement *colElem = m->FirstChildElement ("color"))
        {
          ColorConfig color;
          auto parse_color_comp = [] (XMLElement *comp, core::Vector3 &out)
            {
              if (comp)
                {
                  float r = opt_float (comp, "R", 0.f) / 255.f;
                  float g = opt_float (comp, "G", 0.f) / 255.f;
                  float b = opt_float (comp, "B", 0.f) / 255.f;
                  out = core::Vector3 (r, g, b);
                }
            };
          parse_color_comp (colElem->FirstChildElement ("diffuse"),
                            color.diffuse);

          parse_color_comp (colElem->FirstChildElement ("ambient"),
                            color.ambient);

          parse_color_comp (colElem->FirstChildElement ("specular"),
                            color.specular);

          parse_color_comp (colElem->FirstChildElement ("emissive"),
                            color.emissive);

          if (XMLElement *shinElem = colElem->FirstChildElement ("shininess"))
            {
              color.shininess = opt_float (shinElem, "value", 0.f);
            }

          modelNode->setColor (color);
        }

      node->addChild (std::move (modelNode));
    }

  return 0;
}

int
parse_group (XMLElement *groupElem, const std::filesystem::path &base_dir,
             GroupNode *parentGroup)
{
  if (require_elem (groupElem, "<group>") != 0)
    {
      return 1;
    }

  auto currentGroup = std::make_unique<GroupNode> ();

  if (XMLElement *xf = groupElem->FirstChildElement ("transform"))
    {
      if (parse_transform (xf, currentGroup.get ()) != 0)
        {
          return 1;
        }
    }

  if (XMLElement *models = groupElem->FirstChildElement ("models"))
    {
      if (parse_models (models, base_dir, currentGroup.get ()) != 0)
        {
          return 1;
        }
    }

  for (XMLElement *child = groupElem->FirstChildElement ("group"); child;
       child = child->NextSiblingElement ("group"))
    {
      if (parse_group (child, base_dir, currentGroup.get ()) != 0)
        {
          return 1;
        }
    }

  parentGroup->addChild (std::move (currentGroup));
  return 0;
}

} /* anonymous namespace */

int
parse_scene_xml (const std::filesystem::path &xmlPath,
                 Scene &outScene) noexcept
{
  XMLDocument doc;
  if (doc.LoadFile (xmlPath.string ().c_str ()) != XML_SUCCESS)
    {
      std::cerr << "failed to load XML file: " << doc.ErrorStr () << '\n';
      return 1;
    }

  XMLElement *world = doc.FirstChildElement ("world");
  if (require_elem (world, "<world>") != 0)
    {
      return 1;
    }

  const std::filesystem::path base_dir = xmlPath.parent_path ();

  if (parse_window (world->FirstChildElement ("window"), outScene.window) != 0)
    {
      std::cerr << "failed to parse <window> element\n";
      return 1;
    }

  if (parse_camera (world->FirstChildElement ("camera"), outScene.camera) != 0)
    {
      std::cerr << "failed to parse <camera> element\n";
      return 1;
    }

  if (XMLElement *lightsElem = world->FirstChildElement ("lights"))
    {
      if (parse_lights (lightsElem, outScene) != 0)
        {
          std::cerr << "failed to parse <lights> element\n";
          return 1;
        }
    }

  auto root = std::make_unique<GroupNode> ();

  if (XMLElement *group = world->FirstChildElement ("group"))
    {
      if (parse_group (group, base_dir, root.get ()) != 0)
        {
          std::cerr << "failed to parse <group> element\n";
          return 1;
        }
    }

  outScene.setRoot (std::move (root));
  return 0;
}

} // namespace engine