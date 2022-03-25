package com.xiaoma.model;

/**
 * Created by youthyj on 2018/9/7.
 */
public class ModelConstants {
    private ModelConstants() throws Exception {
        throw new Exception();
    }

    public interface UserType {
        // 系统用户
        int TYPE_SYSTEM_USER = 0;
        // 正常登录用户
        int TYPE_COMMON_USER = 1;
        // 白名单用户
        int TYPE_WHITELIST_USER = 2;
        // 匿名登录
        int TYPE_ANONYMOUS_USER = 3;
        // 扫码登录
        int TYPE_QR_USER = 4;
        // 产线测试账户
        int TYPE_FACTORY_PRODUCTION_TEST = 6;
        // 路试账户
        int TYPE_FACTORY_ROAD_TEST = 7;
    }

    public interface GenderType {
        // 女性
        int TYPE_WOMEN = 0;
        // 男性
        int TYPE_MAN = 1;
    }

    public interface ResultCode {
        int RESULT_OK = 1;
    }
}
