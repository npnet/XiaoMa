package com.xiaoma.launcher.recommend.model;

import com.xiaoma.mqtt.model.DatasBean;

import java.util.List;

/**
 * @Auther: huojie
 * @Date: 2019/1/3 0003 13:11
 * @Description:酒店
 */
public class Hotel extends DatasBean {
    /**
     * images : [{"imageId":"617533","imageName":"酒店外景","imageUrl":"http://pic.cnbooking.net:10541/CT/88/4dad20ecb6d94d86.jpg"},{"imageId":"617534","imageName":"酒店外景","imageUrl":"http://pic.cnbooking.net:10541/CT/88/5df45115b1183819.jpg"}]
     * services : [{"serviceId":"27","serviceName":"电视","groupId":"7"},{"serviceId":"99","serviceName":"客房WIFI","groupId":"4"}]
     * landmark : [{"landid":"A120588","landName":"宝安区","distance":""},{"landid":"","landName":"市中心","distance":"20.86"}]
     * distance : 9
     * address : 深圳宝安47区自由路怡景南一巷一号,近裕安一路
     * currency : CNY
     * lat : 22.57308468889
     * lon : 113.89777669742
     * hotelId : 120588
     * cityName : 深圳
     * score : 3.70
     * affiliatedGroupId :
     * startBusinessDate : 2016-10-01
     * intro : 处于107国道附近，离宝安汽车站，西乡汽车站，翻身地铁站不到十分钟，创意天虹，宝安体育中心临近，交通便利。
     * guide :
     * telephone : 13530951857
     * startPrice : 64
     * hotelName : 深圳中南旅馆
     * countryId : 0001
     * countryName : 中国大陆
     * provinceId : 2000
     * provinceName : 广东
     * cityId : 2003
     * postCode :
     * email :
     * repairdate : 2016-10-01
     * star : 012012
     * starName : 二星级
     */

    private int distance;
    private String address;
    private String currency;
    private String lat;
    private String lon;
    private String hotelId;
    private String cityName;
    private String score;
    private String affiliatedGroupId;
    private String startBusinessDate;
    private String intro;
    private String guide;
    private String telephone;
    private String startPrice;
    private String hotelName;
    private String countryId;
    private String countryName;
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String postCode;
    private String email;
    private String repairdate;
    private String star;
    private String starName;
    private List<ImagesBean> images;
    private List<ServicesBean> services;
    private List<LandmarkBean> landmark;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAffiliatedGroupId() {
        return affiliatedGroupId;
    }

    public void setAffiliatedGroupId(String affiliatedGroupId) {
        this.affiliatedGroupId = affiliatedGroupId;
    }

    public String getStartBusinessDate() {
        return startBusinessDate;
    }

    public void setStartBusinessDate(String startBusinessDate) {
        this.startBusinessDate = startBusinessDate;
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

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
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

    public static class ImagesBean {
        /**
         * imageId : 617533
         * imageName : 酒店外景
         * imageUrl : http://pic.cnbooking.net:10541/CT/88/4dad20ecb6d94d86.jpg
         */

        private String imageId;
        private String imageName;
        private String imageUrl;

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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public static class ServicesBean {
        /**
         * serviceId : 27
         * serviceName : 电视
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
         * landid : A120588
         * landName : 宝安区
         * distance :
         */

        private String landid;
        private String landName;
        private String distance;

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

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }
    }
}
