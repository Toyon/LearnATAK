---
title: "Fragments Tutorial"
icon: icon/svg/fragment.svg
description: A guide on how to create multi-view and/or multi-paged layouts
date: 2023-08-11T15:26:15Z
draft: false
weight: 100
---



This document provides a background on fragments and how to use them in an ATAK plugin. If you haven't yet, check out the UI layouts tutorial [here](../ui_layout_tutorial/) as Fragments build upon concepts described there. 

Fragments represent a reusable section of your UI design by letting you divide your UI into discrete chunks. Fragments can't live on their own and must be hosted by an activity, DropDownReceiver in ATAK, or another fragment. The fragment's view hierarchy is thus attached to the host's view hierarchy. In other words, the activity's layout acts as the host view or root view and contains the Fragment's ViewGroup as a child element. Fragments define and manage their own layouts, have their own lifecycles, and handle their own input events, but once the activity is stopped the fragments hosted by the activity are destroyed. 

For an example of how fragments are used, let's say we wanted to recreate Instagram's UI layout. We would need one activity that hosts several fragments. Our activity would contain a navigation bar and a section for the Fragment views, and each button on the navigation bar (Home, Search, New Post, Reels, Profile) would correspond to a different fragment within the same activity. If we didn't use Fragments, we would need 5 different activities to run and load each new screen, which is an inefficient use of computing resources. Instead we are re-using a singular UI design to display 5 different child views.  

For our tutorial, we will be creating a demo plugin for ATAK. 
Our plugin contains 3 fragments a user can swipe through:
1. A home page of buttons to different activities
2. A camera roll that contains photos we've taken on the plugin
3. A color changing page that allows a user to change the background color of the other two pages

The code for the tutorial can be found in [Demo Fragments](https://github.com/Toyon/LearnATAK/-/tree/main/demo-fragments). We recommend loading this plugin onto ATAK and playing around with the features before we dive into its code. Even though the Buttons Pages has six buttons, only the camera button has logic set up behind it in order to minimize the complexity of this tutorial. So don't worry if the other buttons don't do anything -- they are there just for demonstration purposes! 

___
<br>

# Tutorial 

First, we must add this dependency to our app's `build.gradle` file:

```groovy
dependencies {
    implementation ('com.google.android.material:material:1.6.1'){
        exclude module: 'collection'
        exclude module: 'core'
        exclude module: 'lifecycle'
        exclude module: 'core-common'
        exclude module: 'collection'
        exclude module: 'customview'
    }
}
```
> *NOTE*: You shouldn't use a higher version than 1.6.1 of com.google.android.material because it has bugs that will cause your plugin to fail. We exclude these modules because ATAK already includes them in other packages and the plugin's build may fail if we include them twice. 

Let's first create our [main_layout.xml](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/res/layout/main_layout.xml) which will be the host of our fragments. 
In our demo, we are using a LinearLayout as the root ViewGroup, but you can use any ViewGroup that suits your needs.
Here is a minimal UI that includes a title for our plugin: 

```xml 
<?xml version="1.0" encoding="utf-8"?>
<android.widget.LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@+id/header_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="\nFragments Demo"
        android:textSize="25dp" />

</android.widget.LinearLayout>
```

Next we will cover the widgets we'll be adding to this layout. 


## Creating the swipe views with tabs 

In the Demo Fragments plugin, we can navigate to each fragment either by swiping left/right or by clicking on each tab. In order to accomplish these functionalities we need `ViewPager2` and `TabLayout` in our [DemoFragmentsDropDownReceiver's](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/java/com/toyon/demofragments/DemoFragmentsDropDownReceiver.java) [main_layout.xml](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/res/layout/main_layout.xml). 


1. #### ViewPager2
    [ViewPager2](https://developer.android.com/jetpack/androidx/releases/viewpager2) allows us to navigate between each fragment with a single swipe. You can think of ViewPager2 as the `container` that holds the fragments and determines which one will be displayed upon swiping. We will need to add the following code inside our LinearLayout ViewGroup in order to create this container.

    ```xml 
            <androidx.viewpager2.widget.ViewPager2
                    android:id="@+id/viewPager"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_weight="0.8"/>
    ```


2. #### TabLayout
    [TabLayout](https://developer.android.com/reference/com/google/android/material/tabs/TabLayout) provides a way to display tabs horizontally. It is essentially a container which holds individual `Tabs`. When paired with our ViewPager2 container, the TabLayout provides a convenient interface for navigating between pages. We'll add the following code inside our LinearLayout and above our ViewPager2 container that creates the "container" which will hold our tabs. We set the layout_weights for ViewPager2 and TabLayout to "0.8" and "0.05" respectively, but feel free to set them to whatever suits your UI needs.

    ```xml
    <com.google.android.material.tabs.TabLayout
        android:id="@+id/tabLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="0.05"/>
    ```


To use TabLayout, we **MUST** add `android:theme="@style/Theme.AppCompat"` to the root ViewGroup, otherwise the app will crash. 

Our final main xml file will look like: 

```xml 
<?xml version="1.0" encoding="utf-8"?>
<android.widget.LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:theme="@style/Theme.AppCompat"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@+id/header_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="\nFragments Demo"
        android:textSize="25dp" />

    <com.google.android.material.tabs.TabLayout
        android:id="@+id/tabLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="0.05"/>

    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="0.8"/>

</android.widget.LinearLayout>
```
___
<br>

## Swiping between Fragments with an Adapter

In order to determine which Fragment should appear upon swiping, we must attach ViewPager2 to a `FragmentStateAdapter`. Our class is called [ViewPagerAdapter](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/java/com/toyon/demofragments/fragments/ViewPagerAdapter.java) and can be found in the [fragments](https://github.com/Toyon/LearnATAK/-/tree/main/demo-fragments/app/src/main/java/com/toyon/demofragments/fragments) folder of the plugin.

ViewPagerAdapter extends FragmentStateAdapter and must override 2 methods:

1. ``` public Fragment createFragment(int position) ```

    This method contains logic for determining which Fragment view is displayed upon swiping. In our example, we use if/else statements to determine which fragment to return, but you can use a switch statement or similar logic. Each conditional must return a new instance to the Fragment class of your choosing. 

    The variable `position` has values from 0 --> N-1 where N is the number of pages that can be swiped through. In demo fragments, we have 3 fragments that can be swiped through, so we will have position equal to 0, 1, or 2. 0 corresponds to the first/leftmost page and 2 corresponds to the last/rightmost page. 

    Our implementation looks like this: 

    ```java
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ButtonFragment(receiver);
        }
        else if(position == 1) {
            return new PhotosFragment(receiver);
        }
        return new ColorChangingFragment(receiver);
    }
    ```
    ButtonFragment will be the first page shown, then PhotosFragment after swiping, followed by ColorChangingFragment after swiping again. 
    When returning a Fragment, make sure you return a `new` instance of that Fragment. 


2. `public int getItemCount()` 

    This function determines how many pages will "appear"/how far you can swipe through. Since we only want to show 3 Fragments, we will return 3 for this function. If we wanted a more dynamic layout, we could return a variable instead of a specific number. 

Our Adapter also has a constructor `public ViewPagerAdapter(FragmentActivity fragmentActivity, DemoFragmentsDropDownReceiver demoFragmentsDropDownReceiver)` which takes in two objects of type FragmentActivity and DemoFragmentsDropDownReceiver. The FragmentActivity object is passed into FragmentStateAdapter's constructor while the DemoFragmentsDropDownReceiver object is passed into the creation of each Fragment. Later we will discuss why we need to pass this object to each Fragment. 

___
<br>

### Connecting ViewPagerAdapter to ViewPager2 in our DropDownReceiver

In a similar way to how we connect RecyclerViews to their Adapters, we must connect our ViewPager2 to its adapter in DemoFragmentDropDownReceiver. 
First, we'll create a ViewPagerAdapter: 

```java 
ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter((FragmentActivity) mapView.getContext(), this);
```
Since we're in a DropDownReceiver, we must cast mapView's context to type FragmentActivity because we can't access FragmentActivity directly like we can in an Android Activity. 
Then we'll set our ViewPager2 from `main_layout` to our adapter. 

```java
View  templateView = PluginLayoutInflater.inflate(context, R.layout.main_layout, null);
final ViewPager2 viewPager2 = templateView.findViewById(R.id.viewPager);
viewPager2.setAdapter(viewPagerAdapter);
```
Once we create our Fragments, we will have a swipeable UI in-between the 3 pages.  

___
<br>

## Adding Tabs Programmatically 

Now that we have a finished main_layout, we can create tabs to add to our TabLayout. 
In [DemoFragmentsDropDownReceiver](https://github.com/Toyon/LearnATAK/-/tree/main/demo-fragments/app/src/main/java/com/toyon/demofragments/fragments) we create Tabs by calling `newTab()` on our TabLayout object, and then call `addTab()` to add them.
The below code adds 3 new tabs to our TabLayout and sets a title for each one: 

```java
final TabLayout tabLayout = templateView.findViewById(R.id.tabLayout);
tabLayout.addTab(tabLayout.newTab().setText("Buttons Page"));
tabLayout.addTab(tabLayout.newTab().setText("Photo Album"));
tabLayout.addTab(tabLayout.newTab().setText("Color Changer"));
```

We could replace setText() with `setIcon(Drawable icon)` to use a PNG as an icon instead of text. 

Next, we need to connect our TabLayout to an `OnTabSelectedListener()` so that when a user presses a tab, the UI switches the fragment that is displayed. Below is the code that sets up this listener and action. The ViewPagerAdapter is called and its position is set to the tab's position, so that the Adapter knows to change the Fragment that's displayed.   

```java
tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager2.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
});
```

We will also add logic that changes which tab is highlighted when our ViewPagerAdapter switches fragments using `registerOnPageChangeCallback`:

```java
viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
    @Override
    public void onPageSelected(int position) {
        tabLayout.selectTab(tabLayout.getTabAt(position));
    }
});
```
___

<br>


## Creating a Fragment 

For each page we want displayed, we need to create a class that extends Fragment and connects the class to a UI layout (i.e. a new xml file) that will be displayed in our ViewPager2. Our Fragment class needs to override 2 methods: 
1. ` public void onCreate(Bundle savedInstanceState)` 
This calls the super.onCreate() function as shown below.
2. `public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)` 
This returns the actual view we want to display. This view can have any type of layout depending on your use case.

An **important** note is that when inflating the view we want to return, we have to call `.from(pluginContext)` before we can call `inflate()`. Otherwise the Fragment won't be able to find the xml layout to display. This is because Fragments have their own lifecycle separate from our plugin's lifecycle. However, we can use our Plugin's context to let the Fragment's lifecycle know where to find the view to display. We can retrieve the pluginContext from our DemoFragmentsDropDownReceiver by calling a getter method in the DropDownReceiver: 

```java
public Context getPluginCtx() {
    return pluginContext;
}
```

Here is the example of a simple Fragment class connected to `my_fragment.xml` layout: 

```java
public class MyFragment extends Fragment {

    public View view;
    public Context pluginContext;


    public MyFragment (DemoFragmentsDropDownReceiver demoFragmentsDropDownReceiver){
        pluginContext = demoFragmentsDropDownReceiver.getPluginCtx();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.from(pluginContext).inflate(R.layout.my_fragment, container, false);

        return view;
    }
}

```
As you can see above, we get the pluginContext from DemoFragmentsDropDownReceiver in our constructor and then return the view we want to display in `onCreateView`. 


The 3 fragments created for the Demo Fragments plugin can be found in the [source fragments folder](https://github.com/Toyon/LearnATAK/-/tree/main/demo-fragments/app/src/main/java/com/toyon/demofragments/fragments) along with the ViewPagerAdapter. We recommend you store your Fragments in a folder to keep them organized. 

1. ### Buttons Fragment 

The [Buttons Fragment](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/java/com/toyon/demofragments/fragments/ButtonFragment.java) is connected to the layout [button_fragment](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/res/layout/button_fragment.xml) which contains 6 buttons organized in a GridLayout. Only the Camera button works for the sake of this tutorial. When you take a picture with the camera, it will be saved into a database and displayed on the next page that's titled `Photo Album`, which is controlled by the Photos Fragment. 

Databases provide an easy way to store information that can be shared across fragments. If you're interested in implementing a database check out the document [Room Database with ATAK](../../atak_development/room_database_with_atak/). We create one instance of our ViewModel in DropDownReceiver and implement an associated getter method so that each one of our Fragments can access the database.

`onCreateView` has two listeners: one is for tapping the camera icon to take a photo, and the other tracks when the value of an ObservableInteger called `color` changes. The latter listener automatically change the fragment's background color based on the color submitted in the Color Changing Fragment. We discuss how to set up this custom listener later. 

2. ### Photos Fragment 

The [Photos Fragment](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/java/com/toyon/demofragments/fragments/PhotosFragment.java) is connected to the layout [photo_fragment](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/res/layout/photo_fragment.xml) which contains a RecyclerView that displays a `camera roll` that shows photos taken in the plugin. It also contains the listener for the color change. The RecyclerView is connected to an Adapter, [PhotosAdapter](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/java/com/toyon/demofragments/fragments/PhotosFragment.java), to update its layout. Just like PlantClassifier's [CategoryAdapter](https://github.com/Toyon/LearnATAK/tree/master/PlantClassifier/app/src/main/java/com/toyon/plantclassifier/adapters/PlantCategory.java) it uses a LiveData Observer to listen for dataset changes and update the RecyclerView. For more information on working with LiveData, check out the example found in the [Room Database with ATAK](../../atak_development/room_database_with_atak).  

When setting up RecyclerViews in a Fragment, you **must** set the RecyclerView's layout to a fixed size: `recyclerview.setHasFixedSize(true)`. You must also set the RecyclerView's `layout_height` in the xml file to `match_parent`. Otherwise, when the photo's table gets updated in the database (e.g. deleting a picture), the RecyclerView is redrawn to a smaller size, which may mess up the other fragment's layouts.  

3. ### ColorChanging Fragment 

The [ColorChanging Fragment](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/java/com/toyon/demofragments/fragments/ColorChangingFragment.java) is connected to the layout [color_changing_fragment](https://github.com/Toyon/LearnATAK/tree/master/demo-fragments/app/src/main/res/layout/color_changing_fragment.xml) which contains a Color Wheel, the Hex/RGB color chosen by the user, and a button to apply the color to the other Fragments. 

In order to make the color wheel a functioning color picker, we apply three functions to our Image: `colorWheel`:

1. `colorWheel.setDrawingCacheEnabled(true);`
2. `colorWheel.buildDrawingCache(true);` 
3. `colorWheel.setOnTouchListener(new View.OnTouchListener(){});`

The `setOnTouchListener` waits for touch/an action to be performed on the image. 

Here is our implementation of the `OnTouchListener`: 

```java
colorWheel.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE ){
            newColor = colorWheel.getDrawingCache();
            try {
                int pixels = newColor.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());
                red = Color.red(pixels);
                green = Color.green(pixels);
                blue = Color.blue(pixels);

                String hex = "#" + Integer.toHexString(pixels);
                colorView.setBackgroundColor(Color.rgb(red, green, blue));
                hexText.setText("HEX: " + hex);
                rgbText.setText("RGB: " + red + ", " + green + ", " + blue);
            } catch (Exception e){
                Log.e("Color Changing error", String.valueOf(e));
            }
        }
        return true;
    }
});
```

Here is how the above code works: 
1. When MotionEvent detects movement, getDrawingCache() is called on our image to retrieve the last location of movement.
2. Fetch the X and Y coordinates from this location to get the corresponding pixel.
3. Extract the red, green, and blue values from that pixel to get the color. 

If a user taps the button to apply the color, then the static ObservableInteger `color`, which exists in both ButtonFragment and PhotosFragment, will be updated to the new color. Since the listener has received a change in this integer, the background colors will then be updated. 

## Creating Custom Listeners 

There are many advantages to creating a custom listener. In our case, we cannot directly tell the Button and Photo Fragments to change their view's background colors because they cannot directly communicate with each other. Instead, we can have our ColorChanging Fragment change the value of a static object that exists in the other fragments. Upon that change the other fragments can then perform an action such as changing their background color. Here are the steps needed to create a custom listener: 

1. Create an interface with methods that will be called where there is a change in data. We use interfaces as these methods can be overwritten and customized for any use case. In our case, we have an interface called `OnIntergerChangeListener` that has a function `public void onIntegerChanged(int newValue);` which we activate to change the background color. 

2. We have to create a class that will contain an instance of our interface and an integer that will store our color's value. The class has two functions: 
    1. `setOnIntegerChangeListener(OnIntegerChangeListener listener)`
        This function takes in an instance of our interface and is where methods defined in our interface are overridden in the method's parameters (shown below).
    2. `set(int value)` 
        This function sets the integer to the new color value, then calls our interface instance to execute the function we overrode in `setOnIntegerChangeListener` 


The class(`ObservableInteger`) has two variables: a variable for the interface(`OnIntegerChangeListener listener`) we just created, and a variable for our integer(`int value`). 


Below are the methods needed for the above steps:  
1. Setting our interface listener to the desired logic.
    ```java
    public void setOnIntegerChangeListener(OnIntegerChangeListener listener)
    {
        this.listener = listener;
    }
    ```
2. Setting the integer to a new value, upon which the interface variable calls its method onIntegerChanged() to execute the desired logic. 
    ```java
    public void set(int value)
    {
        this.value = value;

        if(listener != null)
        {
            listener.onIntegerChanged(value);
        }
    }
    ```

3. To set up this listener and a callback in the code, we pass a new instance of `OnIntegerChangeListener` to `setOnIntegerChangeListener`, where we can override `onIntegeChanged` to change the background color with the new value set by the `ColorChangingFragment`:
    ```java
    public static ObservableInteger color = new ObservableInteger();

    color.setOnIntegerChangeListener(new OnIntegerChangeListener() {
        @Override
        public void onIntegerChanged(int newValue) {
            view.setBackgroundColor(color.get());
        }
    });
    ```

The code for the background color-changing listener that changes the fragment's background color is in the folder [CustomListener](https://github.com/Toyon/LearnATAK/-/tree/main/demo-fragments/app/src/main/java/com/toyon/demofragments/CustomListener), which contains an interface `OnIntegerChangeListener` and a class `ObservableInteger`. `ObservableInteger` contains a method `set(int value)` that is called when the integer value is updated. Upon updating this value, the interface's function `onIntegerChanged` is invoked. This can be overwritten to perform any other logic. 

