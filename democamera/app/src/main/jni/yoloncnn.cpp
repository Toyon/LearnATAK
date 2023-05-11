#include <android/asset_manager_jni.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>
#include <android/asset_manager.h>

#include <android/log.h>

#include <jni.h>

#include <string>
#include <vector>

#include <platform.h>
#include <benchmark.h>

#include "ndkcamera.h"

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#if __ARM_NEON
#include <arm_neon.h>
#endif // __ARM_NEON

#define TAG "CTAG"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG,  __VA_ARGS__)

static int draw_fps(cv::Mat& rgb)
{
    // resolve moving average
    float avg_fps = 0.f;
    {
        static double t0 = 0.f;
        static float fps_history[10] = {0.f};
        double t1 = ncnn::get_current_time();
        if (t0 == 0.f)
        {
            t0 = t1;
            return 0;
        }
        float fps = 1000.f / (t1 - t0);
        t0 = t1;

        for (int i = 9; i >= 1; i--)
        {
            fps_history[i] = fps_history[i - 1];
        }
        fps_history[0] = fps;

        if (fps_history[9] == 0.f)
        {
            return 0;
        }

        for (int i = 0; i < 10; i++)
        {
            avg_fps += fps_history[i];
        }
        avg_fps /= 10.f;
    }
    char text[32];
    sprintf(text, "FPS: %.2f", avg_fps);
    int baseLine = 0;
    cv::Size label_size = cv::getTextSize(text, cv::FONT_HERSHEY_SIMPLEX, 0.5, 1, &baseLine);
    int y = 0;
    int x = rgb.cols - label_size.width;
    cv::rectangle(rgb, cv::Rect(cv::Point(x, y), cv::Size(label_size.width, label_size.height + baseLine)),
                    cv::Scalar(255, 255, 255), -1);
    cv::putText(rgb, text, cv::Point(x, y + label_size.height),
                cv::FONT_HERSHEY_SIMPLEX, 0.5, cv::Scalar(0, 0, 0));

    return 0;
}

static ncnn::Mutex lock;

class MyNdkCamera : public NdkCameraWindow
{
public:
    virtual void on_image_render(cv::Mat& rgb) const;
};

void MyNdkCamera::on_image_render(cv::Mat& rgb) const
{
    draw_fps(rgb);

    //LOGI("width: %d, height: %d", rgb.cols, rgb.rows);
}

static MyNdkCamera* g_camera = 0;

extern "C" {

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnLoad");


    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnUnload");

}

JNIEXPORT jboolean JNICALL Java_com_atakmap_android_democamera_NcnnYolov7_createCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "createCamera");

    g_camera = new MyNdkCamera;

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_com_atakmap_android_democamera_NcnnYolov7_openCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "openCamera");

    g_camera->open();

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_com_atakmap_android_democamera_NcnnYolov7_closeCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "closeCamera");

    g_camera->close();

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_com_atakmap_android_democamera_NcnnYolov7_destroyCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "destoryCamera");

    delete g_camera;
    g_camera = 0;

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_com_atakmap_android_democamera_NcnnYolov7_setOutputWindow(JNIEnv* env, jobject thiz, jobject surface)
{
    ANativeWindow* win = ANativeWindow_fromSurface(env, surface);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "setOutputWindow %p", win);

    g_camera->set_window(win);

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_com_atakmap_android_democamera_NcnnYolov7_releaseOutputWindow(JNIEnv* env, jobject thiz, jobject surface)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "releaseOutputWindow");

    ANativeWindow* win = ANativeWindow_fromSurface(env, surface);
    if (win)
    {
        ANativeWindow_release(win);
    }

    return JNI_TRUE;
}

JNIEXPORT void JNICALL Java_com_atakmap_android_democamera_NcnnYolov7_setAssetManager(JNIEnv* env, jobject thiz, jobject assetManager)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Assigning Asset Manager for config");

    g_camera->set_asset_manager(AAssetManager_fromJava(env, assetManager));

}

JNIEXPORT void JNICALL Java_com_atakmap_android_democamera_NcnnYolov7_setPrefOrientation(JNIEnv* env, jobject thiz, int orientation)
{
    g_camera->set_pref_orientation(orientation);
}


}
