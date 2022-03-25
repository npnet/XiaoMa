package com.xiaoma.setting.common.utils;

import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.utils.log.KLog;

public class DebugUtils {

    public static void e(String msg) {
        if (SettingConstants.SHOW_DEBUG_LOG) {
            KLog.e(msg);
        }
    }

    public static void e(String tag, String msg) {
        if (SettingConstants.SHOW_DEBUG_LOG) {
            KLog.e(tag, msg);
        }
    }
}
