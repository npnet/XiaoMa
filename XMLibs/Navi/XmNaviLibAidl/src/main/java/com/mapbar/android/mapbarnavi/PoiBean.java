package com.mapbar.android.mapbarnavi;

import android.os.Parcel;
import android.os.Parcelable;

public class PoiBean implements Parcelable {
	private String name;
	private String address;
	private double longitude;
	private double latitude;
	private int distance;
	private String cityName;
	private String typeName;

	public PoiBean() {
	}

	public PoiBean(Parcel source) {
		readFromParcel(source);
	}

	public static final Creator<PoiBean> CREATOR = new Creator<PoiBean>() {
		@Override
		public PoiBean createFromParcel(Parcel source) {
			return new PoiBean(source);
		}

		@Override
		public PoiBean[] newArray(int size) {
			return new PoiBean[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(address);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeInt(distance);
		dest.writeString(cityName);
		dest.writeString(typeName);
	}

	public void readFromParcel(Parcel source) {
		name = source.readString();
		address = source.readString();
		longitude = source.readDouble();
		latitude = source.readDouble();
		distance = source.readInt();
		cityName = source.readString();
		typeName = source.readString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
