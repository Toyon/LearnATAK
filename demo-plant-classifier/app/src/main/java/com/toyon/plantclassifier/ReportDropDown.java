/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.ipc.AtakBroadcast;
import com.toyon.plantclassifier.adapters.Plant;
import com.toyon.plantclassifier.plugin.R;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.preference.AtakPreferences;

import java.io.ByteArrayOutputStream;

/**
 * This class receives a classified plant from ClassifyPlantDropDown and shows the user what plant was found
 * if the user wants to add the plant to their list, they can click submit which will take them back to the main screen
 */
public class ReportDropDown extends DropDownReceiver implements DropDown.OnStateListener {

    private static final String TAG = ReportDropDown.class.getSimpleName();
    public static final String SHOW_REPORT_PANE = "com.toyon.plantclassifier.SHOW_REPORT_PANE";
    private final View paneView;
    private final Context pluginCtx;
    private final AtakPreferences atakPref;
    private boolean prefCenterMarker = false;
    private final EditText locationInfo;
    private final EditText note;

    public Plant plant;
    private final TextView plantTypeText;
    private final TextView confidenceText;
    private final TextView edibleText;
    private final TextView commonNameText;
    private final ImageView previewImage;
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

    protected ReportDropDown(MapView mapView, final Context pluginContext){
        super(mapView);
        pluginCtx = pluginContext;
        paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.report_layout, null);
        plantTypeText = paneView.findViewById(R.id.scientific_name);
        previewImage = paneView.findViewById(R.id.captured_plant);
        edibleText = paneView.findViewById(R.id.edibility);
        commonNameText = paneView.findViewById(R.id.common_name);
        confidenceText = paneView.findViewById(R.id.certainty_level);


        atakPref = new AtakPreferences(getMapView());
        locationInfo = paneView.findViewById(R.id.entry_location);
        note = paneView.findViewById(R.id.entry_extra_notes);


        Button submitReportBtn = paneView.findViewById(R.id.save_record);
        submitReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                plant.location = locationInfo.getText().toString();
                plant.notes = note.getText().toString();

                locationInfo.setText("");
                note.setText("");

                Intent mainIntent = new Intent();
                mainIntent.putExtra("Plant", plant);
                mainIntent.putExtra("report", true);
                mainIntent.setAction(MainDropDown.SHOW_MAIN_PANE);
                AtakBroadcast.getInstance().sendBroadcast(mainIntent);
            }
        });

        Button homeButton = paneView.findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mainIntent = new Intent();
                mainIntent.setAction(MainDropDown.SHOW_MAIN_PANE);
                AtakBroadcast.getInstance().sendBroadcast(mainIntent);
            }
        });

        Button backButton = paneView.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
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

    private void setPlantInfo(){
        plantTypeText.setText(plant.plantType);
        previewImage.setImageBitmap(plant.img);
        edibleText.setText(plant.edible);
        commonNameText.setText(plant.commonName);
        confidenceText.setText(plant.certainty);
    }

    @Override
    public void onReceive(Context context, Intent intent){
        final String action = intent.getAction();

        plant = (Plant)intent.getParcelableExtra("Plant");
        if(action == null)
            return;
        if(action.equals(SHOW_REPORT_PANE))
            showDropDown(paneView,
                    FULL_WIDTH, FULL_HEIGHT,
                    FULL_WIDTH, FULL_HEIGHT,
                    false ,this );
    }

    @Override
    public void onDropDownVisible(boolean visible) {
        if (visible) {
            setPlantInfo();
        }
    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownClose() { }

    @Override
    public void onDropDownSizeChanged(double v, double v1) { }

    @Override
    protected void disposeImpl() { }

}
