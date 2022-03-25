package com.xiaoma.gdmap;


import com.amap.api.maps.UiSettings;
import com.xiaoma.mapadapter.view.UiSetting;

/**
 * Created by minxiwen on 2017/12/12 0012.
 */

public class GDUiSetting extends UiSetting {
    private UiSettings uiSettings;

    public GDUiSetting(UiSettings uiSettings) {
        this.uiSettings = uiSettings;
    }

    @Override
    public void setScaleControlsEnabled(boolean enabled) {
        uiSettings.setScaleControlsEnabled(enabled);
    }

    @Override
    public void setZoomControlsEnabled(boolean enabled) {
        uiSettings.setZoomControlsEnabled(enabled);
    }
}
