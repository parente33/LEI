#pragma once
#include <string_view>

struct Mesh;

bool write_3d (std::string path, const Mesh &mesh) noexcept;
bool read_3d (std::string path, Mesh &out) noexcept;