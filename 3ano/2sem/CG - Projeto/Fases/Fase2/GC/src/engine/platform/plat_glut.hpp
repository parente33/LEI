#pragma once

#include "state.hpp"


struct GlutConfig
{
    int          width = 512, height = 512;
    const char  *title = "engine";
};

class GlutPlatform
{
    public:
        static int run(int& argc, char *argv[], const GlutConfig &cfg, State &state);
};