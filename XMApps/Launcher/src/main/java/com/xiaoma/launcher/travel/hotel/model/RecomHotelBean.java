package com.xiaoma.launcher.travel.hotel.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.bean
 *  @file_name:      RecomHotelBean
 *  @author:         Rookie
 *  @create_time:    2019/1/2 15:51
 *  @description：   TODO             */

public class RecomHotelBean {

    public RecomHotelBean(boolean isStar, float distance, float price, String address, String hotelName) {
        this.isStar = isStar;
        this.distance = distance;
        this.price = price;
        this.address = address;
        this.hotelName = hotelName;
    }

    public boolean isStar;
    public float distance;
    public float price;
    public String address;
    public String hotelName;
}
