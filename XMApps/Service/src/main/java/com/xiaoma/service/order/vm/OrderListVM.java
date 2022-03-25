package com.xiaoma.service.order.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.service.common.manager.RequestManager;
import com.xiaoma.service.order.model.OrderBean;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Thomas on 2018/11/9 0009
 */

public class OrderListVM extends AndroidViewModel {
    private MutableLiveData<XmResource<List<OrderBean>>> mLiveData;
    private MutableLiveData<XmResource<String>> mCancelOrderFeedback;

    public OrderListVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<OrderBean>>> getOrderDetailList() {
        if (mLiveData == null) {
            mLiveData = new MutableLiveData<>();
        }
        return mLiveData;
    }

    public MutableLiveData<XmResource<String>> getmCancelOrderFeedback() {
        if (mCancelOrderFeedback == null) {
            mCancelOrderFeedback = new MutableLiveData<>();
        }
        return mCancelOrderFeedback;
    }

    /**
     * 获取订单列表
     */
    public void fetchOrderDetailData(String vin) {
        RequestManager.getInstance().getAppointmentListByVIN(vin,new ResultCallback<XMResult<List<OrderBean>>>() {
            @Override
            public void onSuccess(XMResult<List<OrderBean>> result) {
                List<OrderBean> list = new ArrayList<>();
                if (!ListUtils.isEmpty(result.getData())){
                    list.addAll(listSort(result.getData()));
                }
                getOrderDetailList().setValue(XmResource.response(list));
            }

            @Override
            public void onFailure(int code, String msg) {
                getOrderDetailList().setValue(XmResource.<List<OrderBean>>error(code, msg));
            }
        });
    }

    private List<OrderBean> listSort(List<OrderBean> data) {
        Date d1;
        Date d2;
        OrderBean orderBean ;
        for(int i=0; i<data.size()-1; i++){
            for(int j=i+1; j<data.size();j++){
                d1 = new Date(data.get(i).getCreateDate());
                d2 =  new Date(data.get(j).getCreateDate());
                if(d1.before(d2)){
                    orderBean = data.get(i);
                    data.set(i, data.get(j));
                    data.set(j, orderBean);
                }
            }
        }
        return data;
    }

    /**
     * 取消服务订单
     *
     * @param vbillno
     */
    public void cancelAppointment(String vbillno) {
        RequestManager.getInstance().cancelAppointmentByBillNo(vbillno, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getmCancelOrderFeedback().setValue(XmResource.response(result));
            }

            @Override
            public void onFailure(int code, String msg) {
                getmCancelOrderFeedback().setValue(XmResource.<String>error(code, msg));
            }
        });
    }

}
