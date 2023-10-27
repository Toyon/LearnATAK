---
title: "Layouts Quick Overview"
icon: icon/svg/ui.svg
description: An overview on the layouts implemented in this demo
date: 2023-08-11T15:26:15Z
draft: false
weight: 210
---



*Updated: 24 March 2023*

This section describes all of the components implemented on the "Layouts" tab identified by the Layout icon ![android](../../../images/ic_layout.png).Android and ATAK UI elements that act as containers or scaffolds to organize and arrange common UI components. For a more in-depth tutorial on creating layouts, checkout the [UI Layouts Tutorial](../../../android_development/ui_layout_tutorial/).

### Contents

- [Dropdown Pane Sizing](#dropdown-pane-sizing)
- [Layouts](#layouts)
    - [Relative Layout vs Constraint Layout](#relative-layout-vs-constraint-layout)
- [Breakdown: Recycler View](#breakdown-recycler-view)
    - [Potential Errors:](#potential-errors)

___

<br>

## Dropdown Pane Sizing

The ATAK API provides a method to actively resize the plugin's dropdown pane, which is shown in the [`LayoutsFragment.onCreateView`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/fragments/LayoutsFragment.java#L62-80). The provided ATAK API method `callResize(double fractionWidth, double fractionHeight)` can be called on the instance of the `DropDownReceiver` class to set the size of the pane as percentages of the total screen. There are provided fraction constants for height (`FULL_HEIGHT`, `HALF_HEIGHT`, `THIRD_HEIGHT`) and width (`FULL_WIDTH`, `HALF_WIDTH`, `THIRD_WIDTH`).

```java
// Fullscreen
dropDownReceiver.callResize(FULL_WIDTH, FULL_HEIGHT));
// Bottom Half
dropDownReceiver.callResize(FULL_WIDTH, HALF_HEIGHT));
// Bottom Third
dropDownReceiver.callResize(FULL_WIDTH, THIRD_HEIGHT));
// Right Half
dropDownReceiver.callResize(0.5, FULL_HEIGHT));
// Right Third
dropDownReceiver.callResize(THIRD_WIDTH, FULL_HEIGHT));

```

Additionally when it comes to determining the initial size of your plugin's `DropDownReceiver` pane you will want to override the `onReceive` method. The following code snippet breaks down the variables of the `DropDownReceiver.showDropDown` method. 

```java
public void showDropDown(
    android.view.View contentView,  // inflated plugin layout view
    double lwFraction,         // landscape-mode width fraction
    double lhFraction,         // landscape-mode height fraction 
    double pwFraction,         // portrait-mode width fraction 
    double phFraction,         // portrait=mode height fraction
    boolean ignoreBackButton,  // almost always false as this allows back navigation to close the pane
    com.atakmap.android.dropdown.DropDown.OnStateListener stateListener )
```

Examples of a full screen dropdown initial rendering can be found on the [`Icon2dDropDown.onReceive`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/Icon2dDropDown.java#L93-102) or [`Icon3dDropDown.show`](../app/src/main/java/com/toyon/demohelloworld/Icon3dDropDown.java#L91-96) methods. The [primary plugin dropdown pane renders](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/HelloWorldDropDown.java) at three-eights width and full height.

[TOP &#8657;](#contents)

<br>

## Layouts

[Layouts](https://android-developers.googleblog.com/2017/08/understanding-performance-benefits-of.html) are used to define the structure of a user interface in your app or in this case your ATAK plugin. It is important to be aware of the [common layouts](https://developer.android.com/develop/ui/views/layout/declaring-layout#CommonLayouts) to enable you to use the appropriate one to achieve your envisioned layout design, with the goal of limiting nesting to improve rendering speeds. Some common layouts demonstrated in the hello world plugin are [Linear Layouts](https://developer.android.com/develop/ui/views/layout/linear), [Relative Layouts](https://developer.android.com/develop/ui/views/layout/relative) and [Constraint Layouts](https://developer.android.com/develop/ui/views/layout/constraint-layout). Additionally there are examples of Recycler Views (a more performant List View for rendering long lists of items) and Grid Views.

#### Relative Layout vs Constraint Layout

Relative and constraint layouts can produce similar UI structures doing so with different properties. The Constraint Layout is [more performant](https://android-developers.googleblog.com/2017/08/understanding-performance-benefits-of.html) than Relative Layout, but is not included in the base Android packages. You will need to include the dependency in your project's Gradle dependencies to use it in your project.

```groovy
dependencies {
	implementation ('androidx.constraintlayout:constraintlayout:2.1.4')
}
```

The following table outlines how to accomplish the same layout positioning with either Relative or Constraint Layout as the parent container.

{{< include file="layouts/_default/layout_table.html" >}}

<br>

## Breakdown: Recycler View

Source Code: [`LayoutsFragment`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/fragments/LayoutsFragment.java), [`DemoAdapter`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/list/DemoAdapter.java)  
Resources: [`tab_layouts.xml`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/res/layout/tab_layouts.xml), [`item_user.xml`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/res/layout/item_user.xml)  
Dependencies: [`androidx.recyclerview:recyclerview:1.1.0`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/build.gradle#L93-100)

The demonstration recycler view provides the simplest and minimum components needed to understand how to initialize a `RecyclerView` and programmatically add it to a layout. There is a more complex and realistic example in the [`DynamicRecyclerView`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/list/DynamicRecyclerView.java) used by the the [`Icon2dDropDown`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/java/com/toyon/demohelloworld/Icon2dDropDown.java).

```groovy
dependencies {
    // ensure to avoid duplicating androidx dependency libraries
    // provided by ATAK core for recyclerview
    testImplementation 'junit:junit:4.12'
    implementation ('androidx.recyclerview:recyclerview:1.1.0') {
        exclude module: 'collection'
        exclude module: 'core'
        exclude module: 'lifecycle'
        exclude module: 'core-common'
        exclude module: 'collection'
        exclude module: 'customview'
    }
}
```

The  [RecyclerView](https://developer.android.com/develop/ui/views/layout/recyclerview) is not included in the core of Android and requires it to be listed as a dependency in the app Gradle file. It is important for to use the specific version RecyclerView and exclude duplicate dependencies which are already provided by the core of ATAK. The one thing to note is we also needed to include `junit:4.12` as it is required by `RecyclerView`. This version of RecyclerView is compatible with the versions of dependencies already included in ATAK core

```java
public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> {

    private static final String TAG = DemoAdapter.class.getSimpleName();
    private final String[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        
        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.username);
        }
        
        public TextView getTextView() { return textView; }
    }

    public DemoAdapter(String[] dataSet) { localDataSet = dataSet; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_user, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        try { viewHolder.getTextView().setText(localDataSet[position]); } 
        catch (Exception e) { Log.e(TAG, "!!!! FAILED TO BIND TEXT !!!"); }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return localDataSet.length; }
}
```

The driving component of any RecyclerView is the Adapter as it provides the logic to the view for how to grab data from the list of data items to render in a re-used layout view item based on the tracked scroll position. The custom `ViewHolder` class is always provided the [`R.layout.item_user`](https://github.com/Toyon/LearnATAK/tree/master/demo-hello-world/app/src/main/res/layout/item_user.xml) and implements the functions to fetch the `R.id.username` TextView element. This then allows the `onBindViewHolder` to set the TextView to the dataset value when it's position is rendered within the view.

```java
public class LayoutsFragment extends Fragment {

    private Context pluginCtx;
    private String[] users;
    protected RecyclerView demoRecyclerView;
    protected DemoAdapter demoRecyclerAdapter;

    /** Create and instance of the LayoutsFragment */
    public LayoutsFragment construct(final HelloWorldDropDown receiver) {
        pluginCtx = receiver.getPluginCtx();
        users = new String[100];
        for (int i=0; i < users.length; i++) {
            users[i] = String.format(Locale.US, "User %d", i+1);
        }
        return this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(pluginCtx).inflate(R.layout.tab_layouts, container, false);
        LinearLayout layout_container = view.findViewById(R.id.linear_layouts_container);
        demoRecyclerView = new RecyclerView(pluginCtx);
        demoRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200));
        demoRecyclerView.setBackgroundColor(Color.parseColor("#000000"));
        demoRecyclerView.setHorizontalScrollBarEnabled(true);
        demoRecyclerAdapter = new DemoAdapter(users);
        demoRecyclerView.setAdapter(demoRecyclerAdapter);
        demoRecyclerView.setLayoutManager(new LinearLayoutManager(pluginCtx,
                LinearLayoutManager.HORIZONTAL, false));
        layout_container.addView(demoRecyclerView);
        return view;
    }
}
```

The above code snippet is the reduced `LayoutsFragment` class to show the relevant parts of code responsible for setting up the RecyclerView and data to be provided. The `construct` method creates a list of 100 mock user strings with the format "User ###". In the `onCreateView` method we create a RecyclerView UI element and set the layout properties to occupy the entire width of the parent and only have a height of 200px. Then we create an instance of our `DemoAdapter` providing the list of mock user string values and set the adapter to scroll horizontally. Finally we use the main LinearLayout element from the fragment layout to append the RecyclerView as the last child component adding it to the UI.

#### Potential Errors:

If you don't set the context properly for the Recycler View attempting to open your plugin will crash ATAK and result in an error message like the one below referencing the `onCreateView()` method as the source of the problem in attempts to render the component.

```verilog
E  FATAL EXCEPTION: main
Process: com.atakmap.app.civ, PID: 25132
android.view.InflateException: Binary XML file line #155: Binary XML file line #155: Error inflating class androidx.recyclerview.widget.RecyclerView
Caused by: android.view.InflateException: Binary XML file line #155: Error inflating class androidx.recyclerview.widget.RecyclerView
```

[TOP &#8657;](#contents)