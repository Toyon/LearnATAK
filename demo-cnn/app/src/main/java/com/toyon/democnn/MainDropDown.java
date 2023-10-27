package com.toyon.democnn;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.toyon.democnn.adapters.Animal;
import com.toyon.democnn.adapters.AnimalAdapter;
import com.toyon.democnn.plugin.R;
import com.toyon.democnn.util.Util;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapEvent;
import com.atakmap.android.maps.MapEventDispatcher;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/** Broadcast receiver and accompanying logic for UI functionality of primary plugin view. */
public class MainDropDown extends DropDownReceiver
        implements DropDown.OnStateListener, MapEventDispatcher.MapEventDispatchListener {

    public static final String SHOW_MAIN_PANE = "com.toyon.democnn.SHOW_MAIN_PANE";
    private final View paneView;
    private final AnimalAdapter animalAdapter;

    protected MainDropDown(MapView mapView, final Context pluginContext) {
        super(mapView);
        paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.pane_main, null);

        RecyclerView animalList = paneView.findViewById(R.id.animalList);
        animalList.setLayoutManager(new LinearLayoutManager(paneView.getContext(),
                LinearLayoutManager.VERTICAL, false));
        animalAdapter = new AnimalAdapter(new ArrayList<>(), getMapView());
        animalList.setAdapter(animalAdapter);
        refreshAdapter();

        Button cameraButton = paneView.findViewById(R.id.buttonCamera);
        MainDropDown thisReceiver = this;
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thisReceiver.setRetain(true);
                Intent cameraIntent = new Intent();
                cameraIntent.setAction(CameraYoloDropDown.SHOW_CAMERA_PANE);
                AtakBroadcast.getInstance().sendBroadcast(cameraIntent);
            }
        });
        Util.setButtonToast(mapView.getContext(), cameraButton, "Take Picture");

        Button reportButton = paneView.findViewById(R.id.buttonReport);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thisReceiver.setRetain(true);
                Intent reportIntent = new Intent();
                reportIntent.setAction(ReportDropDown.SHOW_REPORT_PANE);
                AtakBroadcast.getInstance().sendBroadcast(reportIntent);
            }
        });
        Util.setButtonToast(mapView.getContext(), reportButton, "Report Pet Sighting");

    }

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
            refreshAdapter();
            getMapView().getMapEventDispatcher().addMapEventListener(this);
        } else
            getMapView().getMapEventDispatcher().removeMapEventListener(this);
    }

    /** Update the RecyclerView with items based on Animal Map Markers with photo attachments */
    private void refreshAdapter() {
        ArrayList<Animal> petSightings = new ArrayList<>();
        Collection<MapItem> allItems =  getMapView().getRootGroup().getAllItems();
        allItems.forEach(mapItem -> {
            if (mapItem != null && mapItem.getGroup().getFriendlyName().equals("Animals")) {
                Animal tempAnimal = new Animal(mapItem);
                if (tempAnimal.img != null)
                    petSightings.add(tempAnimal);
            }
        });
        animalAdapter.updateData(petSightings);
    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownClose() { }

    @Override
    public void onDropDownSizeChanged(double v, double v1) { }

    @Override
    protected void disposeImpl() { }

    // MapEventDispatcher.MapEventDispatchListener
    @Override
    public void onMapEvent(MapEvent mapEvent) {
        if (Objects.equals(mapEvent.getType(), MapEvent.ITEM_ADDED) ||
                Objects.equals(mapEvent.getType(), MapEvent.ITEM_REMOVED)) {
            refreshAdapter();
        }
    }
}
