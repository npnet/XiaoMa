package com.xiaoma.mapadapter.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author taojin
 * @date 2019/1/9
 */
public class SearchAddressInfo implements Parcelable {

    public String addressName;
    public LatLonPoint latLonPoint;
    public boolean isChoose;
    public String title;
    public String provinceCity;
    public String city;

    public SearchAddressInfo(String city,String title, String addressName, String provinceCity, boolean isChoose, LatLonPoint latLonPoint) {
        this.addressName = addressName;
        this.latLonPoint = latLonPoint;
        this.isChoose = isChoose;
        this.title = title;
        this.provinceCity = provinceCity;
        this.city = city;
    }

    protected SearchAddressInfo(Parcel in) {
        addressName = in.readString();
        latLonPoint = in.readParcelable(LatLonPoint.class.getClassLoader());
        isChoose = in.readByte() != 0;
        title = in.readString();
        provinceCity = in.readString();
        city = in.readString();
    }

    public static final Creator<SearchAddressInfo> CREATOR = new Creator<SearchAddressInfo>() {
        @Override
        public SearchAddressInfo createFromParcel(Parcel in) {
            return new SearchAddressInfo(in);
        }

        @Override
        public SearchAddressInfo[] newArray(int size) {
            return new SearchAddressInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressName);
        dest.writeParcelable(latLonPoint, flags);
        dest.writeByte((byte) (isChoose ? 1 : 0));
        dest.writeString(title);
        dest.writeString(provinceCity);
        dest.writeString(city);
    }

    @Override
    public String toString() {
        return "SearchAddressInfo{" +
                "addressName='" + addressName + '\'' +
                ", latLonPoint=" + latLonPoint +
                ", isChoose=" + isChoose +
                ", title='" + title + '\'' +
                ", provinceCity='" + provinceCity + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
