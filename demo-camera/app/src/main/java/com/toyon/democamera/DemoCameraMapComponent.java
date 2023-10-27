/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.democamera;

import android.content.Context;
import android.content.Intent;

import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;

import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;

import com.atakmap.coremap.log.Log;
import com.toyon.democamera.plugin.R;

/**
 * The equivalent of the main Activity for the plugin which initializes the DropDownReceiver to
 * show the plugin pane
 */
public class DemoCameraMapComponent extends DropDownMapComponent
{
    private static final String TAG = DemoCameraMapComponent.class.getSimpleName();

    public void onCreate(final Context lContext, Intent lIntent, final MapView lView) {
        lContext.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(lContext, lIntent, lView);

        Log.d(TAG, "Registering the demo camera plugin filter");
        DemoCameraDropDownReceiver cameraReceiver = new DemoCameraDropDownReceiver(lView, lContext);
        DocumentedIntentFilter ddFilter = new DocumentedIntentFilter();
        ddFilter.addAction(DemoCameraDropDownReceiver.SHOW_PLUGIN);
        registerDropDownReceiver(cameraReceiver, ddFilter);
    }

    @Override
    protected void onDestroyImpl(Context lContext, MapView lView) {
        super.onDestroyImpl(lContext, lView);
    }
}
