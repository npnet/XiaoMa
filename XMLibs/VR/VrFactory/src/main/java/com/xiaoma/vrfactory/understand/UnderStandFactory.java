package com.xiaoma.vrfactory.understand;


import com.xiaoma.openiflytek.understand.OpenUnderStandManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.understand.BaseUnderStandManager;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/20
 * Desc:choose the semantic engine
 */

public class UnderStandFactory {

    public static BaseUnderStandManager getUnderStandManager() {
        switch (VrConfig.mainVrSource) {

            case OpenIFlyTek:
                return OpenUnderStandManager.getInstance();

            case LxIFlyTek:
                return OpenUnderStandManager.getInstance();

            case Speech:
                return null;

            default:
                return OpenUnderStandManager.getInstance();

        }
    }
}
