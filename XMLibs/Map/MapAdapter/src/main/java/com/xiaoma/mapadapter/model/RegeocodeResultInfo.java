package com.xiaoma.mapadapter.model;

import java.util.List;

/**
 * 反地理编码结果model
 * Created by minxiwen on 2017/12/12 0012.
 */

public class RegeocodeResultInfo {
    private String district;
    private String township;
    private String street;
    private String number;
    private String province;
    private String city;
    private String formatAddress;

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    private String building;
    private List<PoiInfo> poiInfoList;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFormatAddress() {
        return formatAddress;
    }

    public void setFormatAddress(String formatAddress) {
        this.formatAddress = formatAddress;
    }

    public List<PoiInfo> getPoiInfoList() {
        return poiInfoList;
    }

    public void setPoiInfoList(List<PoiInfo> poiInfoList) {
        this.poiInfoList = poiInfoList;
    }
}
