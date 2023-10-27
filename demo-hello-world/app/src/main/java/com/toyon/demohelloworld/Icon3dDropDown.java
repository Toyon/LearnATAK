/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.demohelloworld;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.toyon.demohelloworld.plugin.R;
import com.toyon.demohelloworld.list.DynamicGridView;
import com.atakmap.android.gui.PluginSpinner;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.util.SimpleItemSelectedListener;
import com.atakmap.android.vehicle.model.VehicleModelInfo;
import com.atakmap.android.vehicle.model.ui.VehicleModelGridAdapter;
import com.atakmap.coremap.filesystem.FileSystemUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.tak.platform.graphics.Color;

/**
 * Broadcast receiver and accompanying logic for UI functionality of 3D vehicle model viewer.
 * Click a model to show a toast and log message of the path required for creating the VehicleModel.
 */
public class Icon3dDropDown extends DropDownReceiver implements
        AdapterView.OnItemClickListener, DropDown.OnStateListener {

    private static final String TAG = Icon3dDropDown.class.getSimpleName();
    private final View paneView;
    private final Context atakCtx;
    private final VehicleModelGridAdapter adapter;

    public Icon3dDropDown(MapView mapView, final Context pluginCtx) {
        super(mapView);
        atakCtx = mapView.getContext();
        paneView = PluginLayoutInflater.inflate(pluginCtx, R.layout.pane_map_icon_3d, null);
        adapter = new VehicleModelGridAdapter(mapView);
        DynamicGridView grid = paneView.findViewById(R.id.models_grid);

        List<String> modelCategories = getVehicleModelCategories();
        try {
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(this);
        } catch (NullPointerException npe) {
            Log.d(TAG, "FAILED TO GET GRID VIEW");
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Failed to find Model Categories from Meta JSON");
        }
        PluginSpinner modelCategorySpinner = new PluginSpinner(pluginCtx);
        modelCategorySpinner.setBackgroundColor(ContextCompat.getColor(pluginCtx, R.color.darker_gray));
        modelCategorySpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view instanceof TextView) {
                    TextView item = (TextView) view;
                    item.setTextColor(Color.CYAN);
                    adapter.setCategory(modelCategories.get(i));
                }
            }
        });
        modelCategorySpinner.setAdapter(new ArrayAdapter<String>(
                pluginCtx, R.layout.item_spinner_text, modelCategories));
        LinearLayout spinnerContainer = paneView.findViewById(R.id.spinner_holder);
        spinnerContainer.addView(modelCategorySpinner);
        if (!modelCategorySpinner.getAdapter().isEmpty())
            modelCategorySpinner.setSelection(0);
    }

    public void show() {
        showDropDown(paneView,
                FULL_WIDTH, FULL_HEIGHT, // Landscape Dimensions
                FULL_WIDTH, FULL_HEIGHT, // Portrait Dimensions
                false, this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        VehicleModelInfo modelInfo = adapter.getItem(position);
        String type = modelInfo.name;
        if (type == null || type.isEmpty())
            Toast.makeText(atakCtx, "Unable to fetch model info", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(atakCtx, modelInfo.getIconURI(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "Model Path: " + modelInfo.getIconURI());
        }
    }

    @Override
    protected void disposeImpl() { }

    @Override
    public void onReceive(Context context, Intent intent) { }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownClose() { }

    @Override
    public void onDropDownSizeChanged(double v, double v1) { }

    @Override
    public void onDropDownVisible(boolean b) { }

    /**
     * Utilize ATAK's vehicle model metadata JSON file to find all supported categories.
     * @return List of vehicle model category names
     */
    private static List<String> getVehicleModelCategories() {
        String vehicleModelMetadata = FileSystemUtils.getItem("tools" + File.separator +
                "vehicle_models" + File.separator + "metadata.json").getAbsolutePath();
        try {
            File metadataFile = new File(vehicleModelMetadata);
            String jsonMetadataStr = Util.loadJsonFromFile(metadataFile);
            JSONObject metadata = new JSONObject(jsonMetadataStr);
            JSONArray categoryArray = metadata.getJSONArray("categories");
            ArrayList<String> categories = new ArrayList<>();
            for (int i = 0; i < categoryArray.length(); i++) {
                categories.add(categoryArray.getJSONObject(i).getString("name"));
            }
            return categories;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
