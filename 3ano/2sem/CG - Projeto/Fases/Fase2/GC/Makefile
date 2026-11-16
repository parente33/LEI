.PHONY: all clean

CXX      := g++
CXXFLAGS := -std=c++20 -O2 -Wall -Wextra -Wpedantic

BUILDDIR := build

GEN_BIN := $(BUILDDIR)/generator
ENG_BIN := $(BUILDDIR)/engine

GEN_INC  := -Isrc/core -Isrc/generator
ENG_INC  := -Ilib -Isrc/core -Isrc/engine                 \
            -Isrc/engine/world -Isrc/engine/render        \
            -Isrc/engine/platform -Isrc/engine/resources

CORE_SRC := $(wildcard src/core/*.cpp)

GEN_SRC :=                                  \
	$(CORE_SRC)                             \
	$(wildcard src/generator/*.cpp)

ENG_SRC :=                                  \
	lib/tinyxml2.cpp                        \
	$(CORE_SRC)                             \
	$(wildcard src/engine/*.cpp)            \
	$(wildcard src/engine/world/*.cpp)      \
	$(wildcard src/engine/render/*.cpp)     \
	$(wildcard src/engine/platform/*.cpp)   \
	$(wildcard src/engine/resources/*.cpp)

GEN_OBJ := $(patsubst %.cpp,$(BUILDDIR)/%.o,$(GEN_SRC))
ENG_OBJ := $(patsubst %.cpp,$(BUILDDIR)/%.o,$(ENG_SRC))

all: $(GEN_BIN) $(ENG_BIN)

$(BUILDDIR)/%.o: %.cpp
	mkdir -p $(dir $@)
	$(CXX) $(CXXFLAGS) $(INC) -c $< -o $@

$(GEN_BIN): INC := $(GEN_INC)
$(GEN_BIN): $(GEN_OBJ)
	mkdir -p $(BUILDDIR)
	$(CXX) $(CXXFLAGS) $(GEN_OBJ) -o $@

$(ENG_BIN): INC := $(ENG_INC)
$(ENG_BIN): $(ENG_OBJ)
	mkdir -p $(BUILDDIR)
	$(CXX) $(CXXFLAGS) $(ENG_OBJ) -o $@ -lglut -lGLU -lGL

clean:
	rm -rf $(BUILDDIR)