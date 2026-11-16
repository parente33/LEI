#pragma once

#include <string>

struct Mesh;

bool write_3d (const std::string &path, const Mesh &mesh) noexcept;
bool read_3d (const std::string &path, Mesh &out) noexcept;