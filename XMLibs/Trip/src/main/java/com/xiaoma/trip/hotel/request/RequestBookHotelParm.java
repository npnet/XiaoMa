package com.xiaoma.trip.hotel.request;

/**
 * 预定酒店请求参数
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class RequestBookHotelParm {

    //    参数名	            必选	    类型	    说明
//    channelId	        是	    string	车厂渠道号
//    uid	            是	    long	车主用户ID
//    hotelId	        是	    String	CN 酒店 ID
//    roomId	        是	    String	CN 房间 ID
//    rateplanId	    是	    String	RateplanId
//    checkIn	        是	    String	入住时间
//    checkOut	        是	    String	离店时间
//    roomCount	        是	    Int	    房间数量
//    currency	        是	    String	币种 CODE，默认中国CNY
//    orderAmount	    是	    String	订单金额
//    bookName	        是	    String	预订人名称，方便默认与入住人一致
//    bookPhone	        否	    String	预订人电话
//    guestName	        是	    String	入住人名称
//    guestPhone	    是	    String	入住人电话
//    guestFax	        否	    String	入住人传真
//    guestType	        是	    String	入住人国籍,默认国内013002
//    cardTypeId	    否	    String	入住人证件类
//    cardNum	        否	    String	入住人证件号
//    specialRemark	    否	    String	入住人特殊要
//    reserve1	        否	    String	备用字段一
//    reserve2	        否	    String	备用字段二
//    customerOrderId	否	    String	客户订单号

    public String channelId;
    public String uid;
    public String hotelId;
    public String hotelName;
    public String roomId;
    public String rateplanId;
    public String checkIn;
    public String checkOut;
    public int roomCount;
    public String currency;
    public String orderAmount;
    public String bookName;
    public String bookPhone;
    public String guestName;
    public String guestPhone;
    public String guestFax;
    public String guestType;
    public String cardTypeId;
    public String cardNum;
    public String specialRemark;
    public String reserve1;
    public String reserve2;
    public String customerOrderId;

}
