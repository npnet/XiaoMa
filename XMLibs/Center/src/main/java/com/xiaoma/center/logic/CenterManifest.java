package com.xiaoma.center.logic;

/**
 * @author youthyJ
 * @date 2019/3/24
 */
public final class CenterManifest {

    public static class AssistantClient {
        // 公共
        public static final String CLIENT_LOCATION = "com.xiaoma.assistant";
        public static final String DATA_KEY_PACKAGE_NAME = "DATA_KEY_PACKAGE_NAME";
        public static final String DATA_KEY_HANDLE_DETAIL = "DATA_KEY_HANDLE_DETAIL";

        // 业务:指令分发
        public static final int BUSINESS_PORT_DISPATCH = 1733;
        public static final int BUSINESS_ACTION_DISPATCH = 1377;
        public static final String DATA_KEY_FOLLOW_VOICE = "DATA_KEY_FOLLOW_VOICE";

        // 业务:唤醒词注册
        public static final int BUSINESS_PORT_WAKEUP_WORD = 1734;
        public static final int BUSINESS_ACTION_ACTIVATE_WAKEUP_WORD = 1334;
        public static final int BUSINESS_ACTION_CANCEL_WAKEUP_WORD = 1335;
        public static final String DATA_KEY_WAKEUP_WORD = "DATA_KEY_WAKEUP_WORD";
    }

    public static class XtingClient {

    }

    public static class MusicClient {

    }

}
