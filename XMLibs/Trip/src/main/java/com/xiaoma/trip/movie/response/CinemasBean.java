package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

public class CinemasBean implements Serializable {
    /**
     * cinemaId : 34294
     * cinemaName : 大地影院深圳宝安时代城
     * cinemaCode : 9606
     * spCode : sp1007
     * countyCode : 440306
     * countyName : 宝安区
     * address : 宝安区西乡街道宝源路时代城6楼
     * longitude : 113.8683757361
     * latitude : 22.5666641589
     * imgUrl :
     * mobile:"4008503366"
     * isWanda : 0
     * cinemaLinkId :
     * films : [{"filmId":"5734","filmName":"毒液：致命守护者","filmAttrs":"IMAX3D/中国巨幕3D/3D","duration":"112min","shows":[{"showCode":"597065216","showDate":"2018-12-06","showTime":"1315","hallCode":"","hallName":"6号厅(激光)","price":"41.9","standPrice":"71.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065219","showDate":"2018-12-06","showTime":"1520","hallCode":"","hallName":"6号厅(激光)","price":"41.9","standPrice":"71.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065218","showDate":"2018-12-06","showTime":"1725","hallCode":"","hallName":"6号厅(激光)","price":"41.9","standPrice":"71.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065266","showDate":"2018-12-06","showTime":"1825","hallCode":"","hallName":"3号厅(激光)","price":"41.9","standPrice":"71.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065221","showDate":"2018-12-06","showTime":"1930","hallCode":"","hallName":"6号厅(激光)","price":"48.9","standPrice":"85.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065220","showDate":"2018-12-06","showTime":"2135","hallCode":"","hallName":"6号厅(激光)","price":"48.9","standPrice":"85.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""},{"showCode":"597065268","showDate":"2018-12-06","showTime":"2235","hallCode":"","hallName":"3号厅(激光)","price":"42.9","standPrice":"73.8","filmType":"1","filmAttr":"3D","language":"英语","filmPk":""}]}]
     * distance: "2625"
     * buyTimeLimit: 0,
     * newBuyTimeLimit: 25
     * minPrice: "42.4",
     * isCollect: 0
     */

    private String cinemaId;
    private String cinemaName;
    private String cinemaCode;
    private String spCode;
    private String countyCode;
    private String countyName;
    private String address;
    private String longitude;
    private String latitude;
    private String imgUrl;
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    private String isWanda;

    private String cinemaLinkId;
    private String distance;
    private String minPrice;
    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    private List<FilmSessionBean> films;

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

    public String getCinemaCode() {
        return cinemaCode;
    }

    public void setCinemaCode(String cinemaCode) {
        this.cinemaCode = cinemaCode;
    }

    public String getSpCode() {
        return spCode;
    }

    public void setSpCode(String spCode) {
        this.spCode = spCode;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIsWanda() {
        return isWanda;
    }

    public void setIsWanda(String isWanda) {
        this.isWanda = isWanda;
    }

    public String getCinemaLinkId() {
        return cinemaLinkId;
    }

    public void setCinemaLinkId(String cinemaLinkId) {
        this.cinemaLinkId = cinemaLinkId;
    }

    public List<FilmSessionBean> getFilms() {
        return films;
    }

    public void setFilms(List<FilmSessionBean> films) {
        this.films = films;
    }

}