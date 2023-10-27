package com.toyon.demofragments.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.toyon.demofragments.DemoFragmentsDropDownReceiver;
import com.toyon.demofragments.plugin.R;

public class ColorChangingFragment extends Fragment {

    public View view;
    ImageView colorWheel;
    TextView hexText;
    TextView rgbText;
    View colorView;
    Bitmap newColor;
    public  int red;
    public int green;
    public int blue;

    Context pluginContext;

    public ColorChangingFragment(DemoFragmentsDropDownReceiver demoFragmentsDropDownReceiver){
        pluginContext = demoFragmentsDropDownReceiver.getPluginCtx();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.from(pluginContext).inflate(R.layout.color_changing_fragment, container, false);
        colorWheel = view.findViewById(R.id.color_wheel);
        hexText = view.findViewById(R.id.hex_choice);
        rgbText = view.findViewById(R.id.rgb_choice);
        colorView = view.findViewById(R.id.color_choice);

        colorWheel.setDrawingCacheEnabled(true);
        colorWheel.buildDrawingCache(true);

        colorWheel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE ){
                    newColor = colorWheel.getDrawingCache();
                    try {
                        int pixels = newColor.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());
                        red = Color.red(pixels);
                        green = Color.green(pixels);
                        blue = Color.blue(pixels);

                        String hex = "#" + Integer.toHexString(pixels);
                        colorView.setBackgroundColor(Color.rgb(red, green, blue));
                        hexText.setText("HEX: " + hex);
                        rgbText.setText("RGB: " + red + ", " + green + ", " + blue);
                    } catch (Exception e){
                        Log.e("Color Changing error", String.valueOf(e));
                    }
                }
                return true;
            }
        });

        Button changeColor = view.findViewById(R.id.change_color_button);
        changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ButtonFragment.color.set(Color.rgb(red,green,blue));
                PhotosFragment.color.set(Color.rgb(red, green, blue));
            }
        });
        return view;
    }
}
