/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.demohelloworld;

import android.content.Context;
import android.content.Intent;

import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.log.Log;

import com.toyon.demohelloworld.plugin.R;

/**
 * Essentially the main Activity which sets up the DropDownReceivers for controlling the display
 * status of this plugin's panes.
 */
public class HelloWorldMapComponent extends DropDownMapComponent {

    private static final String TAG = HelloWorldMapComponent.class.getSimpleName();

    public void onCreate(final Context context, Intent intent, final MapView view) {
        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, view);

        Log.d(TAG, "Registering the demo hello world plugin filter");
        HelloWorldDropDown helloWorldDropDown = new HelloWorldDropDown(view, context);
        DocumentedIntentFilter ddFilter = new DocumentedIntentFilter();
        ddFilter.addAction(HelloWorldDropDown.SHOW_PLUGIN_PANE,
                "Show the Demo Hello World dropdown pane");
        registerDropDownReceiver(helloWorldDropDown, ddFilter);

        Log.d(TAG, "Registering the 2D Map Icon Viewer filter");
        Icon2dDropDown mapIconViewDropDown = new Icon2dDropDown(view, context);
        DocumentedIntentFilter mapIconFilter = new DocumentedIntentFilter();
        mapIconFilter.addAction(Icon2dDropDown.SHOW_ICON_PANE,
                "Show the 2D Map Icon Viewer");
        this.registerDropDownReceiver(mapIconViewDropDown, mapIconFilter);
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
    }

}
