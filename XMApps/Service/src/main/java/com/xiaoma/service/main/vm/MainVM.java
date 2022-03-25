package com.xiaoma.service.main.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.service.common.manager.RequestManager;
import com.xiaoma.service.main.model.HotLineBean;
import com.xiaoma.service.main.model.MaintenancePeriodBean;

/**
 * Created by Thomas on 2018/11/9 0009
 */

public class MainVM extends AndroidViewModel {
    private String TAG = "uploadDriveDistance";
    private MutableLiveData<XmResource<String>> mUploadDriveDistance;
    private MutableLiveData<XmResource<MaintenancePeriodBean>> periodData;
    private MutableLiveData<XmResource<HotLineBean>> hotLineData;

    public MainVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<String>> getmUploadDriveDistance() {
        if (mUploadDriveDistance == null) {
            mUploadDriveDistance = new MutableLiveData<>();
        }
        return mUploadDriveDistance;
    }

    public MutableLiveData<XmResource<MaintenancePeriodBean>> getPeriodData() {
        if (periodData == null) {
            periodData = new MutableLiveData<>();
        }
        return periodData;
    }

    public MutableLiveData<XmResource<HotLineBean>> getHotLineData() {
        if (hotLineData == null) {
            hotLineData = new MutableLiveData<>();
        }
        return hotLineData;
    }

    /**
     * 用户公里数上报
     */
    public void uploadDriveDistance(double driveDistance) {
        Log.d("MainVM", "uploadDriveDistance:" + driveDistance);
        getPeriodData().setValue(XmResource.<MaintenancePeriodBean>loading());
        RequestManager.getInstance().uploadDriveDistance(driveDistance, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getmUploadDriveDistance().setValue(XmResource.response(result));
            }

            @Override
            public void onFailure(int code, String msg) {
                getmUploadDriveDistance().setValue(XmResource.<String>error(code, msg));
            }
        });
    }

    /**
     * 车服务获取首页信息(包含公里数、时间提醒)
     */
    public void fetchPeriodStatus(String vin) {
        getPeriodData().setValue(XmResource.<MaintenancePeriodBean>loading());
        RequestManager.getInstance().getFirstPageShow(vin,new ResultCallback<XMResult<MaintenancePeriodBean>>() {
            @Override
            public void onSuccess(XMResult<MaintenancePeriodBean> result) {
                getPeriodData().setValue(XmResource.response(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getPeriodData().setValue(XmResource.<MaintenancePeriodBean>error(code, msg));
            }
        });
    }

    /**
     * 获取ICALL和BCALL
     */
    public void fetchHotLineNumber() {

        RequestManager.getInstance().getServicePhone(new ResultCallback<XMResult<HotLineBean>>() {
            @Override
            public void onSuccess(XMResult<HotLineBean> result) {
                getHotLineData().setValue(XmResource.response(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getHotLineData().setValue(XmResource.<HotLineBean>error(code, msg));
            }
        });
    }
}
