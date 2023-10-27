
package com.toyon.democnn.plugin;

import com.toyon.democnn.MainDropDown;
import com.toyon.democnn.plugin.R;
import com.atakmap.android.ipc.AtakBroadcast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import transapps.mapi.MapView;
import transapps.maps.plugin.tool.Group;
import transapps.maps.plugin.tool.Tool;
import transapps.maps.plugin.tool.ToolDescriptor;

public class DemoCameraTool extends Tool implements ToolDescriptor {

    private static final String TAG = DemoCameraTool.class.getSimpleName();
    private final Context context;

    public DemoCameraTool(Context context) {
        this.context = context;
    }

    @Override
    public String getDescription() {
        return context.getString(R.string.app_name);
    }

    @Override
    public Drawable getIcon() {
        return (context == null) ? null
                : context.getResources().getDrawable(R.drawable.ic_paw);
    }

    @Override
    public Group[] getGroups() {
        return new Group[] {
                Group.GENERAL
        };
    }

    @Override
    public String getShortDescription() {
        return context.getString(R.string.app_name);
    }

    @Override
    public Tool getTool() {
        return this;
    }

    @Override
    public void onActivate(Activity arg0, MapView arg1, ViewGroup arg2,
            Bundle arg3,
            ToolCallback arg4) {

        // Hack to close the dropdown that automatically opens when a tool
        // plugin is activated.
        if (arg4 != null) {
            arg4.onToolDeactivated(this);
        }
        // Intent to launch the dropdown or tool
        Intent i = new Intent(MainDropDown.SHOW_MAIN_PANE);
        AtakBroadcast.getInstance().sendBroadcast(i);
        Log.d(TAG, "SENT INTENT TO OPEN");
    }

    @Override
    public void onDeactivate(ToolCallback arg0) {
    }
}
