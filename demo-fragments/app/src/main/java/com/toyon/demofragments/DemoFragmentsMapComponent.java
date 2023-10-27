
package com.toyon.demofragments;

import android.content.Context;
import android.content.Intent;
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;

import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;

import com.atakmap.coremap.log.Log;
import com.toyon.demofragments.plugin.R;

public class DemoFragmentsMapComponent extends DropDownMapComponent {

    private static final String TAG = DemoFragmentsMapComponent.class.getSimpleName();

    private Context pluginContext;

    public void onCreate(final Context context, Intent intent, final MapView view) {

        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, view);
        pluginContext = context;

        Log.d(TAG, "registering the plugin filter");
        DemoFragmentsDropDownReceiver mainDropDown = new DemoFragmentsDropDownReceiver(view, pluginContext);
        DocumentedIntentFilter mainFilter = new DocumentedIntentFilter();
        mainFilter.addAction(DemoFragmentsDropDownReceiver.SHOW_MAIN_PANE, "Show primary plugin pane");
        this.registerDropDownReceiver(mainDropDown, mainFilter);
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
    }

}
