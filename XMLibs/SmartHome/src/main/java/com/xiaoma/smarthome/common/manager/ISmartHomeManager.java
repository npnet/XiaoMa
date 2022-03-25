package com.xiaoma.smarthome.common.manager;

import android.content.Context;

import com.xiaoma.smarthome.common.model.LocalDeviceInfo;

import java.util.List;

/**
 * Created by dc on 18-7-7.
 */

public interface ISmartHomeManager {

    interface OnSmartHomeLoginCallback {

        void onLoginSuccess();

        void onLoginFail(String errorMsg);
    }

    interface OnSmartHomeLoginOutCallback {

        void onLoginOut();

    }

    interface OnGetAllDevicesCallback {

        void onSuccess(List<LocalDeviceInfo> deviceInfos);

        void onFail(int errorCode, String errorMsg);
    }

    void init(Context context);

    boolean isLogin();

    boolean isBind();

    void login(String userName, String passWord, OnSmartHomeLoginCallback callback);

    void loginOut();

    void getAllDeviceList(OnGetAllDevicesCallback callback);

    void sceneControl(String sceneName);

    void setLoginOutListener(OnSmartHomeLoginOutCallback callback);

    void release();
}
