
package com.atakmap.android.democnn;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.FileObserver;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.democnn.util.AspectRatioSurfaceView;
import com.atakmap.android.democnn.util.Constants;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.democnn.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;

import com.atakmap.coremap.filesystem.FileSystemUtils;
import com.atakmap.coremap.log.Log;

import java.io.File;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/** Broadcast receiver and accompanying logic for UI functionality of camera object detection. */
public class CameraYoloDropDown extends DropDownReceiver
        implements OnStateListener, SurfaceHolder.Callback {

  public static final String TAG = CameraYoloDropDown.class.getSimpleName();
  public static final String SHOW_CAMERA_PANE = "com.atakmap.android.democnn.SHOW_CAMERA_PANE";

  private final View paneView;
  private final Context pluginCtx;
  private final NcnnYolov7 ncnnyolov7 = new NcnnYolov7();
  AspectRatioSurfaceView mVideoView;

  FileObserver cameraFileObserver;

  /** Constructor: called the plugin is loaded into ATAK */
  public CameraYoloDropDown(final MapView mapView, final Context context) {
    super(mapView);
    pluginCtx = context;
    paneView = PluginLayoutInflater.inflate(context, R.layout.pane_camera, null);
    Log.i(TAG, "creating ddr");

    // relative path to save the file at from the root of ATAK managed files
    File dir = FileSystemUtils.getItem(Constants.TMP_IMG_DIR);
    if (!dir.exists()) {
      if (dir.mkdir())
        Log.d(TAG, "CREATED TEMP IMAGE DIRECTORY");
      else
        Log.d(TAG, "IMAGE DIRECTORY NOT CREATED. Might already exist");
    }
    ncnnyolov7.setImageDirectory(dir.getAbsolutePath());

    final Button buttonQuit = paneView.findViewById(R.id.buttonQuit);
    buttonQuit.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) { closeDropDown(); }
    });

    final Button buttonCapture = paneView.findViewById(R.id.buttonCapture);
    buttonCapture.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) { ncnnyolov7.capture(); }
    });

    cameraFileObserver = new FileObserver(dir.getAbsolutePath()) {

      private Timer notifyTimer = new Timer();

      @Override
      public void onEvent(int i, @Nullable String s) {
        if (i != FileObserver.CREATE) return;
        Log.d(TAG, "UPDATE LIST OF IDENTIFIED OBJECTS TO SELECT FROM");
        notifyTimer.cancel();
        notifyTimer = new Timer();
        notifyTimer.schedule(new TimerTask() {
          @Override
          public void run() {
            Intent reportIntent = new Intent();
            reportIntent.setAction(ReportDropDown.SHOW_REPORT_PANE);
            AtakBroadcast.getInstance().sendBroadcast(reportIntent);
          }
        }, 500L);
      }
    };
    cameraFileObserver.startWatching();
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    final String action = intent.getAction();
    if (action == null)
      return;
    if (action.equals(SHOW_CAMERA_PANE))
      showDropDown(paneView,
              FULL_WIDTH, FULL_HEIGHT, // Landscape Dimensions
              FULL_WIDTH, FULL_HEIGHT, // Portrait Dimensions
              false, this);
  }

  @Override
  public void onDropDownSelectionRemoved() {
    Log.i(TAG, "ddr removed");
  }

  @Override
  public void onDropDownVisible(boolean v) {
    Log.i(TAG, "ddr visible: " + v);

    if (v) {
      ncnnyolov7.createCamera();
      ncnnyolov7.loadModel(pluginCtx.getAssets());
      // when visible again re-create the surface
      mVideoView = paneView.findViewById(R.id.surface_video);
      mVideoView.getHolder().setFormat(PixelFormat.RGBA_8888);
      mVideoView.getHolder().addCallback(this);
    } else
      closeDropDown();
  }


  @Override
  public void onDropDownSizeChanged(double width, double height) {
    Log.i(TAG, "ddr size changed width:" + width + " height: " + height);
  }

  @Override
  public void onDropDownClose() {
    Log.i(TAG, "ddr closed");
  }

  @Override
  public void disposeImpl() {
    Log.i(TAG, "dispose impl");
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
