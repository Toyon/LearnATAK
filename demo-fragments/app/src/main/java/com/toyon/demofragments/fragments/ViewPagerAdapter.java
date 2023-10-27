package com.toyon.demofragments.fragments;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.toyon.demofragments.DemoFragmentsDropDownReceiver;


public class ViewPagerAdapter extends FragmentStateAdapter {

        DemoFragmentsDropDownReceiver receiver;
    public ViewPagerAdapter(FragmentActivity fragmentActivity, DemoFragmentsDropDownReceiver demoFragmentsDropDownReceiver) {
        super(fragmentActivity);
        receiver = demoFragmentsDropDownReceiver;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ButtonFragment(receiver);
        }
        else if(position == 1) {
            return new PhotosFragment(receiver);
        }
        return new ColorChangingFragment(receiver);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

