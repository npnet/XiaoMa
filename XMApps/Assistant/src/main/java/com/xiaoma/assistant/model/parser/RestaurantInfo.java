package com.xiaoma.assistant.model.parser;


import java.io.Serializable;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */
public class RestaurantInfo implements Serializable {

    /**
     * area : 西涌
     * cate : 美食
     * wifi : 1
     * address : 龙岗区南澳街道西涌社区芽山村131号（芽山村边上）
     * avgprice : 50
     * lng : 114.545607
     * distance : 61.07479296875
     * cityname : 深圳
     * subcate : 其他美食
     * deeplinkurl : imeituan://www.meituan.com/poi?id=159656989&lch=Bcps:x:0:fb51a4df9d7ed3b4c764baeb86a1113b946
     * cityid : 30
     * openinfo : 周一至周日
     * 10:00-22:00
     * waimai : 0
     * phone : 15919468507/18813944822
     * frontimg : http://p0.meituan.net/mogu/f498de5733b7e88e4beff9d199f1ad81389068.png
     * avgscore : 5
     * iurl : http://r.union.meituan.com/url?key=fb51a4df9d7ed3b4c764baeb86a1113b946&url=http%3A%2F%2Fi.meituan.com%2Fshop%2F159656989
     * deals : []
     * name : 芽山人家餐厅
     * id : 159656989
     * historycoupon : 267
     * lat : 22.491237
     */

    private String area;
    private String cate;
    private int wifi;
    private String address;
    private double avgprice;
    private String lng;
    private double distance;
    private String cityname;
    private String subcate;
    private String deeplinkurl;
    private String cityid;
    private String openinfo;
    private int waimai;
    private String phone;
    private String frontimg;
    private double avgscore = -1;
    private String iurl;
    private String name;
    private int id;
    private int historycoupon;
    private String lat;
    private int star;
    private List<?> deals;

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public int getWifi() {
        return wifi;
    }

    public void setWifi(int wifi) {
        this.wifi = wifi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getAvgprice() {
        return avgprice;
    }

    public void setAvgprice(double avgprice) {
        this.avgprice = avgprice;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getSubcate() {
        return subcate;
    }

    public void setSubcate(String subcate) {
        this.subcate = subcate;
    }

    public String getDeeplinkurl() {
        return deeplinkurl;
    }

    public void setDeeplinkurl(String deeplinkurl) {
        this.deeplinkurl = deeplinkurl;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getOpeninfo() {
        return openinfo;
    }

    public void setOpeninfo(String openinfo) {
        this.openinfo = openinfo;
    }

    public int getWaimai() {
        return waimai;
    }

    public void setWaimai(int waimai) {
        this.waimai = waimai;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFrontimg() {
        return frontimg;
    }

    public void setFrontimg(String frontimg) {
        this.frontimg = frontimg;
    }

    public double getAvgscore() {
        return avgscore;
    }

    public void setAvgscore(double avgscore) {
        this.avgscore = avgscore;
    }

    public String getIurl() {
        return iurl;
    }

    public void setIurl(String iurl) {
        this.iurl = iurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHistorycoupon() {
        return historycoupon;
    }

    public void setHistorycoupon(int historycoupon) {
        this.historycoupon = historycoupon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public List<?> getDeals() {
        return deals;
    }

    public void setDeals(List<?> deals) {
        this.deals = deals;
    }
}
