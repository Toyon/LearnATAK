/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.democamera;


import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.democamera.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;

import com.atakmap.android.preference.AtakPreferenceFragment;
import com.atakmap.coremap.log.Log;

import java.util.Locale;

/** Broadcast receiver and accompanying logic for UI functionality of main plugin view. */
public class DemoCameraDropDownReceiver extends DropDownReceiver
        implements OnStateListener, SurfaceHolder.Callback {

  public static final String TAG = DemoCameraDropDownReceiver.class.getSimpleName();
  public static final String SHOW_PLUGIN = "com.atakmap.android.democamera.SHOW_PLUGIN";

  private final View paneView;
  private final NcnnYolov7 ncnnyolov7 = new NcnnYolov7();
  AspectRatioSurfaceView mVideoView;

  private final Context pluginCtx;

  /** Constructor: called when plugin is loaded into ATAK */
  public DemoCameraDropDownReceiver(final MapView mapView, final Context context) {
    super(mapView);
    pluginCtx = context;
    paneView = PluginLayoutInflater.inflate(context, R.layout.main_layout, null);
    mVideoView = paneView.findViewById(R.id.surface_video);
    final Button buttonQuit = paneView.findViewById(R.id.buttonQuit);
    buttonQuit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) { closeDropDown(); }
    });
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    final String action = intent.getAction();
    if (action == null)
      return;
    if (action.equals(SHOW_PLUGIN))
      showDropDown(paneView,
        FULL_WIDTH, FULL_HEIGHT, // landscape view dimensions
        FULL_WIDTH, FULL_HEIGHT, // portrait view dimensions
        false,
        this);
  }

  @Override
  public void onDropDownVisible(boolean v) {
    Log.i(TAG, "DropDown visible: " + v);
    if (v)
    {
      ncnnyolov7.createCamera();
      ncnnyolov7.setAssetManager(pluginCtx.getAssets());
      int orientation = AtakPreferenceFragment.getOrientation(getMapView().getContext());
      ncnnyolov7.setPrefOrientation(orientation);

      // when visible again re-create the surface
      mVideoView.getHolder().setFormat(PixelFormat.RGBA_8888);
      mVideoView.getHolder().addCallback(this);
    }
  }

  @Override
  public void onDropDownSizeChanged(double width, double height) {
    Log.i(TAG, "ddr size changed width:" + width + " height: " + height);
  }

  @Override
  public void onDropDownSelectionRemoved() {
    Log.i(TAG, "DropDown Selection removed");
  }

  @Override
  public void onDropDownClose() {
    Log.i(TAG, "DropDown closed");
  }

  @Override
  public void disposeImpl() {
    Log.i(TAG, "Dispose Demo Camera Plugin");
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    Log.i(TAG, String.format(Locale.US,  "Surface changed to format %s width %d height %d",
            format, width, height));
    ncnnyolov7.setOutputWindow(holder.getSurface());

    Log.i(TAG, "openCamera");
    ncnnyolov7.openCamera();
  }

  public void surfaceCreated(SurfaceHolder holder) {
    Log.i(TAG, "Surface created");
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    // release the surface view as it will be re-created when the plugin becomes visible
    Log.i(TAG, "Surface destroyed");
    ncnnyolov7.closeCamera();
    ncnnyolov7.destroyCamera();
    ncnnyolov7.releaseOutputWindow(holder.getSurface());
    mVideoView.getHolder().removeCallback(this);
    mVideoView.getHolder().getSurface().release();
  }

}
