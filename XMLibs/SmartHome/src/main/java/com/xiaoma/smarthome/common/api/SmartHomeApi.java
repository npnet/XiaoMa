package com.xiaoma.smarthome.common.api;

import com.xiaoma.config.ConfigManager;

/**
 * @author taojin
 * @date 2019/2/27
 */
public interface SmartHomeApi {

    /**
     * 基础链接
     */
    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();

    /**
     * 检测用户是否绑定小米
     */
    String CHECK_USER_BIND_MI = BASE_URL + "user/checkUserBindXiaomi.action";

    /**
     * 小米授权
     */
    String SMART_HOME_MI_AUTH = BASE_URL + "tp/miAuth.action";

    /**
     * 小米用户信息
     */
    String SMART_HOME_MI_USER = BASE_URL + "tp/miUser.action";

    /**
     * 小米垂类
     */
    String SMART_HOME_MI_TEXT_PARSER = BASE_URL + "tp/textRecognize.action";

    /**
     * 退出小米
     */
    String LOGOUT_MI = BASE_URL + "user/clearMiToken.action";

    /**
     * 获取登录云米二维码
     */
    String GET_LOGIN_CLOUDMI_CODE = BASE_URL + "viomi/getQrCode.action";

    /**
     * 查询登录云米结果
     */
    String QUERY_LOGIN_CLOUDMI_RESULT = BASE_URL + "viomi/getUserInfo.action";

    /**
     * 获取云米场景列表
     */
    String GET_CLOUDMI_SCENE = BASE_URL + "viomi/getScenes.action";

    /**
     * 执行云米场景
     */
    String EXCUTE_CLOUDMI_SCENE = BASE_URL + "viomi/executeScene.action";

    /**
     * 执行云米场景（按名字来执行）
     */
    String EXCUTE_CLOUDMI_SCENE_BY_NAME = BASE_URL + "viomi/executeSceneByName.action";

    /**
     * 云米用户登陆状态
     */
    String QUERY_CM_USER_LOGIN_STATE = BASE_URL + "viomi/getUserLoginStatus.action";

    /**
     * 云米用户退出登陆
     */
    String LOGOUT_CM_USER = BASE_URL + "viomi/userLogout.action";

    /**
     * 保存并更新云米场景设置条件
     */
    String UPDATE_CM_SCENE_DATA = BASE_URL + "viomi/saveOrUpdateSceneCondition.action";

    /**
     * 查询用户信息
     */
    String QUERY_CM_USER_INFO = BASE_URL + "viomi/queryUserInfo.action";
}
