/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.foodclassifier;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;

import com.toyon.foodclassifier.plugin.R;

/**
 * Equivalent to the main Activity which sets up the DropDownReceivers for controlling the display
 * status of this plugin's panes.
 */
public class FoodClassifierMapComponent extends DropDownMapComponent {

    private static final String TAG = FoodClassifierMapComponent.class.getSimpleName();

    public void onCreate(final Context context, Intent intent, final MapView view) {
        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, view);
        MainDropDown ddr = new MainDropDown(view, context);

        Log.d(TAG, "registering the MainDropDown");
        DocumentedIntentFilter ddFilter = new DocumentedIntentFilter();
        ddFilter.addAction(MainDropDown.SHOW_PLUGIN);
        ddFilter.addAction(MainDropDown.ADD_TO_LIST);
        registerDropDownReceiver(ddr, ddFilter);

        Log.d(TAG, "register adding the ReviewPageDropDown");
        ReviewPageDropDown reviewPageDDR = new ReviewPageDropDown(view, context);
        DocumentedIntentFilter reviewPageFilter = new DocumentedIntentFilter();
        reviewPageFilter.addAction(ReviewPageDropDown.SAMPLE_ACTION);
        this.registerDropDownReceiver(reviewPageDDR, reviewPageFilter);
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
    }

}



