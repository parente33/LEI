#pragma once

#include <cmath>
#include <memory>

#include "scene.hpp"
#include "renderer.hpp"
#include "math.hpp"


class State
{
public:
    State(Scene scene, std::unique_ptr<IRenderer> renderer)
        : scene_(std::move(scene)), renderer_(std::move(renderer))
    { init_camera(); }

    bool init_after_gl() { return renderer_->init(); }

    void resize(int w, int h)
    {
        viewportW_ = w;
        viewportH_ = h;
        renderer_->resize(w, h);
    }

    void draw(float dtSec, float timeSec)
    {
        update_camera();
        RenderFrame f
        {
            scene_.world, scene_.cache,
            viewportW_, viewportH_, timeSec, dtSec
        };
        renderer_->render(f);
    }

    void key_down(unsigned char key)
    {
        constexpr float kZoomStep = 2.0f;
        switch (key) {
            case '+': case '=': radius_ -= kZoomStep; break;
            case '-': case '_': radius_ += kZoomStep; break;
        }
        if (radius_ < 0.5f) radius_ = 0.5f;
    }

    void special_key(int key)
    {
        constexpr float kAngleStep = 0.05f;

        constexpr int kLeft  = 100;
        constexpr int kUp    = 101;
        constexpr int kRight = 102;
        constexpr int kDown  = 103;

        switch (key) {
            case kLeft:  alpha_ += kAngleStep; break;
            case kRight: alpha_ -= kAngleStep; break;
            case kUp:    beta_  += kAngleStep; break;
            case kDown:  beta_  -= kAngleStep; break;
        }

        constexpr float kMaxBeta = PI * 0.5f - 0.01f;
        if (beta_ >  kMaxBeta) beta_ =  kMaxBeta;
        if (beta_ < -kMaxBeta) beta_ = -kMaxBeta;
    }

private:
    Scene scene_;
    std::unique_ptr<IRenderer> renderer_;
    int viewportW_ = 1, viewportH_ = 1;

    /* orbit camera, around lookAt */
    float alpha_  = 0.f;
    float beta_   = 0.f;
    float radius_ = 5.f;

    void init_camera()
    {
        const Camera &cam = scene_.world.camera;
        const Vec3 d = cam.pos - cam.lookAt;
        radius_ = length(d);

        if (radius_ > 0.001f) {
            beta_  = std::asin(d.y / radius_);
            alpha_ = std::atan2(d.z, d.x);
        }
    }

    void update_camera()
    {
        Camera &cam = scene_.world.camera;
        const float cb = std::cos(beta_);
        cam.pos = cam.lookAt + Vec3 {
            radius_ * cb * std::cos(alpha_),
            radius_ * std::sin(beta_),
            radius_ * cb * std::sin(alpha_)
        };
    }
};