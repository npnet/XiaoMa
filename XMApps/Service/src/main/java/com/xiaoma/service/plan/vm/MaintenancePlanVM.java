package com.xiaoma.service.plan.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.service.common.manager.RequestManager;
import com.xiaoma.service.plan.model.MaintenancePlanBean;

/**
 * Created by Thomas on 2018/11/9 0009
 */

public class MaintenancePlanVM extends AndroidViewModel {
    private MutableLiveData<XmResource<MaintenancePlanBean>> mutableLiveData;

    public MaintenancePlanVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<MaintenancePlanBean>> getMaintenancePlanList() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
        }
        return mutableLiveData;
    }

    /**
     * 获取养车计划列表
     */
    public void fetchMaintenancePlanList(String vin) {
        getMaintenancePlanList().setValue(XmResource.<MaintenancePlanBean>loading());
        RequestManager.getInstance().getUpkeepPlans(vin,new ResultCallback<XMResult<MaintenancePlanBean>>() {
            @Override
            public void onSuccess(XMResult<MaintenancePlanBean> result) {
                getMaintenancePlanList().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getMaintenancePlanList().setValue(XmResource.<MaintenancePlanBean>error(code, msg));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mutableLiveData = null;
    }
}
