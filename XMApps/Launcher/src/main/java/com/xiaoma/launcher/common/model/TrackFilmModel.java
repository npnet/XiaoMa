package com.xiaoma.launcher.common.model;

/**
 * 简介:
 *
 * @author lingyan
 */
public class TrackFilmModel {
    String seats;
    String movie;
    String price;
    String time;

    public TrackFilmModel(String seats, String movie, String price, String time) {
        this.seats = seats;
        this.movie = movie;
        this.price = price;
        this.time = time;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
