package com.xiaoma.trip.parking.response;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/07/18
 *     desc   :
 * </pre>
 */
public class ParkInfoBean {

    /**
     * name : 国信南山温泉酒店停车场
     * address : 红岭中路1028号
     * longitude : 125.5286
     * latitude : 43.684769
     * distance : 5776
     * cityName : 深圳市
     * typeName : 宾馆
     */

    public String name;
    public String address;
    public double longitude;
    public double latitude;
    public int distance;
    public String cityName;
    public String typeName;
    public String photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "ParkInfoBean{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", distance=" + distance +
                ", cityName='" + cityName + '\'' +
                ", typeName='" + typeName + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
