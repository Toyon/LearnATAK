/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier.adapters;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.toyon.plantclassifier.MainDropDown;
import com.toyon.plantclassifier.database.PlantEntity;
import com.atakmap.coremap.maps.coords.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * This Class creates a plant, it is not part of the database but is used to transport data between
 * the ClassifyPlantDropDown, ReportDropDown, and MainDropDown.<br><br>
 * Instead of having to send a bunch of variables through intent we can just send one object
 * toDBEntity() is used in MainDropDown to create the final Plants object that will be stored
 * in PlantsDatabase
 */
public class Plant implements Parcelable {

    private static final String TAG = Plant.class.getSimpleName();
    public String plantType;
    public String edible;
    public String commonName;
    public String certainty = "n/a";
    public Bitmap img = null;
    public String notes = "n/a";
    public String location = "n/a";
    public GeoPoint coordinates;
    public String uid = "";

    public Plant(){ }

    public Plant(String label, String cert, Bitmap b, String common_name, String edible) {
        this.plantType = label;
        this.certainty = cert;
        this.img = b;
        this.commonName = common_name;
        this.edible = edible;
    }

    /** Generate a Plant Entity instance (a table row in the Plant table) from this object */
    public PlantEntity toDBEntity(MainDropDown mainDropDown){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        String coords = getAddress(this.coordinates, mainDropDown);
        return new PlantEntity(this.plantType, this.commonName, byteArray, this.certainty,
                this.edible, this.notes, coords, this.location, this.uid);
    }

    protected Plant(Parcel in) {
        plantType = in.readString();
        edible = in.readString();
        commonName = in.readString();
        certainty = in.readString();
        img = in.readParcelable(Bitmap.class.getClassLoader());
        notes = in.readString();
        location = in.readString();
        coordinates = in.readParcelable(GeoPoint.class.getClassLoader());
        uid = in.readString();
    }

    public static final Creator<Plant> CREATOR = new Creator<Plant>() {
        @Override
        public Plant createFromParcel(Parcel in) {
            return new Plant(in);
        }

        @Override
        public Plant[] newArray(int size) {
            return new Plant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(plantType);
        out.writeString(edible);
        out.writeString(commonName);
        out.writeString(certainty);
        out.writeParcelable(img, flags);
        out.writeString(notes);
        out.writeString(location);
        out.writeParcelable(coordinates, flags);
        out.writeString(uid);
    }

    public String getAddress(GeoPoint geoPoint, MainDropDown mainDropDown){
        Geocoder geocoder = new Geocoder(mainDropDown.getContext(), Locale.getDefault());
        String result = "No location found, please enter manually :(";
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
        } catch (IOException e) {
            Log.w(TAG, "error finding address. " + e);
        }
        if (addressList != null && addressList.size() > 0) {
            Address address = addressList.get(0);

            result = address.getAddressLine(0);

            result = result.replaceFirst(",", "\n");
            int index = result.lastIndexOf(",");
            result = result.substring(0, index) + "\n" + result.substring(index+1);
        }
        return result;
    }

}

