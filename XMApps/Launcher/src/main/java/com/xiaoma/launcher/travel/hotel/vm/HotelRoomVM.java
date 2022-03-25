package com.xiaoma.launcher.travel.hotel.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.vm
 *  @file_name:      HotelRoomVM
 *  @author:         Rookie
 *  @create_time:    2019/1/4 16:55
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.hotel.response.BookOrderResult;
import com.xiaoma.trip.hotel.response.RatePlanStatusBean;
import com.xiaoma.trip.hotel.response.RoomRatePlanBean;
import com.xiaoma.trip.orders.response.OrdersBean;
import com.xiaoma.trip.orders.response.OrdersPerateBean;

import java.util.List;

public class HotelRoomVM extends BaseViewModel {

    private final String COUNTRYID = "0001";
    private final String CURRENCY = "CNY";
    private final String GUESTTYPE = "013002";
    private String bookName = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId()).getName();

    private MutableLiveData<XmResource<List<RoomRatePlanBean>>> roomData;
    private MutableLiveData<XmResource<BookOrderResult>> bookResult;
    private MutableLiveData<XmResource<OrdersBean>> ordersData;
    private MutableLiveData<XmResource<XMResult>> cancelResult;
    private MutableLiveData<XmResource<RatePlanStatusBean>> ratePlanStatusData;

    public HotelRoomVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<RoomRatePlanBean>>> getRoomlData() {
        if (roomData == null) {
            roomData = new MutableLiveData<>();
        }
        return roomData;
    }

    public MutableLiveData<XmResource<BookOrderResult>> getBookResult() {
        if (bookResult == null) {
            bookResult = new MutableLiveData<>();
        }
        return bookResult;
    }

    public MutableLiveData<XmResource<OrdersBean>> getOrdersData() {
        if (ordersData == null) {
            ordersData = new MutableLiveData<>();
        }
        return ordersData;
    }

    public MutableLiveData<XmResource<XMResult>> getCancelResult() {
        if (cancelResult == null) {
            cancelResult = new MutableLiveData<>();
        }
        return cancelResult;
    }

    public MutableLiveData<XmResource<RatePlanStatusBean>> getRatePlanStatusData() {
        if (ratePlanStatusData == null) {
            ratePlanStatusData = new MutableLiveData<>();
        }
        return ratePlanStatusData;
    }

    public void fetchRooms(String hotelId, String checkIn, String checkOut) {
        getRoomlData().setValue(XmResource.<List<RoomRatePlanBean>>loading());

        RequestManager.getInstance().getHotelsRoomratePlans(checkIn, checkOut, hotelId, new ResultCallback<XMResult<List<RoomRatePlanBean>>>() {
            @Override
            public void onSuccess(XMResult<List<RoomRatePlanBean>> result) {
                getRoomlData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getRoomlData().setValue(XmResource.<List<RoomRatePlanBean>>error(msg));
            }
        });


    }

    /**
     * 预定房间
     *
     * @param hotelId
     * @param hotelName
     * @param roomId
     * @param rateplanId
     * @param checkIn
     * @param checkOut
     * @param orderAmount
     * @param roomCount
     * @param guestName
     * @param bookPhone
     */
    public void bookRoom(String hotelId, String hotelName, String roomId, String rateplanId, String checkIn, String checkOut, String orderAmount, int roomCount, String guestName, String guestPhone, String bookPhone, String address, String lat, String lon, String roomType, String roomMsg, boolean canCancel, String lastCancelDate,String storePhone,String imageUrl) {
        getBookResult().setValue(XmResource.<BookOrderResult>loading());
        RequestManager.getInstance().bookHotel(hotelId, hotelName, roomId, rateplanId, checkIn, checkOut, orderAmount, roomCount, guestName, guestPhone, bookName, bookPhone, CURRENCY, GUESTTYPE, address, lat, lon, roomType, roomMsg, canCancel, lastCancelDate, storePhone,imageUrl,new ResultCallback<XMResult<BookOrderResult>>() {
            @Override
            public void onSuccess(XMResult<BookOrderResult> result) {
                getBookResult().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getBookResult().setValue(XmResource.<BookOrderResult>error(msg));
            }
        });
    }

    public void fetchOrder(String orderId) {

        RequestManager.getInstance().queryOrder(orderId, new ResultCallback<XMResult<OrdersBean>>() {
            @Override
            public void onSuccess(XMResult<OrdersBean> result) {
                getOrdersData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getBookResult().setValue(XmResource.<BookOrderResult>error(msg));
            }
        });
    }

    public void cancelOrder(long orderId) {
        getCancelResult().setValue(XmResource.<XMResult>loading());
        RequestManager.getInstance().cancelOrders(orderId, new ResultCallback<XMResult<OrdersPerateBean>>() {
            @Override
            public void onSuccess(XMResult result) {
                getCancelResult().setValue(XmResource.success(result));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCancelResult().setValue(XmResource.<XMResult>error(msg));
            }
        });
    }

    /**
     * 判断是否可以增加房间
     *
     * @param hotelId
     * @param roomId
     * @param ratePlanId
     * @param checkIn
     * @param checkOut
     */
    public void queryHasRoom(String hotelId, String roomId, String ratePlanId, String checkIn, String checkOut) {

        getRatePlanStatusData().setValue(XmResource.<RatePlanStatusBean>loading());
        RequestManager.getInstance().judgeHasRoom(hotelId, roomId, ratePlanId, checkIn, checkOut, new ResultCallback<XMResult<RatePlanStatusBean>>() {
            @Override
            public void onSuccess(XMResult<RatePlanStatusBean> result) {
                getRatePlanStatusData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getRatePlanStatusData().setValue(XmResource.<RatePlanStatusBean>error(msg));
            }
        });

    }

}
