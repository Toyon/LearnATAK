# Develop / Run ATAK Plugins

*Updated: 12 December 2022*

This section will cover how to run an existing ATAK Plugin and how to create your own ATAK Plugin from scratch (the provided template).

[TOC]

------



## Deploying / Running an ATAK Plugin

In order to deploy or run an ATAK plugin on your Android device you must have the following:

- Source code for the ATAK plugin you wish to install
- Android Studio setup on your device ([Completed "Setup for ATAK Development"](./2_Android_Studio_Setup.md))
- ATAK installed on your Android device ([Completed the section "Download ATAK-CIV and Install on Android Device"](./5_Run_ATAK.md#Download_ATAK-CIV_to_Android_Device_and_Install))
- ATAK SDK downloaded on your development machine ([Part of "Download ATAK-CIV and Install on Android Device"](./5_Run_ATAK.md))

Deploying and running ATAK plugins are generally particular to their location on your file system. What this means is that the template which most plugins are developed from require that the plugin's root folder is two directories below the SDK location. The following outlines how to structure your ATAK-CIV folder with additional plugins you wish to run. It does not matter what folder name you use to hold all of your plugins as long as it is `plugin` container folder placed at the to the `main.jar`.

```python
atak-civ
|-- atak-javadoc
|-- espresso
|-- learnatak   		# if you downloaded this repo place it here to use the demo plugins
	|-- demo-ai
	|-- demo-camera
	|-- demo-hello-world
	|-- doc
	└-- README.md
|-- plugin-examples
	|-- helloworld
	|-- plugintemplate
|-- plugins
	└-- <YOUR PLUGIN>	# your own plugins will go here 
|-- atak.apk
|-- ATAK_Plugin_Development_Guide.pdf
|-- ATAK_Plugin_Structure_Guide.pdf
|-- atak-gradle-takdev.jar
|-- atak-javadoc.jar
|-- LICENSE.txt
|-- main.jar
|-- mapping.txt
└-- VERSION.txt
```

When your desired plugin to run is properly located in your downloaded ATAK-CIV SDK directory, and you have the pre-requisites completed you can proceed to the following steps to deploy the plugin on your Android device.

1. Open the Android plugin we want to run in Android Studio 
   A little Android Icon should be displayed in front of the root folder name to open the plugin as a project. For this demonstration we will open the `atak-civ/plugin-examples/helloworld` folder as an Android Studio Project.

   
   *If this is the first time you are attempting to run the program it is expected that the Gradle build will fail since there is a syntax issue in a `build.grade` file and the project requires signing keys in order to build successfully. Follow Steps 2 - 4 if the project fails to build.*
   
2. Open the `<PLUGIN-NAME>/app/build.gradle` file by set the *Project Files* view to "Project", expand the `app` directory and click the `build.gradle` file.
   On line 16 there should be a line of code that we need to change since the `def` function is not properly scoped to be used later in the build script.

   ```groovy
   // app/build.gradle 
   // Original function signature
   def getValueFromPropertiesFile = { propFile, key ->
       
   // New function signature
   ext.getValueFromPropertiesFile = { propFile, key ->
   ```

3. Build the application signing keys which are required by the Android Operating System (OS) for security when installing software packages.
   At the bottom of the IDE there should be a *Terminal* tab you can open to launch a terminal session in the root folder of the plugin. 

   ```sh
   # Run the following commands in your Android Studio Terminal
   # Generate Debug signing key: set "alias", "keypass", and "storepass" flag values as desired
   keytool -genkeypair -dname "CN=Android Debug,O=Android,C=US" -validity 9999 -keystore debug.keystore -alias androiddebugkey -keypass android -storepass android 
   
   # Generate Release signing key: set "alias", "keypass", and "storepass" flag values as desired
   keytool -genkeypair -dname "CN=Android Release,O=Android,C=US" -validity 9999 -keystore release.keystore -alias androidreleasekey -keypass android -storepass android 
   ```

4. Edit the `<PLUGIN-NAME>/local.properties` file to add the following lines. 
   `<ANDROID_SDK_PATH>` and the `sdk.dir` should already be filled out by the IDE with the default Android SDK file path
   `<ABSOLUTE_PLUGIN_PATH>` should be a complete file path to the root plugin folder;
    example plugin path: `C\:\\tak\\atak-civ-sdk-4.5.1.13\\atak-civ\\plugin-examples\\helloworld` 

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

5. Open the *Run Configurations* dropdown menu and select "Edit Configurations". A dialog like the one shown below should appear.
   The menu is located in the upper right toolbar to the left of the *Target Device menu* and the play button.

   ![Android Studio Plugin Run Configuration](../img/atak_plugin_config_android_studio.png)

6. Set `Launch Options > Launch` dropdown selector to the value "Nothing" and press the *Apply* button.
   See the figure above for an example

7. If not already done, connect your Android device to your PC and ensure "USB for file transfer" is enabled. 

8. If not already done, launch the the ATAK Application.

9. Click the *Play* button in Android Studio and check the "Plugins" menu in the side drawer and if the plugin name should be listed like the image below shows the "Hello World Tool".

   ![Hello World Tool loaded in ATAK](../img/hello_world_tool_plugin_loaded.png)

------



## Creating your own Plugin: First Steps

Prior to making a plugin you must have the following:

- Android Studio setup on your device ([Completed "Setup for ATAK Development"](./2_Android_Studio_Setup.md))
- ATAK SDK downloaded on your development machine ([Part of "Download ATAK-CIV and Install on Android Device"](./5_Run_ATAK.md))

The following file structure highlights the key areas we will be working within the ATAK-CIV downloaded files. For the remaining steps we will use the notation `<MY-PLUGIN>` to indicate where to place your plugin name. It is best not to have spaces in this name and use either `_`, `-`, or `CamelCase` to distinguish the words in the name of your plugin if it is more than one word.

```
atak-civ
|-- plugin-examples
	└-- plugintemplate
|-- plugins
	└-- <MY-PLUGIN>	 
...
```

Follow the steps below as a checklist of items every time you start developing a new plugin.

1. If not already done, create a `plugins` folder in the `atak-civ` folder.

2. Copy the `plugin-examples/plugintemplate` folder and paste the copy in the your custom `plugins` folder.
   You should now have a `/plugins/plugintemplate` folder at this point.

3. Rename the `/plugins/plugintemplate` folder to `plugins/<MY-PLUGIN>`

4. Open `plugins/<MY-PLUGIN>` in Android Studio.

5. Follow [steps 2 - 4 in the Deploying / Running an ATAK Plugin](#Deploying-/-Running-an-ATAK-Plugin) to setup your plugins signing keys to allow it to build successfully.

   ![ATAK Plugin Template File Changes](../img/plugin-template-change-files.png)

   *Use the figure above as a reference for key files / packages that need to be changed to match your plugin*

6. In order to change your plugins package name to match your plugin name with the *Project Files* window open on the *Android* view. Click the gear *options* menu and under "Tree Appearance" make sure the "Compact Middle Packages" option is deselected.

7. Right click the `plugintemplate` package highlighted highlighted in the figure above and select `Refactor > Rename` and edit the name of the package. Best conventions is to use all lower case for the package with no underscores or dashes. Click the *Refactor* button for the changes to apply.

8. Everything except for the `AndroidManifest.xml` file (highlighted in the figure above) will be updated to use the new package. On line 3 edit the package name to reflect the new path.

   ```xml
   package="com.atakmap.android.<MY-PLUGIN>.plugin"
   ```

9. Now go to the `res/values/strings.xml` file and edit the `"app_name"` and `"app_desc"` to accurately reflect your plugin name and a description of what your plugin does.

10. At this point you should now be able to follow [steps 5- 9 in the Deploying / Running an ATAK Plugin](#Deploying-/-Running-an-ATAK-Plugin) to view your plugin in ATAK.

Optionally you will probably want to `Refactor > Rename` the following classes to match your plugin name to help ensure your project code files make sense. When renaming it is best practice to use CamelCase and only alter the sections of the name that match `PluginTemplate` as the last part of the class name corresponds to the ATAK Plugin component.

```
com.atakmap.android.<MY-PLUGIN>
|-- plugin
	|-- PluginTemplateLifecycle
	└-- PluginTemplateTool
|-- PluginTemplateDropDownReceiver
└-- PluginTemplateMapComponent
```

We will describe what these key components do to help you understand where to begin writing code for your plugin, but we recommend you checkout the `ATAK_Plugin_Structure_Guide.pdf` file in the ATAK-CIV download resources.

### ATAK Plugin Tool

Class: `plugin/PluginTemplateTool`
This is an optional component which is an entry in the ATAK toolbar. It allows the user to add an entry that can be used to perform and action on click such as launch the plug-in user interface (UI).

### ATAK Plugin Lifecycle

Class: `plugin/PluginTempalteLifecycle`
This is the main plugin entry point as it handles ATAK lifecycle callbacks and is used to load and initialize the main plug-in components like the `MapComponent`. Analogous to Android's `Application` class.

### ATAK Plugin MapComponent

Class: `PluginTemplateMapComponent`
This is the main component for the plug-in an is the building block for all activities within the system. This component sets up other UI components (such as `DropDownReceivers`), preferences and other high-level components. Analogous to an Android `Activity` class.

### ATAK Plugin Dropdown Receiver

Class: `PluginTemplateDropDownReceiver`
This is usually a container (usually a side panel) that can contain any standard Android Layout. Analogous to an Android `Fragment` class.