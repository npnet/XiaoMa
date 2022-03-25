package com.xiaoma.launcher.travel.order.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.order.vm
 *  @file_name:      MovieOrdersVM
 *  @author:         Rookie
 *  @create_time:    2019/1/28 14:51
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.movie.response.CompleteOrderBean;

import java.util.List;

public class MovieOrdersVM extends BaseViewModel {

    private static final String FILM_TYPE="Film";
    private int cinemaMaxPageNum = -1;
    private boolean isCinemaLoadEnd = false;

    private MutableLiveData<XmResource<List<CompleteOrderBean.ListBean>>> ordersData;

    public MovieOrdersVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<CompleteOrderBean.ListBean>>> getMovieData() {
        if (ordersData == null) {
            ordersData = new MutableLiveData<>();
        }
        return ordersData;
    }

    public boolean isCineMaLoadEnd(){
        return isCinemaLoadEnd;
    }

    public void fetchMovieOrders(String type,final int pageNum, int pageSize) {

        getMovieData().setValue(XmResource.<List<CompleteOrderBean.ListBean>>loading());
        RequestManager.getInstance().completeOrder(type,FILM_TYPE,pageNum,pageSize,new ResultCallback<XMResult<CompleteOrderBean>>() {
            @Override
            public void onSuccess(XMResult<CompleteOrderBean> result) {
                cinemaMaxPageNum = result.getData().getPageInfo().getTotalPage();
                if (pageNum >= cinemaMaxPageNum && cinemaMaxPageNum != -1){
                    isCinemaLoadEnd = true;
                }
                List<CompleteOrderBean.ListBean> data = result.getData().getAppOrders();
                getMovieData().setValue(XmResource.success(data));
            }

            @Override
            public void onFailure(int code, String msg) {
                getMovieData().setValue(XmResource.<List<CompleteOrderBean.ListBean>>failure(msg));
            }
        });

    }
}
