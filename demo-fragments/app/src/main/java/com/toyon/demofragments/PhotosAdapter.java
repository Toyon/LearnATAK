package com.toyon.demofragments;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.toyon.demofragments.database.PhotosEntity;
import com.toyon.demofragments.database.PhotosViewModel;
import com.toyon.demofragments.plugin.R;
import com.atakmap.android.maps.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoItemView> {
    public List<PhotosEntity> localDataset;

    private final PhotosViewModel photosViewModel;

    private final ItemBtnListener listener;

    private interface ItemBtnListener{
        void delete(int i);
    }

    public class PhotoItemView extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public PhotoItemView(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            Button delete = view.findViewById(R.id.delete_button);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.delete(getAdapterPosition());
                }
            });
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public PhotosAdapter(PhotosViewModel photosViewModel, DemoFragmentsDropDownReceiver demoFragmentsDropDownReceiver) {
        this.localDataset = new ArrayList<>();
        this.photosViewModel = photosViewModel;
        try {
            this.photosViewModel.getAllPhotos().observe(
                    Objects.requireNonNull(ViewTreeLifecycleOwner.get(MapView.getMapView())),
                    handleDatabaseChange);
        } catch (Exception e) {
            Log.e("Photos Adapter", "Failed with exception " + e);
        }
        this.listener = new ItemBtnListener() {
            @Override
            public void delete(int i) {
                PhotosEntity photo = localDataset.get(i);
                try {
                    photosViewModel.delete(photo);
                    notifyItemRemoved(i);
                } catch (Exception e) {
                    Log.w("Photos Adapter", "Error deleting database record");
                }
            }
        };
    }

    /**
     * Observe live data updates connected to plants table to update plant category lists.
     * Any modifications to the database will trigger a UI update to the recyclerview
     */
    private Observer<List<PhotosEntity>> handleDatabaseChange = new Observer<List<PhotosEntity>>() {
        @Override
        public void onChanged(List<PhotosEntity> newPhotos) {
            int currentSize = localDataset.size();
            localDataset.clear();
            notifyItemRangeRemoved(0, currentSize);
            localDataset.addAll( newPhotos);
            new Handler(Looper.getMainLooper()).post(new Runnable(){
                @SuppressLint(("NotifyDataSetChanged"))
                @Override
                public void run() {
                    notifyItemRangeInserted(0, localDataset.size());
                }
            });
        }
    };

    @NonNull
    @Override
    public PhotoItemView onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.photo_layout, viewGroup, false);
        return new PhotoItemView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoItemView viewHolder, final int position){
        try{
            viewHolder.getImageView().setImageBitmap(BitmapFactory.decodeByteArray(
                    localDataset.get(position).image, 0,
                    localDataset.get(position).image.length));
        }catch (Exception e){
            Log.e("Photo's Adapter", "failed to bind to text" + e);
        }
    }

    @Override
    public int getItemCount(){ return localDataset.size(); }
}
