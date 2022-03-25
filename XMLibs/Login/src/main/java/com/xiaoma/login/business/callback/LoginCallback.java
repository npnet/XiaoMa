package com.xiaoma.login.business.callback;

import com.xiaoma.login.business.bean.LoginFactor;
import com.xiaoma.model.User;

/**
 * Created by youthyj on 2018/9/11.
 */
public interface LoginCallback {
    void onWaiting(LoginFactor factor, long interval);

    void onSuccess(User data);

    void onFailure(int errCode, String errMsg);
}
