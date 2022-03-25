package com.xiaoma.center.logic.remote;

import android.os.Bundle;

/**
 * @author youthyJ
 * @date 2019/1/20
 */
public abstract class ClientCallback {
    private Bundle callbackData;

    ClientCallback() {
    }

    public Bundle getData() {
        return callbackData;
    }

    public void setData(Bundle callbackData) {
        this.callbackData = callbackData;
    }

    public abstract int callback();
}
