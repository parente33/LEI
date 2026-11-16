.PHONY: all clean

all: generator engine

generator:
	g++ -std=c++20 -O2 -Wall -Wextra -Wpedantic  \
	    -Isrc/core -Isrc/generator               \
		src/core/mesh_io.cpp                     \
		src/generator/primitives.cpp             \
		src/generator/commands.cpp               \
		src/generator/generator.cpp              \
		-o generator

engine:
	g++ -std=c++20 -Wall -Wextra -Wpedantic -O2           \
	    -Ilib -Isrc/core -Isrc/engine -Isrc/engine/world  \
		-Isrc/engine/render -Isrc/engine/platform         \
		-Isrc/engine/resources lib/tinyxml2.cpp           \
		src/core/mesh_io.cpp                              \
		src/engine/world/world_parser.cpp                 \
		src/engine/resources/resource_cache.cpp           \
		src/engine/render/renderer_im.cpp                 \
		src/engine/platform/plat_glut.cpp                 \
		src/engine/engine.cpp                             \
		-o engine -lglut -lGLU -lGL

clean:
	rm -f generator engine