package com.xiaoma.trip.movie.response;

import java.io.Serializable;

/**
 * 影片播出信息
 * Created by zhushi.
 * Date: 2018/12/6
 */
public class ShowBean implements Serializable {
    /**
     * showCode : 597065216
     * showDate : 2018-12-06
     * showTime : 1315
     * hallCode :
     * hallName : 6号厅(激光)
     * price : 41.9
     * standPrice : 71.8
     * filmType : 1
     * filmAttr : 3D
     * language : 英语
     * filmPk :
     */

    private String showCode;
    private String showDate;
    private String showTime;
    private String hallCode;
    private String hallName;
    private String price;
    private String standPrice;
    private String filmType;
    private String filmAttr;
    private String language;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    private String filmPk;
    private String imgUrl;

    public String getShowCode() {
        return showCode;
    }

    public void setShowCode(String showCode) {
        this.showCode = showCode;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getHallCode() {
        return hallCode;
    }

    public void setHallCode(String hallCode) {
        this.hallCode = hallCode;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStandPrice() {
        return standPrice;
    }

    public void setStandPrice(String standPrice) {
        this.standPrice = standPrice;
    }

    public String getFilmType() {
        return filmType;
    }

    public void setFilmType(String filmType) {
        this.filmType = filmType;
    }

    public String getFilmAttr() {
        return filmAttr;
    }

    public void setFilmAttr(String filmAttr) {
        this.filmAttr = filmAttr;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFilmPk() {
        return filmPk;
    }

    public void setFilmPk(String filmPk) {
        this.filmPk = filmPk;
    }
}