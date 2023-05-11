package com.atakmap.android.democnn.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.democnn.plugin.R;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.maps.time.CoordinatedTime;

import java.util.List;

/** Array adapter for reported pet sightings */
public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder> {

    private static final String TAG = AnimalAdapter.class.getSimpleName();
    private final List<Animal> localDataSet;
    private final ItemBtnListener listener;

    /** Listener interface allows registration of callbacks to monitor item action button clicks */
    private interface ItemBtnListener {
        void focusCallback(int i);
        void dialogCallback(int i);
    }

    /** Provide a reference to the type of views that you are using  */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView animalTypeText;
        private final TextView confidenceText;
        private final TextView notesText;
        private final TextView detailsText;
        private final ImageView previewImage;

        public ViewHolder(View view) {
            super(view);
            animalTypeText = view.findViewById(R.id.animalType);
            previewImage = view.findViewById(R.id.imgPreview);
            confidenceText = view.findViewById(R.id.labelConfidence);
            notesText = view.findViewById(R.id.notes);
            detailsText = view.findViewById(R.id.extraDetails);
            ImageButton focusBtn = view.findViewById(R.id.buttonMapFocus);
            focusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.focusCallback(getAdapterPosition());
                }
            });

            ImageButton detailsBtn = view.findViewById(R.id.buttonDetails);
            detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.dialogCallback(getAdapterPosition());
                }
            });
        }

        public void setAnimalTypeText(String animalType) { animalTypeText.setText(animalType); }
        public TextView getConfidenceText() { return confidenceText; }
        public TextView getNotesText() { return notesText; }
        public TextView getDetailsText() { return detailsText; }
        public ImageView getPreviewImage() { return previewImage; }
    }

    /**
     * Initialize the dataset of the Adapter
     * @param dataSet List<Animal> animal markers and extra data for rendering
     */
    public AnimalAdapter(List<Animal> dataSet, MapView mapView) {
        localDataSet = dataSet;
        this.listener = new ItemBtnListener() {
            @Override
            public void focusCallback(int i) {
                Intent focusIntent = new Intent();
                focusIntent.setAction("com.atakmap.android.maps.FOCUS");
                focusIntent.putExtra("uid", localDataSet.get(i).mapItemUid);
                AtakBroadcast.getInstance().sendBroadcast(focusIntent);
            }

            @Override
            public void dialogCallback(int selectedIndex) {
                ImageView image = new ImageButton(mapView.getContext());
                Animal pet = localDataSet.get(selectedIndex);
                image.setImageBitmap(pet.img);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mapView.getContext())
                        .setMessage("Reported Pet Sighting:\nAnimal: " + pet.animalType + "\n" +
                                pet.dmsCoordinates + "\nSubmitted By: " + pet.author)
                        .setView(image)
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MapItem item = mapView.getRootGroup().deepFindUID(pet.mapItemUid);

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
                                linkDetail.setAttribute("uid", pet.mapItemUid);
                                linkDetail.setAttribute("relation", "none");
                                linkDetail.setAttribute("type", "none");

                                deleteDetails.addChild(linkDetail);
                                deleteDetails.addChild(new CotDetail("__forcedelete"));
                                deleteEvent.setDetail(deleteDetails);
                                Log.d(TAG, "Send Delete Event to others " + deleteEvent);

                                MapGroup group = item.getGroup();
                                if (group != null)
                                    group.removeItem(item);
                                CotMapComponent.getExternalDispatcher().dispatch(deleteEvent);
                            }
                        });
                dialogBuilder.create().show();
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_pet, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        try {
            viewHolder.setAnimalTypeText(localDataSet.get(position).animalType);
            viewHolder.getConfidenceText().setText(localDataSet.get(position).certainty);
            viewHolder.getNotesText().setText(localDataSet.get(position).notes);
            viewHolder.getDetailsText().setText(localDataSet.get(position).timeLocationText);
            viewHolder.getPreviewImage().setImageBitmap(localDataSet.get(position).img);
        } catch (Exception e) {
            Log.e(TAG, "Failed to bind text" + e);
        }
    }

    @Override
    public int getItemCount() { return localDataSet.size(); }

    /** Update adapter data and notify RecyclerView UI to update with new items */
    public void updateData(List<Animal> newAnimals) {
        localDataSet.clear();
        localDataSet.addAll(newAnimals);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() { notifyDataSetChanged(); }
        });
    }
}
