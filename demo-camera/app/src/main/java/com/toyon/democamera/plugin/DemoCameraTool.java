/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.democamera.plugin;

import com.toyon.democamera.plugin.R;
import com.atakmap.android.ipc.AtakBroadcast;
import com.toyon.democamera.DemoCameraDropDownReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.core.content.res.ResourcesCompat;

import transapps.mapi.MapView;
import transapps.maps.plugin.tool.Group;
import transapps.maps.plugin.tool.Tool;
import transapps.maps.plugin.tool.ToolDescriptor;

public class DemoCameraTool extends Tool implements ToolDescriptor {

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
        if (context == null)
            return null;
        ColorFilter filter = new LightingColorFilter(Color.WHITE, Color.WHITE);
        Drawable icon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_camera, null);
        assert icon != null;
        icon.setColorFilter(filter);
        return icon;
    }

    @Override
    public Group[] getGroups() {
        return new Group[] { Group.GENERAL };
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
    public void onActivate(Activity arg0, MapView arg1,
                           ViewGroup arg2, Bundle arg3, ToolCallback arg4) {
        // Hack to close the dropdown that automatically opens when a tool plugin is activated.
        if (arg4 != null) {
            arg4.onToolDeactivated(this);
        }
        // Intent to launch the dropdown or tool
        Intent i = new Intent(DemoCameraDropDownReceiver.SHOW_PLUGIN);
        AtakBroadcast.getInstance().sendBroadcast(i);
    }

    @Override
    public void onDeactivate(ToolCallback arg0) { }
}
