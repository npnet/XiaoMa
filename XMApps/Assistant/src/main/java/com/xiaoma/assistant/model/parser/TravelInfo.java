package com.xiaoma.assistant.model.parser;


import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */
public class TravelInfo implements Serializable {


    /**
     * name : 深圳野生动物园
     * address : 广东省深圳市南山区西丽湖路4065号
     * distance : 6.51
     * location : {"lng":113.978616,"lat":22.60204}
     * location_type : BD09LL
     * detail_info : {"image_url":"http://hiphotos.baidu.com/lbsugc/pic/item/d058ccbf6c81800ae4d21b3cb63533fa828b47c9.jpg","rating_value":3.8,"summary_info":"3.8分 6.51km 广东省深圳市南山区西丽湖路4065号"}
     */

    private String name;
    private String address;
    private double distance;
    private LocationBean location;
    private String location_type;
    private DetailInfoBean detail_info;

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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public String getLocation_type() {
        return location_type;
    }

    public void setLocation_type(String location_type) {
        this.location_type = location_type;
    }

    public DetailInfoBean getDetail_info() {
        return detail_info;
    }

    public void setDetail_info(DetailInfoBean detail_info) {
        this.detail_info = detail_info;
    }

    public static class LocationBean {
        /**
         * lng : 113.978616
         * lat : 22.60204
         */

        private double lng;
        private double lat;

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }
    }

    public static class DetailInfoBean {
        /**
         * image_url : http://hiphotos.baidu.com/lbsugc/pic/item/d058ccbf6c81800ae4d21b3cb63533fa828b47c9.jpg
         * rating_value : 3.8
         * summary_info : 3.8分 6.51km 广东省深圳市南山区西丽湖路4065号
         */

        private String image_url;
        private double rating_value;
        private String summary_info;

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public double getRating_value() {
            return rating_value;
        }

        public void setRating_value(double rating_value) {
            this.rating_value = rating_value;
        }

        public String getSummary_info() {
            return summary_info;
        }

        public void setSummary_info(String summary_info) {
            this.summary_info = summary_info;
        }
    }
}
