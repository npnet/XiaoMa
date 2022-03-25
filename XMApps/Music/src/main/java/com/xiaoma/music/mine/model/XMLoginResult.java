package com.xiaoma.music.mine.model;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.mod.userinfo.login.LoginResult;

/**
 * Author: loren
 * Date: 2019/6/18 0018
 */
public class XMLoginResult extends XMBean<LoginResult> {

    public XMLoginResult(LoginResult loginResult) {
        super(loginResult);
    }

    public boolean isLogin(){
        return getSDKBean().getState() == getSDKBean().STATE_LOGIN;
    }

    public boolean isWait(){
        return getSDKBean().getState() == getSDKBean().STATE_WAIT;
    }

    public boolean isInvalid(){
        return getSDKBean().getState() == getSDKBean().STATE_INVALID;
    }

    public boolean isFailed(){
        return getSDKBean().getState() == getSDKBean().STATE_FAIL;
    }

}
