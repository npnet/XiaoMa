package com.xiaoma.dualscreen.manager;

import android.view.View;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.dualscreen.constant.TabState;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ThreadUtils;
import com.xiaoma.utils.log.KLog;

/**
 * Created by ZYao.
 * Date ：2019/7/19 0019
 */
public class DualViewManager {
    private int curSkin = 0;
    private boolean mIsHigh = false;


    public void init() {
        curSkin = XmCarVendorExtensionManager.getInstance().getTheme();
        mIsHigh = XmCarConfigManager.isHighEndLcd();
    }

    public static DualViewManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final DualViewManager instance = new DualViewManager();
    }

    public TabState getCurrentType() {
        int currMode = XmCarVendorExtensionManager.getInstance().getInteractMode();
        switch (currMode) {
            case SDKConstants.VALUE.InteractMode_NAVI:
                return TabState.NAVI;
            case SDKConstants.VALUE.InteractMode_MEDIA:
                return TabState.MEDIA;
            case SDKConstants.VALUE.InteractMode_TEL:
                return TabState.PHONE;
            case SDKConstants.VALUE.InteractMode_INACTIVE:
            default:
                return TabState.ITINERARY;
        }
    }

    public void setCurSkin(int curSkin) {
        this.curSkin = curSkin;
    }

    public int getCurSkin() {
        return curSkin;
    }

    public boolean isHigh() {
        return mIsHigh;
    }
}
