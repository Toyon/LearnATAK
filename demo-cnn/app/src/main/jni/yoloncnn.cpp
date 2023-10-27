#include <android/asset_manager_jni.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>

#include <android/log.h>

#include <jni.h>

#include <string>
#include <vector>

#include <platform.h>
#include <benchmark.h>

#include "yolo.h"

#include "ndkcamera.h"

#include <atomic>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/imgcodecs.hpp>
#include <jni.h>

#include <cstdio>    // File remove()
#include <dirent.h>  // Directory listing
#include <iostream>


#pragma clang diagnostic push
#if __ARM_NEON
#include <arm_neon.h>
#endif // __ARM_NEON

#define TAG "ncnn"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG,  __VA_ARGS__)

static int draw_unsupported(cv::Mat& rgb)
{
    const char text[] = "unsupported";

    int baseLine = 0;
    cv::Size label_size = cv::getTextSize(text, cv::FONT_HERSHEY_SIMPLEX, 1.0, 1, &baseLine);

    int y = (rgb.rows - label_size.height) / 2;
    int x = (rgb.cols - label_size.width) / 2;

    cv::rectangle(rgb, cv::Rect(cv::Point(x, y), cv::Size(label_size.width, label_size.height + baseLine)),
                  cv::Scalar(255, 255, 255), -1);

    cv::putText(rgb, text, cv::Point(x, y + label_size.height),
                cv::FONT_HERSHEY_SIMPLEX, 1.0, cv::Scalar(0, 0, 0));

    return 0;
}

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
    sprintf(text, "FPS=%.2f", avg_fps);
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

static Yolo* g_yolo = 0;
static ncnn::Mutex lock;

std::atomic<bool> capture(false);

std::string imgDirectory;

class MyNdkCamera : public NdkCameraWindow
{
public:

    virtual void capture_detections(cv::Mat& rgb, std::vector<Object>* objects) const;

    void on_image_render(cv::Mat& rgb) const override;
};

void MyNdkCamera::on_image_render(cv::Mat& rgb) const
{
    {
        ncnn::MutexLockGuard g(lock);

        if (g_yolo)
        {
            std::vector<Object> objects;
            g_yolo->detect(rgb, objects);

            // if capture flag is set record cropped images for each object identified
            if (capture && !imgDirectory.empty())
            {
                __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Try capture");
                this->capture_detections(rgb, &objects);
                capture = false;
            }

            g_yolo->draw(rgb, objects);
        }
        else
        {
            draw_unsupported(rgb);
        }
    }

    draw_fps(rgb);
}

void MyNdkCamera::capture_detections(cv::Mat &rgb, std::vector<Object>* objects) const {
    CV_Assert(!rgb.empty());

    // remove old files
    {
        DIR* dir = opendir(imgDirectory.c_str());
        if (dir == nullptr)
        {
            __android_log_print(ANDROID_LOG_WARN, "ncnn",
                                "Unable to open and clean temp image directory");
        }
        else
        {
            struct dirent* entry;
            while ((entry = readdir(dir)) != nullptr)
            {
                if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0)
                {
                    continue;
                }
                std::string complete_path = imgDirectory + "/" + entry->d_name;
                remove(complete_path.c_str());
            }
        }
    }

    int counter = 0;
    for (auto & item : *objects) {
        counter += 1;
        __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Item #%d", counter);

        __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Image Size (w x h): %d x %d", rgb.cols, rgb.rows);
        __android_log_print(ANDROID_LOG_DEBUG, "ncnn",
                            "Rectangle Left = %i, Top = %i, Right = %i, Bottom = %i",
                            (int) item.rect.tl().x, (int) item.rect.tl().y,
                            (int) item.rect.br().x,(int) item.rect.br().y);

        // crop the full image to that image contained by the rectangle of the region of interest
        cv::Mat croppedRef(rgb, item.rect);

        // copy the data
        cv::Mat cropped;
        croppedRef.copyTo(cropped);

        std::string fileName = imgDirectory;
        fileName.append("/");

        static const char *class_names[] = {
                "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat",
                "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat",
                "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack",
                "umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball",
                "kite", "baseball bat", "baseball glove", "skateboard", "surfboard", "tennis racket",
                "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
                "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake",
                "chair", "couch", "potted plant", "bed", "dining table", "toilet", "tv", "laptop",
                "mouse", "remote", "keyboard", "cell phone", "microwave", "oven", "toaster", "sink",
                "refrigerator", "book", "clock", "vase", "scissors", "teddy bear", "hair drier",
                "toothbrush"
        };

        fileName.append(std::to_string(counter) + "_" +
                        class_names[item.label] + "_" +
                        std::to_string(item.prob) + ".png");
        __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "SAVE TO %s", fileName.c_str());
        cv::imwrite(fileName, cropped);
    }
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "CAPTURED");
}

static MyNdkCamera* g_camera = 0;

// Provide C Linkage
extern "C" {

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnLoad");

    //g_camera = new MyNdkCamera;

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnUnload");
/*
    {
        ncnn::MutexLockGuard g(lock);

        delete g_yolo;
        g_yolo = 0;
    }

    delete g_camera;
    g_camera = 0;
    */
}

// public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
JNIEXPORT jboolean
Java_com_toyon_democnn_NcnnYolov7_loadModel(JNIEnv* env, jobject thiz, jobject assetManager)
{
    jint modelid = 0;
    jint cpugpu = 0;
    if (modelid < 0 || modelid > 6 || cpugpu < 0 || cpugpu > 1)
    {
        return JNI_FALSE;
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "loadModel %p", mgr);

    const char* modeltypes[] =
            {
                    "yolov7-tiny",
            };

    const int target_sizes[] =
            {
                    640,
            };

    const float norm_vals[][3] =
            {
                    {1 / 255.f, 1 / 255.f , 1 / 255.f},
            };

    const char* modeltype = modeltypes[(int)modelid];
    int target_size = target_sizes[(int)modelid];
    bool use_gpu = (int)cpugpu == 1;

    // reload
    {
        ncnn::MutexLockGuard g(lock);
/*
        if (use_gpu && ncnn::get_gpu_count() == 0)
        {
            // no gpu
            delete g_yolo;
            g_yolo = 0;
        }
        else
        {
        */
        if(g_yolo) {
            g_yolo->load(mgr, modeltype, target_size, norm_vals[(int) modelid], use_gpu);
        }
        //}
    }

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_toyon_democnn_NcnnYolov7_createCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "createCamera");

    g_camera = new MyNdkCamera;

    if (!g_yolo)
    {
        LOGI("creating new yolo");
        g_yolo = new Yolo;
    }

    return JNI_TRUE;
}

// public native boolean openCamera(int facing);
JNIEXPORT jboolean JNICALL
Java_com_toyon_democnn_NcnnYolov7_openCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "openCamera %d");

    g_camera->open();

    return JNI_TRUE;
}

// public native boolean closeCamera();
JNIEXPORT jboolean JNICALL
Java_com_toyon_democnn_NcnnYolov7_closeCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "closeCamera");

    g_camera->close();

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_toyon_democnn_NcnnYolov7_destroyCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "destoryCamera");

    {
        ncnn::MutexLockGuard g(lock);

        delete g_yolo;
        g_yolo = 0;
    }

    delete g_camera;
    g_camera = 0;

    return JNI_TRUE;
}

// public native boolean setOutputWindow(Surface surface);
JNIEXPORT jboolean JNICALL
Java_com_toyon_democnn_NcnnYolov7_setOutputWindow(JNIEnv* env, jobject thiz, jobject surface)
{
    ANativeWindow* win = ANativeWindow_fromSurface(env, surface);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "setOutputWindow %p", win);

    g_camera->set_window(win);

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_toyon_democnn_NcnnYolov7_releaseOutputWindow(JNIEnv* env, jobject thiz, jobject surface)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "releaseOutputWindow");

    ANativeWindow* win = ANativeWindow_fromSurface(env, surface);
    if (win)
    {
        ANativeWindow_release(win);
    }

    return JNI_TRUE;
}



JNIEXPORT void JNICALL
Java_com_toyon_democnn_NcnnYolov7_capture(JNIEnv * env , jobject thiz ) {
    if (g_camera == nullptr)
        return;
    capture = true;
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Set Capture Flag");


}

JNIEXPORT void JNICALL
Java_com_toyon_democnn_NcnnYolov7_setImageDirectory(JNIEnv * env, jobject thiz, jstring absPath)
{
    ncnn::MutexLockGuard g(lock);

    const char *cStr = env->GetStringUTFChars(absPath, nullptr);
    if (nullptr == cStr) return;
    (*env).ReleaseStringUTFChars(absPath, cStr);

    imgDirectory = std::string(cStr);
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Set Image Directory %p", cStr);
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Shared Dir Value %p", imgDirectory.c_str());
}

JNIEXPORT void JNICALL Java_com_toyon_democnn_NcnnYolov7_setAssetManager(JNIEnv* env, jobject thiz, jobject assetManager)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Assigning Asset Manager for config");

    g_camera->set_asset_manager(AAssetManager_fromJava(env, assetManager));

}

JNIEXPORT void JNICALL Java_com_toyon_democnn_NcnnYolov7_setPrefOrientation(JNIEnv* env, jobject thiz, int orientation)
{
    g_camera->set_pref_orientation(orientation);
}

}

#pragma clang diagnostic pop