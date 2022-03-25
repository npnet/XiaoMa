package com.xiaoma.trip.category.response;

import java.util.List;

public class SearchStoreBean {

    /**
     * area : 小营
     * cate : 生活服务
     * wifi : 0
     * address : 朝阳区朝阳区北四环东路73号远洋未来广场3层
     * avgprice : 0
     * lng : 116.432438
     * distance : 0.42611233520507813
     * cityname : 北京
     * subcate : 母婴亲子
     * deeplinkurl : imeituan://www.meituan.com/poi?id=94952796&lch=Bcps:x:0:fb51a4df9d7ed3b4c764baeb86a1113b946
     * cityid : 1
     * openinfo : 周一至周日
     * 10:00-22:00
     * waimai : 0
     * phone : 13521727772
     * frontimg : https://img.meituan.net/ugcpic/c2188755c55ff1f2aad9ad8372a85069523002.png
     * avgscore : 0
     * iurl : http://r.union.meituan.com/url?key=fb51a4df9d7ed3b4c764baeb86a1113b946&url=http%3A%2F%2Fi.meituan.com%2Fshop%2F94952796
     * deals : []
     * name : 动动之家（远洋未来广场店）
     * sid : 94952796
     * historycoupon : 0
     * lat : 39.990548
     */
    private int isCollect;//收藏状态  0未收藏，1已收藏
    private String area;
    private String cate;
    private int wifi;
    private String address;
    private String avgprice;
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
    private float avgscore;
    private String iurl;
    private String name;
    private String sid;
    private int historycoupon;
    private String lat;
    private List<?> deals;

    private int star;//星级
    private int comforLevel;//舒适度
    private int isMiQiLin;//是否美琪琳 0:是   1:不是

    public int getStatus() {
        return isCollect;
    }

    public void setStatus(int status) {
        this.isCollect = status;
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

    public String getAvgprice() {
        return avgprice;
    }

    public void setAvgprice(String avgprice) {
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

    public float getAvgscore() {
        return avgscore;
    }

    public void setAvgscore(float avgscore) {
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

    public String getId() {
        return sid;
    }

    public void setId(String id) {
        this.sid = id;
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

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getComforLevel() {
        return comforLevel;
    }

    public void setComforLevel(int comforLevel) {
        this.comforLevel = comforLevel;
    }

    public int getIsMiQiLin() {
        return isMiQiLin;
    }

    public void setIsMiQiLin(int isMiQiLin) {
        this.isMiQiLin = isMiQiLin;
    }
}
