project(democnn)

cmake_minimum_required(VERSION 3.10)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fexceptions" )
#add_compile_options(-fexceptions)

IF (WIN32)
    set(ncnn_DIR  "${NCNN_ROOT}\\${ANDROID_ABI}\\lib\\cmake\\ncnn")
    set (OpenCV_DIR "${OPENCV_ROOT}\\sdk\\native\\jni")
ELSE()
    set(ncnn_DIR "${NCNN_ROOT}/${ANDROID_ABI}/lib/cmake/ncnn")
    set (OpenCV_DIR "${OPENCV_ROOT}/sdk/native/jni")
ENDIF()
message("ncnn: ${ncnn_DIR}")
message("OpenCV: ${OpenCV_DIR}")

find_package(ncnn REQUIRED)  # https://github.com/Tencent/ncnn
find_package(OpenCV REQUIRED core imgproc imgcodecs)  # https://github.com/opencv/opencv

add_library(democnn SHARED yoloncnn.cpp yolo.cpp ndkcamera.cpp)

target_link_libraries(democnn ncnn ${OpenCV_LIBS} camera2ndk mediandk)
