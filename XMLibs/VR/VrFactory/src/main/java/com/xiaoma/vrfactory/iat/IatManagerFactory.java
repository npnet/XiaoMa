package com.xiaoma.vrfactory.iat;

import com.xiaoma.cariflytek.iat.VrAidlManager;
import com.xiaoma.openiflytek.iat.OpenIatManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.iat.IIatManager;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc:Iat factory
 */

public class IatManagerFactory {

    public static IIatManager getIatManager() {
        switch (VrConfig.mainVrSource) {

            case OpenIFlyTek:
                return OpenIatManager.getInstance();

            case LxIFlyTek:
                return VrAidlManager.getInstance();

            case Speech:
                return null;

            default:
                return OpenIatManager.getInstance();

        }
    }
}
