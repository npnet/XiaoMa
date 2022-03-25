package com.xiaoma.launcher.mark.cluster;

import android.graphics.Bitmap;

import com.amap.api.maps.model.BitmapDescriptor;
import com.xiaoma.mapadapter.model.LatLng;

import java.util.ArrayList;

public class MarkerOptionMark {
    private LatLng latLng;
    private Bitmap bitmap;
    private float anchor1;
    private float anchor2;
    private String title;
    private ArrayList<BitmapDescriptor> bitmapDescriptors = new ArrayList();
    private boolean isRotatingMode = false;
    public MarkerOptionMark position(LatLng latLng) {
        this.latLng = latLng;
        return this;
    }

    public MarkerOptionMark anchor(float anchor1, float anchor2) {
        this.anchor1 = anchor1;
        this.anchor2 = anchor2;
        return this;
    }

    public MarkerOptionMark icon(BitmapDescriptor var1) {
        try {
            this.a();
            this.bitmapDescriptors.clear();
            this.bitmapDescriptors.add(var1);
            this.isRotatingMode = false;
        } catch (Throwable var3) {
            var3.printStackTrace();
        }

        return this;
    }

    private void a() {
        if (this.bitmapDescriptors == null) {
            try {
                this.bitmapDescriptors = new ArrayList();
            } catch (Throwable var2) {
                var2.printStackTrace();
            }
        }

    }

    public MarkerOptionMark title(String title) {
        this.title = title;
        return this;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getAnchor1() {
        return anchor1;
    }

    public void setAnchor1(float anchor1) {
        this.anchor1 = anchor1;
    }

    public float getAnchor2() {
        return anchor2;
    }

    public void setAnchor2(float anchor2) {
        this.anchor2 = anchor2;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
