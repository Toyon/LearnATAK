/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.cameratemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.cameratemplate.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;

import com.atakmap.coremap.log.Log;

import java.util.Locale;


/** Broadcast receiver and accompanying logic for UI functionality of main plugin view. */
public class MainDropDown extends DropDownReceiver implements OnStateListener {

    public static final String TAG = MainDropDown.class.getSimpleName();
    public static final String SHOW_PLUGIN = "com.atakmap.android.cameratemplate.SHOW_PLUGIN";
    private final View templateView;

    private final ImageView imageView;
    private final CameraActivity.CameraDataListener cdl = new CameraActivity.CameraDataListener();
    private final CameraActivity.CameraDataReceiver cdr = new CameraActivity.CameraDataReceiver() {
        public void onCameraDataReceived(Bitmap b) {
            // TODO: operate on the photo taken as a bitmap object
            Log.d(TAG, String.format(Locale.US, "Image Received: %d (w) x %d (h)",
                    b.getWidth(), b.getHeight()));
            imageView.setImageBitmap(b);
        }
    };

    public MainDropDown(final MapView mapView, final Context context) {
        super(mapView);
        // TODO: modify the main_layout meet your needs
        templateView = PluginLayoutInflater.inflate(context, R.layout.main_layout, null);
        imageView = templateView.findViewById(R.id.imageView);
        final Button cameraLauncher = templateView.findViewById(R.id.img_capture_btn);
        cameraLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdl.register(getMapView().getContext(), cdr);
                // launch default camera in activity outside of ATAK classloader paradigm
                Intent intent = new Intent();
                intent.setClassName("com.atakmap.android.cameratemplate.plugin",
                        "com.atakmap.android.cameratemplate.CameraActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getMapView().getContext().startActivity(intent);
            }
        });
        final Button sampleDDRButton = templateView.findViewById(R.id.sampleAction);
        sampleDDRButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(ExamplePageDropDown.SAMPLE_ACTION);
                AtakBroadcast.getInstance().sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null)
            return;
        if (action.equals(SHOW_PLUGIN)) {
            showDropDown(templateView,
                    HALF_WIDTH, FULL_HEIGHT,  // Landscape Dimensions
                    FULL_WIDTH, HALF_HEIGHT,  // Portrait Dimensions
                    false, this);
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

    public void disposeImpl() { }

}

