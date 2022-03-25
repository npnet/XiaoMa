package com.xiaoma.smarthome.common.manager;

import android.content.Context;

public class HuaWeiSmartHomeManager implements ISmartHomeManager {

    private static HuaWeiSmartHomeManager INSTANCE;

    private HuaWeiSmartHomeManager() {

    }

    public static HuaWeiSmartHomeManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HuaWeiSmartHomeManager();
        }
        return INSTANCE;
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public boolean isBind() {
        return false;
    }

    @Override
    public void login(String userName, String passWord, OnSmartHomeLoginCallback callback) {

    }

    @Override
    public void loginOut() {

    }

    @Override
    public void getAllDeviceList(OnGetAllDevicesCallback callback) {

    }

    @Override
    public void sceneControl(String sceneName) {

    }

    @Override
    public void setLoginOutListener(OnSmartHomeLoginOutCallback callback) {

    }

    @Override
    public void release() {

    }
}
