/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.foodclassifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.FileObserver;
import android.view.View;
import android.widget.ListView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.toyon.foodclassifier.database.PictureReview;
import com.toyon.foodclassifier.database.PictureReviewViewModel;
import com.toyon.foodclassifier.util.PackageManager;
import com.toyon.foodclassifier.util.TakUtil;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapEvent;
import com.atakmap.android.maps.MapEventDispatcher;
import com.atakmap.android.maps.MapView;
import com.toyon.foodclassifier.plugin.R;
import com.atakmap.android.missionpackage.file.MissionPackageExtractor;
import com.atakmap.android.missionpackage.file.MissionPackageManifest;
import com.atakmap.app.ATAKActivity;
import com.atakmap.coremap.log.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Broadcast receiver and accompanying logic for UI functionality of main plugin view.
 */
public class MainDropDown extends DropDownReceiver implements OnStateListener, MapEventDispatcher.MapEventDispatchListener {

    public static final String TAG = MainDropDown.class.getSimpleName();
    public static final String SHOW_PLUGIN = "com.toyon.foodclassifier.SHOW_PLUGIN";
    public static final String ADD_TO_LIST = "com.toyon.foodclassifier.PluginTemplateDropDownReceiver.ADD_TO_LIST";

    private final View paneView;
    private final PictureReviewViewModel prViewModel;
    private final FoodReviewAdapter adapter;

    /** Observes ATAK DataPackage directory. stops sending events if garbage collected */
    private final FileObserver fileObserver;

    /** Observes LiveData list synced to query for all food review records */
    private final Observer<List<PictureReview>> dataObserver;

    /** Service to manage timer delayed tasks to clean up unused plugin generated data packages */
    private final ScheduledExecutorService executorService;

    private final CameraActivity.CameraDataListener cdl = new CameraActivity.CameraDataListener();
    private final CameraActivity.CameraDataReceiver cdr = new CameraActivity.CameraDataReceiver() {
        public void onCameraDataReceived(Bitmap b) {
            //this code tels us where to go after we get the picture that the camera has taken
            Log.d(TAG, "Camera Activity produced a bitmap of " + b.getByteCount() + " bytes");
            Intent intent = new Intent()
                    .setAction(ReviewPageDropDown.SAMPLE_ACTION)
                    .putExtra("imageBitmap", b);
            AtakBroadcast.getInstance().sendBroadcast(intent);
        }
    };

    public MainDropDown(final MapView mapView, final Context context) {
        super(mapView);
        executorService = Executors.newSingleThreadScheduledExecutor();
        paneView = PluginLayoutInflater.inflate(context, R.layout.main_pane, null);

        // setup view model to manage room database access and live data query of review records
        ATAKActivity atakActivity = (ATAKActivity) mapView.getContext();
        prViewModel = new ViewModelProvider(atakActivity).get(PictureReviewViewModel.class);

        // register map listener to sync map events with plugin managed data
        getMapView().getMapEventDispatcher().addMapEventListener(this);

        // initialize list view with adapter to display food review records
        ListView listView = paneView.findViewById(R.id.list_view);
        ArrayList<PictureReview> itemList = new ArrayList<>();
        adapter = new FoodReviewAdapter(new FoodReviewAdapter.Callbacks() {
            @Override
            public void deleteReview(PictureReview review) {
                prViewModel.database.delete(review);
            }
        }, itemList);
        listView.setAdapter(adapter);

        // sync food review list adapter with database records by observing LiveData object that
        // is synced to the latest results of a query to fetch all review records
        dataObserver = new Observer<List<PictureReview>>() {
            @Override
            public void onChanged(List<PictureReview> pictureReviews) {
                adapter.updateData(pictureReviews);
            }
        };
        prViewModel.allPR.observe(atakActivity, dataObserver);

        // camera capture and review food item
        final Button cameraBtn = paneView.findViewById(R.id.img_capture_btn);
        TakUtil.setButtonToast(mapView.getContext(), cameraBtn, "Create a new food review");
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdl.register(getMapView().getContext(), cdr);
                // launch default camera in activity outside of ATAK classloader paradigm
                Intent intent = new Intent()
                        .setClassName("com.toyon.foodclassifier.plugin",
                                "com.toyon.foodclassifier.CameraActivity")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getMapView().getContext().startActivity(intent);
            }
        });

        // share food reviews with another TAK user
        final Button shareBtn = paneView.findViewById(R.id.share_btn);
        TakUtil.setButtonToast(mapView.getContext(), shareBtn, "Share my food review records");
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager.shareReviews(prViewModel.getCopyReviews(), getMapView().getContext());
            }
        });

        // export local food review database to TAK data package
        final Button exportBtn = paneView.findViewById(R.id.export_btn);
        TakUtil.setButtonToast(mapView.getContext(), exportBtn,
                "Backup food reviews to local data package");
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager.exportReviews(prViewModel.getCopyReviews(), getMapView());
            }
        });

        // restore local food review database from TAK data package backup
        final Button importBtn = paneView.findViewById(R.id.import_btn);
        TakUtil.setButtonToast(mapView.getContext(), importBtn,
                "Restore reviews from local backup data package");
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MissionPackageManifest manifest = MissionPackageExtractor.GetManifest(
                        new File(PackageManager.getPathExportPluginPackage()));
                PackageManager.importReviews(manifest, mapView.getContext(), prViewModel.database);
            }
        });

        // clear any previously shared or received food reviews
        PackageManager.extractSharedReviews(mapView.getContext(), prViewModel.database);
        File rootDataPackageDir = new File(PackageManager.getRootDir());

        // observe file events in data package folder to take action on plugin managed packages
        fileObserver = new FileObserver(rootDataPackageDir) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                // MOVED_TO event is triggered when creating data package from remote contact
                if (event == FileObserver.MOVED_TO) {
                    Log.d(TAG, "REMOTE FOOD DETECTION REVIEW PACKAGE RECEIVED " + path);
                    PackageManager.extractSharedReviews(mapView.getContext(), prViewModel.database);
                }

                if (event == FileObserver.CREATE) {
                    Log.d(TAG, "GARBAGE COLLECT SHARE PACKAGE IN 30 SECONDS " + path);
                    executorService.schedule(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "CLEANING UP FOOD CLASSIFIER SHARE PACKAGE");
                            PackageManager.removePackage(PackageManager.getPathSharePluginPackage());
                        }
                    }, 30, TimeUnit.SECONDS);
                }
            }
        };
        fileObserver.startWatching();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case ADD_TO_LIST:
                Log.d(TAG, "Add review to plugin database and map");
                String review = intent.getStringExtra("review");
                String name = intent.getStringExtra("name");
                Bitmap picture = intent.getParcelableExtra("bitmap");
                String uid = UUID.randomUUID().toString();
                PictureReview pictureReview = new PictureReview(picture, Integer.parseInt(review),
                        name, getMapView().getContext(), uid);
                prViewModel.database.insert(pictureReview);
                CotMapComponent.getInternalDispatcher().dispatch(pictureReview.toCotEvent());
                pictureReview.addMarkerAttachment();
                // purposefully no break so we show main dropdown after recording review
            case SHOW_PLUGIN:
                showDropDown(paneView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                        HALF_HEIGHT, false, this);
                break;
        }
    }

    /** listen to removed Map Markers to delete associate food review **/
    @Override
    public void onMapEvent(MapEvent mapEvent) {
        if (mapEvent.getType().equals(MapEvent.ITEM_REMOVED)) {
            prViewModel.deleteByUid(mapEvent.getItem().getUID());
        }
    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownVisible(boolean v) { }

    @Override
    public void onDropDownSizeChanged(double width, double height) { }

    @Override
    public void onDropDownClose() { }

    @Override
    public void disposeImpl() {
        // clean up listeners, observers and services
        getMapView().getMapEventDispatcher().removeMapEventListener(this);
        fileObserver.stopWatching();
        prViewModel.allPR.removeObserver(dataObserver);
        executorService.shutdown();
    }

}




