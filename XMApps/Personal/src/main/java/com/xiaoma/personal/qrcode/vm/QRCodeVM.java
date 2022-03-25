package com.xiaoma.personal.qrcode.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.qrcode.model.HologramQRCode;
import com.xiaoma.personal.qrcode.model.KeyQRCode;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/21 0021 15:57
 *   desc:
 * </pre>
 */
public class QRCodeVM extends BaseViewModel {


    public QRCodeVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<XmResource<KeyQRCode>> getNumberKeyQRCode() {
        MutableLiveData<XmResource<KeyQRCode>> mutableLiveData = new MutableLiveData<>();
        RequestManager.getNumberKeyQRCode(new ResultCallback<XMResult<KeyQRCode>>() {
            @Override
            public void onSuccess(XMResult<KeyQRCode> result) {
                mutableLiveData.setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                mutableLiveData.setValue(XmResource.failure(msg));
            }
        });

        return mutableLiveData;
    }


    public MutableLiveData<XmResource<KeyQRCode>> getRemoteControllerQRCode() {
        MutableLiveData<XmResource<KeyQRCode>> mutableLiveData = new MutableLiveData<>();
        RequestManager.getRemoteControllerQRCode(new ResultCallback<XMResult<KeyQRCode>>() {
            @Override
            public void onSuccess(XMResult<KeyQRCode> result) {
                mutableLiveData.setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                mutableLiveData.setValue(XmResource.failure(msg));
            }
        });

        return mutableLiveData;
    }


    public MutableLiveData<XmResource<HologramQRCode>> getHologramQRCode() {
        MutableLiveData<XmResource<HologramQRCode>> mutableLiveData = new MutableLiveData<>();
        RequestManager.getHologramQRCode(new ResultCallback<XMResult<HologramQRCode>>() {
            @Override
            public void onSuccess(XMResult<HologramQRCode> result) {
                mutableLiveData.setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                mutableLiveData.setValue(XmResource.failure(msg));
            }
        });

        return mutableLiveData;
    }


}
