/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.toyon.plantclassifier.adapters.Plant;
import com.toyon.plantclassifier.plugin.R;
import com.toyon.plantclassifier.plugin.ml.LiteModelAiyVisionClassifierPlantsV13;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * This class is where the actual classification happens
 * a plant image is received from MainDropDown and the loaded Tflite model is called and run on the image
 * after the model predicts what the plant is, the common name and edibility are retrieved from plantLabels.csv
 * then this information gets bundled into a plant object and sent to the ReportDropDown receiver where a user
 * can see the plant's information and submit it to their main page
 */
public class ClassifyPlantDropDown extends DropDownReceiver implements DropDown.OnStateListener {

    public static final String TAG = ClassifyPlantDropDown.class.getSimpleName();
    public static final String SHOW_CLASSIFY_PANE = "com.toyon.plantclassifier.SHOW_CLASSIFY_PANE";

    private final View paneView;
    private final Context pluginCtx;
    private Bitmap img;

    public double certainty;

    public String plantLabel;
    public String common_name = "n/a";
    public String edible = "n/a";

    protected ClassifyPlantDropDown(MapView mapView, final Context pluginContext){
        super(mapView);
        pluginCtx = pluginContext;
        paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.classify_layout, null);

    }

    @Override
    public void onReceive(Context context, Intent intent){
        final String action = intent.getAction();
        byte[] byteArray = intent.getByteArrayExtra("image");
        img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        common_name = "Not available";
        edible = "No edible information provided";
        if(action == null)
            return;
        if(action.equals(SHOW_CLASSIFY_PANE))
            showDropDown(paneView,
                    HALF_WIDTH, FULL_HEIGHT, //landscape
                    FULL_WIDTH, THIRD_HEIGHT, //Portrait
                    false, this );
    }

    @Override
    public void onDropDownVisible(boolean visible) {

        if (visible) {
            try {
                LiteModelAiyVisionClassifierPlantsV13 model = LiteModelAiyVisionClassifierPlantsV13.newInstance(pluginCtx);

                // Creates inputs for reference.
                TensorImage image = TensorImage.fromBitmap(img);

                // Runs model inference and gets result.
                LiteModelAiyVisionClassifierPlantsV13.Outputs outputs = model.process(image);
                List<Category> probability = outputs.getProbabilityAsCategoryList();

                certainty = probability.get(0).getScore();
                plantLabel = probability.get(0).getLabel();
                //take one w highest probability and send it to report screen
                for(int i = 0; i < probability.size(); i++){
                    if (probability.get(i).getScore() > certainty){
                        certainty = probability.get(i).getScore();
                        plantLabel = probability.get(i).getLabel();
                    }
                }

                AssetManager manager = pluginCtx.getAssets();
                InputStream inStream = null;
                try {
                    inStream = manager.open("plantLabels.csv");
                } catch (IOException e) {
                    Log.d(TAG, "the error is: "+ e);
                }

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
                String line = "";
                try {
                    while ((line = buffer.readLine()) != null) {
                        String[] columns = line.split(",");
                        String label =  columns[1].replace("\"", "");
                        if (label.compareTo(plantLabel) == 0) {
                            common_name = columns[2].replace("\"", "");
                            edible = columns[3].replace("\"", "");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                String cert = String.format("%.2f",(certainty * 100)) + "%";
                Plant temp = new Plant(plantLabel, cert, img, common_name, edible);
                Intent reportIntent = new Intent();
                reportIntent.putExtra("Plant", temp);
                reportIntent.setAction(ReportDropDown.SHOW_REPORT_PANE);
                AtakBroadcast.getInstance().sendBroadcast(reportIntent);


                // Releases model resources if no longer used.
                model.close();
            } catch (IOException e) {
                Log.e(TAG, "Unable to load TFLite model", e);
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
    protected void disposeImpl() { }

}




