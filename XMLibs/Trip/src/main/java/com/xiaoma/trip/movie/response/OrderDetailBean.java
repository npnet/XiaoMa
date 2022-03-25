package com.xiaoma.trip.movie.response;

import java.io.Serializable;

public class OrderDetailBean implements Serializable {

    /**
     * id : 582
     * channelId : AA1090
     * createDate : 1553507548000
     * type : Film
     * orderNo : 2129250319000007
     * amount : 135.0
     * ticketNum : 2
     * orderId : 201903251752254784994
     * bookPhone : 15833223626
     * orderName : 深圳百老汇电影中心(IMAX万象天地店)
     * orderStatus : 待支付
     * orderStatusId : 2
     * orderDate : 2019-03-252035
     * delFlag : 0
     * uid : 1052177782848892928
     * orderDays : 0
     * payStatus : 0
     * lastpayDate : 1553508448000
     * currentDate: 1554278200211
     * payQrcode : http://www.carbuyin.net/by2/wechatQr/5347afa3-de30-43b6-819f-b129e4be952b.png
     * cinemaJson : {"address":"南山区粤海街道华润万象天地L5层SL562号店铺","cinemaName":"深圳百老汇电影中心(IMAX万象天地店)","filmType":"喜剧/剧情/传记","id":"7228","lat":"22.547441","lon":"113.962472","moblie":"0755-86160222","seat":"3704|3721"}
     * cinemaJsonVo : {"id":"7228","cinemaName":"深圳百老汇电影中心(IMAX万象天地店)","address":"南山区粤海街道华润万象天地L5层SL562号店铺","lat":"22.547441","lon":"113.962472","filmType":"喜剧/剧情/传记","seat":"3704|3721"}
     */

    private int id;
    private String channelId;
    private long createDate;
    private String type;
    private String orderNo;
    private String amount;
    private String ticketNum;
    private String orderId;
    private String bookPhone;
    private String orderName;
    private String orderStatus;
    private int orderStatusId;
    private String orderDate;
    private int delFlag;
    private long uid;
    private int orderDays;
    private int payStatus;
    private long lastpayDate;

    public long getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }

    private long currentDate;
    private String payQrcode;
    private String cinemaJson;
    private CinemaJsonVoBean cinemaJsonVo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(String ticketNum) {
        this.ticketNum = ticketNum;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookPhone() {
        return bookPhone;
    }

    public void setBookPhone(String bookPhone) {
        this.bookPhone = bookPhone;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getOrderDays() {
        return orderDays;
    }

    public void setOrderDays(int orderDays) {
        this.orderDays = orderDays;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public long getLastpayDate() {
        return lastpayDate;
    }

    public void setLastpayDate(long lastpayDate) {
        this.lastpayDate = lastpayDate;
    }

    public String getPayQrcode() {
        return payQrcode;
    }

    public void setPayQrcode(String payQrcode) {
        this.payQrcode = payQrcode;
    }

    public String getCinemaJson() {
        return cinemaJson;
    }

    public void setCinemaJson(String cinemaJson) {
        this.cinemaJson = cinemaJson;
    }

    public CinemaJsonVoBean getCinemaJsonVo() {
        return cinemaJsonVo;
    }

    public void setCinemaJsonVo(CinemaJsonVoBean cinemaJsonVo) {
        this.cinemaJsonVo = cinemaJsonVo;
    }

    public static class CinemaJsonVoBean implements Serializable {
        /**
         * id : 7228
         * cinemaName : 深圳百老汇电影中心(IMAX万象天地店)
         * address : 南山区粤海街道华润万象天地L5层SL562号店铺
         * lat : 22.547441
         * lon : 113.962472
         * filmType : 喜剧/剧情/传记
         * seat : 3704|3721
         */

        private String id;
        private String cinemaName;
        private String address;
        private String lat;
        private String lon;
        private String filmType;
        private String seat;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCinemaName() {
            return cinemaName;
        }

        public void setCinemaName(String cinemaName) {
            this.cinemaName = cinemaName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
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

        public String getFilmType() {
            return filmType;
        }

        public void setFilmType(String filmType) {
            this.filmType = filmType;
        }

        public String getSeat() {
            return seat;
        }

        public void setSeat(String seat) {
            this.seat = seat;
        }
    }
}
