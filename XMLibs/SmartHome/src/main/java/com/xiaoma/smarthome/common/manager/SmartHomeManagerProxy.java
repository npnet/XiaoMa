package com.xiaoma.smarthome.common.manager;

import android.content.Context;

/**
 * Created by dc on 18-1-24.
 */

public class SmartHomeManagerProxy implements ISmartHomeManager {

    private ISmartHomeManager mSmartHomeManager;

    public SmartHomeManagerProxy(ISmartHomeManager manager) {

        this.mSmartHomeManager = manager;
    }

    @Override
    public void init(Context context) {
        mSmartHomeManager.init(context);
    }

    @Override
    public boolean isLogin() {
        return mSmartHomeManager.isLogin();
    }

    @Override
    public boolean isBind() {
        return mSmartHomeManager.isBind();
    }

    @Override
    public void login(String userName, String passWord, OnSmartHomeLoginCallback callback) {
        mSmartHomeManager.login(userName, passWord, callback);
    }

    @Override
    public void loginOut() {
        mSmartHomeManager.loginOut();
    }

    @Override
    public void getAllDeviceList(OnGetAllDevicesCallback callback) {
        mSmartHomeManager.getAllDeviceList(callback);
    }

    @Override
    public void sceneControl(String sceneName) {
        mSmartHomeManager.sceneControl(sceneName);
    }

    @Override
    public void setLoginOutListener(OnSmartHomeLoginOutCallback callback) {
        mSmartHomeManager.setLoginOutListener(callback);
    }

    @Override
    public void release() {
        if (mSmartHomeManager != null) {
            mSmartHomeManager.release();
        }
    }
}
