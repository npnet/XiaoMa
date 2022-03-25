package com.xiaoma.bdmap;

import com.baidu.mapapi.map.UiSettings;
import com.xiaoma.mapadapter.view.UiSetting;

/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDUiSetting extends UiSetting {
    private UiSettings uiSettings;

    public BDUiSetting(UiSettings uiSettings) {
        this.uiSettings = uiSettings;
    }

    @Override
    public void setScaleControlsEnabled(boolean enabled) {

    }

    @Override
    public void setZoomControlsEnabled(boolean enabled) {
        uiSettings.setZoomGesturesEnabled(enabled);
    }
}
