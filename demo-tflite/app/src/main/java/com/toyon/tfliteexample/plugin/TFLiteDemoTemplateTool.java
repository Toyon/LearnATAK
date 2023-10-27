/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.tfliteexample.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;

import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.plugintemplate.plugin.R;

import transapps.mapi.MapView;
import transapps.maps.plugin.tool.Group;
import transapps.maps.plugin.tool.Tool;
import transapps.maps.plugin.tool.ToolDescriptor;

public class TFLiteDemoTemplateTool extends Tool implements ToolDescriptor {

    private final Context ctx;

    public TFLiteDemoTemplateTool(Context context) {
        this.ctx = context;
    }

    @Override
    public String getDescription() {
        return ctx.getString(R.string.app_name);
    }

    @Override
    public Drawable getIcon() {
        return (ctx == null) ? null : ctx.getResources().getDrawable(R.drawable.ic_neuralnet);
    }

    @Override
    public Group[] getGroups() {
        return new Group[]{ Group.GENERAL };
    }

    @Override
    public String getShortDescription() {
        return ctx.getString(R.string.app_name);
    }

    @Override
    public Tool getTool() {
        return this;
    }

    @Override
    public void onActivate(Activity arg0, MapView arg1, ViewGroup arg2,
                           Bundle arg3, ToolCallback arg4)
    {
        // Hack to close the dropdown that automatically opens when a tool
        // plugin is activated.
        if (arg4 != null) { arg4.onToolDeactivated(this); }
        // Intent to launch the dropdown or tool
        //arg2.setVisibility(ViewGroup.INVISIBLE);
        Intent i = new Intent(
                TFLiteDemoDropDownReceiver.SHOW_PLUGIN);
        AtakBroadcast.getInstance().sendBroadcast(i);
    }

    @Override
    public void onDeactivate(ToolCallback arg0) { }
}
