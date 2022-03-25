package com.xiaoma.utils.logintype.constant;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/06
 * @Describe: 登录类型：记录登录类型标识的类
 */

public class LoginTypeModel {

    public static final String LOGIN_FACTORY_MODEL = "login_factory_model";//工厂模式登录
    public static final String LOGIN_NORMAL_MODEL = "login_normal_model";//主账号 钥匙绑定登录
    public static final String LOGIN_TRAVELLER_MODEL = "login_traveller_model";//游客登录
    public static final String LOGIN_VISITOR_MODEL = "login_visitor_model";//访客登录（没有用户信息）
    public static final String LOGIN_OFFLINE_MODEL = "login_offline_model";//离线模式
}
