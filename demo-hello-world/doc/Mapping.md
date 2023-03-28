# ATAK Mapping

*Updated: 24 March 2023*

This section will describe all components implemented on the "Mapping" tab identified by the map icon <img src="../app/src/main/res/drawable/ic_map.png" height="32px"/>. This tab focuses on ATAK API interactions with the mapping engine.

### Contents 

- [Introduction: How to explore the ATAK API](#Introduction)
- [Icons & 3D Models](#Icons-&-3D-Models)
- [Markers](#Markers)
- [Map Controls: Zoom & Tilt](#Map-Controls:-Zoom-&-Tilt)
- [Layers & Drawing](#Layers-&-Drawing)
- [Routes & Map Movement](#Routes-&-Map-Movement)

___

<br>

## Introduction

The ATAK application is a very powerful, elaborate and highly customizable mapping and data sharing application with an extensive set of API methods. There are too many intricacies to cover in an introductory plugin demonstration so we cover a limited set of capabilities that are commonly desired when creating a plugin. In this section we want to provide some guidance for how to navigate the available documentation in order to give you an idea of exploring capabilities that are not covered within this plugin.

**Javadoc:**
Every ATAK SDK comes with an `atak-javadoc.jar` which can be setup within an Android Studio project to have Javadoc display when hovering over ATAK API methods, but this does not help us explore the API to find new capabilities we haven't used before. In order to make our own offline web view of the Javadoc do the following:

1. Extract the contents of `atak-civ/atak-javadoc.jar`. For unpackaging jar files on Windows we recommend [7-Zip](https://www.7-zip.org/download.html). 
   - Right click the jar in your file explorer and select the option `7-Zip > Extract to "atak-javadoc\"`.
   - You can also make a copy of the jar then change the file extension to `*.zip` to use the default Windows File Explorer extraction tool.
2. Navigate into the new directory created from extracting the Javadoc `atak-javadoc`
3. Right click the `index.html` file and select `"Open with" > Preferred Browser`

Now you can browse through the packages and classes provided by the API.

**Source Code Key packages:**
Sometimes when the Javadoc doesn't have enough information to figure out how an API method should work going straight to the source code can be useful as there are instances where the core application uses some of the API methods you are trying to utilize. Make sure when browsing the [ATAK-CIV repo](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/tree/4.5.1.13) you are on the right branch/tag/version (which in this case is 4.5.13). The search feature on GitHub is very useful to browse through the project files. Some search tips are using quotes to find exact matches. For example `"class PlacePointTool"` will restrict the search to find the declaration of the class as opposed to all instances the class was used. Similarly you can find example usages of a method with a search like `".setIconPath("` will find examples of the `MarkerCreator` class method. There could be overlap with more common method names, but these tips will still help you find examples and class declarations quickly without jumping through the highly nested folders for package organization. Here is a cheat sheet of some key points to jump to for primary API packages.

- [ATAK App `com.atkamap.android`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/tree/4.5.1.13/atak/ATAK/app/src/main/java/com/atakmap/android): contains more communication and general ATAK methods
  - [`user.PlacePointTool`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/atak/ATAK/app/src/main/java/com/atakmap/android/user/PlacePointTool.java#L43)
  - [`maps.MapView`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/atak/ATAK/app/src/main/java/com/atakmap/android/maps/MapView.java)
- [TAK Kernel Engine `com.atakmap`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/tree/4.5.1.13/takkernel/engine/src/main/java/com/atakmap): methods for filesystems, map internals, geospatial mathematics & objects 
  - [Useful Geospatial Calculations `coremap.maps.GeoCalculations`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/takkernel/engine/src/main/java/com/atakmap/coremap/maps/coords/GeoCalculations.java)
  - [GeoPoint `coremap.maps.coords.GeoPoint`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/takkernel/engine/src/main/java/com/atakmap/coremap/maps/coords/GeoPoint.java)
  - [ATAK File System Utility functions which are important for finding file paths to expected assets in ATAK `coremap.filesystem.FileSystemUtils`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/takkernel/engine/src/main/java/com/atakmap/coremap/filesystem/FileSystemUtils.java)

**Alternative Sources:**
The [TAK Community Discord](https://discord.com/invite/xTdEcpc) is a great resource to talk to other developers working with TAK products and you can also use the search function of the discord to search through text channels and threads to see if others have had similar issues or questions.  [&#8657;](#contents)

<br>

## Icons & 3D Models

Source Code: [`MapFragment.initIconModels`](../app/src/main/java/com/atakmap/android/demohelloworld/fragments/MapFragment.java#L132-149), [`HelloWorldMapComponent`](../app/src/main/java/com/atakmap/android/demohelloworld/HelloWorldMapComponent.java#L33-38), [`class Icon2dDropDown`](../app/src/main/java/com/atakmap/android/demohelloworld/Icon2dDropDown.java), [`class Icon3dDropDown`](../app/src/main/java/com/atakmap/android/demohelloworld/HelloWorldMapComponent.java), [`class IconAdapter`](../app/src/main/java/com/atakmap/android/demohelloworld/list/IconAdapter.java), [`class DynamicRecyclerView`](../app/src/main/java/com/atakmap/android/demohelloworld/list/DynamicRecyclerView.java)  
Resources: [`tab_map.xml`](../app/src/main/res/layout/tab_map.xml), [`pane_map_icon_2d.xml`](../app/src/main/res/layout/pane_map_icon_2d.xml), [`pane_map_icon_3d.xml`](../app/src/main/res/layout/pane_map_icon_3d.xml), [`item_icon.xml`](../app/src/main/res/layout/item_icon.xml)  
The purpose of the "Map Icons" and "Vehicle Models" buttons are to showcase 2 different ways to open secondary plugin dropdowns and provide a more developer oriented view for accessing 2D icons and 3D vehicle models for use within a plugin. Each pane allows you to select a category which the icons or models are sorted by and presents a grid preview of the icon/model with the name. If you click on the preview item a Toast message and Log message will display the proper path to the icon/model for use in your plugin.

```java
private void initIconModels() {
    Button showIcons = fragmentView.findViewById(R.id.map_show_icons);
    showIcons.setOnClickListener(view -> {
        hwReceiver.setRetain(true);
        Intent mapIconIntent = new Intent();
        mapIconIntent.setAction(Icon2dDropDown.SHOW_ICON_PANE);
        AtakBroadcast.getInstance().sendBroadcast(mapIconIntent);
    });

    icon3dDropDown = new Icon3dDropDown(mapView, pluginCtx);
    Button showModels = fragmentView.findViewById(R.id.map_show_models);
    showModels.setOnClickListener(view -> {
        hwReceiver.setRetain(true);
        icon3dDropDown.show();
    });
}
```

The "Map Icons" button demonstrates how to show another dropdown using an intent while the "Vehicle Models" button demonstrates how to instantiate a dropdown class manually. The first approach requires that the `Icond2dDropDown` is registered by the plugin's map component.

```java
Log.d(TAG, "Registering the 2D Map Icon Viewer filter");
Icon2dDropDown mapIconViewDropDown = new Icon2dDropDown(view, context);
DocumentedIntentFilter mapIconFilter = new DocumentedIntentFilter();
mapIconFilter.addAction(Icon2dDropDown.SHOW_ICON_PANE, "Show the 2D Map Icon Viewer");
this.registerDropDownReceiver(mapIconViewDropDown, mapIconFilter);
```

The above code needs to be called in the `onCreate` method of the plugin `MapComponent` class in order to setup the receiver to listen for intents to show the dropdown pane. For details on how the "filter spinners" get the list of categories for the 2D and 3D assets refer to the `Icon2dDropDown.getIconGroupIdentifiers` and `Icon3dDropDown.getVehicleModelCategories` methods. The [`UserIconDatabase` class](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/atak/ATAK/app/src/main/java/com/atakmap/android/icons/UserIconDatabase.java) doesn't have any methods for accessing icons by their `"groupName"` value which is a more meaningful grouping label for icons compared to the icon set name so we used [`CursorIface`](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/takkernel/engine/src/main/java/com/atakmap/database/CursorIface.java) to make our own query to search the icon table for a list of icon group names. We access the default icon database location `/storage/emulated/0/atak/Databases/iconsets.sqlite` to make the necessary queries. The 2D icons also use a custom RecyclerView and adapter for rendering icon previews. The 3D vehicle model groups are found at `.../atak/tools/vehicle_models/metadata.json` so an example for reading JSON files is provided to access the category names for filtering. We used the `VehicleModelGridAdapter` provided by the API to reduce duplicate work for rendering the vehicle model previews and instead just implement our own `onItemSelectedListener`.

For more information about the icon formatting view this [doc string for iconset path formatting](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV/blob/4.5.1.13/atak/ATAK/app/src/main/java/com/atakmap/android/icons/UserIcon.java#L33). For more information about how CoT types automatically map to icons, read [**this document on the MIL STD 2525C standard**](http://everyspec.com/MIL-STD/MIL-STD-2000-2999/MIL-STD-2525C_20429/). Download the MIL-STD-2525 Rev C. Go to "Appendix A" and refer to T"able A III SIDC table" for the "Function ID" of the desired icon you want to be rendered when manually constructing a CoT message event type. For example the demo CoT message button creates a Reconnaissance Horse where the function id is `UC  RH  --`. That translates to the `U-C-R-H` portion of the complete event type `a-n-G` which covers the grouping of the icon `air - neutral - Ground - Unit - Combat - Reconnaissance - Horse`. [&#8657;](#contents)

<br>

## Markers

Source Code:[`MapFragment.initMarkers`](../app/src/main/java/com/atakmap/android/demohelloworld/fragments/MapFragment.java#L152-175)  
Resources: [`tab_map.xml`](../app/src/main/res/layout/tab_map.xml)  
This example showcases two different methods to create map markers. 

```java
PlacePointTool.MarkerCreator mc = new PlacePointTool.MarkerCreator(
                    mapView.getPointWithElevation());
mc.setUid(UUID.randomUUID().toString());
mc.setCallsign("HelloCar");
mc.setType("a-f-G"); // atoms - friend - Ground
mc.showCotDetails(false);
mc.setNeverPersist(true);
Marker m = mc.placePoint();
m.setStyle(m.getStyle()
           | Marker.STYLE_ROTATE_HEADING_MASK
           | Marker.STYLE_ROTATE_HEADING_NOARROW_MASK);
m.setTrack(45, 10);
m.setMetaString(UserIcon.IconsetPath, "34ae1613-9645-4222-a9d2-e5f243dea2865/Transportation/Car8.png");
m.setColor(Color.parseColor("#fccf03"));
```

The above approach to create a marker utilizes the API's `PlacePointTool` with to properly display the marker. This code is executed when the user clicks on the "Car Marker" button. Preserving the marker in a class managed scope would allow you to make updates to the marker as well.

```java
CotDispatcher internalDispatcher = CotMapComponent.getInternalDispatcher();
CotEvent markerEvent = new CotEvent();
CotDetail contact = new CotDetail("contact");
contact.setAttribute("callsign", "CoT-Horse");
CotDetail details = new CotDetail();
details.addChild(contact);
markerEvent.setVersion("2.0");
markerEvent.setDetail(details);
markerEvent.setUID(UUID.randomUUID().toString());
markerEvent.setTime(new CoordinatedTime());
markerEvent.setStart(new CoordinatedTime());
markerEvent.setStale(new CoordinatedTime().addSeconds(30));
CotPoint markerPoint = new CotPoint(mapView.getPointWithElevation().get());
markerEvent.setPoint(markerPoint);
markerEvent.setType("a-n-G-U-C-R-H");
Log.d(TAG, markerEvent.toString());
internalDispatcher.dispatch(markerEvent);
```

The second button "CoT Marker" or Cursor-on-Target Marker button showcases how to generate a properly formatted CoT event and send it to the internal ATAK `CotDispatcher`. This example relies on the default behavior of ATAK to plot incoming CoT events on the map. One of the interesting points to mention about this approach to rendering icons is that these messages can also be created from a remote device making it a good way to share information with ATAK users on the same network. By providing the `setStale(30 seconds)` the map will grey out and eventually remove the marker after 30 seconds pass. This can be used for providing location information that is time sensitive. If you wanted to share this CoT event with other devices you can swap the internal dispatcher with the external dispatcher.  [&#8657;](#contents)

<br>

## Map Controls: Zoom & Tilt

Source Code: [`MapFragment.initMapZoomLevels` & `MapFragment.initMapTilt`](../app/src/main/java/com/atakmap/android/demohelloworld/fragments/MapFragment.java#L204-233)  
Resources:  [`tab_map.xml`](../app/src/main/res/layout/tab_map.xml)  
Understanding how to zoom and tilt the map can help your plugin provide the best map display for the information your plugin is concerned with. Both of these method programmatically add buttons with different example values for zoom levels and tilt angles. 

```java
// ZOOM : gsd = ground sample distance in meters
double gsd = OSMUtils.mapnikTileResolution(i);
CameraController.Programmatic.zoomTo(mapView.getRenderer3(), gsd, true));

// TILT: range 0 (top-down) to 90 (inline with horizon)
CameraController.Programmatic.tiltTo(mapView.getRenderer3(), angle, true);
```

If you have experience with other mapping engines like Mapbox and Google Maps it is more likely you understand zoom levels as a range of numbers from 0-20. The ATAK map `CameraController` uses a value called ground sample distance (GSD) which is a ratio of the meters represented by the width of a pixel. This is how the map scale tool in the bottom left can render a ruler that presents the user with a visual example of a line that represents X meters. You can long click on the zoom levels to view the translation between a standard map zoom and the GSD value which uses the `OSMUtils.mapnikTileResolution` method. More information about these zoom levels can be found [here](https://wiki.openstreetmap.org/wiki/Zoom_levels). The final line shows how to adjust the camera angle. One of the powerful parts to the map rendering engine is that you have access to a 3-D globe instead of a flat tile map which can make for very interesting displays if you want to track objects in space relative to a ground position. [&#8657;](#contents)

<br>

## Layers & Drawing

Source Code: [`MapFragment.initLayer`](../app/src/main/java/com/atakmap/android/demohelloworld/fragments/MapFragment.java#L236-279), [`MapFragment.initDrawing`](../app/src/main/java/com/atakmap/android/demohelloworld/fragments/MapFragment.java#L283-312), [`class ExampleLayer`](../app/src/main/java/com/atakmap/android/demohelloworld/samplelayer/ExampleLayer.java), [`class GLExampleLayer`](../app/src/main/java/com/atakmap/android/demohelloworld/samplelayer/ExampleLayer.java)  
Resources: [`tab_map.xml`](../app/src/main/res/layout/tab_map.xml), [`isla_vista_ucsb.png`](../app/src/main/assets/isla_vista_ucsb.png)  
These examples demonstrate how to add a georeferenced image as a map layer and how to draw shapes, specifically a rectangle on the map. Without getting into the specifics for how the image is rendered as a layer on the map which is covered in the `GLExampleLayer` class we will focus more on how you can build upon that class to make your own `ExampleLayer` to control the render state of a single image on the map. This could be used to do something like plot a drone video feed on the map after applying the [orthorectification process](https://www.esri.com/about/newsroom/insider/what-is-orthorectified-imagery/) to the image. The example image in this example was actually an orthorectified aerial image taken from [USGS Earth Explorer](https://earthexplorer.usgs.gov/)    

```java
public class ExampleLayer extends AbstractLayer {

    public static final String TAG = Executors.class.getSimpleName();

    final int[] layerARGB;
    final int layerWidth;
    final int layerHeight;

    final GeoPoint upperLeft;
    final GeoPoint upperRight;
    final GeoPoint lowerRight;
    final GeoPoint lowerLeft;

    private final MetaShape metaShape;

    public ExampleLayer(Context plugin, final String name, final String uri) {
        super(name);

        this.upperLeft = GeoPoint.createMutable();
        this.upperRight = GeoPoint.createMutable();
        this.lowerRight = GeoPoint.createMutable();
        this.lowerLeft = GeoPoint.createMutable();

        final Bitmap bitmap = BitmapFactory.decodeFile(uri);
        upperLeft.set(34.424180961, -119.874962718);
        upperRight.set(34.424180961, -119.836972838);
        lowerRight.set(34.404365022, -119.836972838);
        lowerLeft.set(34.404365022, -119.874962718);

        layerWidth = bitmap.getWidth();
        layerHeight = bitmap.getHeight();
        layerARGB = new int[layerHeight * layerWidth];

        bitmap.getPixels(layerARGB, 0, layerWidth, 0, 0, layerWidth,
                layerHeight);

        metaShape = new MetaShape(UUID.randomUUID().toString()) {
            @Override
            public GeoPointMetaData[] getMetaDataPoints() {
                return GeoPointMetaData.wrap(ExampleLayer.this.getPoints());
            }
            @Override
            public GeoPoint[] getPoints() { 
                return ExampleLayer.this.getPoints(); 
            }
            @Override
            public GeoBounds getBounds(MutableGeoBounds bounds) { 
                return ExampleLayer.this.getBounds(); 
            }
        };
        metaShape.setMetaString("callsign", TAG);
        metaShape.setMetaString("shapeName", TAG);
        metaShape.setType("hello_world_layer");
        metaShape.setMetaString("menu", PluginMenuParser.getMenu(plugin, "menus/layer_menu.xml"));
        bitmap.recycle();
    }

    public GeoBounds getBounds() { return GeoBounds.createFromPoints(getPoints()); }

    public GeoPoint[] getPoints() { return new GeoPoint[] { upperLeft, upperRight, lowerRight, lowerLeft }; }

    public MetaShape getMetaShape() { return metaShape; }
}
```

The key parts of the layer to be concerned with are the `GeoPoint` values for the corners of the image and the image bitmap being loaded into the `layerARGB` array. The `GLExampleLayer` class relies on these values in addition to the the `metaShape`. In order to make the class more versatile to rendering images that are received over the network additional methods could be added to update the image bitmap array and corners.

```java
private void initLayer() {
    Button addImgLayer = fragmentView.findViewById(R.id.map_add_img_layer);
    Util.setButtonToast(atakCtx, addImgLayer, "add ");
    GLLayerFactory.register(GLExampleLayer.SPI);
    // check if layer is on Overlay Render stack to set state of button accordingly
    List<Layer> overlays = mapView.getLayers(RenderStack.MAP_SURFACE_OVERLAYS);
    for (Layer layer: overlays) {
        if (this.exampleLayer != null && layer.hashCode() == this.exampleLayer.hashCode()) {
            addImgLayer.setSelected(true);
            addImgLayer.setText(pluginCtx.getString(R.string.remove_image_layer));
            break;
        }
    }
    addImgLayer.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String absImgPath =  Util.assetToFile(pluginCtx,
                                                  "tools/helloworld/isla_vista_ucsb.png",
                                                  "isla_vista_ucsb.png");
            synchronized (hwReceiver.instance()) {
                if (exampleLayer == null)
                    exampleLayer = new ExampleLayer(pluginCtx,
                                                    "HelloWorld Test Layer", absImgPath);
            }
            if (addImgLayer.isSelected()) {
                // remove the layer from the map
                addImgLayer.setText(pluginCtx.getString(R.string.add_image_layer));
                mapView.removeLayer(RenderStack.MAP_SURFACE_OVERLAYS, exampleLayer);
            } else {
                // add the layer to the map
                addImgLayer.setText(pluginCtx.getString(R.string.remove_image_layer));
                mapView.addLayer(RenderStack.MAP_SURFACE_OVERLAYS, exampleLayer);
                exampleLayer.setVisible(true);
                // Pan and zoom to the layer
                ATAKUtilities.scaleToFit(mapView, exampleLayer.getPoints(),
                                         mapView.getWidth(), mapView.getHeight());
            }
            // Refresh Overlay Manager
            AtakBroadcast.getInstance().sendBroadcast(new Intent(
                HierarchyListReceiver.REFRESH_HIERARCHY));
            addImgLayer.setSelected(!addImgLayer.isSelected());
        }
    });
}
```

The above code provides the functionality to the "Add Image Layer" button. Ensure you register the `GLExampleLayer.SPI` before attempting to add the image layer for the map. The first part of the method just checks to the maps overlays to see if the image layer is already present to properly set the state of the toggle button. The contents of the `onClick` method show the necessary steps to adding or removing the image layer. One other part to emphasize is sending an intent `HierarchyListReceiver.REFRESH_HIERARCHY` to ensure the map rendering actually processes the new or removed layer.   

```java
MapGroup doGroup = mapView.getRootGroup().findMapGroup("Drawing Objects");
MapGroup group = doGroup.addGroup("Test Rectangle");
GeoPoint[] points = createKmRectangle(1, 0.5, 5);
DrawingRectangle drawingRectangle = new DrawingRectangle(
    group, 
    GeoPointMetaData.wrap(points[0]), 
    GeoPointMetaData.wrap(points[1]),   
    GeoPointMetaData.wrap(points[2]), 
    GeoPointMetaData.wrap(points[3]),
    UUID.randomUUID().toString());
drawingRectangle.setStyle(0);
drawingRectangle.setLineStyle(0);
drawingRectangle.setFillColor(0x00000000);
drawingRectangle.setStrokeColor(Color.WHITE);
drawingRectangle.setMetaString("shape_name", "Test Rectangle");
drawingRectangle.setMetaString("title", "Test Rectangle");
drawingRectangle.setMetaString("callsign", "Test Rectangle");
// then you need to add this to the parent group.
doGroup.addItem(drawingRectangle);
// example on how to dispatch this rectangle externally
final CotEvent cotEvent = CotEventFactory.createCotEvent(drawingRectangle);
CotMapComponent.getExternalDispatcher().dispatchToBroadcast(cotEvent);
// Pan and zoom to the rectangle
ATAKUtilities.scaleToFit(mapView, points, mapView.getWidth(), mapView.getHeight());
```

The lines above show the implementation of the "Add Rectangle" button. Here we need to add the rectangle to the "Drawing Objects" group for proper rendering. The example shows how to modify the line style, fill color, line color, and meta data information of the shape.  [&#8657;](#contents)

<br>

## Routes & Map Movement

Source Code: [`MapFragment.initRoutes`](../app/src/main/java/com/atakmap/android/demohelloworld/fragments/MapFragment.java#L315-427), [`MapFragment.initFlightThread`](../app/src/main/java/com/atakmap/android/demohelloworld/fragments/MapFragment.java#L437-481)  
Resources:  [`tab_map.xml`](../app/src/main/res/layout/tab_map.xml)  
The following implementations cover the "Add Walking Route", "Add Fly Route", "Start/Stop Flight", and "Focus F-35" buttons functionality. 

```java
Route newRoute = new Route(mapView, routeName, Color, pointPrefix, UniqueID);
Marker[] markers = new Marker[4];
for (int i = 0; i < points.length; i++) {
    markers[i] = Route.createWayPoint(GeoPointMetaData.wrap(points[i]), UUID.randomUUID().toString());
}
newRoute.addMarkers(0, markers);
MapGroup mapGroup = mapView.getRootGroup().findMapGroup("Route");
mapGroup.addItem(newRoute);
// optional
walkRoute.persist(mapView.getMapEventDispatcher(), null, hwReceiver.getClass());
ATAKUtilities.scaleToFit(mapView, points, mapView.getWidth(), mapView.getHeight());
```

Both the walking route and flight route use the same logic as depicted above to create a route which can be activated within ATAK like your traditional Google/Apple Maps navigation. They each create a route from a list of `GeoPoint` objects and add the route to the "Route" map group. The walking route is designed to create a box/loop of 30 meter (approximately 32 yard) stretches that a user could walk along to test out the ATAK navigation feature. The optional steps you can take for your path creation are making it "persist" through app restarts, and `scaleToFit` which will zoom apply the maximum camera zoom and positioning to make the path occupy the majority of the map view. This is vary useful API function to maximize the zoom for the user to see all the important data points with out having to mess with the map controls themselves. The flight route button is primarily included to show the path that the "Start/Stop Flight" will follow when moving the vehicle model on the map. 

```java
 try {
     for (int i = 0; i < FLIGHT_PATH.length; i++) {
         GeoPoint point = FLIGHT_PATH[i];
         aircraft.setCenterPoint(new GeoPointMetaData(point));
         double heading = (i+1 < FLIGHT_PATH.length) ?
             point.bearingTo(FLIGHT_PATH[i+1]) : 220;
         aircraft.setHeading(heading);
         GeoPoint cameraPosition = GeoCalculations.pointAtDistance(
             point, (heading + 180) % 360, 50);
         cameraPosition = new GeoPoint(cameraPosition, GeoPoint.Access.READ_WRITE);
         cameraPosition.set(point.getAltitude() + 18);
         mapView.getRenderer3().lookFrom(cameraPosition,
                                         heading, // Azimuth look degrees from north
                                         -.6 * GeoCalculations.inclinationTo(point, cameraPosition), // Elevation
                                         IMapRendererEnums.CameraCollision.Ignore,
                                         false
                                        );
         Thread.sleep(5000);
     }
     CameraController.Programmatic.rotateTo(mapView.getRenderer3(), 0, false);
     CameraController.Programmatic.panTo(mapView.getRenderer3(),
                                         FLIGHT_PATH[FLIGHT_PATH.length-1], false);
     CameraController.Programmatic.tiltTo(mapView.getRenderer3(), 70, false);
     CameraController.Programmatic.zoomTo(mapView.getRenderer3(),
                                          OSMUtils.mapnikTileResolution(18), true);
     toggleButton.setSelected(false);

     // need to set text on main thread
     new Handler(Looper.getMainLooper()).post(new Runnable() {
         @Override
         public void run() {
             toggleButton.setText(pluginCtx.getText(R.string.start_fly));
         }
     });
 } catch (InterruptedException e) {
     Log.d(TAG, "ENDING FLIGHT EARLY");
 } catch (Exception e) {
     Log.d(TAG, "ISSUE TRYING TO EXIT EARLY " + e);
     e.printStackTrace();
 }
```

The interesting part of the "Start/Stop Flight" button is included in the snippet above. All of the code above is within the flight thread which is why updates to the pane UI must be wrapped and executed in the `MainLooper` since UI updates must be executed on the primary thread. The map movement and model updates provided by the ATAK API are thread safe. The lines in the for loop demonstrate how one can use the API methods to calculate a camera position that provides a "third person over the shoulder" perspective aligned with the view of a vehicle model. After to flight points have all been visited there is a set of example programmatic camera control actions used to set the view of the map camera. [&#8657;](#contents)

<br>





