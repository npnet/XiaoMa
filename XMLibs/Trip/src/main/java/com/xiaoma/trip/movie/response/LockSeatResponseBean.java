package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

/**
 * 锁座下单数据返回
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class LockSeatResponseBean implements Serializable {

    /**
     * orderNo: 2129150219000019
     * qrCode: http:\/\/www.carbuyin.net\/by2\/qrcode\/ali2129140119000015.png
     * price : 54.0
     * orderNum : 201812041656430854014
     * payCount : 2
     * payGenCount : 1
     * latestPayTime: 15,
     * createDate: 2019-01-24 03:13:36
     * tickets : [{"ticketId":"201812041656430854014","sectionId":"","seatId":"2721","seatRow":"1","seatCol":"13"}]
     */

    private String orderNo;
    private String qrCode;
    private String price;
    private String orderNum;
    private String payCount;
    private String payGenCount;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getLatestPayTime() {
        return latestPayTime;
    }

    public void setLatestPayTime(int latestPayTime) {
        this.latestPayTime = latestPayTime;
    }

    private int latestPayTime;
    private String createDate;
    private List<TicketsBean> tickets;

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String orderNum) {
        this.qrCode = qrCode;
    }
    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPayCount() {
        return payCount;
    }

    public void setPayCount(String payCount) {
        this.payCount = payCount;
    }

    public String getPayGenCount() {
        return payGenCount;
    }

    public void setPayGenCount(String payGenCount) {
        this.payGenCount = payGenCount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<TicketsBean> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketsBean> tickets) {
        this.tickets = tickets;
    }

    public static class TicketsBean implements Serializable{
        /**
         * seatRow : 1
         * seatCol : 13
         * seatId : 2721
         * sectionId :
         */

        private String sectionId;
        private String seatId;
        private String seatRow;
        private String seatCol;


        public String getSectionId() {
            return sectionId;
        }

        public void setSectionId(String sectionId) {
            this.sectionId = sectionId;
        }

        public String getSeatId() {
            return seatId;
        }

        public void setSeatId(String seatId) {
            this.seatId = seatId;
        }

        public String getSeatRow() {
            return seatRow;
        }

        public void setSeatRow(String seatRow) {
            this.seatRow = seatRow;
        }

        public String getSeatCol() {
            return seatCol;
        }

        public void setSeatCol(String seatCol) {
            this.seatCol = seatCol;
        }
    }
}
