package com.xiaoma.launcher.mark.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;
import java.util.Objects;

public class MarkPhotoBean implements Serializable {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    private String markId;
    private boolean saveType;


    private String year;
    private String month;
    private String day;
    private String location;

    private String photoTime;
    private String photoAddress;
    private String photoPath;
    private String weather;
    private int facialType;
    private double latitude;
    private double longitude;

    public boolean isSaveType() {
        return saveType;
    }

    public void setSaveType(boolean saveType) {
        this.saveType = saveType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getYear() {
        return year;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarkId() {
        return markId;
    }

    public void setMarkId(String markId) {
        this.markId = markId;
    }

    public String getPhotoTime() {
        return photoTime;
    }

    public void setPhotoTime(String photoTime) {
        this.photoTime = photoTime;
    }

    public String getPhotoAddress() {
        return photoAddress;
    }

    public void setPhotoAddress(String photoAddress) {
        this.photoAddress = photoAddress;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getFacialType() {
        return facialType;
    }

    public void setFacialType(int facialType) {
        this.facialType = facialType;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkPhotoBean that = (MarkPhotoBean) o;
        return id == that.id &&
                facialType == that.facialType &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(markId, that.markId) &&
                Objects.equals(year, that.year) &&
                Objects.equals(month, that.month) &&
                Objects.equals(day, that.day) &&
                Objects.equals(location, that.location) &&
                Objects.equals(photoTime, that.photoTime) &&
                Objects.equals(photoAddress, that.photoAddress) &&
                Objects.equals(photoPath, that.photoPath) &&
                Objects.equals(weather, that.weather);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, markId, year, month, day, location, photoTime, photoAddress, photoPath, weather, facialType, latitude, longitude);
    }
}
