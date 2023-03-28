/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.demohelloworld.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.drawing.mapItems.DrawingRectangle;
import com.atakmap.android.hierarchy.HierarchyListReceiver;
import com.atakmap.android.icons.UserIcon;
import com.atakmap.android.importexport.CotEventFactory;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.demohelloworld.HelloWorldDropDown;
import com.atakmap.android.demohelloworld.Icon2dDropDown;
import com.atakmap.android.demohelloworld.Icon3dDropDown;
import com.atakmap.android.demohelloworld.plugin.R;
import com.atakmap.android.demohelloworld.samplelayer.ExampleLayer;
import com.atakmap.android.demohelloworld.samplelayer.GLExampleLayer;
import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.MapView.RenderStack;
import com.atakmap.android.demohelloworld.Util;
import com.atakmap.android.maps.Marker;
import com.atakmap.android.routes.Route;
import com.atakmap.android.user.PlacePointTool;
import com.atakmap.android.util.ATAKUtilities;
import com.atakmap.android.vehicle.model.VehicleModel;
import com.atakmap.android.vehicle.model.VehicleModelInfo;
import com.atakmap.comms.CotDispatcher;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;
import com.atakmap.coremap.maps.coords.GeoCalculations;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.coords.GeoPointMetaData;
import com.atakmap.coremap.maps.time.CoordinatedTime;
import com.atakmap.map.CameraController;
import com.atakmap.map.layer.Layer;
import com.atakmap.map.layer.opengl.GLLayerFactory;
import com.atakmap.map.layer.raster.osm.OSMUtils;

import com.atakmap.android.vehicle.model.VehicleModelCache;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import gov.tak.api.engine.map.IMapRendererEnums;

/** Demonstrate ATAK Mapping API features. */
public class MapFragment extends Fragment {

    private final String TAG = MapFragment.class.getSimpleName();
    private Context pluginCtx;
    private Context atakCtx;
    private MapView mapView;
    private View fragmentView;
    private HelloWorldDropDown hwReceiver;
    private Icon3dDropDown icon3dDropDown;

    private ExampleLayer exampleLayer;

    private static final GeoPoint[] FLIGHT_PATH = new GeoPoint[] {
            new GeoPoint(37.619,-122.375,3.69), // SFO
            new GeoPoint(38.312, -122.251, 300), // Napa
            new GeoPoint(39.308, -120.892, 1800), // Scotts Flat Reservoir
            new GeoPoint(39.322, -120.319, 2700), // Donner
            new GeoPoint(39.305, -119.893, 3000), // Mt Rose
            new GeoPoint(38.952, -119.955, 2400), // South Lake Tahoe
            new GeoPoint(38.694, -120.087, 2800), // Kirkwood
            new GeoPoint(38.025, -120.414, 632), // Colombia Airport
    };

    private static final String FLIGHT_PATH_UID = "demo.helloworld.mountain_flight_path";
    private static final String AIRCRAFT_UID = "demo.helloworld.demo_aircraft";
    private Thread flyThread;

    private static final int MAX_ZOOM = 20;

    /** Create an instance of the MapFragment */
    public MapFragment construct(final HelloWorldDropDown receiver) {
        pluginCtx = receiver.getPluginCtx();
        mapView = receiver.getMapView();
        atakCtx = receiver.getMapView().getContext();
        hwReceiver = receiver;
        return this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = LayoutInflater.from(pluginCtx).inflate(
                R.layout.tab_map, container, false);
        initIconModels();
        initMarkers();
        initMapZoomLevels();
        initMapTilt();
        initLayer();
        initDrawing();
        initRoutes();
        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GLLayerFactory.unregister(GLExampleLayer.SPI);
    }

    /** Setup buttons that open dropdown receivers showing available icons and 3D vehicle models. */
    private void initIconModels() {
        Button showIcons = fragmentView.findViewById(R.id.map_show_icons);
        Util.setButtonToast(atakCtx, showIcons, "Show Dropdown for 2D map icons");
        showIcons.setOnClickListener(view -> {
            hwReceiver.setRetain(true); // keep the base drop down in the stack to navigate back to
            Intent mapIconIntent = new Intent();
            mapIconIntent.setAction(Icon2dDropDown.SHOW_ICON_PANE);
            AtakBroadcast.getInstance().sendBroadcast(mapIconIntent);
        });

        icon3dDropDown = new Icon3dDropDown(mapView, pluginCtx);
        Button showModels = fragmentView.findViewById(R.id.map_show_models);
        Util.setButtonToast(atakCtx, showModels, "Show Dropdown for 3D vehicle models");
        showModels.setOnClickListener(view -> {
            hwReceiver.setRetain(true);
            icon3dDropDown.show();
        });
    }

    /** Setup buttons that place markers on the map  */
    private void initMarkers() {
        Button addMarker = fragmentView.findViewById(R.id.map_create_marker);
        Util.setButtonToast(atakCtx, addMarker, "Add Aircraft icon centered at current map view");
        addMarker.setOnClickListener(view -> {
            PlacePointTool.MarkerCreator mc = new PlacePointTool.MarkerCreator(
                    mapView.getPointWithElevation());
            mc.setUid(UUID.randomUUID().toString());
            mc.setCallsign("HelloCar");
            // atoms - friend - Ground (remainder MIL-STD-2525B not specified)
            // https://www.mitre.org/sites/default/files/pdf/09_4937.pdf
            // page 9 CoT Type Field Details
            mc.setType("a-f-G");
            mc.showCotDetails(false);
            mc.setNeverPersist(true);
            Marker m = mc.placePoint();
            m.setStyle(m.getStyle()
                    | Marker.STYLE_ROTATE_HEADING_MASK
                    | Marker.STYLE_ROTATE_HEADING_NOARROW_MASK);
            m.setTrack(45, 10);
            m.setMetaString(UserIcon.IconsetPath,
                    "34ae1613-9645-4222-a9d2-e5f243dea2865/Transportation/Car8.png");
            m.setColor(Color.parseColor("#fccf03"));

        });

        Button addCotMarker = fragmentView.findViewById(R.id.map_create_cot_marker);
        Util.setButtonToast(atakCtx, addCotMarker, "Add Marker by sending CoT message");
        addCotMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }

    /** Setup buttons that modify the zoom level of the map.  */
    private void initMapZoomLevels() {
        LinearLayout zoomLevelsGroup = fragmentView.findViewById(R.id.zoom_levels);
        for (int i = 0; i <= MAX_ZOOM; i++) {
            Button btn = new Button(pluginCtx);
            btn.setText(String.valueOf(i));
            zoomLevelsGroup.addView(btn);
            // get ground sample distance for zoom level in meters
            double gsd = OSMUtils.mapnikTileResolution(i);
            Util.setButtonToast(atakCtx, btn, String.format(Locale.US,
                    "Zoom %,.3f meters / pixel", gsd));
            btn.setOnClickListener(view ->
                    CameraController.Programmatic.zoomTo(mapView.getRenderer3(), gsd, true));
        }
    }

    /** Setup buttons the modify the tilt or pitch angle view of the maps globe. */
    private void initMapTilt() {
        LinearLayout tiltAngleGroup = fragmentView.findViewById(R.id.tilt_angles);
        double[] angles = new double[] {0, 30, 45, 60, 75, 90 };
        for (double angle : angles) {
            Button btn = new Button(pluginCtx);
            btn.setText(String.valueOf((int) angle));
            Util.setButtonToast(atakCtx, btn, String.format(Locale.US,
                    "Tilt mapview %d degrees", (int) angle));
            btn.setOnClickListener(view -> {
                CameraController.Programmatic.tiltTo(mapView.getRenderer3(), angle, true);
            });
            tiltAngleGroup.addView(btn);
        }
    }

    /** Setup button that adds a custom image layer. */
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
                Log.d(TAG, "Image Layer: " + absImgPath);
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

    /** Setup button that draws a rectangle on the map. */
    private void initDrawing() {
        Button addRectangleBtn = fragmentView.findViewById(R.id.map_add_rectangle);
        Util.setButtonToast(atakCtx, addRectangleBtn, "Draw a rectangle");
        addRectangleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapGroup doGroup = mapView.getRootGroup().findMapGroup("Drawing Objects");
                MapGroup group = doGroup.addGroup("Test Rectangle");
                GeoPoint[] points = createKmRectangle(1, 0.5, 5);
                DrawingRectangle drawingRectangle = new DrawingRectangle(group,
                        GeoPointMetaData.wrap(points[0]), GeoPointMetaData.wrap(points[1]),
                        GeoPointMetaData.wrap(points[2]), GeoPointMetaData.wrap(points[3]),
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
            }
        });
    }

    /** Setup buttons that involve routes on the map. */
    private void initRoutes() {
        Button addWalkRouteBtn = fragmentView.findViewById(R.id.map_add_walk_route);
        Util.setButtonToast(atakCtx, addWalkRouteBtn, "Create a short walking route");
        addWalkRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start point on map center, recommended to lock to you location first
                GeoPoint refPoint = mapView.getPointWithElevation().get();
                Route walkRoute = new Route(mapView,
                        "Demo Walk Route",
                        Color.WHITE,
                        "CP",
                        UUID.randomUUID().toString());
                // make a walking loop centered at the map view center walking 30 meters / ~30 yards
                GeoPoint[] points = createKmRectangle(.03, .03, refPoint.getAltitude());
                Marker[] markers = new Marker[4];
                for (int i = 0; i < points.length; i++) {
                    markers[i] = Route.createWayPoint(GeoPointMetaData.wrap(points[i]), UUID.randomUUID().toString());
                }
                walkRoute.addMarkers(0, markers);
                MapGroup mapGroup = mapView.getRootGroup().findMapGroup("Route");
                mapGroup.addItem(walkRoute);
                walkRoute.persist(mapView.getMapEventDispatcher(), null, hwReceiver.getClass());
                ATAKUtilities.scaleToFit(mapView, points, mapView.getWidth(), mapView.getHeight());
            }
        });

        Button addFlyRouteBtn = fragmentView.findViewById(R.id.map_add_fly_route);
        Util.setButtonToast(atakCtx, addFlyRouteBtn, "Create a flight route");
        addFlyRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collection<MapGroup> groupList = mapView.getRootGroup().getMapGroups();
                Log.d(TAG, "Map Groups: " + Arrays.toString(groupList.toArray()));
                MapGroup mapGroup = mapView.getRootGroup().findMapGroup("Route");
                if (null != mapGroup.deepFindUID(FLIGHT_PATH_UID)) {
                    Toast.makeText(atakCtx, "Flight route already added", Toast.LENGTH_LONG).show();
                    return;
                }
                Route flyRoute = new Route(mapView, "Demo Flight Path Route",
                        Color.RED, "Point", FLIGHT_PATH_UID);
                Marker[] markers = new Marker[FLIGHT_PATH.length];
                for (int i = 0; i < FLIGHT_PATH.length; i++) {
                    markers[i] = Route.createWayPoint(
                            GeoPointMetaData.wrap(FLIGHT_PATH[i]), FLIGHT_PATH_UID + i);
                }
                flyRoute.addMarkers(0, markers);
                    mapGroup.addItem(flyRoute);

                ATAKUtilities.scaleToFit(mapView, FLIGHT_PATH, mapView.getWidth(), mapView.getHeight());
            }
        });

        Button flyOnRoute = fragmentView.findViewById(R.id.map_toggle_flight);
        Util.setButtonToast(atakCtx, flyOnRoute, "Animate plane flight along flight path");
        flyOnRoute.setSelected((flyThread != null && flyThread.isAlive()));
        flyOnRoute.setText(pluginCtx.getText((flyThread != null && flyThread.isAlive()) ? R.string.stop_fly : R.string.start_fly));
        flyOnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapGroup vehicleGroup = mapView.getRootGroup().findMapGroup("Vehicles");
                // remove previous instance of vehicle model if there is one
                MapItem aircraft = vehicleGroup.deepFindUID(AIRCRAFT_UID);
                if (aircraft != null) {
                    try { vehicleGroup.removeItem(aircraft); }
                    catch (Exception e) { Log.d(TAG, "FAILED TO REMOVE AIRPLANE"); }
                }
                if (flyThread != null && flyThread.isAlive()) {
                    Toast.makeText(atakCtx, "Stopping Flight", Toast.LENGTH_LONG).show();
                    flyThread.interrupt();
                } else {
                    Toast.makeText(atakCtx, "Starting Flight", Toast.LENGTH_LONG).show();
                    VehicleModelInfo f35Info = new VehicleModelInfo("aircraft",
                            "Demo-A-C",
                            new File(VehicleModelCache.DIR + "/aircraft/F-35.zip"));
                    VehicleModel f35Model = new VehicleModel(f35Info,
                            GeoPointMetaData.wrap(FLIGHT_PATH[0]), AIRCRAFT_UID);
                    f35Model.setModelScale(new double[]{ 10, 10, 10 });
                    vehicleGroup.addItem(f35Model);
                    initFlightThread(f35Model, flyOnRoute);
                    flyThread.start();
                }
                flyOnRoute.setSelected(!flyOnRoute.isSelected());
                flyOnRoute.setText((flyOnRoute.isSelected()) ? "Stop Flight" : "Start Flight");
            }
        });

        Button focusItemBtn = fragmentView.findViewById(R.id.map_focus_item);
        Util.setButtonToast(atakCtx, focusItemBtn, "Focus on animated flying plane");
        focusItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapGroup vehicleGroup = mapView.getRootGroup().findMapGroup("Vehicles");
                MapItem aircraft = vehicleGroup.deepFindUID(AIRCRAFT_UID);
                if (aircraft == null) {
                    Toast.makeText(
                            atakCtx,
                            "Ensure flight was started and plane is on map",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // use intent to programmatically focus on plane (similar to clicking on item)
                Intent focusIntent = new Intent();
                focusIntent.setAction("com.atakmap.android.maps.FOCUS");
                focusIntent.putExtra("uid", AIRCRAFT_UID);
                AtakBroadcast.getInstance().sendBroadcast(focusIntent);
                // set up the Intent to show the radial menu
                Intent menuIntent = new Intent();
                menuIntent.setAction("com.atakmap.android.maps.SHOW_MENU");
                menuIntent.putExtra("uid", AIRCRAFT_UID);
            }
        });
    }


    /**
     * Make 3D vehicle model fly along a list of GeoPoints angling the map camera behind the
     * aircraft with an "over the shoulder" view point.
     *
     * @param aircraft MapItem vehicle object to move for flight
     * @param toggleButton The toggle button that determines the visibility of the flying aircraft
     */
    private void initFlightThread(VehicleModel aircraft, Button toggleButton) {
        flyThread = new Thread(new Runnable() {
            public void run() {
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
            }
        });
    }

    /**
     * Create a set of GeoPoints (coordinates) for a rectangle centered at the current map view
     * focal point.
     *
     * @param kmWidth desired width of rectangle in kilometers
     * @param kmHeight desired height of rectangle in kilometers
     * @param altitude desired altitude in meters
     * @return [ur, lr, ll, ul]
     */
    private GeoPoint[] createKmRectangle(double kmWidth, double kmHeight, final double altitude) {
        GeoPoint center = mapView.getPoint().get();
        center = new GeoPoint(center.getLatitude(), center.getLongitude(), altitude);
        // Calculate the hypotenuse distance for the rectangle points using Pythagorean Theorem
        double hyp = Math.sqrt(Math.pow(kmWidth*1000/2, 2) + Math.pow(kmHeight*1000/2, 2));
        // based on trig function cos(theta) = adjacent side / hypotenuse
        double urAz = Math.toDegrees(Math.acos((kmHeight*1000/2) / hyp)); // azimuth to upper right
        GeoPoint ur = GeoCalculations.pointAtDistance(center, urAz, hyp);
        GeoPoint lr = GeoCalculations.pointAtDistance(center, 180 - urAz, hyp);
        GeoPoint ll = GeoCalculations.pointAtDistance(center, urAz + 180.0, hyp);
        GeoPoint ul = GeoCalculations.pointAtDistance(center, 360.0 - urAz, hyp);
        // return new geopoints with the specified altitude
        return new GeoPoint[] {
                new GeoPoint(ur.getLatitude(), ur.getLongitude(), altitude),
                new GeoPoint(lr.getLatitude(), lr.getLongitude(), altitude),
                new GeoPoint(ll.getLatitude(), ll.getLongitude(), altitude),
                new GeoPoint(ul.getLatitude(), ul.getLongitude(), altitude)
        };
    }
}
