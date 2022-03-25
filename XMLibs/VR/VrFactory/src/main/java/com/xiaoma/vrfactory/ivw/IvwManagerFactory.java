package com.xiaoma.vrfactory.ivw;

import com.xiaoma.cariflytek.iat.VrAidlManager;
import com.xiaoma.openiflytek.ivw.OpenIvwManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.ivw.IIvwManager;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc:Ivw factory
 */

public class IvwManagerFactory {

    public static IIvwManager getIvwManager() {
        switch (VrConfig.mainVrSource) {

            case OpenIFlyTek:
                return OpenIvwManager.getInstance();

            case LxIFlyTek:
                return VrAidlManager.getInstance();

            case Speech:
                return null;

            default:
                return OpenIvwManager.getInstance();

        }
    }
}
