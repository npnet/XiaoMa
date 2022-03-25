package com.xiaoma.launcher.main.vm;

import com.xiaoma.launcher.main.manager.CarSettingManager;

public class ControlManager  {

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
