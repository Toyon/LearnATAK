---
title: "Install and Run ATAK"
icon: icon/svg/tablet.svg
date: 2023-08-11T15:26:15Z
description: Guide to Dowloading the ATAK app on your android tablet/phone
draft: false
weight: 40
---


*Updated : 14 November 2022*

This section focuses on installing a developer version of ATAK-CIV and running it on your Android device. While there is a [version available on the Google Play Store](https://play.google.com/store/apps/details?id=com.atakmap.app.civ&hl=en_US&gl=US), this version will enable you to develop your own plugins. This section will only cover installing and running ATAK on a physical device.

Requirements:

- Android device (minimum software requirement: Android 5.0 / API level 21 / Lollipop)
- PC with Android Studio

## Setup Android Device

1. Check if your Android device is capable of running ATAK. 
   
   - To check the version of Android installed on your device open up **Settings**. Scroll to the bottom and select **About phone**. Select **Software information** and look at the number for **Android version**. If the number is greater than or equal to 5.0 your device should work.
2. Enable developer options and USB debugging ([official docs](https://developer.android.com/studio/debug/dev-options#enable)). *Only needs to be done once until developer options are disabled.*
   
   - To enable developer options, tap the **Build Number** option 7 times. Using the Android Version you found in the previous step, you can find this option in one of the following locations:
     - Android 9 (API level 28) and higher: **Settings > About Phone > Build Number**
     - Android 8.0.0 (API level 26) and Android 8.1.0 (API level 26): **Settings > System > About Phone > Build Number**
     - Android 7.1 (API level 25) and lower: **Settings > About Phone > Build Number**
   
   - To enable USB debugging, toggle the **USB debugging** option in the Developer Options menu. This allows Android Studio and other SDK tools to recognize your device when connected via USB. You can find this option in one of the following locations:
     - Android 9 (API level 28) and higher: **Settings > System > Advanced > Developer Options > USB debugging**
     - Android 8.0.0 (API level 26) and Android 8.1.0 (API level 26): **Settings > System > Developer Options > USB debugging**
     - Android 7.1 (API level 25) and lower: **Settings > Developer Options > USB debugging**
   
4. Connect your Android device and Enable "USB for file transfer". *Ensure this is enabled every time you reconnect.*

   - Using the provided USB cable with your phone connect the phone to your PC. Typically the Android device will default to enable "USB for file transfer". To double check though swipe down from the top of the Android screen and look for the "Android System" notification that indicates the state of the USB connection. If you don't see "USB for file transfer" selected then tap to set the USB option.

After successfully completing these steps your Android device is now ready to install ATAK and deploy custom plugins.

## Download ATAK-CIV and Install on Android Device (Windows)

1. Navigate to the [ATAK-CIV v4.5.1.13 Release page on GitHub](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/releases/tag/4.5.1.13) and download the [`atak-civ-sdk-4.5.1.13.zip`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/releases/download/4.5.1.13/atak-civ-sdk-4.5.1.13.zip) archive.

2. Select "Extract All" on the downloaded archive to a location on your device you will remember. 
   For example `C:\tak\atak-civ-4.5.1.13`.

3. Plugin your Android device and ensure "USB for file transfer" is enabled

   

   ![Windows File Explorer load ATAK APK onto Android - hugo](image/android_studio/windows_atak_apk_download.png)

   ![Windows File Explorer load ATAK APK onto Android - local](../../../assets/image/android_studio/windows_atak_apk_download.png)

   *Refer to this image for steps 4, 5, & 6.*

   

4. Open your PC's *File Explorer* to your extracted ATAK download resources

5. Open another instance of your PC's *File Explorer* to the Android's file system *download*  folder

   - In the *left navigation pane* click on `This PC > [YOUR_DEVICE_NAME]`, then using the primary *File List* navigate to `Internal Storage > Download`

6. Click & Drag the `atak.apk` file from the ATAK resource folder to the Android `Download` folder to copy the APK onto the Android device

7. On your Android device, open the File Explorer on your device (Samsung devices call it "My Files")

8. Go to `Internal Storage > Download` on your Android device and tap on the `atak.apk` item to install ATAK with the *Package Installer* on your phone.

   - If your device doesn't successfully install the application from the Android Package Kit (APK) and displays a notification with a message like the one below then check out the **[Third-Party App Installs](#Third-Party-App-Installs)** section.
     *"For your security, your phone is not allowed to install unknown apps from this source"* 

You can now open ATAK on your Android device. On first launch follow the prompts and ensure to provide all permissions the application requires for optimal performance.

## Third-Party App Installs

By default all applications on your Android device will have the *"Install unknown apps"* capability disabled. This protects users from inadvertently downloading and installing Potentially Harmful Apps (PHA) from locations other than a first-party app store, such as Google Play. The APK from the GitHub is a trusted source as it is under the [US Department of Defense](https://github.com/deptofdefense) organization, but you can verify the APK with a service like [VirusTotal](https://www.virustotal.com/gui/home/upload). Perform one of the following procedures below to enable installation of the ATAK-CIV application:

- On devices running Android 8.0 (API level 26) and higher, users must navigate to the *Install unknown apps* system settings screen to enable app installations from a particular source.
- On devices running Android 7.1.1 (API level 25) and lower, users must either enable the **Unknown sources** system setting or allow a single installation of an unknown app.

For more information from the official docs about installing unknown apps check out this [link](https://developer.android.com/studio/publish#publishing-unknown).
