package com.xiaoma.launcher.recommend.model;

import com.xiaoma.mqtt.model.DatasBean;

import java.util.List;

/**
 * @Auther: chenyong
 * @Date: 2018/11/8 14:51
 * @Description:
 */
public class Cinema extends DatasBean {
    private String cinemaId; //影院编号
    private String cinemaName; //影院名称
    private String cinemaCode; //影院编码
    private String spCode; //sp代码
    private String countyCode; //区县编码
    private String countyName; //区县名称
    private String address; //影院地址
    private String longitude; //经度
    private String latitude; //纬度
    private String imgUrl; //影院图片URL
    private String mobile; //联系电话
    private String facilitys; //设施配备
    private String isWanda; //是否万达影院  0-否  1-是
    private String terminal; //取票终端  0,1,2院线通终端  3,7,8影院终端机 9 影院前台
    private String cinemaLinkId; //影院连接编码
    private List<Film> films; //上映影片信息
    private String distance;
    private int buyTimeLimit; //最迟购票时间
    private int newBuyTimeLimit;  //院线通设置的最迟购票时间
    private String minPrice; //影院起步价

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFacilitys() {
        return facilitys;
    }

    public void setFacilitys(String facilitys) {
        this.facilitys = facilitys;
    }

    public String getIsWanda() {
        return isWanda;
    }

    public void setIsWanda(String isWanda) {
        this.isWanda = isWanda;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public List<Film> getFilms() {
        return films;
    }

    public void setFilms(List<Film> films) {
        this.films = films;
    }

    public String getCinemaLinkId() {
        return cinemaLinkId;
    }

    public void setCinemaLinkId(String cinemaLinkId) {
        this.cinemaLinkId = cinemaLinkId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getBuyTimeLimit() {
        return buyTimeLimit;
    }

    public void setBuyTimeLimit(int buyTimeLimit) {
        this.buyTimeLimit = buyTimeLimit;
    }

    public int getNewBuyTimeLimit() {
        return newBuyTimeLimit;
    }

    public void setNewBuyTimeLimit(int newBuyTimeLimit) {
        this.newBuyTimeLimit = newBuyTimeLimit;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }
}
