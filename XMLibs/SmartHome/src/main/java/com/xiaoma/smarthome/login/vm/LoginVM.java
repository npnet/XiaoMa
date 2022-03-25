package com.xiaoma.smarthome.login.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.login.vm
 *  @file_name:      LoginVM
 *  @author:         Rookie
 *  @create_time:    2019/4/23 10:30
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.smarthome.common.manager.RequestManager;
import com.xiaoma.smarthome.login.model.CMUserInfo;
import com.xiaoma.smarthome.login.model.CloudMIBean;

public class LoginVM extends BaseViewModel {

    private MutableLiveData<XmResource<String>> cmCodeBean;
    private MutableLiveData<XmResource<String>> cmLoginState;
    private MutableLiveData<XmResource<CloudMIBean>> cmBean;
    private MutableLiveData<XmResource<CMUserInfo>> cmUserInfoData;
    private MutableLiveData<XmResource<String>> xmLogout;

    public LoginVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<String>> getCmLoginState() {
        if (cmLoginState == null) {
            cmLoginState = new MutableLiveData<>();
        }
        return cmLoginState;
    }

    public MutableLiveData<XmResource<String>> getLoginCodeData() {
        if (cmCodeBean == null) {
            cmCodeBean = new MutableLiveData<>();
        }
        return cmCodeBean;
    }

    public MutableLiveData<XmResource<CloudMIBean>> getCloudMIBean() {
        if (cmBean == null) {
            cmBean = new MutableLiveData<>();
        }
        return cmBean;
    }

    public MutableLiveData<XmResource<CMUserInfo>> getCMUserInfo() {
        if (cmUserInfoData == null) {
            cmUserInfoData = new MutableLiveData<>();
        }
        return cmUserInfoData;
    }

    public MutableLiveData<XmResource<String>> getXmLogout() {
        if (xmLogout == null) {
            xmLogout = new MutableLiveData<>();
        }
        return xmLogout;
    }

    public void fetchCMLoginCode() {
        getLoginCodeData().setValue(XmResource.loading());
        RequestManager.getInstance().getCMLoginCode(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getLoginCodeData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getLoginCodeData().setValue(XmResource.<String>error(msg));
            }
        });
    }

    public void fetchCMLoginBean() {
        RequestManager.getInstance().queryCMLoginResult(new ResultCallback<XMResult<CloudMIBean>>() {
            @Override
            public void onSuccess(XMResult<CloudMIBean> result) {
                getCloudMIBean().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCloudMIBean().setValue(XmResource.<CloudMIBean>error(msg));
            }
        });
    }

    public void queryCMUserInfo() {
        RequestManager.getInstance().fetchUserInfo(new ResultCallback<XMResult<CMUserInfo>>() {
            @Override
            public void onSuccess(XMResult<CMUserInfo> result) {
                getCMUserInfo().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCMUserInfo().setValue(XmResource.<CMUserInfo>error(code, msg));
            }
        });
    }

    public void fetchCMLoginState() {
        RequestManager.getInstance().getCMLoginState(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getCmLoginState().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCmLoginState().setValue(XmResource.<String>error(msg));
            }
        });
    }

    public void logout(){
        RequestManager.getInstance().logoutCM(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getXmLogout().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getXmLogout().setValue(XmResource.<String>error(msg));
            }
        });
    }
}
