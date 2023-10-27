---
title: "Overview"
icon: icon/svg/tensor_flow.svg
date: 2023-08-11T15:26:15Z
lastmod: 2023-08-11T15:26:15Z
description: Demo TensorFlow Lite Plugin
weight: 310
draft: false
---

This plugin was developed to demonstrate how a developer would run a TensorFlow (TF) Lite model within an ATAK plugin. The goal of this plugin is to introduce how an ATAK plugin developer would need to setup their project to enable a pre-trained model to run on the Android device, and to showcase some of the popularly utilized features of the TF Lite toolkit. This sample plugin is not intended to showcase an interesting and well trained model. Instead it can be used as a template to build your own plugin which needs to run a TFLite model. If everything works properly, the plugin will simply display a [success message](https://github.com/Toyon/LearnATAK/tree/master/demotflite/app/src/main/java/com/toyon/tfliteexample/plugin/TFLiteDemoDropDownReceiver.java#50-70) in the plugin pane window when it is displayed. When loaded, the plugin can be found in the ATAK [tools](../../atak_development/atak_quick_reference/) as a mini neural network icon as shown in the title above. More information about TensorFlow Lite and the setup procedure can be found below in [documentation](#tf-lite-plugin-documentation).

## How to build and run this plugin

Quick Reference if you are building/running this plugin for the first time.

1. Build the application signing keys which are required by the Android Operating System (OS) for security when installing software packages.
   At the bottom of the IDE there should be a *Terminal* tab you can open to launch a terminal session in the root folder of the plugin. 

```sh
# Run the following commands in your Android Studio Terminal
# Generate Debug signing key: set "alias", "keypass", and "storepass" flag values as desired
keytool -genkeypair -dname "CN=Android Debug,O=Android,C=US" -validity 9999 -keystore debug.keystore -alias androiddebugkey -keypass android -storepass android 

# Generate Release signing key: set "alias", "keypass", and "storepass" flag values as desired
keytool -genkeypair -dname "CN=Android Release,O=Android,C=US" -validity 9999 -keystore release.keystore -alias androidreleasekey -keypass android -storepass android 
```

2. Edit the `demo-tflite/local.properties` file to add the following lines. 
   `<ANDROID_SDK_PATH>` and the `sdk.dir` should already be filled out by the IDE with the default Android SDK file path
   `<ABSOLUTE_PLUGIN_PATH>` should be a complete file path to the root plugin folder;
    example plugin path: `C\:\\tak\\atak-civ-sdk-4.5.1.13\\atak-civ\\learnatak\\demo-tflite` 

```ini
# the sdk.dir should be automatically assigned to the path of your Android Studio SDK 
sdk.dir=<ANDROID_SDK_PATH>  
takDebugKeyFile=<ABSOLUTE_PLUGIN_PATH>\\debug.keystore
takDebugKeyFilePassword=android
takDebugKeyAlias=androiddebugkey
takDebugKeyPassword=android

takReleaseKeyFile=<ABSOLUTE_PLUGIN_PATH>\\release.keystore
takReleaseKeyFilePassword=android
takReleaseKeyAlias=androidreleasekey
takReleaseKeyPassword=android
```

## TF Lite Plugin Documentation

To understand more about how the project was created and understand what is demonstrated in the plugin review the following documentation

1. [Terminology and TensorFlow Overview](ml_terminology/)
2. [Setup and Importing models](plugin_config_ml/)
3. [Running and Using Models](use_model/)

## TF Model Training

To understand more about building, training, and exporting your own models check out the [machine learning TensorFlow lite directory](https://github.com/Toyon/LearnATAK/-/tree/main/ml_training/tflite).
