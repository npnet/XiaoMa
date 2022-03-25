package com.xiaoma.launcher.message.wechat.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.launcher.message.wechat.model.QrCodeBean;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;

public class UserBindVM  extends BaseViewModel{

    private MutableLiveData<XmResource<Integer>> bindStatus;
    private MutableLiveData<XmResource<Integer>> unbindStatus;
    private MutableLiveData<XmResource<QrCodeBean>> qrCode;
    private MutableLiveData<XmResource<Integer>> pushInfo;
    public UserBindVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<Integer>> getBindStatusInfo() {
        if (bindStatus == null) {
            bindStatus = new MutableLiveData<>();
        }
        return bindStatus;
    }

    public MutableLiveData<XmResource<Integer>> getUnBindStatusInfo() {
        if (unbindStatus == null) {
            unbindStatus = new MutableLiveData<>();
        }
        return unbindStatus;
    }

    public MutableLiveData<XmResource<QrCodeBean>> getOrCode() {
        if (qrCode == null) {
            qrCode = new MutableLiveData<>();
        }
        return qrCode;
    }

    public MutableLiveData<XmResource<Integer>> getPushInfo() {
        if (pushInfo == null) {
            pushInfo = new MutableLiveData<>();
        }
        return pushInfo;
    }

    //判断用户是否绑定过二维码
    public void fetchUserBindStatus(){
        RequestManager.getInstance().fetchUserBindStatus(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getBindStatusInfo().setValue(XmResource.response(result.getResultCode()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getBindStatusInfo().setValue(XmResource.response(code));
            }
        });
    }

    //获取用户绑定的二维码
    public void fetchBindQRCode(){
        RequestManager.getInstance().fetchBindQRCode(new ResultCallback<XMResult<QrCodeBean>>() {
            @Override
            public void onSuccess(XMResult<QrCodeBean> result) {
                getOrCode().setValue(XmResource.response(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    //用户解绑
    public void fetchUnBind(){
        RequestManager.getInstance().fetchUnBind(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getUnBindStatusInfo().setValue(XmResource.response(result.getResultCode()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getUnBindStatusInfo().setValue(XmResource.response(code));
            }
        });

    }

    /**
     * 车机推送消息方法
     */
    public void pushMessageToPhone(String uid,String lon,String lat,String poi,String location){
        RequestManager.getInstance().pushMessageToPhone("887227942571216896","114.217379","30.776258",getApplication().getString(R.string.temporary_poi),getApplication().getString(R.string.temporaty_location),new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getPushInfo().setValue(XmResource.response(result.getResultCode()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getPushInfo().setValue(XmResource.response(code));
            }
        });
    }
}
