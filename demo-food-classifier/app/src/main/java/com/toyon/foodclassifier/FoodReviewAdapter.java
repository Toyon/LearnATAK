/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.foodclassifier;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.atakmap.android.cot.CotMapComponent;
import com.toyon.foodclassifier.database.PictureReview;
import com.toyon.foodclassifier.plugin.R;
import com.toyon.foodclassifier.util.TakUtil;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.cot.event.CotEvent;

import java.util.List;
import java.util.Locale;

/** Adapter to handle display logic of food reviews in plugin's primary pane ListView */
public class FoodReviewAdapter extends BaseAdapter {

    private final static String TAG = FoodReviewAdapter.class.getSimpleName();
    private List<PictureReview> pictureReviewArrayList;
    private final Callbacks callbacks;

    /** Data structure to group view elements of food review list items (review_list_item.xml) */
    private static class ViewHolder {
        ImageView itemImageView;
        TextView itemTitleTextView;
        TextView itemDescriptionTextView;
        Button deleteButton;
        TextView addressTextView;

        public ViewHolder(View view) {
            itemImageView = view.findViewById(R.id.item_img);
            itemTitleTextView = view.findViewById(R.id.item_name);
            itemDescriptionTextView = view.findViewById(R.id.item_description);
            deleteButton = view.findViewById(R.id.item_delete_btn);
            addressTextView = view.findViewById(R.id.item_address);
        }
    }

    /** Constructor parameter interface to provide functional callbacks to separate concerns */
    public interface Callbacks {
        void deleteReview(PictureReview review);
    }

    /** constructor */
    public FoodReviewAdapter(Callbacks callbacks, List<PictureReview> array) {
        this.callbacks = callbacks;
        this.pictureReviewArrayList = array;
    }

    /** Repopulate data for UI list adapter */
    public void updateData(List<PictureReview> reviews) {
        pictureReviewArrayList = reviews;
        notifyDataSetChanged();
    }

    /**
     * Populate food review record info for the given position within the list of all records
     * and register UI functionality specific to each individual review record.
     */
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        if(convertView == null) {
            Log.d(TAG, "Inflating Review Item ViewHolder for item " + position);
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PictureReview pictureReview = pictureReviewArrayList.get(position);
        viewHolder.itemImageView.setImageBitmap(pictureReview.getBitmap());
        viewHolder.itemTitleTextView.setText(pictureReview.getName());
        viewHolder.itemDescriptionTextView.setText(pictureReview.getStarStr());
        viewHolder.addressTextView.setText(pictureReview.getAddress());
        TakUtil.setButtonToast(MapView.getMapView().getContext(), viewHolder.deleteButton,
                String.format(Locale.US, "Delete %s review", pictureReview.getName()));
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureReview pr = pictureReviewArrayList.get(position);
                callbacks.deleteReview(pr);
                // delete local associated food review marker from the map
                MapItem item = MapView.getMapView().getRootGroup().deepFindUID(pr.getUid());
                try {
                    item.getGroup().removeItem(item);
                } catch (NullPointerException e) {
                    Log.w(TAG, "Unable to delete map marker " + pr.getUid());
                }
                // (sync with connected clients) delete remote food review map marker
                CotEvent deleteEvent = TakUtil.deleteCotEvent(pr.getUid());
                CotMapComponent.getExternalDispatcher().dispatch(deleteEvent);
                Log.d(TAG, "Deleted " + pr.getName() + " -- " +pr.getUid());
            }
        });

        return convertView;
    }



    @Override
    public int getCount() { return pictureReviewArrayList.size(); }

    @Override
    public PictureReview getItem(int position){ return pictureReviewArrayList.get(position); }

    @Override
    public long getItemId(int position){ return position; }


}
