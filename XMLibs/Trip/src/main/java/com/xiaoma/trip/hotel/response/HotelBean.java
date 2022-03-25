package com.xiaoma.trip.hotel.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 酒店bean
 * Created by zhushi.
 * Date: 2018/12/6
 */
public class HotelBean implements Parcelable {
    /**
     * minPrice : 0
     * images : [{"imageUrl":"http://pic.cnbooking.net:10541/upload/2012/02/16/23-12-37-404.131613622.jpg","imageId":"","imageName":""},{"imageUrl":"http://pic.cnbooking.net:10541/upload/2012/02/16/23-08-45-951.560283953.jpg","imageId":"","imageName":"酒店大堂"},{"imageUrl":"http://pic.cnbooking.net:10541/upload/2012/02/16/23-09-47-873.939978733.jpg","imageId":"","imageName":"酒店客房"},{"imageUrl":"http://pic.cnbooking.net:10541/upload/2012/02/16/23-07-55-795.324342423.jpg","imageId":"","imageName":"酒店外景"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201802/456079d5-7ba2-4146-b682-f193327c2e15.png","imageId":"","imageName":"酒店外景"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/b18f0c53-0757-472b-a7a1-b5f5129b8cbb.jpg","imageId":"","imageName":"酒店外景"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/397cff0c-bee9-4263-bc82-c6d1a64ef48f.jpg","imageId":"","imageName":"酒店大堂"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/18145dba-5ee2-4cbd-a163-22d9dbab442b.jpg","imageId":"","imageName":"26楼大堂"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/8f2bd901-c9d3-4387-90f7-ee083787f66f.jpg","imageId":"","imageName":"V套房"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/fc369d5a-df8d-40dd-af2c-02a55d1352b4.jpg","imageId":"","imageName":"V套房"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/5ee35578-4b35-4f49-b2e8-408b744d96c6.jpg","imageId":"","imageName":"会议室"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/7879f0f8-9f67-464f-bf84-f47af16fe6f6.jpg","imageId":"","imageName":"健身房"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/37710b17-ac5c-4cc7-b830-060f519ef5a0.jpg","imageId":"","imageName":"游泳池"}]
     * services : [{"serviceId":"008009","serviceName":"免费停车场","groupId":"5"},{"serviceId":"008014","serviceName":"熨斗","groupId":"7"},{"serviceId":"008018","serviceName":"电热水壶","groupId":"7"},{"serviceId":"008019","serviceName":"中央空调","groupId":"7"},{"serviceId":"008021","serviceName":"冰箱","groupId":"7"},{"serviceId":"008022","serviceName":"房间内高速上网","groupId":"4"},{"serviceId":"008024","serviceName":"吹风机","groupId":"7"},{"serviceId":"008030","serviceName":"中餐厅","groupId":"10"},{"serviceId":"008031","serviceName":"客房送餐服务","groupId":"10"},{"serviceId":"008032","serviceName":"宴会厅","groupId":"11"},{"serviceId":"008034","serviceName":"租车服务","groupId":"6"},{"serviceId":"008035","serviceName":"外币兑换","groupId":"8"},{"serviceId":"008038","serviceName":"咖啡厅","groupId":"12"},{"serviceId":"008039","serviceName":"洗衣服务","groupId":"8"},{"serviceId":"008040","serviceName":"ATM机","groupId":"9"},{"serviceId":"008045","serviceName":"商务中心","groupId":"9"},{"serviceId":"008046","serviceName":"酒吧","groupId":"9"},{"serviceId":"008048","serviceName":"叫醒服务","groupId":"8"},{"serviceId":"008053","serviceName":"桑拿按摩","groupId":"12"},{"serviceId":"008054","serviceName":"前台贵重物品保险柜","groupId":"8"},{"serviceId":"008055","serviceName":"健身房","groupId":"9"},{"serviceId":"008058","serviceName":"美容美发室","groupId":"12"},{"serviceId":"008062","serviceName":"免费赠送矿泉水","groupId":"8"},{"serviceId":"008063","serviceName":"免费赠送地图","groupId":"8"},{"serviceId":"008064","serviceName":"棋牌室","groupId":"12"},{"serviceId":"008066","serviceName":"免费赠送报纸","groupId":"8"},{"serviceId":"008070","serviceName":"室外游泳池","groupId":"9"},{"serviceId":"008071","serviceName":"免费洗漱用品","groupId":"7"},{"serviceId":"008075","serviceName":"茶室","groupId":"12"},{"serviceId":"008076","serviceName":"西餐厅","groupId":"10"},{"serviceId":"008077","serviceName":"大堂吧","groupId":"10"},{"serviceId":"008078","serviceName":"会议厅","groupId":"11"},{"serviceId":"008093","serviceName":"邮政服务","groupId":"8"},{"serviceId":"008094","serviceName":"行李存放服务","groupId":"8"},{"serviceId":"008099","serviceName":"客房WIFI","groupId":"4"},{"serviceId":"008100","serviceName":"公共区WIFI","groupId":"4"}]
     * landmark : [{"landid":"0056793","landName":"皇岗口岸","distance":"3.400"},{"landid":"0056796","landName":"购物公园","distance":"1.000"},{"landid":"0059594","landName":"深圳北站","distance":"11.400"},{"landid":"0059644","landName":"深圳宝安国际机场","distance":"32.800"}]
     * hotelName : 深圳皇庭V酒店
     * countryName : 中国大陆
     * provinceName : 广东
     * postCode : 518000
     * email :
     * startBusinessDate : 2011
     * repairdate : 2012
     * recommendedLevel : 006001
     * star : 012017
     * starName: 准五星级
     * lon : 114.07055415
     * lat : 22.53690546
     * intro : 深圳皇庭V酒店位于福田区金田路皇岗商务中心，临近深圳会展中心、市民中心、地铁站，是一家设计时尚、客房温馨的高星级酒店。
     * 　　深圳皇庭V酒店的客房设计融自然与时尚于一体，拥有时尚客房及设计独特的豪华套房，24小时白金管家为宾客提供个性化的尊崇服务，让您尽享宾至如归的感觉。
     * 　　深圳皇庭V酒店另拥有各类时尚餐厅、酒吧及休闲场所，是您与亲朋好友共享美味佳肴的完美之地。近千平米无柱式宴会厅和6个大小不同的会议室，为您提供顶级的视听设备和资深专业的会议服务团队。酒店还配有先进一流的健身设备及SPA、俱乐部等休闲娱乐设施，在这里您可重焕身心活力，体验品味时尚。
     * allowWebSale : 1
     * guide : 福田区最新开业5星级酒店，地处福田会展中心，距离皇岗口岸仅5分钟车程。酒店外观大气，大堂位于26层，所有房间可观无敌城景
     * hotelId : 23113
     * reserve2 : 0755-88911111
     * countryId : 0001
     * provinceId : 2000
     * cityId : 2003
     * cityName : 深圳
     * address : 深圳市福田区金田路2028号皇岗商务中心26楼办理入住
     */
    private int isCollect;//收藏状态  0未收藏，1已收藏
    private String minPrice;
    private String startPrice;
    private String hotelName;
    private String countryName;
    private String provinceName;
    private String postCode;
    private String email;
    private String startBusinessDate;
    private String repairdate;
    private String recommendedLevel;
    private String star;
    private String starName;
    private String score;
    private String lon;
    private String lat;
    private String intro;
    private String allowWebSale;
    private String guide;
    private String hotelId;
    private String reserve2;
    private String countryId;
    private String provinceId;
    private String cityId;
    private String cityName;
    private String address;
    private double distance;
    private String telephone;
    private String iconUrl = "";
    private List<ImageBean> images;
    private List<ServicesBean> services;
    private List<LandmarkBean> landmark;

    public int getStatus() {
        return isCollect;
    }

    public void setStatus(int status) {
        this.isCollect = status;
    }

    public String getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(String startPrice) {
        this.startPrice = startPrice;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
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

    public String getRecommendedLevel() {
        return recommendedLevel;
    }

    public void setRecommendedLevel(String recommendedLevel) {
        this.recommendedLevel = recommendedLevel;
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
        this.starName = star;
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

    public String getAllowWebSale() {
        return allowWebSale;
    }

    public void setAllowWebSale(String allowWebSale) {
        this.allowWebSale = allowWebSale;
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

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public List<ImageBean> getImages() {
        return images;
    }

    public void setImages(List<ImageBean> images) {
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

    public String getScore() {
        return score == null ? "" : score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public static class ServicesBean implements Parcelable {
        /**
         * serviceId : 008009
         * serviceName : 免费停车场
         * groupId : 5
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.serviceId);
            dest.writeString(this.serviceName);
            dest.writeString(this.groupId);
        }

        public ServicesBean() {
        }

        protected ServicesBean(Parcel in) {
            this.serviceId = in.readString();
            this.serviceName = in.readString();
            this.groupId = in.readString();
        }

        public static final Creator<ServicesBean> CREATOR = new Creator<ServicesBean>() {
            @Override
            public ServicesBean createFromParcel(Parcel source) {
                return new ServicesBean(source);
            }

            @Override
            public ServicesBean[] newArray(int size) {
                return new ServicesBean[size];
            }
        };
    }

    public static class LandmarkBean implements Parcelable {
        /**
         * landid : 0056793
         * landName : 皇岗口岸
         * distance : 3.400
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.landid);
            dest.writeString(this.landName);
            dest.writeString(this.distance);
        }

        public LandmarkBean() {
        }

        protected LandmarkBean(Parcel in) {
            this.landid = in.readString();
            this.landName = in.readString();
            this.distance = in.readString();
        }

        public static final Creator<LandmarkBean> CREATOR = new Creator<LandmarkBean>() {
            @Override
            public LandmarkBean createFromParcel(Parcel source) {
                return new LandmarkBean(source);
            }

            @Override
            public LandmarkBean[] newArray(int size) {
                return new LandmarkBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.minPrice);
        dest.writeString(this.hotelName);
        dest.writeString(this.countryName);
        dest.writeString(this.provinceName);
        dest.writeString(this.postCode);
        dest.writeString(this.email);
        dest.writeString(this.startBusinessDate);
        dest.writeString(this.repairdate);
        dest.writeString(this.recommendedLevel);
        dest.writeString(this.star);
        dest.writeString(this.starName);
        dest.writeString(this.score);
        dest.writeString(this.lon);
        dest.writeString(this.lat);
        dest.writeString(this.intro);
        dest.writeString(this.allowWebSale);
        dest.writeString(this.guide);
        dest.writeString(this.hotelId);
        dest.writeString(this.reserve2);
        dest.writeString(this.countryId);
        dest.writeString(this.provinceId);
        dest.writeString(this.cityId);
        dest.writeString(this.cityName);
        dest.writeString(this.address);
        dest.writeList(this.images);
        dest.writeList(this.services);
        dest.writeList(this.landmark);
        dest.writeString(this.telephone);
        dest.writeDouble(this.distance);
        dest.writeString(this.iconUrl);
    }

    public HotelBean() {
    }

    protected HotelBean(Parcel in) {
        this.minPrice = in.readString();
        this.hotelName = in.readString();
        this.countryName = in.readString();
        this.provinceName = in.readString();
        this.postCode = in.readString();
        this.email = in.readString();
        this.startBusinessDate = in.readString();
        this.repairdate = in.readString();
        this.recommendedLevel = in.readString();
        this.star = in.readString();
        this.starName = in.readString();
        this.score = in.readString();
        this.lon = in.readString();
        this.lat = in.readString();
        this.intro = in.readString();
        this.allowWebSale = in.readString();
        this.guide = in.readString();
        this.hotelId = in.readString();
        this.reserve2 = in.readString();
        this.countryId = in.readString();
        this.provinceId = in.readString();
        this.cityId = in.readString();
        this.cityName = in.readString();
        this.address = in.readString();
        this.images = new ArrayList<ImageBean>();
        in.readList(this.images, ImageBean.class.getClassLoader());
        this.services = new ArrayList<ServicesBean>();
        in.readList(this.services, ServicesBean.class.getClassLoader());
        this.landmark = new ArrayList<LandmarkBean>();
        in.readList(this.landmark, LandmarkBean.class.getClassLoader());
        this.telephone = in.readString();
        this.distance = in.readDouble();
        this.iconUrl = in.readString();
    }

    public static final Parcelable.Creator<HotelBean> CREATOR = new Parcelable.Creator<HotelBean>() {
        @Override
        public HotelBean createFromParcel(Parcel source) {
            return new HotelBean(source);
        }

        @Override
        public HotelBean[] newArray(int size) {
            return new HotelBean[size];
        }
    };
}
