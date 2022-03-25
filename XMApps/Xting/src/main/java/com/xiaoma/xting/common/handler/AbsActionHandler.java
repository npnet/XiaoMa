package com.xiaoma.xting.common.handler;

import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/14
 */
public abstract class AbsActionHandler {

    protected ClientCallback mClientCallback;

    public AbsActionHandler(ClientCallback callback) {
        this.mClientCallback = callback;
    }

    protected void dispatchRequestCallback(Bundle bundle) {
        if (mClientCallback != null) {
            mClientCallback.setData(bundle);
            mClientCallback.callback();
        }
    }

    /**
     * 主要用于Request Callback 反馈
     *
     * @param bundle
     */
    public abstract void handleRequestAction(Bundle bundle);
}
