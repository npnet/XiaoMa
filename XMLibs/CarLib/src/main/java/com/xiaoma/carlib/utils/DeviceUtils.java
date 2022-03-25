package com.xiaoma.carlib.utils;

import com.xiaoma.carlib.manager.XmCarConfigManager;

/**
 * 此类已过时,为了兼容业务代码,暂时保留了,新的工具类请参见{@link XmCarConfigManager}
 */
@Deprecated
public class DeviceUtils {
    /**
     * @deprecated 废弃方法
     * 判断高低配方法请调用{@link XmCarConfigManager#isHighEndLcd()}
     */
    @Deprecated
    public static boolean isPro() {
        return XmCarConfigManager.isHighEndLcd();
    }
}
