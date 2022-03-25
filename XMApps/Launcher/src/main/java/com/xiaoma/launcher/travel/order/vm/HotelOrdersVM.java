package com.xiaoma.launcher.travel.order.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.order.vm
 *  @file_name:      HotelOrdersVM
 *  @author:         Rookie
 *  @create_time:    2019/1/28 14:51
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.movie.response.CompleteOrderBean;

public class HotelOrdersVM extends BaseViewModel {

    private static final String HOTEL_TYPE = "Hotel";

    private MutableLiveData<XmResource<CompleteOrderBean>> completeHotelOrders;

    public HotelOrdersVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<CompleteOrderBean>> getCompleteHotelOrders() {
        if (completeHotelOrders == null) {
            completeHotelOrders = new MutableLiveData<>();
        }
        return completeHotelOrders;
    }


    public void queryCompleteHotelOrders(String type,int pageNum) {
        getCompleteHotelOrders().setValue(XmResource.<CompleteOrderBean>loading());
        RequestManager.getInstance().completeOrder(type,HOTEL_TYPE, pageNum, LauncherConstants.PAGE_SIZE, new ResultCallback<XMResult<CompleteOrderBean>>() {
            @Override
            public void onSuccess(XMResult<CompleteOrderBean> result) {
                getCompleteHotelOrders().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCompleteHotelOrders().setValue(XmResource.<CompleteOrderBean>error(code,msg));
            }
        });

    }


}
