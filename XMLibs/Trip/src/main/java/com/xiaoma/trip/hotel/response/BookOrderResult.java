package com.xiaoma.trip.hotel.response;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.trip.hotel.response
 *  @file_name:      BookOrderResult
 *  @author:         Rookie
 *  @create_time:    2019/1/14 14:39
 *  @description：   TODO             */

public class BookOrderResult {

    /*"data": {
        "qrCode": "",
        "id": 171,
        "lastpayDate": 1550559965950,
        "createDate": 1550558165950
    }*/
     private String qrCode;
     private String id;
     private long lastpayDate;
     private long createDate;



    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLastpayDate() {
        return lastpayDate;
    }

    public void setLastpayDate(long lastpayDate) {
        this.lastpayDate = lastpayDate;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
