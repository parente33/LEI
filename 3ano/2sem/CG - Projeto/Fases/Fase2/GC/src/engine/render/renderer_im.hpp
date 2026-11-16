#pragma once

#include "renderer.hpp"


class RendererIM final : public IRenderer
{
    public:
        bool init() override;
        void resize(int w, int h) override;
        void render(const RenderFrame &frame) override;

    private:
        int w_ = 1, h_ = 1;

        void apply_camera(const Camera &cam);
        void draw_node(const World &w, const ResourceCache &rc, NodeId id);
        void draw_mesh(const Mesh &m);
};