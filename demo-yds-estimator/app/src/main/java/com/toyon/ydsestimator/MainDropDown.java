/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.ydsestimator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.ipc.AtakBroadcast;
import com.toyon.ydsestimator.plugin.R;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;

import java.io.ByteArrayOutputStream;

/**
 * The primary plugin pane that provides a user with description of how the plugin works and allows
 * user to launch the camera to begin classification of trails.
 */
public class MainDropDown extends DropDownReceiver implements DropDown.OnStateListener {

    private static final String TAG = MainDropDown.class.getSimpleName();
    public static final String SHOW_MAIN_PANE = "com.toyon.ydsestimator.SHOW_MAIN_PANE";
    private final View paneView;

    private final CameraActivity.CameraDataListener cdl = new CameraActivity.CameraDataListener();
    private final CameraActivity.CameraDataReceiver cdr;

    protected MainDropDown(MapView mapView, final Context pluginContext){
        super(mapView);
        paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.main_layout, null);
        Button cameraButton = paneView.findViewById(R.id.btn_launch_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdl.register(getMapView().getContext(), cdr);
                // launch default camera in activity outside of ATAK classloader paradigm
                Intent intent = new Intent();
                intent.setClassName("com.toyon.ydsestimator.plugin",
                        "com.toyon.ydsestimator.CameraActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getMapView().getContext().startActivity(intent);
            }
        });

        cdr = new CameraActivity.CameraDataReceiver() {
            public void onCameraDataReceived(Bitmap imageToClassify) {
                // classify image path with YDS model
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageToClassify.compress(Bitmap.CompressFormat.PNG, 100, stream);
                String classification = YdsModel.classify(imageToClassify, pluginContext);

                // Create Intent to send results to ReportDropDown
                Intent reportIntent = new Intent();
                reportIntent.putExtra("image", imageToClassify);
                reportIntent.putExtra("classification", classification);
                reportIntent.setAction(ReportDropDown.SHOW_REPORT_PANE);
                AtakBroadcast.getInstance().sendBroadcast(reportIntent);
            }
        };
    }

    @Override
    public void onReceive(Context context, Intent intent){
        final String action = intent.getAction();
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

    @Override
    public void onDropDownSelectionRemoved() {}

    @Override
    public void onDropDownClose() {}

    @Override
    public void onDropDownSizeChanged(double v, double v1) {}

    @Override
    public void disposeImpl() { }


}


