package com.xiaoma.trip.api;

import com.xiaoma.config.ConfigManager;

/**
 * Created by zhushi.
 * Date: 2018/12/3
 */
public interface TripAPI {
    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();

    //---------------酒店相关-------------------
    //查询酒店的基础信息
    String GET_HOTELS_BY_CONDITION = BASE_URL + "hotel/searchHotels.action";

    //获取酒店下房间库存信息
    String GET_HOTELS_ROOMRATE_PLANS = BASE_URL + "hotel/searchRoomRatePlans.action";

    //创建酒店订单
    String BOOK_HOTEL_ORDER = BASE_URL + "hotel/booking.action";

    //判断是否还有房间支持增加
    String BOOK_HOTEL_JUDGE_HAS_ROOM = BASE_URL + "hotel/judgeSingleRatePlanStatus.action";

    //判断酒店房间是否有库存
    String BOOK_HOTEL_JUDGE_ROOM_STATUS = BASE_URL + "hotel/judgeHotelRoomStatus.action";

    //获取酒店政策
    String BOOK_HOTEL_BY_POLICY = BASE_URL + "hotel/getPolicyByHotelId.action";

    //---------------电影相关-------------------
    //查询影院列表
    String QUERY_CINEMAS = BASE_URL + "queryCinemas";
    //查询附近电影院
    String QUERY_NEARBY_CINEMAS = BASE_URL + "queryNearCinemas";

    //查询影片列表
    String QUERY_FILMS = BASE_URL + "queryFilms";

    //查询影院排期
    String QUERY_CINEMA_SHOW = BASE_URL + "queryCinemaShow";

    //查询影片排期
    String QUERY_FILMS_SHOW = BASE_URL + "queryShows";

    //查询影片详情
    String QUERY_FILM_DETAIL = BASE_URL + "queryFilmDetail";

    //查询座位信息
    String QUERY_HALLSEATS_INFO = BASE_URL + "queryHallSeatsInfo";

    //锁座下单
    String LOCK_SEAT = BASE_URL + "lockSeat";

    //我方订单详情
    String USER_APP_ORDER_DETAIL = BASE_URL + "userAppOrderDetail";
    //三方订单详情
    String OTHER_ORDER_DETAIL = BASE_URL + "userOrderDetail";


    //---------------位置相关-------------------


    //查询城市
    String GET_CITY_INFO = BASE_URL + "besturnGoing/getCityInfo";

    //查询城区
    String QUERY_DISTRICT = BASE_URL + "besturnGoing/queryDistrictByCityid";

    //---------------分类信息-------------------

    //查询分类
    String QUERY_CATEGORY = BASE_URL + "besturnGoing/queryCategoryByCityid";

    //查找店铺
    String CONDITION_SEARCH_STORE = BASE_URL + "besturnGoing/conditionSearchStore";

    //查询店铺信息
    String QUERY_STORE_INFO = BASE_URL + "besturnGoing/queryStoreInfoById";

    //收藏影院，酒店，美食，景点
    String COLLECT_ITEM = BASE_URL + "collectionStatus";

    //获取已收藏的
    String QUERY_COLLECT_LIST = BASE_URL + "getAllCollections";

    //---------------订单相关-------------------

    //订单列表
    String QUERY_ORDERS = BASE_URL + "orders/search";

    //查询单一订单
    String QUERY_ONE_ORDER = BASE_URL + "orders/findOrderById";

    //订单取消
    String QUERY_CANCEL = BASE_URL + "orders/cancel";

    //订单删除
    String QUERY_DELETE = BASE_URL + "orders/delete";

    //已完成电影订单
    String COMPLETE_ORDER = BASE_URL + "orders/getCompleteOrder";


    //-------------------停车场相关-------------------

    //获取停车场信息
    String QUERY_PARKING_INFO = BASE_URL + "parking/queryParkingInfo.action";

    //查询对应停车场的收费标准
    String QUERY_PARKING_SPOT_FEE_STANDARD = BASE_URL + "parking/queryParkingSpotFeeStandard.action";

    //对应停车场，对应时间段的停车费用预算
    String PARKING_FEE_BUDGET = BASE_URL + "parking/parkingFeeBudget.action";

    //获取停车场相关图片接口
    String GET_PIC = BASE_URL + "parking/getPic.action";


    //---------------------------------------------------------------------新增接口↓↓↓------------------------------------------------------------

    //获取附近的酒店
    String GET_NEAR_BY_HOTEL = BASE_URL + "hotel/searchNearBy.action";


    //---------------------------------------------------------------------新增接口↑↑↑------------------------------------------------------------

    /**
     * 修改接口
     * 时间：2019年7月17日17:02:07
     */
    //停车场、加油站接口
    String QUERY_CAR=BASE_URL+"extendCategory/queryCar";
    //美食景点接口
    String QUERY_FOOD_ATTRACTION=BASE_URL+"extendCategory/queryFoodOrAttraction";
    //电影接口
    String QUERY_FILM=BASE_URL+"extendCategory/queryFilm";
    //酒店接口
    String QUERY_HOTEL=BASE_URL+"extendCategory/queryHotel";
}

