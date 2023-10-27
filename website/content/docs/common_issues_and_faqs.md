---
title: "Common Issues and FAQs "
icon: icon/svg/question.svg
description: Have a question? We may have already answered it!
date: 2023-08-11T15:26:15Z
draft: false
weight: 360
---

1. [How can I add a CSV file to my plugin?](#how-can-i-add-external-things-to-my-plugin-such-as-a-csv)
2. [How can I pass information between activities](#how-do-i-pass-information-from-one-activitydropdownreceiver-to-the-next)
3. [How can I debug my code?](#how-can-i-debug-my-code)
4. [What are listeners and how can I use them?](#what-are-listeners)
5. [What's a drop down receiver in ATAK?](#what-is-a-dropdownreceiver)
6. [How can I place a point on a map?](#how-can-i-place-a-point-on-the-map)
7. [How can I get a user's current location](#how-can-i-get-a-users-current-location)
8. [Working with Map Tiles](#working-with-map-tiles)


*Updated: 20 July 2023*

This section will cover some common issues and questions that arise when creating plugins for the first time.

## How can I add external files to my plugin such as a CSV? 

There is a folder in `/app/src/main` called `assets` where you can place CSV files
You can then use AssetsManager to retrieve these files and use them. 

```java
AssetManager manager = pluginCtx.getAssets();
InputStream inStream = null;
inStream = manager.open("YOUR_CSV.csv");
BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
```

The plant classifier demo uses a csv file and acesses it in the `ClassifyPlantDropDown` Receiver. You can take a look at the code [here](https://github.com/Toyon/LearnATAK/tree/master/PlantClassifier/app/src/main/java/com/atakmap/android/plantclassifier/ClassifyPlantDropDown.java#L103-124)

## How do I pass information from one Activity/DropDownReceiver to the next? 

You can pass information using [Intent](../android_development/android-basics/#intents). Create an intent object and use the function 
putExtra(name, variable) in order to send it to a new class. You can then call get`variableType`Extra()
in order to retrieve the data. 

```java
Intent reportIntent = new Intent();
reportIntent.putExtra("myInt", 5);

Intent newIntent = new Intent(); 
var_name = newIntent.getIntExtra("myIntent");
```

If you would like to pass an object, not just a variable through intent you will have to make your object `Parcelable`. 
Your object class will need to implement Parcelable and override some methods including: `writeToParcel` and a constructor `ClassName`(Parcel in).
That reads from the Parcel and converts your object back to its class. Here is a snippet of what that looks like. You can check out a full class implementing it [here](https://github.com/Toyon/LearnATAK/tree/master/PlantClassifier/app/src/main/java/com/atakmap/android/plantclassifier/adapters/Plant.java). 

```java
protected MyObject(Parcel in) {
    myString1 = in.readString();
    myString2 = in.readString();
}

public static final Creator<MyObject> CREATOR = new Creator<MyObject>() {
    @Override
    public MyObject createFromParcel(Parcel in) {
        return new Plant(in);
    }

    @Override
    public MyObject[] newArray(int size) {
        return new MyObject[size];
    }
};

@Override
public int describeContents() {
    return 0;
}

@Override
public void writeToParcel(Parcel out, int flags) {
    out.writeString(myString1);
    out.writeString(myString2);
}
```

You can then pass and retrieve them through intent using: 
```java
myObject = (MyObject)intent.getParcelableExtra("MyObject");``` and ```intent.putExtra("myObject", temp);
```

## How can I make my UI layouts more organized? 

There are many ways to make your UI layouts more complex and organized. You can use nested view groups in order to organize sections of your page different. 

See [`UI Tutorial`](../android_development/ui_layout_tutorial/) for an in-depth tutorial of how to utilize ViewGroups and Views to create a UI layout. 

If you are interested in a Multi-page layout, checkout the [Fragments](../android_development/fragments/) tutorial. 


## How can I debug my code? 


One easy way to debug your code is using Log statements. Import the below statement to use Logs:

``` import android.util.Log ```

You can then use Log.d(TAG, message) where d stands for debug to output statements or variables.
 You can see these outputs at runtime in the Logcat. Log.d(string, string) has two string parameters, the first of which is the "tag" you can filter by in the Logcat, and the second of which is the message you can write.
You can also add local string variables to the second argument if you want to see their values at runtime. 

## What are Listeners? 

An event listener is an interface in the View class that contains a single callback method. These methods will be called when a user interacts with an item in a UI design that's connected to a listener. For example, Buttons are connected to the `setOnClickListener`. When a users presses a button, this will trigger the `onClick` method and an interaction will happen. Here's an example of how to use the `setOnClickListener` with a button: 

```java
Button btnExample = findViewById(R.id.btnExample);
btnExample.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        // Do something here	
    }
});
```
Here is a list of common event listeners that can be applied to any view: 
1. setOnClickListener - Callback when the view is clicked
2. setOnDragListener - Callback when the view is dragged
3. setOnFocusChangeListener - Callback when the view changes focus
4. setOnGenericMotionListener - Callback for arbitrary gestures
5. setOnHoverListener - Callback for hovering over the view
6. setOnKeyListener - Callback for pressing a hardware key when view has focus
7. setOnLongClickListener - Callback for pressing and holding a view
8. setOnTouchListener - Callback for touching down or up on a view

You can also create your own custom listeners if you want your app to perform an action upon changes/interactions to objects that aren't of type View. In the Fragments document, there is a [section](../../android_development/fragments/#creating-custom-listeners) that explains the steps behind doing so.



## ATAK Specific 

### What is a DropDownReceiver? 

A special type of Android Broadcast Receiver that was created by ATAK. It can display views, connect with the ATAK System and application events, and can interact with ATAK's map. A DropDownReceiver contains several functions that 
require overriding: 

```java
 @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null)
            return;
        if (action.equals(SHOW_MAIN_PANE))
            showDropDown(paneView,
                    HALF_WIDTH, FULL_HEIGHT, // Landscape Dimensions
                    FULL_WIDTH, THIRD_HEIGHT, // Portrait Dimensions
                    false, this);
    }

    @Override
    public void onDropDownVisible(boolean visible) {
        if (visible) {
            //do something 
        } else
            //do this instead
    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownClose() { }

    @Override
    public void onDropDownSizeChanged(double v, double v1) { }

    @Override
    protected void disposeImpl() { }

```
1. The onReceive function is used to display a UI layout, plus receive extra variables and objects stored in intent 
2. onDropDownVisible can be used to run code in the background when the UI is visible 
3. In the main function, you can set up listeners for buttons or modify dynamic views you have in your layout

> **_NOTE:_**  When creating a new DropDownReceiver make sure you register it in the <`PluginTemplateName`>MapComponent.java file
>
> Ex: 
>
> ```java
> ReviewPageDropDown reviewPageDDR = new ReviewPageDropDown(view, context);
>         DocumentedIntentFilter reviewPageFilter = new DocumentedIntentFilter();
>         reviewPageFilter.addAction(ReviewPageDropDown.SAMPLE_ACTION); //add actions
>         this.registerDropDownReceiver(reviewPageDDR, reviewPageFilter);
> ```




### How can I place a point on the Map? 

If you want your plugin to drop a point on a Map you can use the ATAK class: `PlacePointTool.MarkerCreator()`
DemoCNN and PlantClassifier both have examples of this in [`ReportDropDown` class](https://github.com/Toyon/LearnATAK/tree/master/democnn/app/src/main/java/com/atakmap/android/democnn/ReportDropDown.java) and [`MainDropDown` class](https://github.com/Toyon/LearnATAK/tree/master/PlantClassifier/app/src/main/java/com/atakmap/android/plantclassifier/MainDropDown.java) respectively. 
If you want to chose what icon will be displayed you can checkout the HelloWorldDemo and select and icon from there.
You can get the path to the icon in the Logcat if you press on the icon. 


### How can I get a user's current location? 

There are a few ways to get a user's current location. One is the standard android way and the other uses ATAK's map to retrieve the location. 

#### **The Standard Android Way** 
You can create a class that extends Service and implements LocationLister. 
An example of this is [`GPSTracker` class](https://github.com/Toyon/LearnATAK/tree/master/PlantClassifier/app/src/main/java/com/atakmap/android/plantclassifier/GPSTracker.java) in the PlantClassifier plugin. 
Then you can create an instance of the class and retrieve the current longitude and latitude. 

#### **The ATAK Way** 
The ATAK way is much simpler: 
``` getMapView().getSelfMarker().getPoint() ``` 
This will return the longitude, latitude, and altitude. Here's a sample: 

```java
GeoPoint geoPoint = getMapView().getSelfMarker().getPoint();
String longitude = geoPoint.getLongitude();
String latitude = geoPoint.getLatitude();

```
#### **Converting GeoPoints to an Address**

If you want a user's location in the format of a postal address, follow the format of the code below. It is **important** to note that the `Geocoder` class use's the `MapViews` context instead of the plugin 
context otherwise it will cause an error. 

```java
    public String getAddress(GeoPoint geoPoint){
        Geocoder geocoder = new Geocoder(getMapView().getContext(), Locale.getDefault());
        String result = "";
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (addressList != null && addressList.size() > 0) {
            Address address = addressList.get(0);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                sb.append(address.getAddressLine(i)).append("\n");
            }
            sb.append(address.getLocality()).append(", ");
            sb.append(address.getPostalCode()).append(", ");
            sb.append(address.getCountryName());
            result = sb.toString();
        }else{
            result = "No Address found, please enter manually :(";
        }
        return result; 
```

### Working with Map Tiles 

When you use Google Maps, you can see image data of cities, streets, mountain ranges, and more within a single view. Map tiles help make this possible. To add a set of Map tiles to display on the ATAK globe, download [ATAK-Maps.zip](https://github.com/joshuafuller/ATAK-Maps/releases). 

Follow the README instructions located in [this](https://github.com/joshuafuller/ATAK-Maps) repository to add the tiles to your app. In order to access your new Map tiles, click on the map icon ![](icon/svg/map_tile.svg) and then click the `MOBILE` tab. From there you can choose which view you prefer. 




