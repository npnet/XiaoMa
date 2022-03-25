package com.xiaoma.travel.main.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.travel.R;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.hotel.request.RequestBookHotelParm;
import com.xiaoma.trip.hotel.request.RequestHotelsParm;
import com.xiaoma.trip.hotel.request.RequestRoomRatePlansParm;
import com.xiaoma.trip.hotel.response.BookOrderResult;
import com.xiaoma.trip.hotel.response.HotelPageDataBean;
import com.xiaoma.trip.hotel.response.RoomRatePlanBean;
import com.xiaoma.trip.orders.response.OrdersBean;
import com.xiaoma.trip.orders.response.OrdersPerateBean;

import java.util.List;

/**
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class HotelActivity extends BaseActivity {
    public static final String TAG = "HotelActivity";
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        mImageView = findViewById(R.id.qr_code);
    }

    public void getHotels(View view) {
        RequestHotelsParm requestHotelsParm = new RequestHotelsParm();
        requestHotelsParm.countryId = "0001";
        requestHotelsParm.province = "广东省";
        requestHotelsParm.city = "深圳";
        requestHotelsParm.hotelId = "";
        requestHotelsParm.checkIn = "2018-12-18";
        requestHotelsParm.checkOut = "2018-12-19";
        requestHotelsParm.pageNo = 1;
        requestHotelsParm.pageCount = 2;

        RequestManager.getInstance().getHotelsBaseInfo(requestHotelsParm, new ResultCallback<XMResult<HotelPageDataBean>>() {
            @Override
            public void onSuccess(XMResult<HotelPageDataBean> result) {
                Log.d("result", "onSuccess:" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("result", "onFailure:" + msg);
            }
        });
    }

    public void getRoomRatePlans(View view) {
        RequestRoomRatePlansParm requestHotelsParm = new RequestRoomRatePlansParm();
        requestHotelsParm.countryId = "0001";
        requestHotelsParm.province = "广东省";
        requestHotelsParm.city = "深圳市";
        requestHotelsParm.checkIn = "2019-1-3";
        requestHotelsParm.checkOut = "2019-1-4";
        requestHotelsParm.hotelId = "5948";
        requestHotelsParm.roomId = "";
        requestHotelsParm.ratePlanId = "";
        requestHotelsParm.pageNo = 6;
        requestHotelsParm.pageCount = 10;

        RequestManager.getInstance().getHotelsRoomratePlans(requestHotelsParm, new ResultCallback<XMResult< List<RoomRatePlanBean>>>() {

            @Override
            public void onSuccess(XMResult<List<RoomRatePlanBean>> result) {
                Log.d("result", "result:" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    public void book(View view) {
        RequestBookHotelParm bookHotelParm = new RequestBookHotelParm();

        bookHotelParm.hotelId =  "5948";
        bookHotelParm.hotelName =  "长春前进大厦";
        bookHotelParm.roomId = "40118";
        bookHotelParm.rateplanId = "1185";
        bookHotelParm.checkIn = "2018-12-22";
        bookHotelParm.checkOut = "2018-12-23" ;
        bookHotelParm.roomCount = 1;
        bookHotelParm.currency = "CNY";
        bookHotelParm.orderAmount = "220";
        bookHotelParm.bookName = "wuzongwei";
        bookHotelParm.bookPhone = "";
        bookHotelParm.guestName = "wuzongwei";
        bookHotelParm.guestPhone = "15893123812";
        bookHotelParm.guestFax = "" ;
        bookHotelParm.guestType = "013002";
        bookHotelParm.cardTypeId = "" ;
        bookHotelParm.cardNum = "" ;
        bookHotelParm.specialRemark = "" ;
        bookHotelParm.reserve1 = "";
        bookHotelParm.reserve2 = "";
        bookHotelParm.customerOrderId = "";

        RequestManager.getInstance().bookHotel(bookHotelParm, new ResultCallback<XMResult<BookOrderResult>>() {

            @Override
            public void onSuccess(XMResult<BookOrderResult> result) {
                Log.d("result", "result: onSuccess" + result.getData());
                Glide.with(HotelActivity.this).load(result.getData().getQrCode()).into(mImageView);
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("result", "result: onFailure" + msg);
            }
        });
    }


    public void ordersQuery(View view) {
        RequestManager.getInstance().queryOrders( new ResultCallback<XMResult<List<OrdersBean>>>() {
            @Override
            public void onSuccess(XMResult<List<OrdersBean>> result) {
                Log.d("result", "result: onSuccess" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("result", "result: onFailure" + msg);
            }
        });
    }

    public void ordersCancel(View view) {

        RequestManager.getInstance().cancelOrders(1,0, new ResultCallback<XMResult<OrdersPerateBean>>() {
            @Override
            public void onSuccess(XMResult<OrdersPerateBean> result) {
                Log.d("result", "result: onSuccess" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("result", "result: onFailure" + msg);
            }
        });
    }

    public void ordersDelete(View view) {

        RequestManager.getInstance().deleteOrders(1,0, new ResultCallback<XMResult<OrdersPerateBean>>() {
            @Override
            public void onSuccess(XMResult<OrdersPerateBean> result) {
                Log.d("result", "result: onSuccess" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("result", "result: onFailure" + msg);
            }
        });

    }


}
