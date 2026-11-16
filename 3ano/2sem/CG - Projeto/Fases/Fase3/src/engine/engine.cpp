#include <iostream>

#include "world_parser.hpp"
#include "state.hpp"
#include "plat_glut.hpp"
#include "renderer_im.hpp"


int main(int argc, char *argv[])
{
    if (argc <= 1) {
        std::fprintf(stderr, "Usage: %s <scene.xml>\n", argv[0]);
        return 1;
    }

    Scene scene;
    WorldParseError err;
    if (!load_scene(argv[1], scene, err)) {
        std::fprintf(stderr, "error: %s\n", err.message.c_str());
        return 1;
    }

    GlutConfig cfg;
    cfg.width  = scene.world.window.width;
    cfg.height = scene.world.window.height;
    cfg.title  = "engine";

    State state(std::move(scene), std::make_unique<RendererIM>());
    return GlutPlatform::run(argc, argv, cfg, state);
}
