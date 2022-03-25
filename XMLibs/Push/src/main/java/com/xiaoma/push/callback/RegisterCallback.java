package com.xiaoma.push.callback;

import com.xiaoma.push.contract.IPush;

/**
 * Created by LKF on 2018/4/8 0008.
 */

public interface RegisterCallback {
    void onSuccess(IPush push, Object data, int flag);

    void onFail(IPush push, Object data, int errCode, String msg);
}
