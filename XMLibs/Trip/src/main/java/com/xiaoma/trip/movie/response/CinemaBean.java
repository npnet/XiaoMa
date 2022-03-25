package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

/**
 * 影院bean
 * Created by zhushi.
 * Date: 2018/12/6
 */
public class CinemaBean implements Serializable{
    /**
     * cinemaId : 10194
     * cinemaName : 中影国际影城（欢乐海岸店）
     * cinemaCode : 20000606
     * spCode : sp015
     * countyCode : 440305
     * countyName : 南山区
     * address : 南山区 白石路东8号欢乐海岸曲水湾2栋A区
     * longitude : 113.992531
     * latitude : 22.52189
     * imgUrl : http://image.189mv.cn/images/cinema/cinema_39062.jpg
     * mobile :
     * facilitys : []
     * terminal : 1
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
    private String facilitys;
    private String terminal;
    private List<FilmBean> films;

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

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public List<FilmBean> getFilms() {
        return films;
    }

    public void setFilms(List<FilmBean> films) {
        this.films = films;
    }
}
