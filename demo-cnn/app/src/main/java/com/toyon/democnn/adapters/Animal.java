package com.toyon.democnn.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.atakmap.android.importexport.CotEventFactory;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.util.AttachmentManager;
import com.atakmap.coremap.conversions.CoordinateFormat;
import com.atakmap.coremap.conversions.CoordinateFormatUtilities;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;

import java.io.File;
import java.util.List;

/** Animal Map Item and Attachment data holder */
public class Animal {

    private static final String TAG = Animal.class.getSimpleName();
    public String mapItemUid;
    public String animalType;
    public String timeLocationText;
    public String notes;
    public String certainty = "n/a";
    public Bitmap img;
    public String dmsCoordinates = "n/a"; // DMS == Degree Minute Seconds
    public String author = "n/a";

    public Animal(MapItem mapItem) {

        List<File> attachments = AttachmentManager.getAttachments(mapItem.getUID());
        if (attachments.size() != 1)
            return;
        mapItemUid = mapItem.getUID();
        animalType = mapItem.getTitle();
        img = BitmapFactory.decodeFile(attachments.get(0).getAbsolutePath());
        notes = mapItem.getRemarks();
        CotEvent petCot = CotEventFactory.createCotEvent(mapItem);
        Log.d(TAG, petCot.toString());
        timeLocationText = petCot.getTime().toString();
        dmsCoordinates = CoordinateFormatUtilities.formatToShortString(
                petCot.getGeoPoint(), CoordinateFormat.DMS);

        CotDetail details = petCot.getDetail();
        CotDetail linkDetail = details.getChild("link");
        try {
            author = linkDetail.getAttribute("parent_callsign");
        } catch (Exception e) {
            Log.w(TAG, "unable to extract author from CoT\n" + details);
        }

        String[] parsedRemarks = mapItem.getRemarks().split("~");
        if (parsedRemarks.length == 2) {
            timeLocationText += " @ " + parsedRemarks[0];
            notes = parsedRemarks[1];
        }
        if (parsedRemarks.length == 3) {
            timeLocationText += " @ " + parsedRemarks[0];
            certainty = parsedRemarks[1];
            notes = parsedRemarks[2];
        }

    }
}
