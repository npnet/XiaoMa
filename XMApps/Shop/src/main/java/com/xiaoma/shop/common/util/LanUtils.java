package com.xiaoma.shop.common.util;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.component.AppHolder;
import com.xiaoma.shop.BuildConfig;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/08/05
 * @Describe:
 */
public class LanUtils {

    public static boolean isEnglish() {
        if (BuildConfig.BUILD_PLATFORM == "PAD") {
            return IsEnglishFromPrimeval();
        }
        return XmCarFactory.getCarVendorExtensionManager().getLanguage() == SDKConstants.LANGUAGE_EN;
    }

    private static boolean IsEnglishFromPrimeval() {
        return UK() || US();
    }

    private static boolean US() {
        return AppHolder.getInstance().getAppContext().getResources().getConfiguration().locale.getCountry().equals("US");
    }

    private static boolean UK() {
        return AppHolder.getInstance().getAppContext().getResources().getConfiguration().locale.getCountry().equals("UK");
    }
}
