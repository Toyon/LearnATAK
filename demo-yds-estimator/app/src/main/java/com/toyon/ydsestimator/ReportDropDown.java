/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.ydsestimator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.user.PlacePointTool;
import com.toyon.ydsestimator.plugin.R;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.filesystem.FileSystemUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Receives classified image from YDSClassificationDropDown and shows result of classification. */
public class ReportDropDown extends DropDownReceiver implements DropDown.OnStateListener {

    private static final String TAG = ReportDropDown.class.getSimpleName();
    public static final String SHOW_REPORT_PANE = "com.toyon.ydsestimator.SHOW_REPORT_PANE";
    private final View paneView;
    private Bitmap image;
    private final ImageView capturedYds;
    private final TextView classification;

    protected ReportDropDown(MapView mapView, final Context pluginContext){
        super(mapView);
        paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.report_layout, null);
        capturedYds = paneView.findViewById(R.id.captured_yds);
        classification = paneView.findViewById(R.id.classification_result);

        Button homeButton = paneView.findViewById(R.id.home_button);
        homeButton.setOnClickListener(view -> {
            Intent mainIntent = new Intent().setAction(MainDropDown.SHOW_MAIN_PANE);
            AtakBroadcast.getInstance().sendBroadcast(mainIntent);
        });

        Button submitReportBtn = paneView.findViewById(R.id.mark_location);
        submitReportBtn.setOnClickListener(view -> {
            // Get form values
            String text = classification.getText().toString().trim();
            String titleResult = "";
            Pattern pattern = Pattern.compile("Class (\\d+)");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                titleResult = "Class " + matcher.group(1);
            }

            String uid = UUID.randomUUID().toString();
            MapItem item = new PlacePointTool.MarkerCreator(getMapView().getCenterPoint().get())
                    .setUid(uid).setType("a-n-G").showCotDetails(true)
                    .setIconPath("34ae1613-9645-4222-a9d2-e5f243dea2865/Hiking/mountain.png")
                    .setCallsign(titleResult).setColor(Color.BLUE)
                    .placePoint();
            item.setMetaBoolean("archive", true);
            saveTrailMarkerImage(image, uid);
        });
    }

    @Override
    public void onReceive(Context context, Intent intent){
        final String action = intent.getAction();
        if (action == null)
            return;
        if (!intent.hasExtra("image") || !intent.hasExtra("classification")) {
            Toast.makeText(context, "Report Requires Image and Classification",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(action.equals(SHOW_REPORT_PANE)) {
            showDropDown(paneView,
                    FULL_WIDTH, FULL_HEIGHT,
                    FULL_WIDTH, FULL_HEIGHT,
                    false, this);
            image = (Bitmap) intent.getParcelableExtra("image");
            capturedYds.setImageBitmap(image);
            classification.setText(intent.getStringExtra("classification"));
        }
    }

    /** save bitmap image capture as marker attachment within ATAK */
    public static void saveTrailMarkerImage(Bitmap png, String markerUid) {
        File f = FileSystemUtils.getItem("attachments" + File.separatorChar +
                markerUid + File.separatorChar + "trail.png");
        try {
            Objects.requireNonNull(f.getParentFile()).mkdirs();
            boolean created = f.createNewFile();
            if (!created) return;
            FileOutputStream fStream = new FileOutputStream(f);
            png.compress(Bitmap.CompressFormat.PNG, 100, fStream);
            fStream.flush();
            fStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Issue writing bitmap to atak resource directory.\n" + e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDropDownVisible(boolean visible) { }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownClose() { }

    @Override
    public void onDropDownSizeChanged(double v, double v1) { }

    @Override
    protected void disposeImpl() { }

}
