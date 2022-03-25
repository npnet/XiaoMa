package com.xiaoma.shop.business.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.business.model.HoloManInfo;
import com.xiaoma.shop.common.RequestManager;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 */
public class HologramDetailVm extends AndroidViewModel {


    public HologramDetailVm(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<XmResource<HoloManInfo>> mHoloManInfoLiveData;

    public void fetchHoloManInfo(long holoId) {
        RequestManager.requestHoloManInfo(holoId, new ResultCallback<XMResult<HoloManInfo>>() {
            @Override
            public void onSuccess(XMResult<HoloManInfo> result) {
                Log.d("Jir", "onSuccess: ");
                HoloManInfo data = result.getData();
                if (data != null) {
                    setHoloManInfoLiveData(XmResource.success(data));
                } else {
                    setHoloManInfoLiveData(XmResource.<HoloManInfo>failure("Null"));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                setHoloManInfoLiveData(XmResource.<HoloManInfo>error(code, msg));
            }
        });
    }

    public MutableLiveData<XmResource<HoloManInfo>> getHoloManInfoLiveData() {
        if (mHoloManInfoLiveData == null) {
            mHoloManInfoLiveData = new MutableLiveData<>();
        }
        return mHoloManInfoLiveData;
    }

    private void setHoloManInfoLiveData(XmResource<HoloManInfo> value) {
        getHoloManInfoLiveData().setValue(value);
    }
}
