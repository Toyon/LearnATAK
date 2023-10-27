/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier;

import android.content.Context;
import android.content.Intent;

import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;

import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.coremap.log.Log;
import com.toyon.plantclassifier.plugin.R;

/**
 * Essentially the main Activity which sets up the DropDownReceivers for controlling the display
 * status of this plugin's panes.
 */
public class PlantClassifierMapComponent extends DropDownMapComponent {
    private static final String TAG = PlantClassifierMapComponent.class.getSimpleName();

    public void onCreate(final Context pluginCtx, Intent lIntent, final MapView lView) {
        pluginCtx.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(pluginCtx, lIntent, lView);

        Log.d(TAG, "registering the plugin filter for the main pane");
        MainDropDown mainDropDown = new MainDropDown(lView, pluginCtx);
        DocumentedIntentFilter mainFilter = new DocumentedIntentFilter();
        mainFilter.addAction(MainDropDown.SHOW_MAIN_PANE, "Show primary plugin pane");
        this.registerDropDownReceiver(mainDropDown, mainFilter);

        Log.d(TAG, "registering the plugin filter for the classifying pane");
        ClassifyPlantDropDown classifyDropDown = new ClassifyPlantDropDown(lView, pluginCtx);
        DocumentedIntentFilter classifyFilter = new DocumentedIntentFilter();
        classifyFilter.addAction(ClassifyPlantDropDown.SHOW_CLASSIFY_PANE);
        this.registerDropDownReceiver(classifyDropDown, classifyFilter);

        Log.d(TAG, "registering the plugin filter for the report pane");
        ReportDropDown reportDropDown = new ReportDropDown(lView, pluginCtx);
        DocumentedIntentFilter reportFilter = new DocumentedIntentFilter();
        reportFilter.addAction(ReportDropDown.SHOW_REPORT_PANE);
        registerDropDownReceiver(reportDropDown, reportFilter);
    }

    @Override
    protected void onDestroyImpl(Context lContext, MapView lView) {
        super.onDestroyImpl(lContext, lView);
    }
}
