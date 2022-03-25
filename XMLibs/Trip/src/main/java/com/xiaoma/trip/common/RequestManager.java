package com.xiaoma.trip.common;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.trip.api.TripAPI;
import com.xiaoma.trip.category.request.RequestSearchStoreParm;
import com.xiaoma.trip.category.response.CategoryBean;
import com.xiaoma.trip.category.response.SearchStoreBean;
import com.xiaoma.trip.category.response.StoreInfoBean;
import com.xiaoma.trip.hotel.request.HotelInfo;
import com.xiaoma.trip.hotel.request.RequestQueryOrderParam;
import com.xiaoma.trip.hotel.response.BookOrderResult;
import com.xiaoma.trip.hotel.response.HotelPageDataBean;
import com.xiaoma.trip.hotel.response.HotelPolicyBean;
import com.xiaoma.trip.hotel.response.RatePlanStatusBean;
import com.xiaoma.trip.hotel.response.RoomRatePlanBean;
import com.xiaoma.trip.movie.request.RequestHallSeatsInfoParm;
import com.xiaoma.trip.movie.request.RequestLockSeatParm;
import com.xiaoma.trip.movie.response.CinemaShowDataBean;
import com.xiaoma.trip.movie.response.CinemasPageDataBean;
import com.xiaoma.trip.movie.response.CompleteOrderBean;
import com.xiaoma.trip.movie.response.ConfirmOrderBean;
import com.xiaoma.trip.movie.response.FilmDetailBean;
import com.xiaoma.trip.movie.response.FilmShowBean;
import com.xiaoma.trip.movie.response.FilmsPageDataBean;
import com.xiaoma.trip.movie.response.HallSeatsInfoBean;
import com.xiaoma.trip.movie.response.LockSeatResponseBean;
import com.xiaoma.trip.movie.response.NearbyCinemaBean;
import com.xiaoma.trip.movie.response.OrderDetailBean;
import com.xiaoma.trip.orders.response.OrdersBean;
import com.xiaoma.trip.orders.response.OrdersPerateBean;
import com.xiaoma.trip.parking.response.ParkInfoBean;
import com.xiaoma.trip.parking.response.ParkingFeeBudgetBean;
import com.xiaoma.trip.parking.response.ParkingInfoBean;
import com.xiaoma.trip.parking.response.ParkingSpotFeeStandardBean;
import com.xiaoma.trip.place.response.CityBean;
import com.xiaoma.trip.place.response.DistrictBean;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 奔腾出行请求管理
 * Created by zhushi.
 * Date: 2018/12/3
 */
public class RequestManager {
    private static String TAG="[RequestManager]";
    public static RequestManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final RequestManager instance = new RequestManager();
    }


    /**
     * 查询酒店基础信息
     *
     * @param countryId 国家 ID ,默认0001 ，可选
     * @param province  省份名称
     * @param city      城市名称
     * @param checkIn   入住时间
     * @param checkOut  离店时间
     * @param pageNo    分页页数
     * @param pageCount 分页条目
     * @param callback
     */
    public void getHotelsBaseInfo(String countryId, String province, String city, String checkIn, String checkOut, int pageNo, int pageCount, ResultCallback<XMResult<HotelPageDataBean>> callback) {


        Map<String, Object> params = new HashMap<>();
        params.put("countryId", countryId);
        params.put("province", province);
        params.put("city", city);
        params.put("checkIn", checkIn);
        params.put("checkOut", checkOut);
        params.put("pageNo", pageNo);
        params.put("pageCount", pageCount);

        request(TripAPI.GET_HOTELS_BY_CONDITION, params, callback);
    }

    /**
     * @param checkIn  入住时间
     * @param checkOut 离店时间
     * @param hotelId  酒店 ID
     * @param callback
     */
    public void getHotelsRoomratePlans(String checkIn, String checkOut, String hotelId, ResultCallback<XMResult<List<RoomRatePlanBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("checkIn", checkIn);
        params.put("checkOut", checkOut);
        params.put("hotelId", hotelId);
        request(TripAPI.GET_HOTELS_ROOMRATE_PLANS, params, callback);
    }

    /**
     * 预定酒店
     *
     * @param hotelId
     * @param hotelName
     * @param roomId
     * @param rateplanId  价格计划ID
     * @param checkIn
     * @param checkOut
     * @param orderAmount
     * @param roomCount
     * @param guestName   入住人名称，多个用“，”连接
     * @param bookPhone
     * @param currency    币种 CODE，默认中国CNY
     * @param guestType   入住人国籍,默认国内013002
     * @param callback
     */
    public void bookHotel(String hotelId, String hotelName, String roomId, String rateplanId,
                          String checkIn, String checkOut, String orderAmount, int roomCount,
                          String guestName, String guestPhone, String bookName, String bookPhone, String currency, String guestType,
                          String address, String lat, String lon, String roomType, String roomMsg, boolean canCancel, String lastCancelDate,
                          String storePhone, String imageUrl, ResultCallback<XMResult<BookOrderResult>> callback) {


        HotelInfo hotelInfo = new HotelInfo(hotelId, address, lat, lon, roomType, roomMsg, canCancel, lastCancelDate, guestPhone, hotelName, imageUrl);

        Map<String, Object> params = new HashMap<>();
        params.put("hotelId", hotelId);
        params.put("hotelName", hotelName);
        params.put("roomId", roomId);
        params.put("rateplanId", rateplanId);
        params.put("checkIn", checkIn);
        params.put("checkOut", checkOut);
        params.put("orderAmount", orderAmount);
        params.put("roomCount", String.valueOf(roomCount));
        params.put("guestName", guestName);
        params.put("bookName", bookName);
        params.put("bookPhone", bookPhone);
        params.put("currency", currency);
        params.put("guestPhone", guestPhone);
        params.put("guestType", guestType);
        params.put("storePhone", storePhone);
        params.put("hotelJson", GsonHelper.toJson(hotelInfo));
        request(TripAPI.BOOK_HOTEL_ORDER, params, callback);
    }


    /**
     * 查询单一订单
     * uid统一在请求添加，所以这里不用再添加
     *
     * @param callback
     */
    public void queryOrder(RequestQueryOrderParam parm, ResultCallback<XMResult<OrdersBean>> callback) {
        request(TripAPI.QUERY_ONE_ORDER, parm, callback);

    }

    /**
     * @param orderId  订单id
     * @param callback
     */
    public void queryOrder(String orderId, ResultCallback<XMResult<OrdersBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderId);
        request(TripAPI.QUERY_ONE_ORDER, params, callback);

    }


    /**
     * 查询所有订单
     * uid统一在请求添加，所以这里不用再添加
     *
     * @param callback
     */
    public void queryOrders(ResultCallback<XMResult<List<OrdersBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        request(TripAPI.QUERY_ORDERS, params, callback);

    }

    /**
     * 取消订单
     * 酒店状态code为1、2、3均可取消,影院状态code为2方可取消
     *
     * @param ordersId
     * @param orderCode
     * @param callback
     */
    public void cancelOrders(long ordersId, int orderCode, ResultCallback<XMResult<OrdersPerateBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("id", ordersId);           //否        订单 ID
        params.put("statusId", orderCode);    //否        订单状态code
        request(TripAPI.QUERY_CANCEL, params, callback);

    }

    /**
     * 取消订单
     * 酒店状态code为1、2、3均可取消,影院状态code为2方可取消
     *
     * @param ordersId
     * @param callback
     */
    public void cancelOrders(long ordersId, ResultCallback<XMResult<OrdersPerateBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("id", ordersId);           //否        订单 ID
        request(TripAPI.QUERY_CANCEL, params, callback);

    }

    /**
     * 删除订单
     *
     * @param ordersId
     * @param orderCode
     * @param callback
     */
    public void deleteOrders(long ordersId, int orderCode, ResultCallback<XMResult<OrdersPerateBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", ordersId);
        params.put("statusId", orderCode);
        request(TripAPI.QUERY_DELETE, params, callback);
    }

    /**
     * 查询影院列表
     *
     * @param city       城市代码
     * @param countyName 地区名称
     * @param cinemaName 影院名称
     * @param pageNum    分页页数
     * @param pageSize   每页数目
     * @param callback
     */
    public void queryCinemas(String city, String countyName, String cinemaName, int pageNum, int pageSize,
                             ResultCallback<XMResult<CinemasPageDataBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("city", city);            //是        城市名称
        params.put("countyName", countyName);//否        地区名称
        params.put("cinemaName", cinemaName);//否        影院名称
        params.put("pageNum", pageNum);      //是        分页页数
        params.put("pageSize", pageSize);    //是        每页数目

        request(TripAPI.QUERY_CINEMAS, params, callback);
    }

    /**
     * 查询附近影院列表
     *
     * @param city       城市代码
     * @param countyName 地区名称
     * @param cinemaName 影院名称
     * @param pageNum    分页页数
     * @param pageSize   每页数目
     * @param lat        纬度
     * @param lon        经度
     * @param callback
     */
    public void queryNearcyCinemas(String type,String city, String countyName, String cinemaName, int pageNum, int pageSize, String lat, String lon,
                                   ResultCallback<XMResult<NearbyCinemaBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("type", type);
        params.put("city", city);            //是        城市名称
        params.put("countyName", countyName);//否        地区名称
        params.put("cinemaName", cinemaName);//否        影院名称
        params.put("pageNum", pageNum);      //是        分页页数
        params.put("pageSize", pageSize);    //是        每页数目
        params.put("lat", lat);    //是        每页数目
        params.put("lon", lon);    //是        每页数目

        request(TripAPI.QUERY_FILM, params, callback);
    }

    /**
     * 查询影片列表
     *
     * @param dataStyle 影片类型 0：全部；1：正在热映影片；2：即将上映影片
     * @param city      城市名
     * @param pageNum   分页页数
     * @param pageSize  每页数目
     * @param callback
     */
    public void queryFilms(String type,int dataStyle, String city, int pageNum, int pageSize,
                           ResultCallback<XMResult<FilmsPageDataBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("type", type);
        params.put("dataStyle", dataStyle); //是        影片类型 0：全部；1：正在热映影片；2：即将上映影片
        params.put("city", city);           //是        城市
        params.put("pageNum", pageNum);     //是        分页页数
        params.put("pageSize", pageSize);   //是        每页数目

        request(TripAPI.QUERY_FILM, params, callback);
    }

    /**
     * 查询影院排期（该影院播放哪些影片）
     *
     * @param cinemaId 影院Id 34492
     * @param filmId   影片Id 6120
     * @param city     城市名
     * @param showDate 放映日期
     * @param callback
     */
    public void queryCinemaShow(String cinemaId, String filmId, String city, String showDate,
                                ResultCallback<XMResult<CinemaShowDataBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("cinemaId", cinemaId);//是          影院Id
        params.put("filmId", filmId);    //否          影院Id
        params.put("city", city);        //是          影院Id
        params.put("showDate", showDate);//否          放映日期

        request(TripAPI.QUERY_CINEMA_SHOW, params, callback);
    }

    /**
     * 查询影片排期（该影片在哪些影院播放）
     *
     * @param filmId   影片Id 6120
     * @param city     地区编码
     * @param showDate 放映日期
     * @param callback
     */
    public void queryFilmShow(String filmId, String city, String showDate, String lat, String lon, int pageNum, int pageSize,
                              ResultCallback<XMResult<FilmShowBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("filmId", filmId);    //否        影片Id
        params.put("city", city);        //是          城市
        params.put("showDate", showDate);//否        放映日期
        params.put("lat", lat);    //是        每页数目
        params.put("lon", lon);    //是        每页数目
        params.put("pageNum", pageNum);  //否        	分页页数
        params.put("pageSize", pageSize);//否        每页数目

        request(TripAPI.QUERY_FILMS_SHOW, params, callback);
    }

    /**
     * 查询影片详情
     *
     * @param filmName 影片名称
     * @param city     城市名
     * @param callback
     */
    public void queryFilmDetail(String filmName, String city, ResultCallback<XMResult<FilmDetailBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("filmName", filmName); //是        影片名称
        params.put("city", city);         //是        城市

        request(TripAPI.QUERY_FILM_DETAIL, params, callback);
    }

    /**
     * 查询座位信息
     *
     * @param parm
     * @param callback
     */
    public void queryHallSeatsInfo(RequestHallSeatsInfoParm parm, ResultCallback<XMResult<HallSeatsInfoBean>> callback) {
        request(TripAPI.QUERY_HALLSEATS_INFO, parm, callback);
    }

    /**
     * 锁座下单
     *
     * @param lockSeatParm
     * @param callback
     */
    public void lockSeat(RequestLockSeatParm lockSeatParm, ResultCallback<XMResult<LockSeatResponseBean>> callback) {
        request(TripAPI.LOCK_SEAT, lockSeatParm, callback);
    }

    /**
     * 订单详情
     *
     * @param orderNum
     * @param callback
     */
    public void orderDetail(String orderNum, ResultCallback<XMResult<OrderDetailBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("orderNo", orderNum); //是  订单编号
        request(TripAPI.USER_APP_ORDER_DETAIL, params, callback);
    }

    /**
     * 观影码
     *
     * @param orderNum
     * @param callback
     */
    public void confirmOrder(String orderNum, ResultCallback<XMResult<ConfirmOrderBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("orderNum", orderNum); //是  订单编号
        request(TripAPI.OTHER_ORDER_DETAIL, params, callback);
    }

    /**
     * 电影已完成订单
     *
     * @param type
     * @param pageNum
     * @param pageSize
     * @param callback
     */
    public void completeOrder(String orderType,String type, int pageNum, int pageSize, ResultCallback<XMResult<CompleteOrderBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("orderType", type);
        params.put("type", orderType); //是  类型 电影Film 酒店Hotel
        params.put("pageNum", pageNum); //是  页码
        params.put("pageSize", pageSize); //是  页码
        if("Film".equals(type)){
            request(TripAPI.QUERY_FILM, params, callback);
        }else if("Hotel".equals(type)){
            request(TripAPI.QUERY_HOTEL, params, callback);
        }
    }

    /**
     * 查询城市
     *
     * @param callback
     */
    public void queryCity(ResultCallback<XMResult<List<CityBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        request(TripAPI.GET_CITY_INFO, params, callback);
    }

    /**
     * 查询城区
     *
     * @param cityId
     * @param callback
     */
    public void queryCityDistrict(int cityId, ResultCallback<XMResult<List<DistrictBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("cityId", cityId);  //是        城市id
        request(TripAPI.QUERY_DISTRICT, params, callback);
    }


    /**
     * 查询分类
     *
     * @param cityName
     */
    public void queryCategory(String type,String cityName, double lat,double lon,ResultCallback<XMResult<List<CategoryBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("type", type); //是        城市id
        params.put("cityName", cityName); //是        城市id
        params.put("lat", lat); //是        城市id
        params.put("lon", lon); //是        城市id
        request(TripAPI.QUERY_FOOD_ATTRACTION, params, callback);
    }

    /**
     * 查询店铺
     */
    public void conditionSearchStore(RequestSearchStoreParm requestSearchStoreParm, ResultCallback<XMResult<List<SearchStoreBean>>> callback) {

        request(TripAPI.CONDITION_SEARCH_STORE, requestSearchStoreParm, callback);

    }

    /**
     * 查询店铺
     */
    public void conditionSearchStore(String type, String query, String pos, String city, String cateid, int limit, int offset, ResultCallback<XMResult<List<SearchStoreBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("query", query);
        params.put("pos", pos);
        params.put("city", city);
        params.put("cateid", cateid);
        params.put("limit", limit);
        params.put("offset", offset);
        request(TripAPI.QUERY_FOOD_ATTRACTION, params, callback);
    }

    /**
     * 获取店铺信息
     *
     * @param userName
     * @param password
     * @param name
     * @param callback
     */
    public void queryStoreInfo(String userName, String password, String name, String storeid, ResultCallback<XMResult<StoreInfoBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要    描述
        params.put("username", userName); //是        用户名
        params.put("password", password); //是        密码
        params.put("name", name);         //否        昵称
        params.put("id", "93653388");      //店铺id
        request(TripAPI.QUERY_STORE_INFO, params, callback);
    }

    /**
     * 收藏影院，酒店，美食，景点
     *
     * @param status         0取消收藏，1收藏
     * @param collectionId
     * @param type           收藏类型电影Film 酒店Hotel，美食Food，景点Attractions
     * @param collectionJson 收藏保存信息
     * @param callback
     */
    public void collectItem(int status, String collectionId, String type, String collectionJson, ResultCallback<XMResult<String>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("collectionId", collectionId);
        params.put("type", type);
        params.put("collectionJson", collectionJson);
        postRequest(TripAPI.COLLECT_ITEM, params, callback);
    }

    /**
     * 获取已收藏
     *
     * @param page
     * @param size
     * @param type     收藏类型电影Film 酒店Hotel，美食Food，景点Attractions
     * @param callback
     */
    public void getCollectItems(String collectionType,int page, int size, String type, ResultCallback<XMResult<CollectItems>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("collectionType", type);
        params.put("page", page);
        params.put("size", size);
        params.put("type", collectionType);
        switch (type){
            case "Food":
            case "Attractions":
                request(TripAPI.QUERY_FOOD_ATTRACTION, params, callback);
                break;
            case "Film":
                request(TripAPI.QUERY_FILM, params, callback);
                break;
            case "Hotel":
                request(TripAPI.QUERY_HOTEL, params, callback);
                break;
            default:
                break;
        }
    }


    /**
     * 获取停车场信息
     *
     * @param city
     * @param district
     * @param q
     * @param poi
     * @param distance
     * @param id
     * @param callback
     */
    public void queryParkingInfo(String city, String district, String q, String poi, String distance, String id, int size, int offset, ResultCallback<XMResult<List<ParkingInfoBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要   描述
        params.put("city", city);         //否     城市名称
        params.put("district", district); //否     区域名称
        params.put("q", q);               //否     查询关键字
        params.put("poi", poi);           //否     经纬度(高德)。传参规则 poi=114.077146,22.545313
        params.put("distance", distance); //否     查询半径，单位：米 。只有有poi参数时有效。默认5000米
        params.put("id", id);             //否     对应停车场id。此接口也可获取对应id停车场的详情，当有id传入时，其他参数无效
        params.put("size", size);         //否
        params.put("offset", offset);     //否
        request(TripAPI.QUERY_PARKING_INFO, params, callback);
    }

    /**
     * 获取停车场信息
     *
     * @param city
     * @param district
     * @param query
     * @param poi
     * @param distance
     * @param id
     * @param callback
     */
    public void queryParkingInfoNew(String type,String city, String district, String query, String poi, String distance, String id, int size, int offset, ResultCallback<XMResult<List<ParkInfoBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要   描述
        params.put("type", type);
        params.put("city", city);         //否     城市名称
        params.put("district", district); //否     区域名称
        params.put("query", query);               //否     查询关键字
        params.put("poi", poi);           //否     经纬度(高德)。传参规则 poi=114.077146,22.545313
        params.put("distance", distance); //否     查询半径，单位：米 。只有有poi参数时有效。默认5000米
        params.put("id", id);             //否     对应停车场id。此接口也可获取对应id停车场的详情，当有id传入时，其他参数无效
        params.put("size", size);         //否
        params.put("offset", offset);     //否
        request(TripAPI.QUERY_CAR, params, callback);
    }

    /**
     * 获取附近加油站信息
     *
     * @param type
     * @param query
     * @param poi
     * @param distance
     * @param size
     * @param offset
     * @param callback
     */
    public void queryGasStationInfo(String type,String query,String poi,String distance,int  size,int offset,ResultCallback<XMResult<List<ParkInfoBean>>> callback){
        Map<String, Object> params = new HashMap<>();
        //是否必要   描述
        params.put("type", type);
        params.put("query", query);               //否     查询关键字
        params.put("poi", poi);           //否     经纬度(高德)。传参规则 poi=114.077146,22.545313
        params.put("distance", distance); //否     查询半径，单位：米 。只有有poi参数时有效。默认5000米
        params.put("size", size);         //否
        params.put("offset", offset);     //否
        request(TripAPI.QUERY_CAR, params, callback);
    }

    /**
     * 查询对应停车场的收费标准
     *
     * @param id
     */
    public void queryParkingToll(String id, ResultCallback<XMResult<List<ParkingSpotFeeStandardBean>>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要   描述
        params.put("id", id); //是        停车场id

        request(TripAPI.QUERY_PARKING_SPOT_FEE_STANDARD, params, callback);
    }

    /**
     * 对应停车场，对应时间段的停车费用预算
     *
     * @param id
     * @param startTime
     * @param period
     * @param unit
     * @param callback
     */
    public void parkingCostBuget(String id, String startTime, String period, String unit, ResultCallback<XMResult<ParkingFeeBudgetBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要   描述
        params.put("id", id);              //是       停车场id
        params.put("startTime", startTime);//否       格式 2018-12-26 00:00:00 如果不传默认为当前时间
        params.put("period", period);      //是       预计停车时间
        params.put("unit", unit);          //否       停车时间单位。 m：分钟 (默认);h：小时;d：天; M:月
        request(TripAPI.PARKING_FEE_BUDGET, params, callback);
    }

    /**
     * 获取停车场相关图片接口
     *
     * @param id
     * @param file
     */
    public void getParkingImg(String id, String file, ResultCallback<XMResult<String>> callback) {
        Map<String, Object> params = new HashMap<>();
        //是否必要   描述
        params.put("id", id);             //是       对应停车场的id
        params.put("file", file);         //是       对应图片的文件名
        request(TripAPI.GET_PIC, params, callback);
    }


    //---------------------------------------------------------------------新增接口↓↓↓------------------------------------------------------------


    public void getNearByHotel(String type,String city, String lat, String lon, String checkIn, String checkOut, int pageNo, int pageSize, ResultCallback<XMResult<HotelPageDataBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("city", city);
        params.put("lat", lat);
        params.put("lon", lon);
        params.put("checkIn", checkIn);
        params.put("checkOut", checkOut);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);

        request(TripAPI.QUERY_HOTEL, params, callback);
    }


    public void judgeHasRoom(String hotelId, String roomId, String ratePlanId, String checkIn, String checkOut, ResultCallback<XMResult<RatePlanStatusBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotelId", hotelId);
        params.put("roomId", roomId);
        params.put("ratePlanId", ratePlanId);
        params.put("checkIn", checkIn);
        params.put("checkOut", checkOut);
        request(TripAPI.BOOK_HOTEL_JUDGE_HAS_ROOM, params, callback);
    }

    public void judgeHotelRoomStatus(String hotelId, String checkIn, String checkOut, ResultCallback<XMResult<String>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotelId", hotelId);
        params.put("checkIn", checkIn);
        params.put("checkOut", checkOut);

        request(TripAPI.BOOK_HOTEL_JUDGE_ROOM_STATUS, params, callback);
    }

    public void getHotelPolicy(String hotelId, ResultCallback<XMResult<HotelPolicyBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotelId", hotelId);

        request(TripAPI.BOOK_HOTEL_BY_POLICY, params, callback);
    }


    //---------------------------------------------------------------------新增接口↑↑↑------------------------------------------------------------


    private <T> void request(String url, Map<String, Object> params, final ResultCallback<XMResult<T>> callback) {
        if (callback == null) {
            return;
        }

        XmHttp.getDefault().postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                KLog.e(TAG,"request() response.body()= "+response.body());
                XMResult<T> result = GsonHelper.fromJson(response.body(), type);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }

                if (result.isSuccess()) {
                    callback.onSuccess(result);

                } else {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {
                callback.onFailure(response.code(), response.message());
            }
        });
    }

    private <T> void postRequest(String url, Map<String, Object> params, final ResultCallback<XMResult<T>> callback) {
        if (callback == null) {
            return;
        }

        XmHttp.getDefault().postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                XMResult<T> result = GsonHelper.fromJson(response.body(), type);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }

                if (result.isSuccess()) {
                    callback.onSuccess(result);

                } else {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {
                callback.onFailure(response.code(), response.message());
            }
        });
    }

    private <T> void request(String url, Object object, final ResultCallback<XMResult<T>> callback) {
        if (callback == null) {
            return;
        }
        String json = GsonHelper.toJson(object);
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, Object> params = GsonHelper.fromJson(json, type);
        XmHttp.getDefault().postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                XMResult<T> result = GsonHelper.fromJson(response.body(), type);
                if (result == null) {
                    callback.onFailure(response.code(), "gson 转换bean异常");
                    return;
                }

                if (result.isSuccess()) {
                    callback.onSuccess(result);

                } else {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {
                callback.onFailure(response.code(), response.message());
            }
        });
    }
}
