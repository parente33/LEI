set(SOURCE_FILES
    Matrix.cpp
    MeshIO.cpp
    include/core/Matrix.h
    include/core/MeshIO.h
    include/core/Mesh.h
    include/core/Vector.h
)

set(THIS_MODULE core)

add_library(${THIS_MODULE} STATIC ${SOURCE_FILES})

target_include_directories(${THIS_MODULE} PRIVATE include/${THIS_MODULE})
target_include_directories(${THIS_MODULE} PUBLIC include)