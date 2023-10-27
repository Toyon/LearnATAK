package com.toyon.demofragments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toyon.demofragments.customlistener.ObservableInteger;
import com.toyon.demofragments.customlistener.OnIntegerChangeListener;
import com.toyon.demofragments.DemoFragmentsDropDownReceiver;
import com.toyon.demofragments.PhotosAdapter;
import com.toyon.demofragments.database.PhotosViewModel;
import com.toyon.demofragments.plugin.R;


public class PhotosFragment extends Fragment {

    public static ObservableInteger color = new ObservableInteger();

    RecyclerView recyclerview;
    Context pluginContext;
    PhotosViewModel photosViewModel;
    public View view;

    DemoFragmentsDropDownReceiver demoFragmentsDropDownReceiver;

    public PhotosFragment(DemoFragmentsDropDownReceiver demoFragmentsDropDownReceiver){
        pluginContext = demoFragmentsDropDownReceiver.getPluginCtx();
        photosViewModel = demoFragmentsDropDownReceiver.getPhotosViewModel();
        this.demoFragmentsDropDownReceiver = demoFragmentsDropDownReceiver;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.from(pluginContext).inflate(R.layout.photo_fragment, container, false);
        color.setOnIntegerChangeListener(new OnIntegerChangeListener() {
            @Override
            public void onIntegerChanged(int newValue) {
                view.setBackgroundColor(color.get());
                recyclerview.setBackgroundColor(color.get());
            }
        });

        recyclerview = (RecyclerView) view.findViewById(R.id.photo_recyclerview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        PhotosAdapter adapter = new PhotosAdapter(photosViewModel,demoFragmentsDropDownReceiver );
        recyclerview.setAdapter(adapter);

        return view;
    }

}
