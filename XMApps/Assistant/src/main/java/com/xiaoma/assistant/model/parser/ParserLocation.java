package com.xiaoma.assistant.model.parser;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/30
 * Desc:
 */
public class ParserLocation {
    private String cityAddr;
    private String city;
    private String type;
    private String poi;
    private String area;
    private String areaAddr;
    private String province;
    private String provinceAddr;
    private String location;
    //车载版原始poi
    private String ori_loc;
    //街道，车载版
    private String street;
    private boolean IS_CURRENT_POI;

    public String getCityAddr() {
        return cityAddr == null ? "" : cityAddr;
    }

    public void setCityAddr(String cityAddr) {
        this.cityAddr = cityAddr;
    }

    public String getCity() {
        return city == null ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoi() {
        return poi == null ? "" : poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaAddr() {
        return areaAddr;
    }

    public void setAreaAddr(String areaAddr) {
        this.areaAddr = areaAddr;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceAddr() {
        return provinceAddr;
    }

    public void setProvinceAddr(String provinceAddr) {
        this.provinceAddr = provinceAddr;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean IS_CURRENT_POI() {
        return IS_CURRENT_POI;
    }

    public void setIS_CURRENT_POI(boolean IS_CURRENT_POI) {
        this.IS_CURRENT_POI = IS_CURRENT_POI;
    }

    public String getOri_loc() {
        return ori_loc;
    }

    public void setOri_loc(String ori_loc) {
        this.ori_loc = ori_loc;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
