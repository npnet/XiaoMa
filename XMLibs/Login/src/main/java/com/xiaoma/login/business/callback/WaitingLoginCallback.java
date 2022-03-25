package com.xiaoma.login.business.callback;

import com.xiaoma.login.business.bean.LoginFactor;
import com.xiaoma.model.User;

/**
 * Created by youthyj on 2018/9/12.
 */
public abstract class WaitingLoginCallback implements LoginCallback {
    @Override
    public void onWaiting(LoginFactor factor, long interval) {
    }

    @Override
    public void onSuccess(User data) {

    }

    @Override
    public void onFailure(int errCode, String errMsg) {

    }
}
