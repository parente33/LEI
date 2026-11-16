set(SOURCE_FILES
    main.cpp
    Commands.cpp
    PatchReader.cpp
    Commands.h
    PatchReader.h
    primitives/BezierPatch.cpp
    primitives/BoxGenerator.cpp
    primitives/ConeGenerator.cpp
    primitives/Generator.cpp
    primitives/PatchGenerator.cpp
    primitives/PlaneGenerator.cpp
    primitives/SphereGenerator.cpp
    primitives/BezierPatch.h
    primitives/BoxGenerator.h
    primitives/ConeGenerator.h
    primitives/Generator.h
    primitives/PatchGenerator.h
    primitives/PlaneGenerator.h
    primitives/SphereGenerator.h
)

set(THIS_MODULE generator)

add_executable(${THIS_MODULE} ${SOURCE_FILES})

target_link_libraries(${THIS_MODULE} PRIVATE core)

target_include_directories(${THIS_MODULE} PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})