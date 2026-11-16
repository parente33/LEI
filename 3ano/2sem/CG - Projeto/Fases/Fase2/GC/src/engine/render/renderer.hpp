#pragma once

#include "world.hpp"
#include "resource_cache.hpp"


struct RenderFrame
{
    const World          &world;
    const ResourceCache  &resources;

    int    viewportW = 1, viewportH = 1;
    float  timeSec = 0.f, dtSec = 0.f;
};

class IRenderer
{
    public:
        virtual ~IRenderer() = default;

        /* called after GL context exists. */
        virtual bool init() = 0;

        virtual void resize(int w, int h) = 0;
        virtual void render(const RenderFrame &f) = 0;
};