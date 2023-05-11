package com.atakmap.android.democnn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.attachment.DeleteAfterSendCallback;
import com.atakmap.android.contact.Contact;
import com.atakmap.android.contact.Contacts;
import com.atakmap.android.democnn.adapters.DetectedObject;
import com.atakmap.android.democnn.adapters.DetectedObjectAdapter;
import com.atakmap.android.democnn.plugin.R;
import com.atakmap.android.democnn.util.Constants;
import com.atakmap.android.democnn.util.Util;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.missionpackage.api.MissionPackageApi;
import com.atakmap.android.missionpackage.file.MissionPackageManifest;
import com.atakmap.android.preference.AtakPreferences;
import com.atakmap.android.user.PlacePointTool;
import com.atakmap.coremap.filesystem.FileSystemUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Receiver that implements logic for report generation pane */
public class ReportDropDown extends DropDownReceiver implements DropDown.OnStateListener  {

    private static final String TAG = ReportDropDown.class.getSimpleName();
    public static final String SHOW_REPORT_PANE = "com.atakmap.android.democnn.SHOW_REPORT_PANE";
    private final View paneView;
    private final Context pluginCtx;
    private final File tempImageDir;
    private final AtakPreferences atakPref;
    private boolean prefCenterMarker = false;

    private final EditText animalType;
    private final EditText locationInfo;
    private final EditText note;
    private Bitmap img;

    protected ReportDropDown(MapView mapView, final Context pluginContext) {
        super(mapView);
        pluginCtx = pluginContext;
        paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.pane_report, null);
        tempImageDir = FileSystemUtils.getItem(Constants.TMP_IMG_DIR);
        atakPref = new AtakPreferences(getMapView());
        animalType = paneView.findViewById(R.id.animalText);
        locationInfo = paneView.findViewById(R.id.locationText);
        note = paneView.findViewById(R.id.noteText);

        if (!tempImageDir.exists() && tempImageDir.mkdir())
            Log.d(TAG, "CREATED TEMP IMAGE DIRECTORY");

        Button submitReportBtn = paneView.findViewById(R.id.submitReport);
        submitReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get form values
                String titleAnimalType = animalType.getText().toString().trim()
                        .replaceAll("[^a-zA-Z0-9]", "-");
                String remarks = locationInfo.getText().toString() + " ~ " +
                        note.getText().toString() + " ~ ";

                if (titleAnimalType.equals("") || img == null) {
                    Toast.makeText(mapView.getContext(),
                            "Image and Animal Type Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uid = UUID.randomUUID().toString();
                MapItem item = new PlacePointTool.MarkerCreator(getMapView().getCenterPoint().get())
                        .setUid(uid).setType("a-n-G").showCotDetails(true)
                        .setIconPath("34ae1613-9645-4222-a9d2-e5f243dea2865/Animals/pawprint.png")
                        .setCallsign(titleAnimalType).setColor(Color.YELLOW)
                        .placePoint();
                item.setMetaBoolean("archive", true);
                item.setRemarks(remarks);

                File markImg = Util.bitmapToFile(img, String.format(Locale.US,
                        "attachments/%s/%s.png", uid, titleAnimalType));

                if (!FileSystemUtils.isFile(markImg)) {
                    Log.w(TAG, "Cannot send invalid file: " + markImg);
                    return;
                }

                Log.d(TAG, "Sending file via Mission Package Tool: " + markImg.getName());
                // Create the Mission Package containing the file
                // receiving device will delete after file is imported
                MissionPackageManifest manifest = MissionPackageApi.CreateTempManifest(
                        markImg.getName(), true, true, null);
                manifest.addMapItem(uid);
                manifest.addFile(markImg, uid);

                Contacts contactsInstance = Contacts.getInstance();
                List<Contact> netContacts =  contactsInstance.getAllContacts();

                // instead send null contact list to make user select destination
                // delete local mission package after sent
                MissionPackageApi.Send(pluginContext, manifest,
                        DeleteAfterSendCallback.class, netContacts.toArray(new Contact[0]));
            }
        });
    }

    /** Setup image selection based on cropped pics in the temporary directory */
    private void setListAdapter() {
        RecyclerView objectsView = paneView.findViewById(R.id.picList);
        objectsView.setLayoutManager(new LinearLayoutManager(pluginCtx,
                LinearLayoutManager.HORIZONTAL, false));
        File[] listOfImg = tempImageDir.listFiles();
        assert listOfImg != null;
        DetectedObject[] detectedObjects = new DetectedObject[listOfImg.length];
        for (int i = 0; i < listOfImg.length; i++) {
            detectedObjects[i] = new DetectedObject(listOfImg[i]);
        }
        objectsView.setAdapter(new DetectedObjectAdapter(detectedObjects,
                new DetectedObjectAdapter.Listener() {
                    @Override
                    public void callback(int i) {
                        animalType.setText(detectedObjects[i].label);
                        note.setText(String.format(Locale.US,
                                "confidence %.2f%%", detectedObjects[i].certainty));
                        img = detectedObjects[i].image;
                    }
                }
        ));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null)
            return;
        if (action.equals(SHOW_REPORT_PANE))
            showDropDown(paneView,
                    THIRD_WIDTH, FULL_HEIGHT, // Landscape Dimensions
                    FULL_WIDTH, THIRD_HEIGHT, // Portrait Dimensions
                    false, this);
    }

    @Override
    public void onDropDownVisible(boolean visible) {
        if (visible) {
            setListAdapter();
            // record users pref map center before we force it to be rendered for report
            this.prefCenterMarker = atakPref.get("map_center_designator", false);
            atakPref.set("map_center_designator", true);
        } else
            // when reporting pane is closed restore original pref for no map center designator
            if (!this.prefCenterMarker)
                atakPref.set("map_center_designator", false);
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
