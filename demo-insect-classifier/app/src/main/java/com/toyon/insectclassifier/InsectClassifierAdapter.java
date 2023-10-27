/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.insectclassifier;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.toyon.insectclassifier.plugin.R;

import java.util.List;

/** Logic for displaying insect classification cards on the main plugin pane */
public class InsectClassifierAdapter extends BaseAdapter {
    private static final String TAG = InsectClassifierAdapter.class.getSimpleName();
    private List<PictureReview> pictureReviewArrayList;

    public InsectClassifierAdapter(Context context, List<PictureReview> array){
        this.pictureReviewArrayList = array;
    }

    public void addPictureReview(PictureReview pictureReview){
        pictureReviewArrayList.add(pictureReview);
        notifyDataSetChanged();
    }
    public void setPictureReviews(List<PictureReview> pictureReviews){
        this.pictureReviewArrayList = pictureReviews;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pictureReviewArrayList.size();
    }

    @Override
    public PictureReview getItem(int position) {
        return pictureReviewArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
            Log.d(TAG, "inflating Review Item ViewHolder for Item " + position);
            viewHolder = new ViewHolder();
            viewHolder.itemImageView = convertView.findViewById(R.id.item_img);
            viewHolder.itemTitleTextView = convertView.findViewById(R.id.item_name);
            viewHolder.itemDescriptionTextView = convertView.findViewById(R.id.item_description);
            viewHolder.addressTextView = convertView.findViewById(R.id.item_address);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PictureReview pictureReview = pictureReviewArrayList.get(position);
        viewHolder.itemImageView.setImageBitmap(pictureReview.getBitmap());
        viewHolder.itemTitleTextView.setText(pictureReview.getName());
        String setTextString = pictureReview.getSciName();
        viewHolder.itemDescriptionTextView.setText(setTextString);
        viewHolder.deleteButton = convertView.findViewById(R.id.item_delete_btn);
        viewHolder.addressTextView.setText(pictureReview.getAddress());
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.format("Deleting item at index %d - %s",
                        position, pictureReview.getName()));
                Log.d(TAG, "picture: " + pictureReviewArrayList.get(position));
                PictureReview pr = pictureReviewArrayList.get(position);
                pictureReviewArrayList.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView itemImageView;
        TextView itemTitleTextView;
        TextView itemDescriptionTextView;
        Button deleteButton;
        TextView addressTextView;
    }
}
