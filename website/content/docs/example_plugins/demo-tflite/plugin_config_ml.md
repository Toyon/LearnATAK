---
title: "Add a TensorFlow Lite Model to Plugin "
icon: icon/svg/model.svg
description: Steps for adding a pre-trained model to your existing plugin
date: 2023-08-11T15:26:15Z
draft: false
weight: 330
---



*Updated: 5 July 2023*

This page will discuss the additional steps to follow to build an ATAK plugin capable of running a trained TensorFlow (TF) Lite model locally on you Android device. Please ensure you have a plugin ready for these modification by completing the ["Creating your own Plugin: First Steps"](../../../setup/atak_plugin/#creating-your-own-plugin-first-steps) before you proceed with the following instructions.

## 1. Gradle Change: Protect Model

Within the `android` block of the [`app/build.gradle`](https://github.com/Toyon/LearnATAK/tree/master/demotflite/app/build.gradle#L285), add the Android Asset Packaging Tool (AAPT) Options `aaptOptions` with the `noCompress "tflite"` option to ensure your plugin's `*.tflite` model files are not compressed during the build process. See Below.

## 2. Gradle Change: Enable ML  Model Binding

Within the `android` block of the [`app/build.gradle`](https://github.com/Toyon/LearnATAK/tree/master/demotflite/app/build.gradle#L281-283), add the Android Build Features `buildFeatures` with the Android Machine Learning (ML) Model Binding feature `buildFeatures` option enabled. See Below.

```groovy
android {
	...
    aaptOptions {
        noCompress "tflite"
    }
	buildFeatures {
    	mlModelBinding true
	}
}
```

## 3. Import a TensorFlow Lite Model 

Import a TF Lite Model (`*.tflite`) by right clicking the `main` directory (`app/main/`) and selecting `New -> Other -> Tensorflow Lite Model`.
In the pop up, specify the path to your desired model.

![Import TensorFlow Lite Model popup - hugo](/image/android_studio/tflite-import-popup.PNG)

![Import TensorFlow Lite Model popup - local](../../../../assets/image/android_studio/tflite-import-popup.PNG)

This will create a new folder  `main/ml` in your project with the imported TF Lite model inside. Additionally, the following dependencies will be added to the `app/build.gradle` file if this is the first model you have imported:

```groovy
dependencies {
    ...
	implementation 'org.tensorflow:tensorflow-lite-support:0.1.0'
	implementation 'org.tensorflow:tensorflow-lite-metadata:0.1.0'
	implementation 'org.tensorflow:tensorflow-lite-gpu:2.3.0'
}
```

We recommend increasing the `tensorflow-lite-support` to `0.4.3` to make use of newer features:

```groovy
implementation 'org.tensorflow:tensorflow-lite-support:0.4.3'
```

## 4. Access Your Model

To check that everything was setup correctly you should be able to build "Make Project" successfully.
To access and run your model, read the next section.