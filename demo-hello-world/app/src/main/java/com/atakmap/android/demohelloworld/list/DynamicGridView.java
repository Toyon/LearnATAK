/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.demohelloworld.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.GridView;

import androidx.annotation.StyleableRes;
import androidx.recyclerview.widget.GridLayoutManager;

public class DynamicGridView extends GridView {
    private GridLayoutManager manager;
    private int columnWidth = -1;

    public DynamicGridView(Context context) {
        super(context);
        this.init(context, null);
    }

    public DynamicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public DynamicGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
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
            setNumColumns(spanCount);
        }
    }


}
