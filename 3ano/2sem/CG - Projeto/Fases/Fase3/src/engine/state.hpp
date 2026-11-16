#pragma once

#include <memory>

#include "scene.hpp"
#include "renderer.hpp"


class State
{
    public:
        State(Scene scene, std::unique_ptr<IRenderer> renderer)
            : scene_(std::move(scene)), renderer_(std::move(renderer)) {}

        bool init_after_gl() { return renderer_->init(); }
        void resize(int w, int h) { viewportW_ = w; viewportH_ = h; renderer_->resize(w,h); }

        void draw(float dtSec, float timeSec)
        {
            RenderFrame f { scene_.world, scene_.cache, viewportW_, viewportH_, timeSec, dtSec };
            renderer_->render(f);
        }

    private:
        Scene scene_;
        std::unique_ptr<IRenderer> renderer_;
        int viewportW_ = 1, viewportH_ = 1;
};