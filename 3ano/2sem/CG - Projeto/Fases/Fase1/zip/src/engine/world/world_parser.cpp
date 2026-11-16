#include "world_parser.hpp"

#include "tinyxml2.h"
using namespace tinyxml2;


namespace {

    bool require_elem(XMLElement *e, const char *what, WorldParseError &err)
    {
        if (e) return true;
        err.message = std::string("missing required element: ") + what;

        return false;
    }

    bool read_int_attr(XMLElement *e, const char *attr, int &out,
        const char *what, WorldParseError &err)
    {
        if (e->QueryIntAttribute(attr, &out) == XML_SUCCESS) return true;
        err.message = std::string("missing/invalid int attribute '") + attr + "' in " + what;

        return false;
    }

    bool read_float_attr(XMLElement *e, const char *attr, float &out,
        const char *what, WorldParseError &err)
    {
        if (e->QueryFloatAttribute(attr, &out) == XML_SUCCESS) return true;
        err.message = std::string("missing/invalid float attribute '") + attr + "' in " + what;

        return false;
    }

    bool read_vec3_xyz(XMLElement *e, Vec3 &out,
        const char *what, WorldParseError &err)
    {
        float x = 0.f, y = 0.f, z = 0.f;
        if (e->QueryFloatAttribute("x", &x) != XML_SUCCESS ||
            e->QueryFloatAttribute("y", &y) != XML_SUCCESS ||
            e->QueryFloatAttribute("z", &z) != XML_SUCCESS)
        {
            err.message = std::string("missing/invalid vec3 attributes x/y/z in ") + what;
            return false;
        }

        out = Vec3 { x, y, z };
        return true;
    }

    bool parse_window(XMLElement *win, WindowConfig &out, WorldParseError &err)
    {
        if (!win) return true;

        int w = 0, h = 0;
        if (!read_int_attr(win, "width",  w, "<window>", err)) return false;
        if (!read_int_attr(win, "height", h, "<window>", err)) return false;

        out.width  = w;
        out.height = h;

        return true;
    }

    bool parse_camera(XMLElement *cam, Camera &out, WorldParseError &err)
    {
        if (!require_elem(cam, "<camera>", err)) return false;

        XMLElement *pos = cam->FirstChildElement("position");
        XMLElement *la  = cam->FirstChildElement("lookAt");
        if (!require_elem(pos, "<camera>/<position>", err)) return false;
        if (!require_elem(la,  "<camera>/<lookAt>",   err)) return false;

        if (!read_vec3_xyz(pos, out.pos,    "<camera>/<position>", err)) return false;
        if (!read_vec3_xyz(la,  out.lookAt, "<camera>/<lookAt>",   err)) return false;

        if (XMLElement *up = cam->FirstChildElement("up")) {
            Vec3 tmp;
            if (!read_vec3_xyz(up, tmp, "<camera>/<up>", err)) return false;
            out.up = tmp;
        }

        if (XMLElement *proj = cam->FirstChildElement("projection")) {
            float fov = out.fovDeg, nz = out.nearZ, fz = out.farZ;

            if (!read_float_attr(proj, "fov",  fov, "<camera>/<projection>", err)) return false;
            if (!read_float_attr(proj, "near", nz,  "<camera>/<projection>", err)) return false;
            if (!read_float_attr(proj, "far",  fz,  "<camera>/<projection>", err)) return false;

            out.fovDeg = fov;
            out.nearZ  = nz;
            out.farZ   = fz;
        }

        return true;
    }

    bool parse_models(XMLElement *modelsElem, const std::filesystem::path &base_dir,
        std::vector<Model> &out, WorldParseError &err)
    {
        if (!modelsElem) return true;

        for (XMLElement *m = modelsElem->FirstChildElement("model");
            m; m = m->NextSiblingElement("model"))
        {
            const char *file = m->Attribute("file");
            if (!file || !*file) {
                err.message = "missing required attribute 'file' in <model>";
                return false;
            }

            Model r;
            r.meshFile = (base_dir / file).lexically_normal();
            out.push_back(std::move(r));

            /* ... */
        }

        return true;
    }

    bool parse_group(XMLElement *group, const std::filesystem::path &base_dir,
        World &w, NodeId nodeId, WorldParseError &err)
    {
        if (!require_elem(group, "<group>", err)) return false;

        Node &node = world_node(w, nodeId);

        if (XMLElement *models = group->FirstChildElement("models")) {
            if (!parse_models(models, base_dir, node.models, err)) return false;
        }

        for (XMLElement *child = group->FirstChildElement("group");
            child; child = child->NextSiblingElement("group"))
        {
            NodeId cid = world_make_node(w);
            node.children.push_back(cid);
            if (!parse_group(child, base_dir, w, cid, err)) return false;
        }

        /* ... */
        return true;
    }

} /* namespace */


bool parse_world_xml(const std::filesystem::path &xml_path,
    World &out_world, WorldParseError& out_err) noexcept
{
    XMLDocument doc;
    if (doc.LoadFile(xml_path.string().c_str()) != XML_SUCCESS) {
        out_err.message = std::string("xml load failed: ") + doc.ErrorStr();
        return false;
    }

    XMLElement *world = doc.FirstChildElement("world");
    if (!require_elem(world, "<world>", out_err)) return false;

    const std::filesystem::path base_dir = xml_path.parent_path();

    if (!parse_window(world->FirstChildElement("window"), out_world.window, out_err)) return false;
    if (!parse_camera(world->FirstChildElement("camera"), out_world.camera, out_err)) return false;

    out_world.root = world_make_node(out_world);
    if (!parse_group(world->FirstChildElement("group"),
            base_dir, out_world, out_world.root, out_err)) return false;

    return true;
}