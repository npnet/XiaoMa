package com.xiaoma.personal.order.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.order.model.OrderInfo;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/12 0012 8:57
 *       desc：order data
 * </pre>
 */
public class OrderVM extends BaseViewModel {


    public OrderVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<XmResource<OrderInfo>> getOrderInfoList(long userId, int orderType, int pageNum, int pageSize) {
        final MutableLiveData<XmResource<OrderInfo>> liveData = new MutableLiveData<>();
        RequestManager.queryMineOrderList(
                userId,
                orderType,
                pageNum,
                pageSize,
                new ResultCallback<XMResult<OrderInfo>>() {
                    @Override
                    public void onSuccess(XMResult<OrderInfo> result) {
                        if (result.isSuccess() && result.getData() != null) {
                            liveData.setValue(XmResource.success(result.getData()));
                        } else {
                            liveData.setValue(XmResource.<OrderInfo>failure(result.getResultMessage()));
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        liveData.setValue(XmResource.<OrderInfo>failure(msg));
                    }
                });

        return liveData;
    }


    public MutableLiveData<XmResource<OrderInfo.Order>> getOrderDetailInfo(long orderId) {
        final MutableLiveData<XmResource<OrderInfo.Order>> liveData = new MutableLiveData<>();
        RequestManager.getMineOrderDetail(orderId, new ResultCallback<XMResult<OrderInfo.Order>>() {
            @Override
            public void onSuccess(XMResult<OrderInfo.Order> result) {
                if (result.isSuccess() && result.getData() != null) {
                    liveData.setValue(XmResource.success(result.getData()));
                } else {
                    liveData.setValue(XmResource.<OrderInfo.Order>failure(result.getResultMessage()));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                liveData.setValue(XmResource.<OrderInfo.Order>failure(msg));
            }
        });
        return liveData;
    }


}
