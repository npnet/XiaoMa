package com.xiaoma.login.common;

/**
 * Created by youthyj on 2018/9/11.
 */
public enum LoginMethod {
    PASSWD,//密码登录
    FACE,//人脸登录
    KEY_BLE,//蓝牙钥匙登录
    KEY_NORMAL,//普通钥匙登录
    FACTORY,//工厂模式登陆
    TOURISTS;//游客模式登录

    public LoginMethod getByName(String name) {
        for (LoginMethod loginMethod : LoginMethod.values()) {
            if (loginMethod.name().equals(name)) {
                return loginMethod;
            }
        }
        return null;
    }
}
