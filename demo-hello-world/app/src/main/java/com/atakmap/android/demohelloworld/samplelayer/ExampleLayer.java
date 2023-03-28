/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.demohelloworld.samplelayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.atakmap.android.maps.MetaShape;
import com.atakmap.android.menu.PluginMenuParser;
import com.atakmap.coremap.log.Log;
import com.atakmap.coremap.maps.coords.GeoBounds;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.coords.GeoPointMetaData;
import com.atakmap.coremap.maps.coords.MutableGeoBounds;
import com.atakmap.map.layer.AbstractLayer;

import java.util.UUID;
import java.util.concurrent.Executors;

/** Example image layer using an orthorectified image from USGS  */
public class ExampleLayer extends AbstractLayer {

    public static final String TAG = Executors.class.getSimpleName();

    final int[] layerARGB;
    final int layerWidth;
    final int layerHeight;

    final GeoPoint upperLeft;
    final GeoPoint upperRight;
    final GeoPoint lowerRight;
    final GeoPoint lowerLeft;

    private final MetaShape metaShape;

    public ExampleLayer(Context plugin, final String name, final String uri) {
        super(name);

        this.upperLeft = GeoPoint.createMutable();
        this.upperRight = GeoPoint.createMutable();
        this.lowerRight = GeoPoint.createMutable();
        this.lowerLeft = GeoPoint.createMutable();

        final Bitmap bitmap = BitmapFactory.decodeFile(uri);
        upperLeft.set(34.424180961, -119.874962718);
        upperRight.set(34.424180961, -119.836972838);
        lowerRight.set(34.404365022, -119.836972838);
        lowerLeft.set(34.404365022, -119.874962718);

        layerWidth = bitmap.getWidth();
        layerHeight = bitmap.getHeight();
        Log.d(TAG, "decode file: " + uri + " " + layerWidth + " " + layerHeight);
        layerARGB = new int[layerHeight * layerWidth];

        bitmap.getPixels(layerARGB, 0, layerWidth, 0, 0, layerWidth,
                layerHeight);

        metaShape = new MetaShape(UUID.randomUUID().toString()) {
            @Override
            public GeoPointMetaData[] getMetaDataPoints() {
                return GeoPointMetaData.wrap(ExampleLayer.this.getPoints());
            }

            @Override
            public GeoPoint[] getPoints() {
                return ExampleLayer.this.getPoints();
            }

            @Override
            public GeoBounds getBounds(MutableGeoBounds bounds) {
                return ExampleLayer.this.getBounds();
            }
        };
        metaShape.setMetaString("callsign", TAG);
        metaShape.setMetaString("shapeName", TAG);
        metaShape.setType("hello_world_layer");
        metaShape.setMetaString("menu", PluginMenuParser.getMenu(plugin, "menus/layer_menu.xml"));
        bitmap.recycle();
    }

    public GeoBounds getBounds() {
        return GeoBounds.createFromPoints(getPoints());
    }

    public GeoPoint[] getPoints() {
        return new GeoPoint[] { upperLeft, upperRight, lowerRight, lowerLeft };
    }

    public MetaShape getMetaShape() {
        return metaShape;
    }
}
