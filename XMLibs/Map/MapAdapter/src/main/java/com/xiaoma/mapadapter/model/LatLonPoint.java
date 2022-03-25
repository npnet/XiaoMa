package com.xiaoma.mapadapter.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by minxiwen on 2017/12/12 0012.
 */

public class LatLonPoint implements Parcelable {
    private double latitude;
    private double longitude;

    public LatLonPoint() {

    }

    public LatLonPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected LatLonPoint(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<LatLonPoint> CREATOR = new Creator<LatLonPoint>() {
        @Override
        public LatLonPoint createFromParcel(Parcel in) {
            return new LatLonPoint(in);
        }

        @Override
        public LatLonPoint[] newArray(int size) {
            return new LatLonPoint[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
