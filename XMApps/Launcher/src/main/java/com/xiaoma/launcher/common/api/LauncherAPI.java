package com.xiaoma.launcher.common.api;

import com.xiaoma.config.ConfigManager;

public interface LauncherAPI {
    /**
     * 基础链接
     */
    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();
    String BASE_URL_FILE = ConfigManager.EnvConfig.getEnv().getFile();

    /**
     * 检查微信绑定状态
     */
    String CHECK_USER_BIND = BASE_URL + "wxmsg/checkUserBind.action";

    /**
     * 获得微信绑定二维码
     */
    String GET_BIND_QR_CODE = BASE_URL + "wxmsg/getbindQrCode.action";

    /**
     * 解除微信绑定
     */
    String UNBIND_DEVCIE = BASE_URL + "wxmsg/unbindDevcie.action";
    /**
     * 发送定位信息到手机
     */
    String SEND_NAVIGATE_MSG_TO_MOBILE = BASE_URL + "msg/sendNavigateMsgToMobile.action";

    /**
     * 请求服务列表
     */
    String SERVICE_LIST_QUERY = BASE_URL + "extendCategory/queryAll";

    /**
     * 获取白名单
     */
    String GET_WHITE_APP = BASE_URL + "apk/getApkWhitelist.action";

    /**
     * 获取桌面音频数据
     */
    String GET_LAUNCHER_AUDIO = BASE_URL + "audio/getAudioTypeList.action";
    /**
     * 获取听歌识曲图片和ID
     */
    String SEARCH_BY_NAME_AND_SINGER = BASE_URL + "music/searchByNameAndSinger.action";

    /**
     * 获取天气信息
     */
    String GET_CURRENT_CITY_WEATHER = BASE_URL + "app/weather2.action";

    /**
     * 获取消息列表
     */
    String GET_HEAD_MSG_LIST = BASE_URL + "msgNotify/headMsg.action";
    /**
     * 上传百公里油耗
     */
    String POST_OIL_CONSUME = BASE_URL + "uploadOilConsume";
    /**
     * 同步MQTT配置信息
     */
    String GET_MQTT_INFO = BASE_URL + "app/getMqttInfo";
    /**
     * 同步MQTT配置信息
     */
    String MARK_UPLOAD_PHOTO = BASE_URL_FILE + "mark/uploadPhoto";
    /**
     * 请求后台下发油量不足提示
     */
    String GET_MQTT_OIL = BASE_URL + "notify/oilNotify";

    //上传日程
    String POST_UPLOAD_SCH = BASE_URL + "scheduleAndMemo/saveSchedule";

    //删除日程
    String POST_DEL_SCH = BASE_URL + "scheduleAndMemo/delScheduleOrMemo";

    //获取手机端日程
    String POST_FETCH_PHONE_ALLSCH = BASE_URL + "scheduleAndMemo/pullScheduleFromPhone";
    /**
     * 检测照片保存状态
     */
    String GET_PHOTO_UPLOAD_STATUS = BASE_URL + "mark/checkPhotoExist";
}
