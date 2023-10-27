---
title: "Tensorflow Terminology"
icon: icon/svg/guidelines.svg
description: Tensorflow vs TFLite and more
date: 2023-08-11T15:26:15Z
draft: false
weight: 320
---


*Updated: 5 July 2023*

## Inference

In our context we refer to Machine Learning inference as the process of running data points into a machine learning model to calculate an output such as a single numerical score. Some alternative phrases with the same meaning include "operationalizing a Machine Learning Model", or "putting a Machine Learning model into production". Models are used for inference once sufficient training has occurred and it is ready to work on real live data. 

## TensorFlow vs TensorFlow Lite

[TensorFlow](https://www.tensorflow.org/learn) (TF) is a advanced machine learning toolkit developed by Google. TF is an end-to-end machine learning platform designed for development in Python. It provides the necessary tools to prepare data, train models, and deploy models. One of the deployment options is to export the model to a *TensorFlow Lite* format which we will use to run within our ATAK plugin. [TensorFlow Lite](https://www.tensorflow.org/lite/guide) is actually a set of tools that enables developers to run their models on mobile, embedded and edge devices. Android has a series of libraries for using TF Lite models. This demo plugin will show you how to set up an ATAK plugin to use TF Lite, import models, and call functions on models.

A different folder in this repo `learnatak\ml_training\tflite` contains Python code that shows how to export a TF Lite model and include functions in the model. We recommend you read the README and code documentation for this project as well.

## TensorFlow Hub

[Tensorflow Hub](https://tfhub.dev) is a place to host open source TensorFlow models, and provides high-performance models developed by Google and the wider research community. For your ATAK plugins check out the available [TF Lite models](https://tfhub.dev/s?deployment-format=lite) which are optimized for mobile devices.

## Android Studio ML Binding vs TF Lite Interpreter

The Android Studio Machine Learning Binding Feature/Plugin was [introduced in 2020](https://android-developers.googleblog.com/2020/06/tools-for-custom-ML-models.html) to improved developer experiences using TensorFlow Lite models. It requires at a minimum the interaction with the TensorFlow Lite support library to input and retrieve `TensorBuffer` outputs. This tool simplifies the model import process into your project and properly configures the project to generate a wrapper class to interact with your model using the basic TF Lite API after you build your project. It is the only thing required to be able to perform inference with your model.
The TensorFlow Lite Interpreter is an additional feature within the TFLite library that enables a developer to do additional functions with your model like on-device training, access information about the model, or enable GPU processing with the model.