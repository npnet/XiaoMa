package com.xiaoma.trip.hotel.request;

/**
 * 获取酒店下房间房价库存信息请求参数
 * Created by zhushi.
 * Date: 2018/12/6
 */
public class RequestRoomRatePlansParm {
//    参数名	       必选	    类型	        说明
//    countryId	   是	    string	    国家 ID
//    province   是     	string	    省份 ID
//    city	   是     	string	    城市 ID
//    hotelId	   否	    string	    CN 酒店 ID
//    roomId	   否	    string	    CN 房间 ID
//    ratePlanId   否	    string	    CN 价格计划 ID
//    checkIn	   是	    string	    入住时间
//    checkOut	   是	    string	    离店时间
//    pageNo	   是     	string	    分页页数
//    pageCount	   是	    string	    每页数目

    public String countryId;
    public String province;
    public String city;
    public String hotelId;
    public String roomId;
    public String ratePlanId;
    public String checkIn;
    public String checkOut;
    public int pageNo;
    public int pageCount;
}
