/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.demohelloworld.fragments;

import static com.atakmap.android.dropdown.DropDownReceiver.FULL_HEIGHT;
import static com.atakmap.android.dropdown.DropDownReceiver.HALF_HEIGHT;
import static com.atakmap.android.dropdown.DropDownReceiver.FULL_WIDTH;
import static com.atakmap.android.dropdown.DropDownReceiver.THIRD_HEIGHT;
import static com.atakmap.android.dropdown.DropDownReceiver.THIRD_WIDTH;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atakmap.android.demohelloworld.HelloWorldDropDown;
import com.atakmap.android.demohelloworld.plugin.R;
import com.atakmap.android.demohelloworld.list.DemoAdapter;
import com.atakmap.android.demohelloworld.Util;

import java.util.Locale;

/** Demonstrate resizing ATAK plugin side pane and built in Android UI organization features. */
public class LayoutsFragment extends Fragment {

    private Context pluginCtx;
    private Context atakCtx;
    private HelloWorldDropDown ddReceiver;
    private String[] users;

    protected RecyclerView demoRecyclerView;
    protected DemoAdapter demoRecyclerAdapter;

    /** Create and instance of the LayoutsFragment */
    public LayoutsFragment construct(final HelloWorldDropDown receiver) {
        pluginCtx = receiver.getPluginCtx();
        ddReceiver = receiver;
        atakCtx = receiver.getMapView().getContext();
        users = new String[100];
        for (int i=0; i < users.length; i++) {
            users[i] = String.format(Locale.US, "User %d", i+1);
        }
        return this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(pluginCtx).inflate(R.layout.tab_layouts, container, false);

        Button panelFullscreen = view.findViewById(R.id.panelFullscreen);
        panelFullscreen.setOnClickListener(view1 -> ddReceiver.callResize(FULL_WIDTH, FULL_HEIGHT));
        Util.setButtonToast(atakCtx, panelFullscreen, "resize pane to fullscreen");

        Button pBottomHalf = view.findViewById(R.id.spBottomHalf);
        pBottomHalf.setOnClickListener(v1 -> ddReceiver.callResize(FULL_WIDTH, HALF_HEIGHT));
        Util.setButtonToast(atakCtx, pBottomHalf, "resize pane to bottom half");

        Button pBottomThird = view.findViewById(R.id.spBottomThird);
        pBottomThird.setOnClickListener(v2 -> ddReceiver.callResize(FULL_WIDTH, THIRD_HEIGHT));
        Util.setButtonToast(atakCtx, pBottomThird, "resize pane to bottom third");

        Button pSideHalf = view.findViewById(R.id.spSideHalf);
        pSideHalf.setOnClickListener(v3 -> ddReceiver.callResize(0.5, FULL_HEIGHT));
        Util.setButtonToast(atakCtx, pSideHalf, "resize pane to right half");

        Button pSideThird = view.findViewById(R.id.spSideThird);
        pSideThird.setOnClickListener(v4 -> ddReceiver.callResize(THIRD_WIDTH, FULL_HEIGHT));
        Util.setButtonToast(atakCtx, pSideThird, "resize pane to right third");

        // Programmatically Added RecyclerView of Mock Users
        LinearLayout layout_container = view.findViewById(R.id.linear_layouts_container);
        demoRecyclerView = new RecyclerView(pluginCtx);
        demoRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200));
        demoRecyclerView.setBackgroundColor(Color.parseColor("#000000"));
        demoRecyclerView.setHorizontalScrollBarEnabled(true);
        demoRecyclerAdapter = new DemoAdapter(users);
        demoRecyclerView.setAdapter(demoRecyclerAdapter);
        demoRecyclerView.setLayoutManager(new LinearLayoutManager(pluginCtx,
                LinearLayoutManager.HORIZONTAL, false));
        layout_container.addView(demoRecyclerView);

        return view;
    }

}
