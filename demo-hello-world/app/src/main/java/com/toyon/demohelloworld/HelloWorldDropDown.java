/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.demohelloworld;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.toyon.demohelloworld.fragments.CoreFragment;
import com.toyon.demohelloworld.fragments.LayoutsFragment;
import com.toyon.demohelloworld.fragments.MapFragment;
import com.atakmap.android.maps.MapView;
import com.toyon.demohelloworld.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;

import java.util.ArrayList;
import java.util.List;

/** Broadcast receiver and accompanying logic for UI functionality of main plugin view. */
public class HelloWorldDropDown extends DropDownReceiver implements OnStateListener {

    public static final String TAG = HelloWorldDropDown.class.getSimpleName();
    public static final String SHOW_PLUGIN_PANE = "com.toyon.demohelloworld.SHOW_PLUGIN";
    private final View paneView;
    private final Context pluginCtx;
    private final Button[] tabIcons;

    public HelloWorldDropDown(final MapView mapView, final Context context) {
        super(mapView);
        this.pluginCtx = context;
        paneView = PluginLayoutInflater.inflate(context, R.layout.pane_main, null);
        ViewPager viewPager = paneView.findViewById(R.id.tabPager);
        Button coreComponentTab = paneView.findViewById(R.id.topicAndroid);
        Button layoutBtn = paneView.findViewById(R.id.topicLayout);
        Button mapTab = paneView.findViewById(R.id.topicMap);

        // setup tab button clicks
        Util.setButtonToast(mapView.getContext(), coreComponentTab, "Open Core Components Page");
        Util.setButtonToast(mapView.getContext(), layoutBtn,"Open Layouts Tab");
        Util.setButtonToast(mapView.getContext(), mapTab,"Open Mapping Tab");
        tabIcons = new Button[] { coreComponentTab, layoutBtn, mapTab };
        for (int i = 0; i < tabIcons.length; i++) {
            final int index = i;
            tabIcons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(index);
                }
            });
        }

        // synchronize tab icon coloring with state of the displayed fragment
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }
            @Override
            public void onPageSelected(int position) { colorActiveTab(position);  }
            @Override
            public void onPageScrollStateChanged(int i) { }
        });

        // initialize fragment pages and setup pager adapter to manage the fragments
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CoreFragment().construct(getPluginCtx(), getMapView().getContext()));
        fragments.add(new LayoutsFragment().construct(HelloWorldDropDown.this));
        fragments.add(new MapFragment().construct(HelloWorldDropDown.this));
        FragmentPagerAdapter fpa = new FragmentPagerAdapter(
                ((FragmentActivity) getMapView().getContext()).getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        colorActiveTab(viewPager.getCurrentItem());
        viewPager.setAdapter(fpa);
    }

    /**
     * Apply a highlight color to the active tab icon and apply an inactive color to the other tabs.
     * @param position Position in tab icon list for the Active Tab
     */
    private void colorActiveTab(int position) {
        for (int i = 0; i < tabIcons.length; i++) {
            tabIcons[i].setSelected(position == i);
            tabIcons[i].setBackgroundTintList(pluginCtx.getResources().getColorStateList(
                    (position == i) ? R.color.android_green : R.color.white
            ));
        }
    }

    /**
     * Create an immutable instance of this HelloWorld DropDownReceiver. Useful for synchronizing
     * calls for the map rendering engine and custom layers.
     * @return final instance of this DropDownReceiver
     */
    public final HelloWorldDropDown instance() {
        return HelloWorldDropDown.this;
    }

    /**
     * Method for safe access to plugin context.
     * @return Context of plugin
     */
    public Context getPluginCtx() {
        return pluginCtx;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null)
            return;
        if (action.equals(SHOW_PLUGIN_PANE))
            showDropDown(paneView,
                    THREE_EIGHTHS_WIDTH, FULL_HEIGHT, // Landscape Dimensions
                    FULL_WIDTH, THREE_EIGHTHS_HEIGHT, // Portrait Dimensions
                    false, this);
    }

    @Override
    public void onDropDownVisible(boolean v) { }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownSizeChanged(double width, double height) { }

    @Override
    public void onDropDownClose() { }

    public void disposeImpl() { }  // abstract DropDownReceiver method

}
