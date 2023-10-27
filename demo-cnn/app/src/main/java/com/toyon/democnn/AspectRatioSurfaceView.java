package com.toyon.democnn;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;


/** A SurfaceView which obeys aspect ratio determined by media size */
public class AspectRatioSurfaceView extends SurfaceView {
    private static final String TAG = AspectRatioSurfaceView.class.getSimpleName();
    private int media_width = 640;
    private int media_height = 480;

    public AspectRatioSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AspectRatioSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioSurfaceView (Context context) {
        super(context);
    }

    /**
     * If we wanted to adjust the image size we would use this function to dynamically update the
     * media size. Refer to `ndkcamera.cpp` AImageReeader_new() to see why we have static media
     * width of 640 and height of 480.
     */
    public void setMediaSize(final int width, final int height) {
        if (media_width == width && media_height == height)
            return;
        media_width = width;
        media_height = height;
        requestLayout();
    }

    // Called by the layout manager to find out our size and give us some rules.
    // We will try to maximize our size, and preserve the media's aspect ratio if
    // we are given the freedom to do so.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0, height = 0;
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, String.format("media size: %d x %d , window size: %d x %d",
                media_width, media_height, maxWidth, maxHeight));

        float aspectRatio = (float) media_width / (float) media_height;

        // assume width of media is always greater than height
        if (maxWidth < maxHeight) {
            // portrait; limiting dimension is width
            Log.d(TAG, "ORIENTATION Portrait; width is smallest");
            width = maxWidth;
            height = (int) (width / (1/ aspectRatio));
        } else {
            Log.d(TAG, "ORIENTATION Landscape; height is smallest");
            height = maxHeight;
            width = (int) (aspectRatio * height);
        }
        Log.d(TAG, "set size: " + width + " x " + height);

        setMeasuredDimension(width, height);
    }



}
