/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.insectclassifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.toyon.insectclassifier.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;

import com.atakmap.coremap.log.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

/** Broadcast receiver and accompanying logic for UI functionality of main plugin view. */
public class MainDropDown extends DropDownReceiver implements OnStateListener {

    public static final String TAG = MainDropDown.class.getSimpleName();
    public static final String SHOW_PLUGIN = "com.toyon.insectclassifier.SHOW_PLUGIN";
    public static final String ADD_TO_LIST = "com.toyon.insectclassifier.PluginTemplateDropDownReceiver.ADD_TO_LIST";

    private final View templateView;
    private final InsectClassifierAdapter adapter;
    private final CameraActivity.CameraDataListener cdl = new CameraActivity.CameraDataListener();
    private final CameraActivity.CameraDataReceiver cdr = new CameraActivity.CameraDataReceiver() {
        public void onCameraDataReceived(Bitmap b) {
            //this code tels us where to go after we get the picture that the camera has taken
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Intent classifyIntent = new Intent();
            classifyIntent.setAction(ClassifyInsectDropDown.SHOW_CLASSIFY_PANE);
            classifyIntent.putExtra("image", byteArray);
            AtakBroadcast.getInstance().sendBroadcast(classifyIntent);
        }
    };

    public MainDropDown(final MapView mapView, final Context context) {
        super(mapView);
        templateView = PluginLayoutInflater.inflate(context, R.layout.main_layout, null);

        final Button cameraLauncher = templateView.findViewById(R.id.camera_launcher);
        cameraLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                cdl.register(getMapView().getContext(), cdr);

                // launch default camera in activity outside of ATAK classloader paradigm
                Intent intent = new Intent();
                intent.setClassName("com.toyon.insectclassifier.plugin",
                        "com.toyon.insectclassifier.CameraActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getMapView().getContext().startActivity(intent);
            }
        });

        ListView listView = templateView.findViewById(R.id.list_view);
        adapter = new InsectClassifierAdapter(getMapView().getContext(), new ArrayList<>());
        listView.setAdapter(adapter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null)
            return;

        if (action.equals(SHOW_PLUGIN)) {
            Log.d(TAG, "showing plugin drop down");
            showDropDown(templateView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                    HALF_HEIGHT, false, this);
        }
        if (action.equals(ADD_TO_LIST)){
            Log.d(TAG, "Handle Intent to add new review to list");
            String review = intent.getStringExtra("sci_name");
            String name = intent.getStringExtra("name");
            Bitmap picture = intent.getParcelableExtra("bitmap");
            String uid = UUID.randomUUID().toString();
            PictureReview pictureReview = new PictureReview(picture, review,
                    name, getMapView().getContext(), uid);
            adapter.addPictureReview(pictureReview);
        }
    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownVisible(boolean v) { }

    @Override
    public void onDropDownSizeChanged(double width, double height) { }

    @Override
    public void onDropDownClose() { }

    public void disposeImpl() {  }

}
