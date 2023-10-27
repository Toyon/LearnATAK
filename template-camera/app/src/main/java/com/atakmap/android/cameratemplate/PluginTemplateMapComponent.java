/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.cameratemplate;

import android.content.Context;
import android.content.Intent;
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;

import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;

import com.atakmap.coremap.log.Log;
import com.atakmap.android.cameratemplate.plugin.R;

/**
 * Essentially the main Activity which sets up the DropDownReceivers for controlling the display
 * status of this plugin's panes.
 */
public class PluginTemplateMapComponent extends DropDownMapComponent {
    private static final String TAG = PluginTemplateMapComponent.class.getSimpleName();

    public void onCreate(final Context context, Intent intent, final MapView view) {

        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, view);

        MainDropDown mainDropDown = new MainDropDown(view, context);

        Log.d(TAG, "registering the plugin filter");
        DocumentedIntentFilter ddFilter = new DocumentedIntentFilter();
        ddFilter.addAction(MainDropDown.SHOW_PLUGIN);
        registerDropDownReceiver(mainDropDown, ddFilter);

        ExamplePageDropDown exampleDropDown = new ExamplePageDropDown(view, context);
        DocumentedIntentFilter eddFilter = new DocumentedIntentFilter();
        eddFilter.addAction(ExamplePageDropDown.SAMPLE_ACTION);
        registerDropDownReceiver(exampleDropDown, eddFilter);
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
    }

}
