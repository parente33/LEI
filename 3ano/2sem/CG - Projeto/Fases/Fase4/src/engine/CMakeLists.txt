set(SOURCE_FILES
    Application.cpp
    Application.h
    main.cpp
    SceneParser.cpp
    SceneParser.h
    camera/Camera.cpp
    camera/Camera.h
    camera/Frustum.cpp
    camera/Frustum.h
    renderer/MeshBuffer.cpp
    renderer/MeshBuffer.h
    renderer/Renderer.cpp
    renderer/Renderer.h
    renderer/ResourceManager.cpp
    renderer/ResourceManager.h
    renderer/ShaderProgram.cpp
    renderer/ShaderProgram.h
    renderer/stb_image.cpp
    scene/Scene.cpp
    scene/Scene.h
    scene/SceneNode.cpp
    scene/SceneNode.h
    scene/Transform.cpp
    scene/Transform.h
)

set(THIS_MODULE engine)

add_executable(${THIS_MODULE} ${SOURCE_FILES})

target_link_libraries(${THIS_MODULE} PRIVATE core tinyxml glfw GL)

target_include_directories(${THIS_MODULE} PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})