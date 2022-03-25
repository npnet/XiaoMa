package com.xiaoma.launcher.recommend.push;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.bean.Env;

/**
 * Created by youthyj on 2018/9/7.
 */
public class PushConstants {
    private PushConstants() throws Exception {
        throw new Exception();
    }

    /*==========================常量定义=================================*/

    /**
     * 消息广播的Action
     */
    public static final String PUSH_NOTICE_ACTION = "push_notice_action";
    /**
     * 消息广播intent extra中的消息action
     */
    public static final String PUSH_NOTICE_EXTRA_MSG_ACTION = "push_notice_extra_msg_action";
    /**
     * 消息广播intent extra中的消息tag
     */
    public static final String PUSH_NOTICE_EXTRA_MSG_TAG = "push_notice_extra_msg_tag";
    /**
     * 消息广播intent extra中的消息data
     */
    public static final String PUSH_NOTICE_EXTRA_MSG_DATA = "push_notice_extra_msg_data";

    /**
     * 弹窗广播的Action
     */
    public static final String POPUP_ACTION = "extra_popup_action";
    /**
     * 弹窗广播intent extra中的data
     */
    public static final String EXTRA_POPUP_DATA = "extra_popup_data";

    /*==========================接口定义=================================*/

    /**
     * 当前环境服务器地址
     */
    private static final Env ENV = ConfigManager.EnvConfig.getEnv();
    /**
     * 上传用户日志文件的url
     */
    public static final String UPLOAD_LOG_FILE_URL = ENV.getFile() + "wnsin/uploadUserLogFile.action";
}
