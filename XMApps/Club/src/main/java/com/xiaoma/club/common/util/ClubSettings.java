package com.xiaoma.club.common.util;

import android.app.Application;

import com.xiaoma.component.AppHolder;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Author: loren
 * Date: 2019/1/3 0003
 */

public class ClubSettings {
    private static final String TP_CHAT_PUSH_SWITCH = "tp_chat_push";
    private static final boolean CHAT_PUSH_SWITCH_DEFAULT = true;

    private static final String TP_AUTO_PLAY_SWITCH = "tp_auto_play";
    private static final boolean AUTO_PLAY_SWITCH_DEFAULT = true;

    private static Application sApp = AppHolder.getInstance().getAppContext();

    public static boolean isChatPushOpen() {
        return TPUtils.get(sApp, TP_CHAT_PUSH_SWITCH, CHAT_PUSH_SWITCH_DEFAULT);
    }

    public static void setChatPushOpen(boolean chatPushOpen) {
        TPUtils.put(sApp, TP_CHAT_PUSH_SWITCH, chatPushOpen);
        KLog.d("setChatPushOpen: " + chatPushOpen);
    }

    public static boolean isAutoPlayOpen() {
        return TPUtils.get(sApp, TP_AUTO_PLAY_SWITCH, AUTO_PLAY_SWITCH_DEFAULT);
    }

    public static void setAutoPlayOpen(boolean autoPlayOpen) {
        TPUtils.put(sApp, TP_AUTO_PLAY_SWITCH, autoPlayOpen);
        KLog.d("setAutoPlayOpen: " + autoPlayOpen);
    }
}
