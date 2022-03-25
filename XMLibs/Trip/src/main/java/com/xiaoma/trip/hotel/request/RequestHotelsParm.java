package com.xiaoma.trip.hotel.request;

/**
 * 查询酒店的基础信息请求参数封装
 * Created by zhushi.
 * Date: 2018/12/4
 */
public class RequestHotelsParm {

//    参数名	       必选	    类型	        说明
//    countryId	   是	    string	    国家 ID
//    province     是     	string	    省份 ID
//    city	       是     	string	    城市 ID
//    hotelId	   否	    string	    CN 酒店 ID
//    checkIn	   是	    string	    入住时间
//    checkOut	   是	    string	    离店时间
//    pageNo	   是     	string	    分页页数
//    pageCount	   是	    string	    每页数目

    public String countryId;
    public String province;
    public String city;
    public String hotelId;
    public String checkIn;
    public String checkOut;
    public int pageNo;
    public int pageCount;
}
