package com.xiaoma.trip.movie.response;

import java.io.Serializable;

public class NearbyCinemasDetailsBean implements Serializable {
    /**
     * cinemaId : 8471
     * cinemaName : 太平洋影城·深圳京基百纳店
     * cinemaCode : 20002845
     * spCode : sp015
     * countyCode : 440305
     * countyName : 南山区
     * address : 深圳市南山区白石路与石洲中路交汇处京基百纳广场3楼（中信红树湾旁）
     * longitude : 113.970477
     * latitude : 22.530615
     * imgUrl : http://image.189mv.cn/images/cinema/cinema_31453.jpg
     * mobile : 0755-86286000
     * facilitys : [{"name":"地铁","value":"蛇口线红树湾站D出口步行920米","type":"0"},{"name":"公交","value":"610、109到石洲中路站下步行400米；45、49到百纳广场站下步行320米","type":"1"},{"name":"停车","value":"京基百纳广场地下停车场，免费停车3小时","type":"2"},{"name":"卖品","value":"爆米花、可乐等","type":"3"},{"name":"取票机","value":"影院自助取票机","type":"4"},{"name":"3D眼镜","value":"影院2016年9月1日起不再提供免费3D眼镜，眼镜价格以影城公告为准","type":"5"},{"name":"儿童票","value":"1.3米（含）以下儿童可免费观看2D电影（无座），1位家长最多可带1名免票儿童；儿童观看3D电影均需购票入场","type":"6"},{"name":"休息区","value":"大厅中心区","type":"7"},{"name":"餐饮","value":"醉忆江南、黄记煌、一点味餐厅、肯德基","type":"9"},{"name":"购物","value":"天虹超市、世界之窗","type":"10"}]
     * terminal : 8
     * distance : 1.0
     * buyTimeLimit : 0
     * newBuyTimeLimit : 0
     * minPrice:34.5
     */
    private int isCollect;//收藏状态  0未收藏，1已收藏
    private String cinemaId;
    private String cinemaName;
    private String cinemaCode;
    private String spCode;
    private String countyCode;
    private String countyName;
    private String address;
    private String longitude;
    private String latitude;
    private String imgUrl;
    private String mobile;
    private String facilitys;
    private String terminal;
    private String distance;

    public int getStatus() {
        return isCollect;
    }

    public void setStatus(int status) {
        this.isCollect = status;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    private int buyTimeLimit;
    private int newBuyTimeLimit;
    private String minPrice;

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCinemaCode() {
        return cinemaCode;
    }

    public void setCinemaCode(String cinemaCode) {
        this.cinemaCode = cinemaCode;
    }

    public String getSpCode() {
        return spCode;
    }

    public void setSpCode(String spCode) {
        this.spCode = spCode;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFacilitys() {
        return facilitys;
    }

    public void setFacilitys(String facilitys) {
        this.facilitys = facilitys;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getBuyTimeLimit() {
        return buyTimeLimit;
    }

    public void setBuyTimeLimit(int buyTimeLimit) {
        this.buyTimeLimit = buyTimeLimit;
    }

    public int getNewBuyTimeLimit() {
        return newBuyTimeLimit;
    }

    public void setNewBuyTimeLimit(int newBuyTimeLimit) {
        this.newBuyTimeLimit = newBuyTimeLimit;
    }
}
