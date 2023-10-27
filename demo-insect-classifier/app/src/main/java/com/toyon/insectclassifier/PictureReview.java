/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.insectclassifier;

import static com.atakmap.android.maps.MapView.getMapView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.atakmap.coremap.maps.coords.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/** Data structure object to group associated text descriptors and map info for a processed image */
public class PictureReview {
    private static final String TAG = PictureReview.class.getSimpleName();
    private byte[] bitmapData;
    private String sciName;
    private String name;
    private String address;
    private String uid;

    public PictureReview(){
        bitmapData = null;
        sciName = "";
        name = "";
        address = "";
    }
    public PictureReview(Bitmap bitmap, String review, String inputName, Context context, String uid) {
        this.bitmapData = getBitmapAsByteArray(bitmap);
        Log.d(TAG, "after Bitmap");
        this.sciName = review;
        this.name = inputName;
        this.address = getAddressLastLocation(context);
        this.uid = uid;
        Log.d(TAG, "Geocoded address = " + this.address);
    }

    public void setSciName(String sciName){ this.sciName = sciName; }
    public String getSciName() { return sciName; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public void setAddress(String address){ this.address = address; }
    public String getAddress() { return address; }
    public Bitmap getBitmap() {
        return BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }

    public void setBitmapData(byte[] byteArray){ this.bitmapData = byteArray; }
    public byte[] getBitmapData(){ return bitmapData; }
    public String getUid(){ return uid; }
    public void setUid(String uid){ this.uid = uid; }

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



}
