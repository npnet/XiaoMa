package com.xiaoma.login.business.callback;

import com.xiaoma.login.business.bean.LoginFactor;
import com.xiaoma.model.User;

/**
 * Created by youthyj on 2018/9/12.
 */
public abstract class DefaultLoginCallback implements LoginCallback {

    @Override
    public void onWaiting(LoginFactor factor, long interval) {
    }

    @Override
    public abstract void onSuccess(User data);

    @Override
    public abstract void onFailure(int errCode, String errMsg);
}
