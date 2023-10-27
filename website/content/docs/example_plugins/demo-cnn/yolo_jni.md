---
title: "Working with Live Camera Data"
icon: icon/svg/code.svg
description: Overview of the tools needed to use live camera fee
date: 2023-08-11T15:26:15Z
draft: false
weight: 270
---

# YOLO & JNI

| Acronym | Full Term                    |
| ------- | ---------------------------- |
| CNN     | Convolutional Neural Network |
| ML      | Machine Learning             |
| YOLO    | You-Only-Look-Once Model     |
| JNI     | Java Native Interface        |

In order to take a snapshot of the camera feed and YOLO results we implemented two functions which are extended via the JNI to setup a temporary image directory and another to set a capture flag which triggers the C++ pipeline to crop detected objects from the image feed and place them in the temporary image directory. 

Below is the Java code utilized to monitor the temporary image directory. We listen for changes to the files within the temporary directory which triggers the "reporting" pane to be displayed with a recycler view showing a preview of the detected and classified items from the image.

```java
public CameraYoloDropDown(final MapView mapView, final Context context) {

	cameraFileObserver = new FileObserver(dir.getAbsolutePath()) {

        private Timer notifyTimer = new Timer();

        @Override
        public void onEvent(int i, @Nullable String s) {
            if (i != FileObserver.CREATE) return;
            Log.d(TAG, "UPDATE LIST OF IDENTIFIED OBJECTS TO SELECT FROM");
            notifyTimer.cancel();
            notifyTimer = new Timer();
            notifyTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent reportIntent = new Intent();
                    reportIntent.setAction(ReportDropDown.SHOW_REPORT_PANE);
                    AtakBroadcast.getInstance().sendBroadcast(reportIntent);
                }
            }, 500L);
        }
    };
    cameraFileObserver.startWatching();
}
```

The following block of code shows the significant C++ functions used by the JNI interface methods which implement the logic to set the capture flag, check the capture flag on each image rendered, and write cropped images based on the bounding box provided by the YOLO model. These methods are all located in the top level [`yolocnn.cpp`](https://github.com/Toyon/LearnATAK/tree/master/democnn/app/src/main/jni/yoloncnn.cpp) file.

```c++
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

JNIEXPORT void JNICALL
Java_com_atakmap_android_democnn_NcnnYolov7_setImageDirectory(JNIEnv * env, jobject thiz, jstring absPath)
{
    ncnn::MutexLockGuard g(lock);

    const char *cStr = env->GetStringUTFChars(absPath, nullptr);
    if (nullptr == cStr) return;
    (*env).ReleaseStringUTFChars(absPath, cStr);

    imgDirectory = std::string(cStr);
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Set Image Directory %p", cStr);
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "Shared Dir Value %p", imgDirectory.c_str());
}
```

