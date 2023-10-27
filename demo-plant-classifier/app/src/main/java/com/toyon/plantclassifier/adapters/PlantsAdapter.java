/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.toyon.plantclassifier.database.PlantEntity;
import com.toyon.plantclassifier.database.PlantsViewModel;
import com.toyon.plantclassifier.MainDropDown;
import com.toyon.plantclassifier.plugin.R;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.time.CoordinatedTime;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Plants adapter is used to update the plants displayed on the MainDropDown layout
 * When a plant is submitted or deleted, this class will be called by TabItems to handle the request
 * and populate the main layout with the updated plants. <br><br>
 * This is nested inside the PlantCategoryAdapter.
 */
public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.PlantListItemView> {

    private static final String TAG = PlantsAdapter.class.getSimpleName();
    private final List<PlantEntity> localDataSet;
    private final ItemBtnListener listener;
    private Bitmap img;
    public PlantsViewModel viewPlants;
    private ViewGroup viewGroup;
    PopupWindow popupWindow;

    private interface ItemBtnListener{
        void popup(int i);
        void location(int i);
        void delete(int i);
    }

    /**
     * Object to describe UI elements of each plant record card and implements common functionality
     * for each items interactive buttons.
     */
    public class PlantListItemView extends RecyclerView.ViewHolder {
        private final TextView commonNameText;
        private final TextView locationText;
        private final ImageView previewImage;


        public PlantListItemView(View view){
            super(view);
            previewImage = view.findViewById(R.id.img_preview);
            commonNameText = view.findViewById(R.id.common_name);
            locationText = view.findViewById(R.id.location_text);

            Button locationButton = view.findViewById(R.id.location_button);
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.location(getAdapterPosition());
                }
            });

            Button detailsButton = view.findViewById(R.id.info_button);
            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.popup(getAdapterPosition());
                }
            });

            Button deleteButton = view.findViewById(R.id.trash_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.delete(getAdapterPosition());
                }
            });
        }
        public TextView getCommonNameText(){ return commonNameText; }
        public ImageView getPreviewImage(){ return previewImage; }

        public TextView getLocationText(){return locationText; }
    }

    public PlantsAdapter(List<PlantEntity> dataSet, PlantsViewModel viewPlants) {
        localDataSet = dataSet;
        this.viewPlants = viewPlants;
        MapView mapView = MapView.getMapView();
        this.listener = new ItemBtnListener() {
            @Override
            public void location(int i) {
                Intent focusIntent = new Intent();
                focusIntent.setAction("com.atakmap.android.maps.FOCUS");
                focusIntent.putExtra("uid", localDataSet.get(i).uid);
                AtakBroadcast.getInstance().sendBroadcast(focusIntent);
            }

            @Override
            public void popup(int i) {
                View popupView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.plant_popup, viewGroup, false);
                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        popupWindow.showAtLocation(mapView, Gravity.CENTER,0,0);
                    }

                }, 200L);

                TextView scientific_name_entry = popupView.findViewById(R.id.scientific_name_entry);
                TextView common_name_entry = popupView.findViewById(R.id.common_name_entry);
                TextView confidence = popupView.findViewById(R.id.confidence_level);
                TextView edibility_advice_entry = popupView.findViewById(R.id.edibility_advice_entry);
                TextView location_entry = popupView.findViewById(R.id.location_entry);
                TextView notes = popupView.findViewById(R.id.notes_entry);

                Button close_button = popupView.findViewById(R.id.close_button);

                scientific_name_entry.setText(String.valueOf(localDataSet.get(i).plantType));
                common_name_entry.setText(localDataSet.get(i).commonName);
                confidence.setText("Confidence: " + localDataSet.get(i).certainty);
                edibility_advice_entry.setText(localDataSet.get(i).edible);
                location_entry.setText(localDataSet.get(i).locationName);
                notes.setText(localDataSet.get(i).notes);

                close_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });

            }

            @Override
            public void delete(int i) {

                PlantEntity plant = localDataSet.get(i);
                img = BitmapFactory.decodeByteArray(plant.image, 0, plant.image.length);
                MapItem item = mapView.getRootGroup().deepFindUID(plant.uid);

                CotEvent deleteEvent = new CotEvent();
                deleteEvent.setUID("any_uid_is_good");
                deleteEvent.setHow("m-g");
                deleteEvent.setType("t-x-d-d");
                CoordinatedTime currentTime =
                        new CoordinatedTime(CoordinatedTime.currentTimeMillis());
                deleteEvent.setStale(currentTime);
                deleteEvent.setStart(currentTime);
                deleteEvent.setTime(currentTime);

                CotDetail deleteDetails = new CotDetail();
                CotDetail linkDetail = new CotDetail("link");
                linkDetail.setAttribute("uid", plant.uid);
                linkDetail.setAttribute("relation", "none");
                linkDetail.setAttribute("type", "none");

                deleteDetails.addChild(linkDetail);
                deleteDetails.addChild(new CotDetail("__forcedelete"));
                deleteEvent.setDetail(deleteDetails);
                Log.d(TAG, "Send Delete Event to others " + deleteEvent);
                try {
                    MapGroup group = item.getGroup();
                    if (group != null)
                        group.removeItem(item);
                    CotMapComponent.getExternalDispatcher().dispatch(deleteEvent);
                } catch(Exception e) {
                    Log.w(TAG, "Failed to delete map marker " + e);
                }
                try {
                    viewPlants.delete(plant);
                } catch (Exception e) {
                    Log.w(TAG, "Error deleting database record");
                }
                Intent mainIntent = new Intent();
                mainIntent.setAction(MainDropDown.SHOW_MAIN_PANE);
                AtakBroadcast.getInstance().sendBroadcast(mainIntent);

            }
        };
    }

    @NonNull
    @Override
    public PlantListItemView onCreateViewHolder(ViewGroup viewGroup, int viewType){
        this.viewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_plant, viewGroup, false);
        return new PlantListItemView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantListItemView viewHolder, final int position){
        try{
            viewHolder.getCommonNameText().setText(localDataSet.get(position).commonName);
            viewHolder.getLocationText().setText(localDataSet.get(position).location);
            viewHolder.getPreviewImage().setImageBitmap(BitmapFactory.decodeByteArray(
                    localDataSet.get(position).image, 0,
                    localDataSet.get(position).image.length));
        }catch (Exception e){
            Log.e(TAG, "failed to bind to text" + e);
        }
    }

    @Override
    public int getItemCount(){ return localDataSet.size(); }

    public void updateData(List<PlantEntity> newPlants){
        localDataSet.clear();
        localDataSet.addAll(newPlants);

        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @SuppressLint(("NotifyDataSetChanged"))
            @Override
            public void run(){ notifyDataSetChanged(); }
        });
    }


}
