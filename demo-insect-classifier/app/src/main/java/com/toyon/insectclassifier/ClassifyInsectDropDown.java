/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.insectclassifier;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.toyon.insectclassifier.plugin.R;
import com.toyon.insectclassifier.plugin.ml.LiteModelAiyVisionClassifierInsectsV13;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.log.Log;
import com.atakmap.coremap.maps.coords.GeoPoint;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/** Plugin pane that shows the ML interference results from the image capture */
public class ClassifyInsectDropDown extends DropDownReceiver implements DropDown.OnStateListener {

    public static final String TAG = ClassifyInsectDropDown.class.getSimpleName();
    public static final String SHOW_CLASSIFY_PANE = "ClassifyInsectDropDown.SHOW_CLASSIFY_PANE";
    private final View paneView;
    private final ImageView imageView;
    private final Context pluginCtx;
    private Bitmap img;
    private String name = "temp";
    private Bitmap myBitmap;
    private String sci_name = "sci";
    public double certainty;
    public String insectLabel;

    public ClassifyInsectDropDown(MapView mapView, final Context context) {
        super(mapView);
        pluginCtx = context;
        paneView = PluginLayoutInflater.inflate(context, R.layout.classifypage, null);
        imageView = paneView.findViewById(R.id.img_preview);

        // BACK BUTTON TO GO BACK TO MAIN PAGE
        final Button backtoMain = paneView.findViewById(R.id.back_to_main);
        backtoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                Intent intent = new Intent();
                intent.setAction(MainDropDown.SHOW_PLUGIN);
                AtakBroadcast.getInstance().sendBroadcast(intent);
                Intent intent2 = new Intent();
                intent2.setAction(MainDropDown.ADD_TO_LIST);
                intent2.putExtra("bitmap", myBitmap);
                intent2.putExtra("name", name);
                intent2.putExtra("sci_name", sci_name);
                AtakBroadcast.getInstance().sendBroadcast(intent2);
            }
        });
    }

    /** Create formatted certainty string for display */
    private String getCertainty(double certainty){
        double cert = certainty * 100.0;
        DecimalFormat dec = new DecimalFormat("#.00");
        return dec.format(cert);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        byte[] byteArray = intent.getByteArrayExtra("image");
        img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Log.d(TAG, "CLASSIFY==========img received======>" + img.getByteCount());
        if (action == null)
            return;

        if (action.equals(SHOW_CLASSIFY_PANE)) {
            Log.d(TAG, "showing CLASSIFY drop down");
            showDropDown(paneView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                    THIRD_HEIGHT, false, this);
            Log.d(TAG, "MY BITMAP AFTER PIC: " + img);
            imageView.setImageBitmap(img);
            myBitmap = img;
        }
    }

    @Override
    public void onDropDownVisible(boolean visible) {
        if (visible){
            try {
                LiteModelAiyVisionClassifierInsectsV13 model = LiteModelAiyVisionClassifierInsectsV13.newInstance(pluginCtx);
                // Creates inputs for reference
                TensorImage image = TensorImage.fromBitmap(img);
                // Run inference
                LiteModelAiyVisionClassifierInsectsV13.Outputs outputs = model.process(image);
                List<Category> probability = outputs.getProbabilityAsCategoryList();
                model.close();


                // CLASSIFICATION DONE : BEGIN GEOLOCATE
                GeoPoint geoPoint = getMapView().getSelfMarker().getPoint();
                String longitude = String.valueOf(geoPoint.getLongitude());
                String latitude = String.valueOf(geoPoint.getLatitude());
                Log.d(TAG,"longitude: " + longitude +" latitude: " + latitude);

                certainty = 0.0;
                insectLabel = "";

                for (Category cat : probability){
                    if (cat.getScore() > certainty) {
                        certainty = cat.getScore();
                        insectLabel = cat.getLabel();
                    }
                }

                Log.d(TAG, String.format(Locale.US,
                        "CLASSIFY DONE: LABEL = %s WITH %f CERTAINTY", insectLabel, certainty));

                AssetManager manager = pluginCtx.getAssets();
                InputStream inStream = null;
                try {
                    inStream = manager.open("insect_labels.csv");
                } catch (IOException e){
                    Log.e(TAG, "Error opening file");
                }

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
                String line = "";
                try{
                    while ((line = buffer.readLine()) != null){
                        String[] columns = line.split(",");
                        if (insectLabel.compareTo(columns[0]) == 0){

                            //UNECESSARY BUT CSV HAS COMMON NAMES UNCAPITALIZED BY PYTHON SCRIPT
                            // SO RECAPITALIZE FIRST LETTER
                            StringBuilder result = new StringBuilder();
                            boolean capitalizeNext = true;
                            for (char c : columns[1].toCharArray()) {
                                if (Character.isWhitespace(c)) {
                                    capitalizeNext = true;
                                } else if (capitalizeNext) {
                                    c = Character.toUpperCase(c);
                                    capitalizeNext = false;
                                }
                                result.append(c);
                            }

                            String common_name = result.toString();
                            // GENERATE LABEL TO BE PRINTED TO CLASSIFY PAGE UNDER IMG
                            String printLabel = "Classified Insect: " + common_name + "\n";
                            printLabel += "Scientific Name: " + insectLabel + "\n";
                            printLabel += "Certainty: " + getCertainty(certainty) +  "%";
                            name = common_name;
                            sci_name = insectLabel + " with certainty: " + getCertainty(certainty) +"%";
                            Log.d(TAG, printLabel);
                            TextView edittext = paneView.findViewById(R.id.img_label_txt);
                            edittext.setText(printLabel);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                Log.e(TAG, "Error loading TFLite Model");
            }
        }

    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownClose() { }

    @Override
    public void onDropDownSizeChanged(double v, double v1) { }

    @Override
    public void disposeImpl() {}
}
