---
title: "UI Widgets Tutorial"
icon: icon/svg/widget.svg
date: 2023-08-11T15:26:15Z
description: "A tutorial on the different items you can add to your UI structures (ie: text, buttons, images)"
draft: false
weight: 92
---

This document covers the different widgets we can add to our layouts. Dozens of unique widgets exist. In this tutorial we will cover the most important widgets as well as widgets that are used in the [Example Plugins](../../example_plugins/). 


## Background on the Drawable and Color Folders 

The images and icons that we want to use in our layouts will be stored in the `/app/res/drawable/` folder as `.png` files. Format the names of images as `ic_<name>`. For example, you will find `ic_camera.png` in plugins that use the camera. 

In this folder, we can also create our own background color gradients and shapes in the form of `.xml` files. For example, in the [`Plant Classifier Plugin`](https://github.com/Toyon/LearnATAK/-/tree/main/demo-plant-classifier) one of the backgrounds used is the are the [popup_background.xml](https://github.com/Toyon/LearnATAK/tree/master/demo-plant-classifier/app/src/main/res/drawable/popup_background.xml) which contains a rectangular shape that has a color gradient. Feel free to use that file as a template for creating your own background gradients. These colored rectangles can be used as button backgrounds, text backgrounds, or layout backgrounds.

If you are interested in using specific solid colors across your app, you can define them in the `/app/res/values/color.xml` file. Here is an example of the file from the plugin template:

```xml 
<?xml version="1.0" encoding="utf-8"?>
<resources>
    
    <!-- COMMON -->
    <color name="white">#ffffff</color>
    <color name="darker_gray">#121212</color>
    <color name="transparent">#00FFFFFF</color>

    <!-- LED -->

    <!-- PRESET -->

</resources>
```

You can create a new color with the following template: 
```xml
<color name = "color name"> #hex_value</color>
```

Next we'll dive into the different types of widgets we can add. 

## Text widgets

### TextView

[TextView](https://developer.android.com/reference/android/widget/TextView) is an element that allows text to be displayed to the user on your layout. This element can't be edited by the user. Here is an example of a TextView that displays "Hello World" to the screen.

```xml
    <TextView
        android:id = "@+id/our_text_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@drawable/my_color_gradient"
        android:text=" Hello World"
        android:textSize="20dp"
        android:textColor="@color/white"
        android:layout_marginTop="15dp"/>
```

Here are some useful attributes to know when working with TextView: 

- `android:textSize` sets the size of the text. It takes in a string and must have `dp` or `sp` at the end. 
- `android:background` can be used to set the text background to a certain color. If you want to use a custom color gradient from the `drawable` folder you can access it with `@drawable/` in front of the file's name. 
- `android:textColor` sets the color of the text. In the example above we used the custom color white that we defined in our `/app/res/values/color.xml` file. You can access your other colors in this file by placing `@color/` in front of the color's name.  

To access your TextView and modify it, use the id: `our_text_view` in the `findViewById` method of `DropDownReceiver`. 


```java
View paneView = PluginLayoutInflater.inflate(context, R.layout.home_page, null); 
TextView myText = paneView.findViewById(R.id.our_text_view);
myText.setText("Adding Text!");
```

### EditText 
[EditText](https://developer.android.com/reference/android/widget/EditText) is a text element used for user input. When you want information that needs to be entered by the user (e.g. a name), use this widget: 

```xml
    <android.widget.EditText
        android:id="@+id/enter_name_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:hint=" Please enter your name here "/>
```
The `android:hint` parameter prompts to the user what to input in the blank text box. Upon the cursor being placed in the box, "Please enter your name here" will disappear. 

To retrieve a user's input in a DropDownReceiver, you can call `getText()` on your EditText object: 

```java
EditText user_name = ourView.findViewById(R.id.enter_name_text). 
String name = user_name.getText().toString()
```


### AutoCompleteTextView 
[AutoCompleteTextView](https://developer.android.com/reference/android/widget/AutoCompleteTextView) is an editable text view that takes user input and shows completion suggestions as the user is typing. An example usage is the autocomplete function on Google searches. 

AutoCompleteTextView's implementation looks very similar to EditText in xml layout file. To get autocompletion suggestions to appear, in the appropriate DownDropReceiver we must setup an ArrayAdapter that contains all of the possible search results. Then we attach the ArrayAdapter to our AutoCompleteTextView instance. 

In the below example, we set completion suggestions to several countries. If a user goes to use our text and types in "Be", Belgium will appear as a result. 

```java

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                 android.R.layout.simple_dropdown_item_1line, COUNTRIES);
    AutoCompleteTextView textView = (AutoCompleteTextView)
                 findViewById(R.id.countries_list);
    textView.setAdapter(adapter);

    private static final String[] COUNTRIES = new String[] {
         "Belgium", "France", "Italy", "Germany", "Spain"
     };
 }
 ```

## Images 

### ImageView

[ImageView](https://developer.android.com/reference/android/widget/ImageView) displays Bitmaps or images stored in the `Drawable` folder. Here's an example of implementing ImageView with a PNG icon in the `Drawable` folder. 

```xml 
    <ImageView
        android:id = "@+id/our_image"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:background="@drawable/ic_icon"
        android:backgroundTint="@color/white"/>
```
Let's say `ic_icon` is a circle outlined in black. If we wanted to change the circle to be white, we would set the ImageView's background to our icon and then set the backgroundTint to white.  

Let's say we were using `ic_icon` as a placeholder for an image capture with the device's camera through the app. Here's how we could change the ImageView's background in a DropDownReceiver:

```java
Bitmap photo; //our photo taken by camera
ImageView ourImage = view.findViewById(R.id.our_image); 
ourImage.setImageBitmap(photo);
```

Android's camera returns photos in the form of `Bitmap`, which is why we use `setImageBitmap` on ImageView to change the photo that's displayed on the screen. 


## Buttons 

### Button

A [Button](https://developer.android.com/reference/android/widget/Button) is an element a user can press to perform an action. Below is an example of how to set up a button with an icon as the background. If you do not set `android:background` to an image, the default button shape is a rectangle. You can also set `android:text` if you would like your button to say something. The following button is book shaped. 
```xml

    <Button
        android:id = "@+id/myButton"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:background="@drawable/ic_book"
        android:backgroundTint="#D3FF5722"
        android:layout_marginTop="15dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btn_search_image"/>
```

To make your button perform an action, you need to set up a listener in the DropDownReceiver that's attached to the button's xml file. We use the `setOnClickListener` that `listens` for the button to be pressed. Once the button is pressed we can perform an action. 

```java
Button newButton = paneView.findViewById(R.id.myButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){ 

                Intent mainIntent = new Intent();
                mainIntent.setAction("MYACTION");
                AtakBroadcast.getInstance().sendBroadcast(mainIntent);
            }
        });
```

### Radio Buttons 

[Radio Buttons](https://developer.android.com/reference/android/widget/RadioButton) are buttons that can either be checked or unchecked. You've interacted with radio buttons if you've ever answered a multiple-choice question on a computer. These buttons are normally use together in a [RadioGroup](https://developer.android.com/reference/android/widget/RadioGroup). When several Radio Buttons are used inside of a RadioGroup, checking one radio button unchecks all of the other buttons. This widget is useful when you want a user to choose only **one** option out of a group of options. An example can be found in [Demo Hello World's Core Components](../../example_plugins/demo-hello-world/core_components/#breakdown-radio-group--radio-buttons) section.



## Overview

The 3 categories discussed here provide a foundation for setting up layouts. [Here](https://developer.android.com/reference/android/widget/package-summary#classes) you can find a list of all of the widgets that can be used in Android Studio. 