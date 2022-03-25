package com.xiaoma.launcher.mark.model;

import java.io.Serializable;

public class TripAlbumBean implements Serializable{
    String year;
    String month;

    public TripAlbumBean(String year, String month) {
        this.year = year;
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
