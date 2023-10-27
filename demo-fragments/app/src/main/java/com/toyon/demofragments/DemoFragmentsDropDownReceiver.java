
package com.toyon.demofragments;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.toyon.demofragments.database.PhotosViewModel;
import com.toyon.demofragments.fragments.ViewPagerAdapter;
import com.atakmap.android.maps.MapView;
import com.toyon.demofragments.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;

import com.google.android.material.tabs.TabLayout;


public class DemoFragmentsDropDownReceiver extends DropDownReceiver implements
        OnStateListener {

    public static final String SHOW_MAIN_PANE = "com.toyon.demofragments.SHOW_MAIN_PANE";
    private final View templateView;
    private final Context pluginContext;

    public ViewPagerAdapter viewPagerAdapter;

    private final PhotosViewModel photosViewModel;


    public PhotosViewModel getPhotosViewModel(){
        return photosViewModel;
    }
    public Context getPluginCtx() {
        return pluginContext;
    }

    public DemoFragmentsDropDownReceiver(final MapView mapView,
                                         final Context context) {
        super(mapView);
        this.pluginContext = context;

        photosViewModel = new ViewModelProvider(ViewTreeViewModelStoreOwner.get(getMapView())).get(PhotosViewModel.class);

        templateView = PluginLayoutInflater.inflate(context, R.layout.main_layout, null);

        viewPagerAdapter = new ViewPagerAdapter((FragmentActivity) mapView.getContext(), this);
        final ViewPager2 viewPager2 = templateView.findViewById(R.id.viewPager);
        viewPager2.setAdapter(viewPagerAdapter);

        final TabLayout tabLayout = templateView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Buttons Page"));
        tabLayout.addTab(tabLayout.newTab().setText("Photo Album"));
        tabLayout.addTab(tabLayout.newTab().setText("Color Changer"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }

    public void disposeImpl() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (action == null)
            return;

        if (action.equals(SHOW_MAIN_PANE)) {
            showDropDown(templateView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                    HALF_HEIGHT, false, this);
        }
    }
    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownVisible(boolean v) {
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
    }

}
