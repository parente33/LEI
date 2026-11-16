### three layers

#### **World (scene description)**

* **What it is:** the parsed representation of the XML: window, camera, scene graph, model references.
* **What it must contain:** numbers, hierarchy, and assets references as path and/or small handles.
* **What it must not contain:** mesh objects, textures, OpenGL objects, shared_ptr to resources, file I/O logic.

**Reason**: if you let runtime resources leak into **World**, every future phase will force a redesign. Keeping **World** “pure” prevents band-aids.


#### **ResourceCache (asset ownership)**

* **What it is**: the single owner of all loaded assets (mesh data now; textures later; maybe GPU handles later).
* **What it does:** load, deduplicate, store, and return references.
* **What it must not do:** know about the scene graph or node structure; it should not traverse **World**; it should not be called from random places that cause hidden I/O.

**Reason**: one owner makes lifetime clear and prevents refcount and coupling bloat.


#### **Loader (World + Cache binding)**

* **What it is:** the only place that knows both XML/World and the cache.
* **What it does:** parse the XML into World; then bind asset refs in World to cache references (eager or lazy).
* **What it must not do:** rendering or platform work.

**Reason:** this is the one legitimate “bridge” between description and ownership.

___

### extras

file layout:

    [u32 vertex_count]
    [u32 index_count]
    [Vertex vertices[vertex_count]]
    [u32 indices[index_count]]

engine:
g++ -std=c++20 -Wall -Wextra -Wpedantic -O2 -Ilib -Isrc/core -Isrc/engine -Isrc/engine/world -Isrc/engine/render -Isrc/engine/platform -Isrc/engine/resources lib/tinyxml2.cpp src/core/mesh_io.cpp src/engine/world/world_parser.cpp src/engine/resources/resource_cache.cpp src/engine/world/world_resolve.cpp src/engine/render/renderer_gl1.cpp src/engine/platform/plat_glut.cpp src/engine/state.cpp src/engine/engine.cpp -o engine -lglut -lGLU -lGL

generator:
g++ -std=c++20 -O2 -Wall -Wextra -Wpedantic -Isrc/core -Isrc/generator src/core/mesh_io.cpp src/generator/primitives.cpp src/generator/commands.cpp src/generator/generator.cpp -o generator

___

### future

Transforms (phase 2)

World: store an ordered list of transform operations per node (because order matters).

Renderer: during traversal, accumulate transforms.

Loader: parse <transform> and fill those ops. No resource involvement.

Lights (phase 4)

World: store a small list of light descriptors (max 8).

Renderer: apply lights per frame.

No cache involvement unless you later add IES profiles etc.

Textures/images (phase 3+)

World: store texture references as paths + material descriptors (pure data).

Cache: load image files, create GPU textures later.

Loader: bind texture paths to texture handles in cache.

Multiple cameras / camera controllers

World: store the “default camera” from XML only.

State: owns the active camera controller mode (free, orbit, third-person).

Camera controller reads input and produces a camera for the renderer.

Renderer just consumes a camera each frame (from context). It doesn’t care whether it came from XML or user input.

Debug toggles (wireframe, culling, axes, etc.)

State: store toggles (booleans/enum).

Render context includes those toggles.

Renderer applies them.

Notice the pattern: World stores defaults; State stores runtime control; Renderer consumes final frame settings.