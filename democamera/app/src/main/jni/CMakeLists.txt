project(demo_camera)

cmake_minimum_required(VERSION 3.10)

IF (WIN32)
    set(ncnn_DIR "${NCNN_ROOT}\\${ANDROID_ABI}\\lib\\cmake\\ncnn")
    set (OpenCV_DIR "${OPENCV_ROOT}\\sdk\\native\\jni")
ELSE()
    set(ncnn_DIR "${NCNN_ROOT}/${ANDROID_ABI}/lib/cmake/ncnn")
    set (OpenCV_DIR "${OPENCV_ROOT}/sdk/native/jni")
ENDIF()
message("ncnn: ${ncnn_DIR}")
message("OpenCV: ${OpenCV_DIR}")

find_package(ncnn REQUIRED)  # https://github.com/Tencent/ncnn
find_package(OpenCV REQUIRED core imgproc)  # https://github.com/opencv/opencv

add_library(demo_camera SHARED yoloncnn.cpp ndkcamera.cpp)

target_link_libraries(demo_camera ncnn ${OpenCV_LIBS} camera2ndk mediandk nativewindow)
