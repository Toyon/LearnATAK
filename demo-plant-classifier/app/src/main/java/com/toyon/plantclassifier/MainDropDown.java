/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapItem;
import com.toyon.plantclassifier.database.PlantsViewModel;
import com.toyon.plantclassifier.adapters.Plant;
import com.toyon.plantclassifier.adapters.PlantCategoryAdapter;
import com.toyon.plantclassifier.plugin.R;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.user.PlacePointTool;
import com.atakmap.coremap.maps.coords.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * This class controls all of the logic for the main pane.<br>
 * It contains a list of all the classified plants and allows a user to access the camera to
 * classify even more plants upon receiving a classified plant from the ReportDropDown receiver,
 * addMarker() will be called which will run GPSTracker to get a user's current location and
 * add a marker to the map demonstrating where the plant was found
 */
public class MainDropDown extends DropDownReceiver implements DropDown.OnStateListener {

    public static final String TAG = MainDropDown.class.getSimpleName();
    public static final String SHOW_MAIN_PANE = "com.toyon.plantclassifier.SHOW_MAIN_PANE";
    private final View paneView;
    public Context pluginCtx;
    private final PlantsViewModel viewPlants;

    private final CameraActivity.CameraDataListener cdl = new CameraActivity.CameraDataListener();
    private final CameraActivity.CameraDataReceiver cdr = new CameraActivity.CameraDataReceiver() {
        public void onCameraDataReceived(Bitmap b) {
            //convert bitmap to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Intent classifyIntent = new Intent();
            classifyIntent.setAction(ClassifyPlantDropDown.SHOW_CLASSIFY_PANE);
            classifyIntent.putExtra("image", byteArray);
            AtakBroadcast.getInstance().sendBroadcast(classifyIntent);
        }
    };

    protected MainDropDown(MapView mapView, final Context pluginContext){
        super(mapView);
        pluginCtx = pluginContext;
        paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.main_layout, null);
        RecyclerView plantList = paneView.findViewById(R.id.plant_list);
        plantList.setLayoutManager(new LinearLayoutManager(paneView.getContext(), LinearLayoutManager.VERTICAL, false));
        viewPlants = new ViewModelProvider(ViewTreeViewModelStoreOwner.get(getMapView())).get(PlantsViewModel.class);
        PlantCategoryAdapter plantCategoryAdapter = new PlantCategoryAdapter(viewPlants);
        plantList.setAdapter(plantCategoryAdapter);
        Button cameraButton = paneView.findViewById(R.id.btn_launch_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdl.register(getMapView().getContext(), cdr);
                // launch default camera in activity outside of ATAK classloader paradigm
                Intent intent = new Intent();
                intent.setClassName("com.toyon.plantclassifier.plugin",
                        "com.toyon.plantclassifier.CameraActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getMapView().getContext().startActivity(intent);
            }
        });
    }

    public Context getContext(){
        return getMapView().getContext();
    }

    @Override
    public void onReceive(Context context, Intent intent){
        final String action = intent.getAction();
        if(intent.getBooleanExtra("report", false)){
            Plant plant = intent.getParcelableExtra("Plant");
            addMarker(context, plant);
        }
        if(action == null)
            return;
        if(action.equals(SHOW_MAIN_PANE))
            showDropDown(paneView,
                    HALF_WIDTH, FULL_HEIGHT, //landscape
                    FULL_WIDTH, TWO_THIRDS_HEIGHT, //Portrait
                    false, this );
    }

    @Override
    public void onDropDownVisible(boolean visible){ }

    public void addMarker(Context context, Plant plant){
        GPSTracker gpsTracker = new GPSTracker(context);

        // use GPS tracker if it can get location, otherwise use ATAK self marker location
        if(gpsTracker.canGetLocation()){
            plant.coordinates = new GeoPoint(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        } else {
            gpsTracker.showSettingsAlert();
            plant.coordinates = getMapView().getSelfMarker().getPoint();
        }

        String plantTypeFormed = plant.commonName.trim()
                .replaceAll("[^a-zA-Z0-9]", "-");

        String uid = UUID.randomUUID().toString();
        MapItem item = new PlacePointTool.MarkerCreator(plant.coordinates)
                .setUid(uid).setType("a-n-G").showCotDetails(false)
                .setIconPath("34ae1613-9645-4222-a9d2-e5f243dea2865/Plants/Tree3.png")
                .setCallsign(plantTypeFormed).setColor(Color.rgb(32, 188, 63))
                .placePoint();
        item.setMetaBoolean("archive", true);

        item.refresh(getMapView().getMapEventDispatcher(), null,
                this.getClass());
        // it is important to get map item UID to enable focus map item click
        plant.uid = item.getUID();
        viewPlants.insert(plant.toDBEntity(this));
    }


    @Override
    public void onDropDownSelectionRemoved() {}

    @Override
    public void onDropDownClose() {}

    @Override
    public void onDropDownSizeChanged(double v, double v1) {}

    @Override
    public void disposeImpl() { }


}
