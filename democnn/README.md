# <img src="./app/src/main/res/drawable/ic_paw.png" height="64px"/>Demo CNN Plugin - Pet Finder

This plugin was developed to demonstrate a realistic application utilizing a Convolutional Neural Network (CNN) to assist in sharing information with a connected TAK community. The goal of this plugin is to introduce how one can integrate a real-time machine learning (ML) model into an ATAK plugin using [OpenCV](https://github.com/opencv/opencv) and [ncnn](https://github.com/Tencent/ncnn). The `ncnn` library provides us with the necessary tools to convert and process the image pixels with a You Only Look Once (YOLO) model. The Java Native Interface (JNI) methods allow us to utilize the object detection and image classification results within an ATAK plugin. When loaded, the plugin can be found in the ATAK [tools](../doc/7_ATAK_Quick_Reference.md/#toolbar) as a paw icon as shown in the title. More implementation details can be found in the plugin's [doc](./doc) folder.

![CNN / YOLO / Pet Finder Demo Plugin Gif](../img/ATAK_CNN_Pet_Finder.gif)

## How to build and run this plugin

When building/running this plugin first ensure you have the listed dependencies then procedure to the [Quick Reference](#Quick-Reference) section. This plugin has the same dependencies as the Camera demonstration so you shouldn't have to install anything new if you can build and run the camera plugin.

### Dependencies

- [CMake](https://cmake.org/download/)

  1. Click the link above to download CMake. 
     Windows: advised to download the latest `*.msi` link and follow the default options in the installer. Ensure it is included in your environment path.
  2. Restart all terminal sessions and ensure install works. Open up a terminal session and `cmake --version`. You should see `cmake version 3.23.2` or the version you installed as your response.
     **MAY REQUIRE SYSTEM REBOOT.**
  3. Ensure you specify your version of CMake in the `cmake_version` variable of the projects `local.properties` file.

- [OpenCV for Android version 4.6.0](https://sourceforge.net/projects/opencvlibrary/files/4.6.0/opencv-4.6.0-android-sdk.zip/download)

  1. Click the link above to download OpenCV version 4.6.0 for Android.

  2. Extract the zip archive to place the contents in:
     - Windows: `C:\tak\AndroidLibs\opencv-4.6.0-android-sdk`
     - Linux:

  3. Ensure you specify your install path in the `opencv_dir` variable of the projects `local.properties` file. 

- [NCNN Android Vulkan 20221128](https://sourceforge.net/projects/ncnn.mirror/files/20221128/ncnn-20221128-android-vulkan.zip/download)

  1. Click the link above to download ncnn build number 20221128 .
  2. Extract the zip archive to place the contents in:
     - Windows: `C:\tak\AndroidLibs\ncnn-20221128-android-vulkan`
     - Linux: 
  3. Ensure you specify your install path in the `ncnn_dir` variable of the projects `local.properties` file.

- [Ninja Build System](https://github.com/ninja-build/ninja/releases)

  1. Click the link above to navigate to the GitHub ninja release page. 

  2. Select the latest asset `*.zip` download (at the time of writing we downloaded v1.11.1)
  3. Extract the download to your desired path. The download only has one executable `ninja.exe`. 
     We placed the executable at `c:\tak\win-ninja\ninja.exe`.
     - Windows: Add `ninja.exe` to System Environment Variables. 
       Open Environment Variables window. Find "Path" in the "System variables" view. Select "Edit" then press "New". Add the complete path to the folder holding your ninja executable `C:\tak\win-ninja`.
     - Linux: 
  4. Ensure install works. Open up a terminal session and enter `ninja --version`. You should see `1.11.1` or the version you installed.
     **MAY REQUIRE SYSTEM REBOOT.**

### Quick Reference

1. Build the application signing keys which are required by the Android Operating System (OS) for security when installing software packages.
   At the bottom of the IDE there should be a *Terminal* tab you can open to launch a terminal session in the root folder of the plugin.

   ```sh
   # Run the following commands in your Android Studio Terminal
   # Generate Debug signing key: set "alias", "keypass", and "storepass" flag values as desired
   keytool -genkeypair -dname "CN=Android Debug,O=Android,C=US" -validity 9999 -keystore debug.keystore -alias androiddebugkey -keypass android -storepass android 
   
   # Generate Release signing key: set "alias", "keypass", and "storepass" flag values as desired
   keytool -genkeypair -dname "CN=Android Release,O=Android,C=US" -validity 9999 -keystore release.keystore -alias androidreleasekey -keypass android -storepass android 
   ```

2. Edit the `democamera/local.properties` file to add the following lines.`<ANDROID_SDK_PATH>` and the `sdk.dir` should already be filled out by the IDE with the default Android SDK file path. The key here is to specify the paths to your signing keys,  OpenCV library, and  `ncnn` library. It is also required to specify the CMake version you have installed.
   `<ABSOLUTE_PLUGIN_PATH>` should be a complete file path to the root plugin folder;
    example plugin path: `C\:\\tak\\atak-civ-sdk-4.5.1.13\\atak-civ\\plugin-examples\\democnn` 

   NOTE: Ensure your directory path is [escaped](https://www.gnu.org/software/bash/manual/html_node/Escape-Character.html) properly on Windows. Follow the default `sdk.dir` for formatting reference or the example path above. The most common mistake is forgetting the first escape character `C\:`. Gradle Exceptions will be thrown until your paths are correctly formatted.

   ```ini
   sdk.dir=<ANDROID_SDK_PATH>
   takDebugKeyFile=<ABSOLUTE_PLUGIN_PATH>\\debug.keystore
   takDebugKeyFilePassword=android
   takDebugKeyAlias=androiddebugkey
   takDebugKeyPassword=android
   
   takReleaseKeyFile=<ABSOLUTE_PLUGIN_PATH>\\release.keystore
   takReleaseKeyFilePassword=android
   takReleaseKeyAlias=androidreleasekey
   takReleaseKeyPassword=android
   
   cmake_version=3.23.2
   opencv_dir="C:\\tak\\AndroidLibs\\opencv-4.6.0-android-sdk"
   ncnn_dir="C:\\tak\\AndroidLibs\\ncnn-20221128-android-vulkan"
   ```

## Logcat Filter

Copy and paste this in the filter field of the [Logcat](https://developer.android.com/studio/debug/logcat) to make it easier to follow the feed of log messages that are printed by the demo camera plugin to help further your understanding of the code in the project. 

```sh
# FILTER
tag:MainDropDown tag:ReportDropDown tag:AnimalAdapter tag:Animal 
```

## Potential Errors

#### 1. OpenCV not provided

If you see the following stack trace error when trying to build the application there is an issue with your installation of OpenCV. 

If you have installed and extracted the library as outlined in the [dependencies](#dependencies) section it is likely just an issue with the `opencv_dir` path provided in the `local.properties` file.

```verilog
CMake Error at CMakeLists.txt:6 (find_package):
By not providing "FindOpenCV.cmake" in CMAKE_MODULE_PATH this project has asked CMake to find a package configuration file provided by "OpenCV", but CMake did not find one. Could not find a package configuration file provided by "OpenCV" with any of the following names:
OpenCVConfig.cmake
opencv-config.cmake
Add the installation prefix of "OpenCV" to CMAKE_PREFIX_PATH or set "OpenCV_DIR" to a directory containing one of the above files.  If "OpenCV" provides a separate development package or SDK, be sure it has been installed.
```

#### 2. CMake Error and Ninja

If you see the following stack trace error when trying to build the application there is an issue with your installation of ninja. First attempt to restart Android Studio to resolve this issue, if that doesn't work then a complete computer reboot should fix this issue.

```verilog
CMake Error: CMake was unable to find a build program corresponding to "Ninja".  CMAKE_MAKE_PROGRAM is not set.  You probably need to select a different build tool.
```



