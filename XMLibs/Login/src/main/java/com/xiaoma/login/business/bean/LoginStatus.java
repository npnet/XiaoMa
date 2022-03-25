package com.xiaoma.login.business.bean;

import android.text.TextUtils;

import com.xiaoma.login.common.LoginMethod;

/**
 * Created by youthyj on 2018/9/11.
 */
public class LoginStatus {
    private long time;
    private String userId;
    private String method;

    public LoginStatus(String userId, String loginMethod, long time) {
        this.userId = userId;
        this.method = loginMethod;
        this.time = time;
    }

    public boolean isTourist() {
        return LoginMethod.TOURISTS.name().equals(method);
    }

    public boolean isFactory() {
        return LoginMethod.FACTORY.name().equals(method);
    }

    public String getLoginUserId() {
        return userId;
    }

    public String getLoginMethod() {
        return method;
    }

    public long getLoginTime() {
        return time;
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(userId);
    }
}
