/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.foodclassifier.database;

import static com.atakmap.android.maps.MapView.getMapView;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.atakmap.android.missionpackage.file.MissionPackageContent;
import com.atakmap.android.missionpackage.file.NameValuePair;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;
import com.atakmap.coremap.filesystem.FileSystemUtils;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.time.CoordinatedTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * Objects that store a picture of the food, the address it was taken out, how many stars the
 * user gave it and the name of the food. We want to place these objects into the ArrayList so that
 * the ListView can display each PictureReview object
 */
@Entity(tableName = "picture_reviews")
public class PictureReview {

    private final static String TAG = PictureReview.class.getSimpleName();

    /** database unique identifier key */
    @PrimaryKey(autoGenerate = true)
    private int id;
    private byte[] bitmapData;
    private int starReview;
    private String name;
    private String address;

    /** map unique identifier */
    private String uid;
    private double longitude;
    private double latitude;

    public PictureReview() {
        bitmapData = null;
        starReview = -1;
        name = "";
        address = "";
    }

    /** Construct Picture Review using user's current location for the review record */
    public PictureReview(Bitmap bitmap, int review, String inputName, Context context, String uid) {
        this.bitmapData = getBitmapAsByteArray(bitmap);
        this.starReview = review;
        this.name = inputName;
        this.address = getAddressLastLocation(context);
        this.uid = uid;
        GeoPoint geoPoint = getMapView().getSelfMarker().getPoint();
        this.latitude = geoPoint.getLatitude();
        this.longitude = geoPoint.getLongitude();
        Log.d(TAG, "Geocoded address = " + this.address);
    }

    /** Construct Picture Review using specified coordinates for the review record */
    public PictureReview(byte[] byteArray, int review, String inputName, Context context,
                         String uid, String address, double latitude, double longitude) {
        this.bitmapData = byteArray;
        this.starReview = review;
        this.name = inputName;
        this.address = address;
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        Log.d(TAG, "Geocoded address = " + this.address);
    }

    public void setStarReview(int starReview){ this.starReview = starReview; }
    public int getStarReview() { return starReview; }
    public String getStarStr() { return String.format(Locale.US, "%d stars", starReview); }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public void setAddress(String address){ this.address = address; }
    public String getAddress() { return address; }
    public Bitmap getBitmap() {
        return BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }
    public void setId(int id){ this.id = id; }
    public int getId(){ return id; }
    public void setBitmapData(byte[] byteArray){ this.bitmapData = byteArray; }
    public byte[] getBitmapData(){ return bitmapData; }
    public String getUid(){ return uid; }
    public void setUid(String uid){ this.uid = uid; }
    public double getLongitude(){ return longitude; }
    public void setLongitude(double longitude){ this.longitude = longitude; }
    public double getLatitude(){ return latitude; }
    public void setLatitude(double latitude){ this.latitude = latitude; }

    private static String addressToString(Address addr) {
        return String.format("%s, %s, %s, %s, %s", addr.getAddressLine(0),
                addr.getLocality(), addr.getAdminArea(),
                addr.getPostalCode(), addr.getCountryName());
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    /** reverse geocode a street address using the coordinates of the last known device location */
    private String getAddressLastLocation(Context context) {
        GeoPoint loc = getMapView().getSelfMarker().getPoint();
        try {
            List<Address> addressList = new Geocoder(context, Locale.getDefault())
                    .getFromLocation(loc.getLatitude(), loc.getLongitude(),1);
            return addressList.isEmpty() ? loc.toString() : addressToString(addressList.get(0));
        } catch (IOException e) {
            e.printStackTrace();
            return String.format(Locale.US, "Lat: %.2f, Lng: %.2f",
                    loc.getLatitude(), loc.getLongitude());
        }
    }

    /** Create Mission Package Content for Picture Review to include in Data Package Manifest */
    public MissionPackageContent toMissionPackageContent() {
        MissionPackageContent content = new MissionPackageContent();
        content.setIsCoT(true);
        content.setManifestUid(uid + File.separatorChar + uid + ".cot");
        content.setParameter(new NameValuePair("name", name));
        content.setParameter("uid", uid);
        content.setParameter("stars", Integer.toString(starReview));
        content.setParameter("address", address);
        content.setParameter("latitude", Double.toString(latitude));
        content.setParameter("longitude", Double.toString(longitude));
        return content;
    }

    /** Create CoT Event object for Picture Review to generate Map Marker */
    public CotEvent toCotEvent() {
        // specify <contact callsign=''/> within details to render marker name / title
        CotDetail contact = new CotDetail("contact");
        contact.setAttribute("callsign", name);

        // specify <marker iconsetpath=''/> within details to specify default dining icon on map
        CotDetail markerIcon = new CotDetail("usericon");
        markerIcon.setAttribute("iconsetpath", "f7f71666-8b28-4b57-9fbb-e38e61d33b79/Google/dining.png");

        // specify additional custom data for the plugin's use
        CotDetail pluginDetail = new CotDetail("foodclassifier");
        pluginDetail.setAttribute("stars", getStarStr());
        pluginDetail.setAttribute("address", address);

        // set CoT Event detail children with render info <detail><contact\><marker\></detail>
        CotDetail details = new CotDetail();
        details.addChild(markerIcon);
        details.addChild(contact);
        details.addChild(pluginDetail);

        // set CoT Event location <point lat='' lon='' hae='' ce='' le=''/>
        CotPoint markerPoint = new CotPoint(latitude, longitude,
                getMapView().getElevation(), 0, 0);

        // provide minimum default CoT Event attributes for valid event to be added to the map
        // include the point and details to properly render the food review map marker
        CotEvent markerEvent = new CotEvent();
        markerEvent.setVersion("2.0");
        markerEvent.setUID(uid);
        markerEvent.setType("a-n-G");
        markerEvent.setTime(new CoordinatedTime(CoordinatedTime.currentTimeMillis()));
        markerEvent.setStart(new CoordinatedTime(CoordinatedTime.currentTimeMillis()));
        markerEvent.setPoint(markerPoint);
        markerEvent.setDetail(details);
        return markerEvent;
    }

    /** Populate Picture Review data from Mission Package Content object */
    public PictureReview fromCotEvent(CotEvent baseEvent) {
        uid = baseEvent.getUID();
        CotDetail details = baseEvent.getDetail();
        name = details.getChild("contact").getAttribute("callsign");
        try {
            starReview = Integer.parseInt(details.getChild("foodclassifier")
                    .getAttribute("stars").split(" ")[0]);
            address = details.getChild("foodclassifier").getAttribute("address");
        } catch (NullPointerException e) {
            Log.w(TAG, "Error extracting food classifier details");
        }
        latitude = baseEvent.getCotPoint().getLat();
        longitude = baseEvent.getCotPoint().getLon();
        return this;
    }


    /** save food review image to file system for ATAK to locate map marker associated picture */
    public void addMarkerAttachment() {
        // add picture as attachment to map marker to simplify Data Package creation
        File foodPng = FileSystemUtils.getItem("attachments" + File.separatorChar +
                uid + File.separatorChar+ name + ".png");
        try {
            Objects.requireNonNull(foodPng.getParentFile()).mkdir();
            FileOutputStream picStream = new FileOutputStream(foodPng);
            getBitmap().compress(Bitmap.CompressFormat.PNG, 100, picStream);
            picStream.flush();
            picStream.close();
        } catch (IOException e) {
            com.atakmap.coremap.log.Log.w(TAG, "Unable to save food review image as marker attachment\n" + e);
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public String toString() {
        return "PictureReview{" +
                ", starReview=" + starReview +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", uid='" + uid + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
