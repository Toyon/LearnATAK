---
title: "Documentation Overview"
description: "Table of Contents for all of the Documents"
icon: "icon/svg/overview.svg"
date: "2023-05-22T00:27:57+01:00"
lastmod: "2023-05-22T00:27:57+01:00"
draft: false
toc: true
weight: 10
---


Welcome to the LearnATAK project. The following is an outline of the order you should follow to cover the subject material. Demo plugins are also provided.

###  First Step For All

Please review the [Project Guidelines](../guidelines/) to understand how to report issues and submit questions in a format that allows us to help you.


#### Setup android studios and ATAK 

Follow the documents in the [Setup](../setup/) folder to install android studios and use it to run ATAK on your android device. 

1. [Android Studio Setup](../setup/android_studio_setup/)
2. [Run ATAK](../setup/run_atak/)
3. [Run ATAK Plugins](../setup/atak_plugin/)
4. [ATAK Plugin Quick Setup](../setup/quick_setup/)


#### New to Android Development? 

Checkout the [Android Guide](../android_development/) for Android Basics, and User Interface design tutorials. 

1. [Android Basic Concepts](../android_development/android-basics/)
    1. [Lifecycles](../android_development/android-basics/#lifecycle)
    2. [Activities](../android_development/android-basics/#activities)
    3. [Intents](../android_development/android-basics/#intents)
    4. [ViewModels](../android_development/android-basics/#view-models)
    5. [Fragments](../android_development/android-basics/#fragments)
    6. [Services](../android_development/android-basics/#services)
2. [A Tutorial for Creating UI layouts](../android_development/ui_layout_tutorial/)
    1. [Intro to XML Files](../android_development/ui_layout_tutorial/#background-on-xml-files)
    2. [Intro to Android's Interface for designing UIs](../android_development/ui_layout_tutorial/#initial-steps)
    3. [Linear Layouts](../android_development/ui_layout_tutorial/#linear-layouts)
    4. [Frame Layouts](../android_development/ui_layout_tutorial/#frame-layout)
    5. [Grid Layouts](../android_development/ui_layout_tutorial/#grid-layout)
    6. [Scroll View](../android_development/ui_layout_tutorial/#scroll-view)
    7. [Relative Layouts](../android_development/ui_layout_tutorial/#relative-layouts)
    8. [Constraint Layouts](../android_development/ui_layout_tutorial/#constraint-layouts)
    9. [Recycler View](../android_development/ui_layout_tutorial/#recycler-view)
    10. [Display your UIs](../android_development/ui_layout_tutorial/#displaying-uis)
    11. [Access Text and Images](../android_development/ui_layout_tutorial/#accessing-text-and-images)
    12. [Create working Buttons](../android_development/ui_layout_tutorial/#buttons)
3. [Using UI Widgets](../android_development/ui_widgets/)
    1. [Drawable and Colors Folders](../android_development/ui_widgets/#background-on-the-drawable-and-color-folders)
    2. [Using Text](../android_development/ui_widgets/#text-widgets)
        1. [TextView](../android_development/ui_widgets/#textview)
        2. [EditText](../android_development/ui_widgets/#edittext)
        3. [AutoCompleteTextView](../android_development/ui_widgets/#autocompletetextview)
    3. [Working with Images](../android_development/ui_widgets/#images)
        1. [ImageView](../android_development/ui_widgets/#imageview)
    4. [Creating Buttons](../android_development/ui_widgets/#buttons)
        1. [Button](../android_development/ui_widgets/#button)
        2. [Radio Buttons](../android_development/ui_widgets/#radio-buttons)
4. [Working with Multi-Page Views: Fragments](../android_development/fragments/)
    1. [Why Fragments?](../android_development/fragments/#fragments)
    2. [Creating a Swipe View](../android_development/fragments/#creating-the-swipe-views-with-tabs)
    3. [Adding Tabs](../android_development/fragments/#tablayout)
    4. [Creating Fragments](../android_development/fragments/#creating-a-fragment)


#### New to Android Tactical Awareness Kit (ATAK)?

Checkout [ATAK Guide](../atak_development/) for an overview on ATAK, a quick reference, working with databases, and common issues and questions. 

1. [Team Awareness Kit (TAK) Overview](../atak_development/tak_overview/)
2. [Navigating ATAK](../atak_development/atak_quick_reference/)
3. [Room Database with ATAK](../atak_development/room_database_with_atak/)
    1. [Creating the database object](../atak_development/room_database_with_atak/#entities)
    2. [Add CRUD and Query functions](../atak_development/room_database_with_atak/#data-access-objects)
    3. [Creating the database](../atak_development/room_database_with_atak/#database)
    4. [Create a Wrapper Class](../atak_development/room_database_with_atak/#repository)
    5. [Creating a view model](../atak_development/room_database_with_atak/#view-model)
    6. [Using your database in ATAK](../atak_development/room_database_with_atak/#accessing)
    7. [Querying your database](../atak_development/room_database_with_atak/#querying-the-database)

#### Working with Machine Learning? 

Checkout the [Machine Learning](../machine_learning/) folder for an introduction to ML and guides for setting up and creating models for your plugin. 

1. [Introduction to Machine Learning](../machine_learning/ml_overview/)
    1. [What is Machine Learning?](../machine_learning/ml_overview/#what-is-machine-learning)
    2. [How is Machine Learning Used?](../machine_learning/ml_overview/#how-is-machine-learning-being-used)
    3. [How Machine Learning works](../machine_learning/ml_overview/#how-it-works)
    4. [Training and Testing](../machine_learning/ml_overview/#training-and-testing)
    5. [Supervised, Unsupervised, and Reinforcement Learning](../machine_learning/ml_overview/#supervised-unsupervised-and-reinforcement-learning)
    6. [Deep Learning](../machine_learning/ml_overview/#deep-learning)
    7. [Model Inference](../machine_learning/ml_overview/#model-inference)
2. [Machine Learning Breakdown](../machine_learning/ml_guide/)
    1. [Intro & Basics](../machine_learning/ml_guide/#ml-intro--basics) 
    2. [ML Development and Plugin Integration Process](../machine_learning/ml_guide/#ml-development--atak-plugin-integration-process)
    3. [Tips & Tricks](../machine_learning/ml_guide/#ml-tips--tricks)
    4. [ML Problems, Datasets, and Models from Scratch](../machine_learning/ml_guide/#ml-problems-datasets-and-models-from-scratch)
    5. [Notes & Further Resources](../machine_learning/ml_guide/#notes--further-resources)
    6. [Learning More About ML Beyond ATAK](../machine_learning/ml_guide/#learning-more-about-ml) 


#### Example Plugins 

Checkout the [example plugins](../example_plugins/) below! A great starting point is the hello world demo because it incorporates lots of UI designs and functionalities. 

- [Hello World Demo Plugin](../example_plugins/demo-hello-world/)
    1. [ReadME!](../example_plugins/demo-hello-world/readme/)
    1. [Core Components](../example_plugins/demo-hello-world/core_components/)
        1. [Using Fragments and Tabs with ViewPager - deprecated](../example_plugins/demo-hello-world/core_components/#breakdown-fragments--tabs)
        2. [Using Radio Groups and Radio Buttons to select an item from a list](../example_plugins/demo-hello-world/core_components/#breakdown-radio-group--radio-buttons)
        3. [Create a Notification Example](../example_plugins/demo-hello-world/core_components/#breakdown-notifications)
        4. [Create a Service Example](../example_plugins/demo-hello-world/core_components/#breakdown-service)
        5. [App Resources](../example_plugins/demo-hello-world/core_components/#app-resources)
        6. [Customizing Plugin Icon](../example_plugins/demo-hello-world/core_components/#customize-plugin-icon)
        7. [What are BroadCast Receivers](../example_plugins/demo-hello-world/core_components/#broadcast-receivers)
    3. [Layouts](../example_plugins/demo-hello-world/layouts/)
        1. [Plugin Pane Sizing](../example_plugins/demo-hello-world/layouts/#dropdown-pane-sizing)
        2. [Relative vs Constrain Layout](../example_plugins/demo-hello-world/layouts/#relative-layout-vs-constraint-layout)
        3. [Recycler View](../example_plugins/demo-hello-world/layouts/#breakdown-recycler-view)
    4. [Mapping](../example_plugins/demo-hello-world/mapping/)
        1. [How to Explore ATAK's classes](../example_plugins/demo-hello-world/mapping/#introduction)
        2. [Using Icons](../example_plugins/demo-hello-world/mapping/#icons--3d-models)
        3. [Map Markers/Points](../example_plugins/demo-hello-world/mapping/#markers)
        4. [Zooming on Map](../example_plugins/demo-hello-world/mapping/#map-controls-zoom--tilt)
        5. [Layers an Drawing on Map](../example_plugins/demo-hello-world/mapping/#layers--drawing)
        6. [Routes and Map Movement](../example_plugins/demo-hello-world/mapping/#routes--map-movement)
- [Android Camera Demo Plugin](../example_plugins/demo-android-camera-readme/)
- [Live Camera Feed Demo Plugin](../example_plugins/demo-camera-readme/)
- [CNN Demo with Live Camera Plugin](../example_plugins/demo-cnn/)
    1. [ReadMe!](../example_plugins/demo-cnn/readme/)
    2. [ATAK specifics](../example_plugins/demo-cnn/tak/)
        1. [Listening to Map Events](../example_plugins/demo-cnn/tak/#map-events)
        2. [Storing records using the Map](../example_plugins/demo-cnn/tak/#map-items)
        3. [Sharing information with other TAK users](../example_plugins/demo-cnn/tak/#cot-and-dispatcher)
    3. [Working with Live Camera Data](../example_plugins/demo-cnn/yolo_jni/) 
- [Design and train Tensorflow Lite Models for Android](../example_plugins/tflite-training-readme/)
- [Run a Tensorflow Lite Model in ATAK](../example_plugins/demo-tflite/)
    1. [ReadMe!](../example_plugins/demo-tflite/readme/)
    2. [ML Terminology](../example_plugins/demo-tflite/ml_terminology/)
        1. [TensorFlow vs TensorFlow Lite](../example_plugins/demo-tflite/ml_terminology/#tensorflow-vs-tensorflow-lite)
    3. [Dependencies Required](../example_plugins/demo-tflite/plugin_config_ml/)
    4. [Using the Models](../example_plugins/demo-tflite/use_model/)
        1. [View Model Files](../example_plugins/demo-tflite/use_model/viewing-model-files)
        2. [Generating Wrapper Class](../example_plugins/demo-tflite/use_model/#generating-wrapper-classes)
        3. [Basic Interface](../example_plugins/demo-tflite/use_model/#basic-inference)
        4. [Load Model](../example_plugins/demo-tflite/use_model/#loading-a-model-and-creating-an-interpreter)
        5. [Access Model Functions](../example_plugins/demo-tflite/use_model/#access-model-functions-with-signatures)
        6. [Model Data Format](../example_plugins/demo-tflite/use_model/#model-data-format)
        7. [Retrieving Output](../example_plugins/demo-tflite/use_model/#retrieving-output)
- [Fragments Demo](../example_plugins/demo-fragments-readme/)
- [Plant Classifier](../example_plugins/plant-classifier-readme/)
- [Food Detector Plugin](../example_plugins/food-detector/)
    1. [ReadMe!](../example_plugins/food-detector/readme/)
    2. [Mission Packages](../example_plugins/food-detector/mission_packages/)
        1. [What are Mission Packages?](../example_plugins/food-detector/mission_packages/#what-are-mission-packages)
        2. [Create a Mission Package](../example_plugins/food-detector/mission_packages/#building-a-missionpackagecontent-object)
        3. [Mission Package Functions](../example_plugins/food-detector/mission_packages/#mission-package-functions)
- [Yosemite Decimal System Plugin](../example_plugins/yds-estimator/)
    1. [A Breakdown of the Model's Development](../example_plugins/yds-estimator/ml_development/)
        1. [The problem](../example_plugins/yds-estimator/ml_development/#ml-problem-description)
        2. [The dataset](../example_plugins/yds-estimator/ml_development/#dataset)
            1. [Dataset discussion](../example_plugins/yds-estimator/ml_development/#dataset-discussion)
        3. [Models](../example_plugins/yds-estimator/ml_development/#models)
        4. [Development, Training, Performance, Results](../example_plugins/yds-estimator/ml_development/#development-training-performance-and-results)
        5. [Conclusion](../example_plugins/yds-estimator/ml_development/#conclusions)
- [Insect Classifier](../example_plugins/insect-readme/)

#### Have Questions? Need More Help? 

Checkout the [Common FAQs](../common_issues_and_faqs/)
