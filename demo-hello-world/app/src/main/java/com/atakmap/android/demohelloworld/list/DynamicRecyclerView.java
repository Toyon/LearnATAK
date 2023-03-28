/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.demohelloworld.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.StyleableRes;
import androidx.recyclerview.widget.GridLayoutManager;


/**
 * Dynamic Column Span Grid Layout RecyclerView for use within ATAK
 */
public class DynamicRecyclerView extends androidx.recyclerview.widget.RecyclerView {

    private static final String TAG = DynamicRecyclerView.class.getSimpleName();
    private GridLayoutManager manager;
    private int columnWidth = -1;

    public DynamicRecyclerView(Context context) {
        super(context);
        this.init(context, null);
    }

    public DynamicRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public DynamicRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y) {
        // Not supported and causes a crash when called
        // Samsung likes to call this method directly outside of our control
        // So we need to override it with a no-op
    }

    /**
     * Calculate column width based on width of each item to be rendered
     * @param context Context of RecyclerView
     * @param attributeSet Attribute Set of RecyclerView
     */
    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            @StyleableRes int[] attributeArray = new int[]{ android.R.attr.columnWidth };
            TypedArray array = context.obtainStyledAttributes(attributeSet, attributeArray);
            this.columnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }
        this.manager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(this.manager);
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0) {
            // ensure calculated span count is at least 1
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
            manager.setSpanCount(spanCount);
        }
    }
}
