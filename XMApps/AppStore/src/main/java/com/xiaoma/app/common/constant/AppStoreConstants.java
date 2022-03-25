package com.xiaoma.app.common.constant;

/**
 * Created by Thomas on 2018/10/18 0018
 */

public class AppStoreConstants {
    //应用安装，卸载
    public static final String APP_INSTALL_RECEIVER = "app_install_receiver";

    //下载列表项取消
    public static final String MSG_DOWNLOAD_LIST_ITEM_CANCEL = "msg_download_list_item_cancel";

    //下载任务数目
    public static final String MSG_DOWNLOAD_LIST_SIZE = "msg_download_list_size";

    //任务开始下载
    public static final String MSG_DOWNLOAD_TASK_START = "msg_download_task_start";

    //未安装应用
    public static final int INSTALL_STATE_NOTHING = 0;
    //最新版本
    public static final int INSTALL_STATE_NEW = 1;
    //非最新版本
    public static final int INSTALL_STATE_OLD = 2;

    //网络改变
    public static final String MSG_NETWORK_CHANGED = "msg_network_changed";

    //数据空返回code
    public static final int EMPTY_DATA_RESULT_CODE = 1026;

    //不可卸载
    public static int APP_UNINSTALL = 0;
    //可卸载
    public static int APP_ALLOW_INSTALL = 1;
    //应用市场app进入应用详情
    public static int APP_MARKRT = 11;
    //应用管理app进入应用详情
    public static int APP_MANAGER = 12;

    //车应用屏蔽打开的应用
    public static String SYSTEM_UI = "com.xiaoma.systemui";

    public static String FACERECOGNIZE = "com.xiaoma.facerecognize";

    public static String DUAL_SCREEN = "com.xiaoma.dualscreen";

    public static String ASSISTANT = "com.xiaoma.assistant";

    public static String APPSTORE = "com.xiaoma.app";
}
