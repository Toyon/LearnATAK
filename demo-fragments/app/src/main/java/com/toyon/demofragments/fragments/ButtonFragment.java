package com.toyon.demofragments.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.toyon.demofragments.CameraActivity;
import com.toyon.demofragments.customlistener.ObservableInteger;
import com.toyon.demofragments.customlistener.OnIntegerChangeListener;
import com.toyon.demofragments.DemoFragmentsDropDownReceiver;
import com.toyon.demofragments.database.PhotosEntity;
import com.toyon.demofragments.database.PhotosViewModel;
import com.toyon.demofragments.plugin.R;
import com.atakmap.android.maps.MapView;

import java.io.ByteArrayOutputStream;


public class ButtonFragment extends Fragment {

    public static ObservableInteger color = new ObservableInteger();
    public View view;
    public Context pluginContext;
    public MapView mapView;
    PhotosViewModel photosViewModel;

    private final CameraActivity.CameraDataListener cdl = new CameraActivity.CameraDataListener();
    private final CameraActivity.CameraDataReceiver cdr = new CameraActivity.CameraDataReceiver() {
        public void onCameraDataReceived(Bitmap b) {
            //convert bitmap to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            photosViewModel.insert(new PhotosEntity(byteArray));
            b.recycle();
        }
    };

    public ButtonFragment(DemoFragmentsDropDownReceiver demoFragmentsDropDownReceiver){
        pluginContext = demoFragmentsDropDownReceiver.getPluginCtx();
        mapView = demoFragmentsDropDownReceiver.getMapView();
        photosViewModel = demoFragmentsDropDownReceiver.getPhotosViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.from(pluginContext).inflate(R.layout.button_fragment, container, false);
        color.setOnIntegerChangeListener(new OnIntegerChangeListener() {
            @Override
            public void onIntegerChanged(int newValue) {
                view.setBackgroundColor(color.get());
            }
        });

        Button camera = view.findViewById(R.id.camera_button);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdl.register(mapView.getContext(), cdr);
                Intent intent = new Intent();
                intent.setClassName("com.toyon.demofragments.plugin",
                        "com.toyon.demofragments.CameraActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mapView.getContext().startActivity(intent);
            }
        });

        return view;
    }
}
