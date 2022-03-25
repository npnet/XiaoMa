package com.xiaoma.launcher.travel.hotel.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.vm
 *  @file_name:      RecomHotelVM
 *  @author:         Rookie
 *  @create_time:    2019/1/3 17:17
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.hotel.response.HotelBean;
import com.xiaoma.trip.hotel.response.HotelPageDataBean;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.List;

public class RecomHotelVM extends BaseCollectVM {

    private MutableLiveData<XmResource<HotelPageDataBean>> hotelData;
    private MutableLiveData<XmResource<XMResult<String>>> roomStatus;


    public RecomHotelVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<HotelPageDataBean>> getHotelData() {
        if (hotelData == null) {
            hotelData = new MutableLiveData<>();
        }
        return hotelData;
    }

    public MutableLiveData<XmResource<XMResult<String>>> getRoomStatus() {
        if (roomStatus == null) {
            roomStatus = new MutableLiveData<>();
        }
        return roomStatus;
    }

    /**
     * 查询附近的酒店
     *
     * @param checkIn
     * @param checkOut
     * @param pageNo
     */
    public void fetchNearByHotel(String type,String city, String lat, String lon, String checkIn, String checkOut, int pageNo) {
        getHotelData().setValue(XmResource.<HotelPageDataBean>loading());

        RequestManager.getInstance().getNearByHotel(type,city, lat, lon, checkIn, checkOut, pageNo, LauncherConstants.PAGE_SIZE, new ResultCallback<XMResult<HotelPageDataBean>>() {
            @Override
            public void onSuccess(XMResult<HotelPageDataBean> result) {
                getHotelData().setValue(XmResource.success(result.getData()));
                Log.d("result2", "onSuccess " + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                getHotelData().setValue(XmResource.<HotelPageDataBean>failure(msg));
                Log.d("result2", "onFailure " + msg);
            }
        });
    }

    /**
     * 获取酒店房间库状态
     *
     * @param hotelId
     * @param checkIn
     * @param checkOut
     */
    public void fetchHotelRoomStatus(String hotelId, String checkIn, String checkOut) {
        getRoomStatus().setValue(XmResource.<XMResult<String>>loading());
        RequestManager.getInstance().judgeHotelRoomStatus(hotelId, checkIn, checkOut, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getRoomStatus().setValue(XmResource.success(result));
            }

            @Override
            public void onFailure(int code, String msg) {
                getRoomStatus().setValue(XmResource.<XMResult<String>>error(msg));
            }
        });

    }

    /**
     * 获取推荐酒店
     */
    public void fetchRecommendByHotel() {
        getHotelData().setValue(XmResource.<HotelPageDataBean>loading());

        List<HotelBean> hotelBeans = TPUtils.getList(AppHolder.getInstance().getAppContext(), LauncherConstants.RecommendExtras.RECOMMEND_HOTEL_LIST, HotelBean[].class);

        if (!ListUtils.isEmpty(hotelBeans)) {
            HotelPageDataBean hotelPageDataBean = new HotelPageDataBean();
            hotelPageDataBean.setHotelList(hotelBeans);
            getHotelData().setValue(XmResource.success(hotelPageDataBean));
        } else {
            getHotelData().setValue(XmResource.<HotelPageDataBean>error(AppHolder.getInstance().getAppContext().getString(R.string.error_msg)));
        }
    }


}
