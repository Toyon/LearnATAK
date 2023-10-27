---
title: "Core Components"
icon: icon/svg/components.svg
description: Shows how to use notifiations, radio groups, broadcast receivers, services, and more
date: 2023-08-11T15:26:15Z
lastmod: 2023-08-11T15:26:15Z
draft: false
weight: 190
---

 

*Updated: 24 March 2023*

This section will describe all components implemented on the "Core Components" tab identified by the Android icon ![IMG](../../../images/ic_brand_android.png) . This tab focuses on demonstrating views, buttons, controls and other visual elements that a user interacts with. In addition, this tab provides examples for backend Android components such as `Services` to help manage application data. We will discuss the components in order of appearance on the tab layout with some additional sections afterwards to elaborate on additional topics.

### Contents

- [Breakdown: Fragments \& Tabs](#breakdown-fragments--tabs)
- [Breakdown: Radio Group \& Radio Buttons](#breakdown-radio-group--radio-buttons)
- [Breakdown: Notifications](#breakdown-notifications)
- [Breakdown: Service](#breakdown-service)
- [App Resources](#app-resources)
  - [Customize Plugin Icon](#customize-plugin-icon)
- [Broadcast Receivers](#broadcast-receivers)

___

<br>

## Breakdown: Fragments & Tabs 

Source Code: [`HelloWorldDropDown`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/HelloWorldDropDown.java)  
Resources: [`pane_main.xml`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/res/layout/pane_main.xml)  
The Core Components UI is managed by a [Fragment](https://developer.android.com/guide/fragments) which has its own [lifecycle](../../android_development/android-basics.md/#lifecycle) to help the operating system free resources when its view is not actively in use. The other two tab view user interfaces are also managed by Fragments. In order to avoid the complexities of working directly with a [`FragmentManager`](https://developer.android.com/guide/fragments/fragmentmanager) we use the [`ViewPager`](https://developer.android.com/reference/kotlin/androidx/viewpager/widget/ViewPager) to manage Fragments and create a tab layout with the ability to flip left and right through tabs like flipping pages of a book.  

> *NOTE* 
> ViewPager has been deprecated, so for later version of ATAK use ViewPager2 instead. [Demo-Fragments]() uses it, and a tutorial can be found in the [Fragments](../../../android_development/fragments/) document. 

The ViewPager and icon buttons corresponding to the available pages as tab icons are specified in the [`pane_main.xml`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/res/layout/pane_main.xml) layout file, and the functional logic of the `ViewPager` and tab icon buttons is defined in the [`HelloWorldDropDown` class](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/HelloWorldDropDown.java). The remainder of this section will discuss the details of how the ViewPager and tab buttons are setup to provide the user a seamless tab layout. Everything related to the management of the tabs and ViewPager is contained within the dropdown constructor, a single member variable and single helper method . 

```java
// member variable
private final Button[] tabIcons;

// helper method
private void colorActiveTab(int position) {
    for (int i = 0; i < tabIcons.length; i++) {
        tabIcons[i].setSelected(position == i);
        tabIcons[i].setBackgroundTintList(pluginCtx.getResources().getColorStateList(
            (position == i) ? R.color.android_green : R.color.white
        ));
    }
}
```

The member variable `tabIcons` is used to group and sort the tab buttons to be indexed in the same order as the ViewPager's fragments. The private helper function is used to synchronize the tab icon highlight coloring to the state of the selected page or fragment for the ViewPager. This helper method is used by the ViewPager's `onPageSelected` callback as well as at the end of the constructor to initialize the tab coloring before the first page is selected.

```java
// constructor 
public HelloWorldDropDown(final MapView mapView, final Context context) {
    super(mapView);
    this.pluginCtx = context;
    paneView = PluginLayoutInflater.inflate(context, R.layout.pane_main, null);
    
    // 1
    ViewPager viewPager = paneView.findViewById(R.id.tabPager);
    Button coreComponentTab = paneView.findViewById(R.id.topicAndroid);
    Button layoutBtn = paneView.findViewById(R.id.topicLayout);
    Button mapTab = paneView.findViewById(R.id.topicMap);
    
    // 2
    Util.setButtonToast(mapView.getContext(), coreComponentTab, "Open Core Components Page");
    Util.setButtonToast(mapView.getContext(), layoutBtn,"Open Layouts Tab");
    Util.setButtonToast(mapView.getContext(), mapTab,"Open Mapping Tab");
    tabIcons = new Button[] { coreComponentTab, layoutBtn, mapTab };
    for (int i = 0; i < tabIcons.length; i++) {
        final int index = i;
        tabIcons[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(index);
            }
        });
    }

    // 3
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) { }
        @Override
        public void onPageSelected(int position) { colorActiveTab(position);  }
        @Override
        public void onPageScrollStateChanged(int i) { }
    });

    // 4
    List<Fragment> fragments = new ArrayList<>();
    fragments.add(new CoreFragment().construct(getPluginCtx(), getMapView().getContext()));
    fragments.add(new LayoutsFragment().construct(HelloWorldDropDown.this));
    fragments.add(new MapFragment().construct(HelloWorldDropDown.this));
    FragmentPagerAdapter fpa = new FragmentPagerAdapter(
        ((FragmentActivity) getMapView().getContext()).getSupportFragmentManager()) {
        @NonNull
        @Override
        public Fragment getItem(int i) { return fragments.get(i); }
        @Override
        public int getCount() { return 3; }
    };
    colorActiveTab(viewPager.getCurrentItem());
    viewPager.setAdapter(fpa);
}
```

The initialization of the tab buttons and ViewPager within the constructor can be broken down into 4 main steps.

1. First we find the the UI elements within the layout and assign them to local variables to work with.
2. Next we add a long press toast message to each tab button and iterate through the layout icon buttons adding them to the `tabIcons` member variable to be used by the helper coloring function and add `onClickListener` functionality to make the button click go to the proper page in the ViewPager
3. After the tab buttons are setup we add a listener to the ViewPager to update the tabs highlight colors when a new page is selected
4. Finally we create instances of each page fragment adding them to a list of fragments to be managed by the ViewPager's adapter. [&#8657;](#contents)

<br>

## Breakdown: Radio Group & Radio Buttons

Source Code: [`CoreFragment.initRadioButtons`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/fragments/CoreFragment.java#L118-140)   
Resources: [`tab_core.xml`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/res/layout/tab_core.xml#L17-53)  
The radio button implementation is designed to showcase how your plugin UI could allow a user to select a single item from a short list of options. This example demonstrates how to determine the user selection to modify the page layout gravity and update a TextView item on the display to describe the selected option.

```java
TextView labelActiveGravity = fragmentView.findViewById(R.id.label_active_gravity);
RadioGroup gravityRadioGroup = fragmentView.findViewById(R.id.radio_group_gravity);
LinearLayout componentContainer = fragmentView.findViewById(R.id.topic_android_container);
gravityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
        String activeGravityText = "Gravity Active: ";
        if (checkId ==  R.id.radio_btn_left) {
            activeGravityText += "Left";
            componentContainer.setGravity(Gravity.START);
        } else if (checkId ==  R.id.radio_btn_center) {
            activeGravityText += "Center";
            componentContainer.setGravity(Gravity.CENTER);
        } else if (checkId ==  R.id.radio_btn_right) {
            activeGravityText += "Right";
            componentContainer.setGravity(Gravity.END);
        } else
            Log.e(TAG, "Check Changed Value Unknown ID" + checkId);
        labelActiveGravity.setText(activeGravityText);
    }
});
```

We only need to find the `RadioGroup` in the layout to add a single `RadioGroup.OnCheckedChangeListener` to identify which `RadioButton` was selected. We could technically add `onClickListener` callbacks for each individual `RadioButton` but would require us to add listeners to each item and track the selected item ourselves which would be complicated when clicking on a radio button that is already the active selection. To enable the radio button example to do more than log the selected option we access a TextView (`R.id.label_active_gravity`) component and the LinearLayout (`R.id.topic_android_container`) which is the parent to all elements on the core components page. As seen in the `setOnCheckedChangeListener` callback we check the ID of the selected radio button to determine how to set the gravity of the container layout and properly set the TextView text to describe the gravity being applied to the layout. [&#8657;](#contents)

<br>

## Breakdown: Notifications

Source Code: [`CoreFragment.initNotifications`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/fragments/CoreFragment.java#L143-187)    
Resources: [`tab_core.xml`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/res/layout/tab_core.xml#L73-103)    
The notification implementation is designed to showcase the various ways you can create a non-disruptive notification for information provided by your plugin.

```java
// immutable class member variable 
private final String NOTIFICATION_CHANNEL_ID = "com.atakmap.android.demohelloworld.notify";

// 1
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    CharSequence name = pluginCtx.getString(R.string.notify_channel_name);
    String description = pluginCtx.getString(R.string.notify_channel_desc);
    int importance = NotificationManager.IMPORTANCE_DEFAULT;
    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
    channel.setDescription(description);
    NotificationManager notificationManager = (NotificationManager) atakCtx
        .getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.createNotificationChannel(channel);
}

// 2
Button atakNotifyBtn = fragmentView.findViewById(R.id.atak_notification_btn);
atakNotifyBtn.setOnClickListener(view -> NotificationUtil.getInstance().postNotification(
    1, com.atakmap.app.R.drawable.info, 
    "NotificationUtil", "NotificationUtil", 
    "Generated by ATAK Util Notification", null, true));
Util.setButtonToast(atakCtx, atakNotifyBtn, "Generate Notification using ATAK Utility");

// 3
int androidNotificationId = 54321;
Button androidNotifyBtn = fragmentView.findViewById(R.id.android_notification_btn);
androidNotifyBtn.setOnClickListener(view -> {
    NotificationManager notificationManager = (NotificationManager) atakCtx
        .getSystemService(Context.NOTIFICATION_SERVICE);
    Notification.Builder builder = (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) ?
        new Notification.Builder(atakCtx) :
    new Notification.Builder(atakCtx, NOTIFICATION_CHANNEL_ID);
    builder.setContentTitle("Notification Builder generated notification")
        .setSmallIcon(com.atakmap.app.R.drawable.hints);
    notificationManager.notify(androidNotificationId, builder.build());
});
Util.setButtonToast(atakCtx, androidNotifyBtn, "Generate Notification using Android Notification Builder");
```

The initialization of the notification buttons can be broken down into 3 main steps:

1. Regardless if you use the convenience methods provided by the ATAK API or the standard Android API to generate your notifications, you must register your plugin's [notification channel](https://developer.android.com/training/notify-user/channels) with the system in order to deliver notifications on Android 8.0 and higher.
2. This section of code sets up the ATAK Notify button click to generate an Android Notification using the ATAK API [`NotificationUtil`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/atak/ATAK/app/src/main/java/com/atakmap/android/util/NotificationUtil.java#L462) which according to the documentation comment on the method will produce a dismissible notification without a ticker, no sound, vibrate or flashing. The variables provided to the API utility function are as follows:

   - Notification ID: (integer) unique number to identify the specific ID which can be used to update a notification or dismiss it programmatically 

   - Icon ID: (integer) identifier of a drawable and this API uses ATAK context so the drawable ID requires an [ATAK drawable](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/tree/4.5.1.13/atak/ATAK/app/src/main/res/drawable)

   - Title: (string) A brief headline text for the notification displayed on the first line in the content area of the notification template

   - Ticker: (string) The text that summarizes the notification for accessibility services

   - Message: (string) The supporting information for the notification displayed on the second line in the content area

   - Notification Intent: (Intent) The intent to fire when the notification is selected within ATAK. If ATAK is not in the foreground, the notification will bring ATAK into display then pass the intent.

   - User: (boolean) Allows user to dismiss the notification if true
3. This section of code uses the core Android API [`Notification.Builder`](https://developer.android.com/reference/android/app/Notification.Builder) and [`NotificationManager`](https://developer.android.com/reference/android/app/NotificationManager?hl=en) to display a system notification for the plugin.

Some [useful icons](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/tree/4.5.1.13/atak/ATAK/app/src/main/res/drawable) provided by ATAK for notifications are the following: 
`camera.png`, `cancel.png`, `caution.png`, `chatsmall.png`, `check.png`, `done.png`, `checkpoint_blue/green/red/yellow.png`, `close.png`, `damaged.png`, `hints.png`, `ic_debugging`, `ic_notify_drawing.png`, `ic_notify_friendly.png`, `ic_notify_neutral.png`, `ic_notify_survey.png`, `ic_notify_target.png`, `ic_notify_unkown.png`, `ic_self.png`, `info.png` [&#8657;](#contents)

<br>

## Breakdown: Service

Source Code: [`CoreFragment.initServiceButton`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/fragments/CoreFragment.java#L190-202), [`class DemoService`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/DemoService.java), [`IUptime.aidl`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/aidl/com/toyon/demohelloworld/service/IUptime.aidl), [`IUptimeCallback.aidl`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/aidl/com/toyon/demohelloworld/service/IUptimeCallback.aidl)     
Resources: [`tab_core.xml`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/res/layout/tab_core.xml#L73-103), [`AndroidManifest.xml` `<service>`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/AndroidManifest.xml#L25-32)     
The service implementation requires an extensive setup compared to most of the other components we have covered so far with especially if you want the service to communicate information to your plugin's interface which is the most likely case. In order for an activity/fragment to communicate with a service there are three ways you can do so: (1) [broadcast receiver](https://developer.android.com/guide/components/broadcasts), (2) [Android Interface Definition Language - AIDL](https://developer.android.com/guide/components/aidl), (3) [Messenger](https://developer.android.com/guide/components/bound-services#Messenger). This demonstration will cover the second option using AIDL as it is the recommended option by the primary developers of ATAK. Using this approach is also required for your service to utilize ATAK API provided objects since your service is not started through the ATAK plugin class loader capability. Unlike your dropdowns, the service runs in a separate application process (remote process) from your plugin which makes communication between the service and UI elements of ATAK a little more complex.

TLDR; This example runs a simple service with the recommended communication approach, AIDL, for ATAK plugin services. If you want a service to communicate with your plugin interface or access ATAK API methods follow this example outline.

```xml
<service
    android:name="com.atakmap.android.demohelloworld.DemoService"
    android:label="Demo Uptime Counting Service"
    android:exported="true">
    <intent-filter>
        <action android:name="com.atakmap.android.demohelloworld.service.DemoService"/>
    </intent-filter>
</service>
```

The first step is to define your service in the [`AndroidManifest.xml` ](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/AndroidManifest.xml#L25-32) ensuring you make sure `exported="true"` since it allows other applications, in this case ATAK, to launch the service. 

Next we must create a new directory named `aidl` under the `src/main` directory. The folder should be converted to a blue "source root" folder automatically in the "Project" viewer. Then within the new `aidl` directory we create the package structure to correspond with our java packages. In the case of the demonstration the package is `com.atakmap.android.demohelloworld.service`, but you can make this custom to your application. With the AIDL packages in place we create the necessary AIDL interface files to be utilized by our Fragment and Service in order for the Service to send information to our plugin UI. 

```java
// IUptimeCallback.aidl
package com.atakmap.android.demohelloworld.service;

interface IUptimeCallback {
    // do something when a second is "counted"
    void update(int uptimeSeconds);
}
```

The first AIDL file we create is an interface for a callback method that can be provided to the Service to execute when the uptime count is updated by the service. The UI element should implement this interface as a handler to update the interface with the uptime seconds value on each call.

```java
// IUptime.aidl
package com.atakmap.android.demohelloworld.service;

// Declare any non-default types here with import statements
import com.atakmap.android.demohelloworld.service.IUptimeCallback;

interface IUptime {
	// pass a callback function for use by the uptime counter class
    void register(IUptimeCallback callback);
}
```

The second AIDL file created is an interface that is designed to allow the UI component to provide an implemented update callback to the uptime counter service when it is bound to the service. The service should implement this interface as its binder which is provided as a hook to all Fragments/Activities that bind to the service for receiving data.

Once you have your AIDL interface files created you want to run `Build > Clean Project` / `Build > Rebuild Project` and ensure you see the "generated" java files corresponding to the AIDL interface files. Look in the "build" folder in the Project View, or look for a "java (generated)" folder under "app" if in Android View. Once you have your generated java interfaces you can now utilize them in your Service and Fragment/Activity classes.

```java
public class DemoService extends Service {

    private final static String TAG = DemoService.class.getSimpleName();
    private final AtomicInteger uptimeSec = new AtomicInteger(0);
    private ScheduledFuture<?> timerSchedule;
    private IUptimeCallback uptimeCallback = null;
    private final IUptime.Stub binder = new IUptime.Stub() {
        @Override
        public void register(IUptimeCallback callback) throws RemoteException {
            uptimeCallback = callback;
        }
    };

    /** The count increment command to execute on one second interval */
    private Runnable countTask() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    int count = uptimeSec.incrementAndGet();
                    if (uptimeCallback != null) uptimeCallback.update(count);
                } catch (Exception e) { Log.e(TAG, "FAILED TO EXECUTE UPDATE"); }
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        // (0) command, (1) initial delay, (2) period, (3) time unit of provided values
        timerSchedule = executor.scheduleAtFixedRate(countTask(), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        if (timerSchedule != null) timerSchedule.cancel(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }
}
```

Above is the reduced [`DemoService`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/DemoService.java) class to showcase the significant elements that enable the uptime counting service. The `binder` member variable creates an implemented instance of the `IUptime` interface which is provided to our `CoreFragment`  when it tries to bind to the service which calls the `onBind` method. The implemented `register` method will save the callback function to be used in service's `countTask()` method which is executed every second. While services are run in a separate process, we still don't want to pause the main thread of that process. We use the `Executor` to schedule a fixed rate task to be executed every second when the service `onCreate`. We save the `ScheduledFuture` as a member variable to allow the task to be canceled when the service is destroyed.

```java
public class CoreFragment extends Fragment {

	private final IUptimeCallback.Stub handler = new IUptimeCallback.Stub() {
        @Override
        public void update(int uptimeSeconds) throws RemoteException {
            String updateText = "Uptime: " + uptimeSeconds + " sec";
            try {
                // ensure we are on the UI thread when updating the text view
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() { serviceUptimeText.setText(updateText); }
                });
            }
            catch (Exception e) { Log.w(TAG, "Unable to update uptime text: " + e); }
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IUptime service = IUptime.Stub.asInterface(iBinder);
            try { service.register(handler); } 
            catch (RemoteException e) { Log.e(TAG, "FAILED TO REGISTER CALLBACK: " + e); }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "SERVICE DISCONNECTED");
        }
    };
    
   	@Override
    public void onDestroyView() {
        try { pluginCtx.unbindService(connection); } catch (Exception ignored) { }
        super.onDestroyView();
    }

    /** Setup button to start a bound uptime counter service. */
    private void initServiceButton() {
        serviceUptimeText = fragmentView.findViewById(R.id.service_uptime);
        Button startServiceBtn = fragmentView.findViewById(R.id.start_service);
        Util.setButtonToast(atakCtx, startServiceBtn, "Starts a Demo Service");
        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(pluginCtx, DemoService.class);
                boolean result = pluginCtx.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
                Log.d(TAG, "Successfully started and bound to service? " + result);
            }
        });
    }
    
    ...
}
```

Above is the reduced  [`CoreFragment`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/fragments/CoreFragment.java) class which focuses on the pertinent code that interacts with the demonstration service. First the Fragment defines a member variable `handler` as an instance of the AIDL `IUptimeCallback` which is then provided to the service `connection` member variable to register the UI updating callback handler with the service counter. The key "entry point" to all of the service code is within the `initServiceButton` callback function which makes the button click have the fragment bind to the `DemoService` launching the service if it doesn't already exist. With the service being started as a bound service to the `CoreFragment` it is only active while the fragment view is initialized. You can swipe across the different tabs of the Hello World Plugin to see the view of the fragment destroyed resulting in the service shutting down due to the `FragmentManager` of the `ViewPager` releasing the `CoreFragment` view resources. [&#8657;](#contents)

<br>

## App Resources

Official Guides for:

- [Drawable Resources](https://developer.android.com/guide/topics/resources/drawable-resource). [more details](https://developer.android.com/develop/ui/views/graphics/drawables)
- [String Resources](https://developer.android.com/guide/topics/resources/string-resource)

Given that layout XML files, drawable resources, and string resources are used throughout the plugin, this section will cover generics for adding and using these common application resources. 

### Customize Plugin Icon

Copy a PNG from your file explorer and right click on `res/drawable` then select "Paste" to add your new icon image asset. Specify the desired name in the project taking note it is good practice to start the name with `ic_` which indicates it is an icon. There is no official recommendation for the plugin image format but we suggest a square minimal dimension lengths to achieve the desired resolution to keep your overall plugin file size down. 

```xml
<application android:icon="@drawable/%ICON_FILE_NAME%" ...></application>
```

To set your new icon to be rendered in the "Tools" pane for your plugin you need to change the following 2 items. The [AndroidManifest.xml](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/AndroidManifest.xml#L8) needs to name the new drawable resource you added to your project. 

```java
public Drawable getIcon() {
    return (context == null) ? null : context.getResources().getDrawable(R.drawable.%ICON_FILE_NAME%);
}
```

You also need to update the `getIcon()` method in the [plugin/PluginTool.java](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/plugin/PluginHelloWorldTool.java#L42) file to specify the drawable with `R.drawable.%ICON_FILE_NAME%`. The icon file name doesn't need to specify the file type extension (no need to specify `*.png`). [&#8657;](#contents)

<br>

## Broadcast Receivers

Official Guide:

- [Broadcasts overview](https://developer.android.com/guide/components/broadcasts?hl=en)

Given the nature of ATAK, most tools or plugins can be activated or accessed by sending intents. The [class extending `DropDownMapComponent`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/HelloWorldDropDown.java) will typically register these receivers allowing you to send an intent to "SHOW" the tool or plugin pane. Viewing ATAK's [`UserMapComponent._registerReceivers`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/atak/ATAK/app/src/main/java/com/atakmap/android/user/UserMapComponent.java#L217) method we can view the tools that your plugin can interact with and provide a user shortcut to. [&#8657;](#contents)