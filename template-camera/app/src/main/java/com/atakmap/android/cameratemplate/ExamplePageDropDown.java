/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.cameratemplate;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.cameratemplate.plugin.R;

public class ExamplePageDropDown extends DropDownReceiver implements OnStateListener {

    public static final String SAMPLE_ACTION = "com.atakmap.android.cameratemplate.ExampleDropDownReceiver.SAMPLE_ACTION";
    private final View paneView;

    protected ExamplePageDropDown(MapView mapView, Context context){
        super(mapView);
        paneView = PluginLayoutInflater.inflate(context, R.layout.sample_layout);
        Button submitButton = paneView.findViewById(R.id.button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(MainDropDown.SHOW_PLUGIN);
                AtakBroadcast.getInstance().sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SAMPLE_ACTION)){
            showDropDown(paneView,
                    HALF_WIDTH, FULL_HEIGHT, // Landscape Dimensions
                    FULL_WIDTH, THIRD_HEIGHT, // Portrait Dimensions
                    false, this);
        }
    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownClose() { }

    @Override
    public void onDropDownSizeChanged(double v, double v1) { }

    @Override
    public void onDropDownVisible(boolean b) { }

    @Override
    protected void disposeImpl() { }

}
