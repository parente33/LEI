#ifndef CG_ENGINE_SCENE_PARSER_H
#define CG_ENGINE_SCENE_PARSER_H

#include "scene/Scene.h"

#include <filesystem>
#include <string>

namespace engine
{

int parse_scene_xml (const std::filesystem::path &xmlPath,
                     Scene &outScene) noexcept;

} // namespace engine

#endif // CG_ENGINE_SCENE_PARSER_H