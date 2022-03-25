package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

public class CinemasShowBean implements Serializable {
    private String cinemaId;
    private String cinemaName;
    private String filmId;
    private String filmName;
    private String address;
    private String cinemaLinkId;
    private String mobile;
    private String lat;
    private String lon;
    private String iconUrl;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCinemaLinkId() {
        return cinemaLinkId;
    }

    public void setCinemaLinkId(String cinemaLinkId) {
        this.cinemaLinkId = cinemaLinkId;
    }

    private List<ShowBean> shows;

    public List<ShowBean> getShows() {
        return shows;
    }

    public void setShows(List<ShowBean> shows) {
        this.shows = shows;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    private String duration;
}
