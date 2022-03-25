package com.xiaoma.launcher.main.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.main.vm
 *  @file_name:      ControlVM
 *  @author:         Rookie
 *  @create_time:    2019/3/8 11:01
 *  @description：   TODO             */

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.launcher.main.manager.CarSettingManager;

public class ControlVM extends BaseViewModel {

    public ControlVM(@NonNull Application application) {
        super(application);
    }

    public void setBrightness(int value) {
        if (value <= 0) {
            value = 1;
        }
        CarSettingManager.getInstance().setBrightness(value);
    }

    public int getBrightness() {
        return CarSettingManager.getInstance().getBrightness();
    }

    public void setVolume(int value) {
        CarSettingManager.getInstance().setMediaVolume(value);
    }

    public int getVolume() {
        return CarSettingManager.getInstance().getMediaVolume();
    }

    public void toggleBt() {
        CarSettingManager.getInstance().toggleBt();
    }

    public boolean getBtState() {
        return CarSettingManager.getInstance().getBtState();
    }

    public int toggleInternet(boolean isOpenData) {
        return CarSettingManager.getInstance().toggleInternet(isOpenData);
    }

    public boolean getInterState() {
        return CarSettingManager.getInstance().getInternetState();
    }

    public void getWorkPattern() {
        CarSettingManager.getInstance().getWorkPattern();
    }

    /**
     * 热点开关
     */
    public int toggleHotspot(boolean isOpenHotSpot) {
        return CarSettingManager.getInstance().toggleHotspot(isOpenHotSpot);
    }

    public int toggleWifi(boolean isOpenWifi) {
        return CarSettingManager.getInstance().toggleWifi(isOpenWifi);
    }

    public void getInternet() {
        CarSettingManager.getInstance().getInternet();
    }

    public boolean getWifiState() {
        return CarSettingManager.getInstance().getWifiState();
    }

    public void turnOff() {
        CarSettingManager.getInstance().turnOff();
    }
}
