package com.xiaoma.assistant.model.parser;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/8 14:34
 * Desc:
 */
public class HotelBean implements Serializable {

    /**
     * images : [{"imageUrl":"http://pic.cnbooking.net:10541/CT/77/c1521f42f93157bd.jpg","imageId":"748557","imageName":"酒店外景"}]
     * services : [{"serviceId":"19","serviceName":"中央空调","groupId":"7"},{"serviceId":"24","serviceName":"吹风机","groupId":"7"},{"serviceId":"48","serviceName":"叫醒服务","groupId":"8"},{"serviceId":"98","serviceName":"24小时热水","groupId":"7"},{"serviceId":"9","serviceName":"免费停车场","groupId":"5"},{"serviceId":"27","serviceName":"电视","groupId":"7"},{"serviceId":"94","serviceName":"行李存放服务","groupId":"8"},{"serviceId":"100","serviceName":"公共区WIFI","groupId":"4"}]
     * distance : 5
     * isCollect : 0
     * affiliatedGroupId :
     * score : 4.00
     * telephone : 0755-86111199
     * startPrice : 124.29
     * hotelName : 深圳皇庭商务宾馆
     * countryName : 中国大陆
     * provinceName : 广东
     * postCode : 518000
     * email :
     * startBusinessDate : 2015-01-01
     * repairdate : 2015-01-01
     * star : 012011
     * starName : 准二星级
     * lon : 113.987025
     * lat : 22.567261
     * intro : 客房干净、卫生，具有基础性设施，住宿环境舒适。
     * guide :
     * hotelId : 122177
     * countryId : 0001
     * provinceId : 2000
     * cityId : 2003
     * cityName : 深圳
     * currency : CNY
     * address : 深圳深圳市南山区龙珠大道与龙珠六路交汇处皇庭香格里花园三楼,
     * minPrice : 1000
     * reserve2 : 0755-88911111
     * recommendedLevel : 006001
     * allowWebSale : 1
     */

    private int distance;
    private int isCollect;
    private String affiliatedGroupId;
    private String score;
    private String telephone;
    private String startPrice;
    private String hotelName;
    private String countryName;
    private String provinceName;
    private String postCode;
    private String email;
    private String startBusinessDate;
    private String repairdate;
    private String star;
    private String starName;
    private String lon;
    private String lat;
    private String intro;
    private String guide;
    private String hotelId;
    private String countryId;
    private String provinceId;
    private String cityId;
    private String cityName;
    private String currency;
    private String address;
    private List<ImagesBean> images;
    private List<ServicesBean> services;
    private List<LandmarkBean> landmark;
    private String minPrice;
    private String reserve2;
    private String recommendedLevel;
    private String allowWebSale;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public String getAffiliatedGroupId() {
        return affiliatedGroupId;
    }

    public void setAffiliatedGroupId(String affiliatedGroupId) {
        this.affiliatedGroupId = affiliatedGroupId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(String startPrice) {
        this.startPrice = startPrice;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStartBusinessDate() {
        return startBusinessDate;
    }

    public void setStartBusinessDate(String startBusinessDate) {
        this.startBusinessDate = startBusinessDate;
    }

    public String getRepairdate() {
        return repairdate;
    }

    public void setRepairdate(String repairdate) {
        this.repairdate = repairdate;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getStarName() {
        return starName;
    }

    public void setStarName(String starName) {
        this.starName = starName;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public List<ServicesBean> getServices() {
        return services;
    }

    public void setServices(List<ServicesBean> services) {
        this.services = services;
    }

    public List<LandmarkBean> getLandmark() {
        return landmark;
    }

    public void setLandmark(List<LandmarkBean> landmark) {
        this.landmark = landmark;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }

    public String getRecommendedLevel() {
        return recommendedLevel;
    }

    public void setRecommendedLevel(String recommendedLevel) {
        this.recommendedLevel = recommendedLevel;
    }

    public String getAllowWebSale() {
        return allowWebSale;
    }

    public void setAllowWebSale(String allowWebSale) {
        this.allowWebSale = allowWebSale;
    }

    public static class ImagesBean {
        /**
         * imageUrl : http://pic.cnbooking.net:10541/CT/77/c1521f42f93157bd.jpg
         * imageId : 748557
         * imageName : 酒店外景
         */

        private String imageUrl;
        private String imageId;
        private String imageName;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }
    }

    public static class ServicesBean {
        /**
         * serviceId : 19
         * serviceName : 中央空调
         * groupId : 7
         */

        private String serviceId;
        private String serviceName;
        private String groupId;

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }

    public static class LandmarkBean {
        /**
         * landid : A119201
         * landName : 南山区
         * distance :
         */

        private String landid;
        private String landName;
        @SerializedName("distance")
        private String distanceX;

        public String getLandid() {
            return landid;
        }

        public void setLandid(String landid) {
            this.landid = landid;
        }

        public String getLandName() {
            return landName;
        }

        public void setLandName(String landName) {
            this.landName = landName;
        }

        public String getDistanceX() {
            return distanceX;
        }

        public void setDistanceX(String distanceX) {
            this.distanceX = distanceX;
        }
    }
}
