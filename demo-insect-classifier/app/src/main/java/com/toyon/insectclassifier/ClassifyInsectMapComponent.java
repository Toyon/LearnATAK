/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.insectclassifier;

import android.content.Context;
import android.content.Intent;
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;

import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;

import com.atakmap.coremap.log.Log;
import com.toyon.insectclassifier.plugin.R;

/**
 * Essentially the main Activity which sets up the DropDownReceivers for controlling the display
 * status of this plugin's panes.
 */
public class ClassifyInsectMapComponent extends DropDownMapComponent {

    private static final String TAG = ClassifyInsectMapComponent.class.getSimpleName();

    public void onCreate(final Context context, Intent intent, final MapView view) {
        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, view);

        Log.d(TAG, "registering the main plugin pane receiver");
        MainDropDown mainDropDown = new MainDropDown(view, context);
        DocumentedIntentFilter mainFilter = new DocumentedIntentFilter();
        mainFilter.addAction(MainDropDown.SHOW_PLUGIN);
        mainFilter.addAction(MainDropDown.ADD_TO_LIST);
        registerDropDownReceiver(mainDropDown, mainFilter);

        Log.d(TAG, "registering the review page pane receiver");
        ClassifyInsectDropDown classifyPageDDR = new ClassifyInsectDropDown(view, context);
        DocumentedIntentFilter classifyPageFilter = new DocumentedIntentFilter();
        classifyPageFilter.addAction(ClassifyInsectDropDown.SHOW_CLASSIFY_PANE);
        this.registerDropDownReceiver(classifyPageDDR, classifyPageFilter);
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
    }

}
