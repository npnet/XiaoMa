package com.xiaoma.launcher.recommend.model;

import com.xiaoma.mqtt.model.DatasBean;

/**
 * @Auther: huojie
 * @Date: 2019/1/3 0003 13:11
 * @Description:美食
 */
public class Food extends DatasBean {

    //店铺id
    private String id;
    //店铺名称
    private String name;
    //店铺 所在城市id
    private String cityid;
    //店铺 所在城市名称
    private String cityname;
    //店铺地址
    private String address;
    //店铺所属一级分类
    private String cate;
    //店铺所属二级分类
    private String subcate;
    //店铺平均评分
    private String avgscore;
    //店铺消费 人均价格
    private String avgprice;
    //店铺所属商圈
    private String area;
    //店铺纬度
    private String lat;
    //店铺经度
    private String lng;
    //店铺展示的首图
    private String frontimg;
    /**
     * 店铺是否有wifi
     *
     */
    private String wifi;
    /**
     * 店铺是否支持外卖
     */
    private String waimai;
    //店铺电话
    private String phone;
    //推荐菜名称           使用申请使用
    private String featuremenus ;
    //总购买人数
    private String historycoupon ;
    //唤起商家详情页面
    private String deeplinkurl ;
    //商家详情页面 h5url
    private String iurl ;
    //排队服务
    private String queuedeeplink ;
    //买单服务
    private String cashierdeeplink ;
    //可预订时预订
    private String reservedeeplink;
    //h5评论页
    private String reviewh5url ;
    //评论页
    private String reviewurl  ;
    //营业时间
    private String openinfo  ;
    //图片集
    private String[] imgurls   ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getSubcate() {
        return subcate;
    }

    public void setSubcate(String subcate) {
        this.subcate = subcate;
    }

    public String getAvgscore() {
        return avgscore;
    }

    public void setAvgscore(String avgscore) {
        this.avgscore = avgscore;
    }

    public String getAvgprice() {
        return avgprice;
    }

    public void setAvgprice(String avgprice) {
        this.avgprice = avgprice;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getFrontimg() {
        return frontimg;
    }

    public void setFrontimg(String frontimg) {
        this.frontimg = frontimg;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getWaimai() {
        return waimai;
    }

    public void setWaimai(String waimai) {
        this.waimai = waimai;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFeaturemenus() {
        return featuremenus;
    }

    public void setFeaturemenus(String featuremenus) {
        this.featuremenus = featuremenus;
    }

    public String getHistorycoupon() {
        return historycoupon;
    }

    public void setHistorycoupon(String historycoupon) {
        this.historycoupon = historycoupon;
    }

    public String getDeeplinkurl() {
        return deeplinkurl;
    }

    public void setDeeplinkurl(String deeplinkurl) {
        this.deeplinkurl = deeplinkurl;
    }

    public String getIurl() {
        return iurl;
    }

    public void setIurl(String iurl) {
        this.iurl = iurl;
    }

    public String getQueuedeeplink() {
        return queuedeeplink;
    }

    public void setQueuedeeplink(String queuedeeplink) {
        this.queuedeeplink = queuedeeplink;
    }

    public String getCashierdeeplink() {
        return cashierdeeplink;
    }

    public void setCashierdeeplink(String cashierdeeplink) {
        this.cashierdeeplink = cashierdeeplink;
    }

    public String getReservedeeplink() {
        return reservedeeplink;
    }

    public void setReservedeeplink(String reservedeeplink) {
        this.reservedeeplink = reservedeeplink;
    }

    public String getReviewh5url() {
        return reviewh5url;
    }

    public void setReviewh5url(String reviewh5url) {
        this.reviewh5url = reviewh5url;
    }

    public String getReviewurl() {
        return reviewurl;
    }

    public void setReviewurl(String reviewurl) {
        this.reviewurl = reviewurl;
    }

    public String getOpeninfo() {
        return openinfo;
    }

    public void setOpeninfo(String openinfo) {
        this.openinfo = openinfo;
    }

    public String[] getImgurls() {
        return imgurls;
    }

    public void setImgurls(String[] imgurls) {
        this.imgurls = imgurls;
    }
}
