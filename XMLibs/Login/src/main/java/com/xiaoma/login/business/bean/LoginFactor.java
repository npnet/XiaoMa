package com.xiaoma.login.business.bean;

import com.xiaoma.login.common.LoginMethod;

import java.util.HashMap;

/**
 * Created by youthyj on 2018/9/11.
 */
public class LoginFactor {
    private LoginMethod method;
    private HashMap<String, String> loginParams;

    public LoginFactor(LoginMethod method, HashMap<String, String> loginParams) {
        this.method = method;
        this.loginParams = loginParams;
    }

    public LoginMethod getMethod() {
        return method;
    }

    public HashMap<String, String> getLoginParams() {
        return loginParams;
    }
}
