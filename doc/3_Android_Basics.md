# Android Quick Start Fundamentals

*Updated 14 November 2022*

This section will introduce some common Android components and concepts that will help you quickly start understanding existing ATAK Plugins and wriiting your own. The [Android developer website docs](https://developer.android.com/docs) cover all topics discussed in this section in greater detail and will stay up to date with the latest changes; however, ATAK plugin development is not like the traditional "Build your first app" example. In this section we will try to guide you to the essential pages within the developer docs to cover to provide a foundation for a basic understanding of Android principles.

- [Official Android Developer Guides](https://developer.android.com/guide)

[TOC]

___

## 1. Application Fundamentals

The [Official Android App Fundamentals](https://developer.android.com/guide/components/fundamentals) is a good page to start with for a quick overview of core framework components. The list below are components and concepts to have some familiarity of as well before moving on to developing and ATAK Plugin. It will also help you better understand what and ATAK Plugin is and how it works with the core ATAK application.

> TODO: continue to update this list with more relevant material as we develop examples and other documentation and want to ensure we highlight key areas.

### User Interface 

#### 1.1 [Activities](https://developer.android.com/guide/components/activities/intro-activities) & [Activity Lifecycle](https://developer.android.com/guide/components/activities/activity-lifecycle)

Activities are the entry point for an application's interaction with the user. It is the single screen with a user interface presented to the user. Each Activity instance transitions through different states in their lifecycle as a user navigates to and out of an application. Properly implementing callbacks for lifecycle state changes will help avoid undesirable behaviors like your app crashing, consuming system resources when the app is not actively in use, losing user progress when returning to the app, or crashing on screen rotations. Although devices have gotten more computational power, the Android operating system still manages the systems memory and resources for optimal user experience when using an application. You don't want your application being slowed down when it is being used because another app was developed poorly. There is a lot of information to digest when it comes to these two topics if you have never learned about them in the past, but it is important to understand when it comes to your own app / plugin design decisions.   

####  1.1.1 [View Models ](https://developer.android.com/topic/libraries/architecture/viewmodel)

A fairly straight forward class to provide data persistence to enahnce your activities by caching data through UI changes such as screen rotations.

#### 1.1.2 [Fragments](https://developer.android.com/guide/fragments) & [Fragment Lifecycle](https://developer.android.com/guide/fragments/lifecycle)

Another enhancement to your application user interface would be the utilization of Fragments to have multiple displays contained within a single activity. If you need a more dynamic UI with something like tab views this is an important UI element.

### 1.2 [Services](https://developer.android.com/guide/components/services) & [Service Lifecycle](https://developer.android.com/guide/components/services#Lifecycle)

There are a variety of different types of services, but they all essentially provide a mechanism for your application to perform long-running operations in the background as they do not provide a user interface element. They are generally more resiliant to users navigating between applications as their lifecycle is deteremined by other factors. Understand the difference between Foreground, Background, and Bound services weighing the pros and cons of using each one.