/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.tfliteexample.plugin;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.plugintemplate.plugin.R;
import com.atakmap.coremap.log.Log;
import com.toyon.tfliteexample.ExampleFunctionWrapper;

import java.io.IOException;

public class TFLiteDemoDropDownReceiver extends DropDownReceiver implements OnStateListener {

    public static final String TAG = TFLiteDemoDropDownReceiver.class.getSimpleName();
    public static final String SHOW_PLUGIN = "com.atakmap.android.plugintemplate.SHOW_PLUGIN";
    private final View templateView;
    private final Context pluginContext;

    public TFLiteDemoDropDownReceiver(final MapView mapView, final Context context) {
        super(mapView);
        this.pluginContext = context;
        // Remember to use the PluginLayoutInflator if you are actually inflating a custom view
        // In this case, using it is not necessary - but I am putting it here to remind
        // developers to look at this Inflator
        templateView = PluginLayoutInflater.inflate(context, R.layout.main_layout, null);
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
    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownVisible(boolean v) {
        if (v) {
            TextView tv = templateView.findViewById(R.id.textView);
            ExampleFunctionWrapper exampleFunctionWrapper;
            try {
                exampleFunctionWrapper
                        = new ExampleFunctionWrapper(this.pluginContext);
            } catch (IOException e) {
                Log.e(TAG, "Unable to load TFLite model", e);
                tv.setText("Error: Unable to load TFLite model");
                return;
            }
            ExampleFunctionWrapper.Weights w = exampleFunctionWrapper.getWeights();
            if (w != null) {
                tv.setText("Success! Able to load a model and call a tf.function");
            } else {
                tv.setText("Error: Unable to call a tf.function on the loaded model");
            }
        }
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) { }

    @Override
    public void onDropDownClose() { }

    public void disposeImpl() { }

}
