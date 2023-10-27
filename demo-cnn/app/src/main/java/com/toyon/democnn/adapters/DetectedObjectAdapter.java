package com.toyon.democnn.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.toyon.democnn.plugin.R;

import java.util.Locale;

/** Array adapter for detected objects in image for reporting pets */
public class DetectedObjectAdapter extends RecyclerView.Adapter<DetectedObjectAdapter.ViewHolder> {

    private static final String TAG = DetectedObjectAdapter.class.getSimpleName();
    private int selectedPosition = RecyclerView.NO_POSITION;
    private final DetectedObject[] objects;
    private final Listener listener;

    /** Listener interface to allow other classes to register callbacks to monitor selection */
    public interface Listener {
        void callback(int i);
    }

    /** Class that populates each item view layout (item_cnn_object.xml) with data */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView croppedImg;
        private final TextView animalLabel;
        private final TextView certainty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            croppedImg = itemView.findViewById(R.id.croppedImg);
            animalLabel = itemView.findViewById(R.id.animalLabel);
            certainty = itemView.findViewById(R.id.certainty);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int lastSelection = selectedPosition;
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(lastSelection);
                    notifyItemChanged(selectedPosition);
                    listener.callback(selectedPosition);
                }
            });
        }

        public void bind(DetectedObject object) {
            itemView.setClickable(true);
            croppedImg.setImageBitmap(object.image);
            animalLabel.setText(object.label);
            certainty.setText(String.format(Locale.US, "%.2f %%", object.certainty));
            ViewCompat.setBackgroundTintList(itemView, (selectedPosition == getAdapterPosition()) ?
                    ColorStateList.valueOf(Color.rgb(69, 133, 24)) :
                    ColorStateList.valueOf(Color.rgb(35, 53, 89)));
        }
    }

    /** Constructor: default listener logs the selected item index */
    public DetectedObjectAdapter(@NonNull DetectedObject[] objects,
                                 @Nullable Listener listener) {
        this.objects = objects;
        this.listener = (listener != null) ? listener : new Listener() {
            @Override
            public void callback(int i) {
                Log.d(TAG, "SELECTED ITEM INDEX [" + i + "]");
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cnn_object, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.bind(objects[position]);
        } catch (Exception e) {
            Log.e(TAG, "FAILED TO BIND VIEW");
        }
    }

    @Override
    public int getItemCount() { return objects.length; }

}