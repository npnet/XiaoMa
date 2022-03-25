package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

public class CompleteOrderBean implements Serializable {

    /**
     * list : [{"id":236,"channelId":"AA1090","createDate":1550740067000,"type":"Film","orderNo":"2129210219000020","amount":"133.6","ticketNum":"2","orderId":"201902211707490182471","bookPhone":"13870700857","orderName":"今夜在浪漫剧场","orderStatus":"已完成","orderStatusId":3,"orderDate":"2019-02-212225","delFlag":0,"cinemaName":"深圳百老汇电影中心（IMAX万象天地店）","orderDays":0,"uid":1050220216212271104,"address":"南山区粤海街道华润万象天地L5层562号店铺","lat":"22.533175","lon":"113.95385","voucherValue":"序列号:55555|验证码:77777","payStatus":0,"filmType":"爱情,奇幻","seat":"C:1|D:1"}]
     * pageInfo : {"pageNum":1,"pageSize":4,"totalRecord":7,"totalPage":2}
     */

    private PageInfoBean pageInfo;
    private List<ListBean> appOrders;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }


    public List<ListBean> getAppOrders() {
        return appOrders;
    }

    public void setAppOrders(List<ListBean> appOrders) {
        this.appOrders = appOrders;
    }

    public static class PageInfoBean {
        /**
         * pageNum : 1
         * pageSize : 4
         * totalRecord : 7
         * totalPage : 2
         */

        private int pageNum;
        private int pageSize;
        private int totalRecord;
        private int totalPage;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalRecord() {
            return totalRecord;
        }

        public void setTotalRecord(int totalRecord) {
            this.totalRecord = totalRecord;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }
    }

    public static class ListBean {


        /**
         * "id": 198,
         * "channelId": "AA1090",
         * "createDate": 1550570331000,
         * "modifyDate": 1550570490000,
         * "type": "Hotel",
         * "orderNo": "2129190219000029",
         * "amount": "0.01",
         * "ticketNum": "1",
         * "orderId": "CN19021900012A",
         * "bookPhone": "13870700857",
         * "orderName": "长春前进大厦",
         * "orderStatus": "已完成",
         * "orderStatusId": 3,
         * "delFlag": 0,
         * "orderDays": 1,
         * "checkIn": "2019-2-19",
         * "checkOut": "2019-2-20",
         * "uid": 1050220216212271104,
         * "address": "长春市前进大街1377号（省委党校南侧）",
         * "lat": "43.838100000000",
         * "lon": "125.289300000000",
         * "paySource": "wx",
         * "payStatus": 2,
         * "lastpayDate": 1550572131000
         *  payQrcode
         *  cinemaJson
         *  hotelJson
         **/

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
        private String cinemaName;
        private int orderDays;
        private long uid;
        private String address;
        private String lat;
        private String lon;
        private String voucherValue;
        private int payStatus;
        private String filmType;
        private String seat;
        private String checkIn;
        private String checkOut;
        private String payQrcode;
        private String cinemaJson;
        private String hotelJson;

        public String getHotelJson() {
            return hotelJson;
        }

        public void setHotelJson(String hotelJson) {
            this.hotelJson = hotelJson;
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

        public String getCinemaName() {
            return cinemaName;
        }

        public void setCinemaName(String cinemaName) {
            this.cinemaName = cinemaName;
        }

        public int getOrderDays() {
            return orderDays;
        }

        public void setOrderDays(int orderDays) {
            this.orderDays = orderDays;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
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

        public String getVoucherValue() {
            return voucherValue;
        }

        public void setVoucherValue(String voucherValue) {
            this.voucherValue = voucherValue;
        }

        public int getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(int payStatus) {
            this.payStatus = payStatus;
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

        public String getCheckIn() {
            return checkIn;
        }

        public void setCheckIn(String checkIn) {
            this.checkIn = checkIn;
        }

        public String getCheckOut() {
            return checkOut;
        }

        public void setCheckOut(String checkOut) {
            this.checkOut = checkOut;
        }
    }
}
