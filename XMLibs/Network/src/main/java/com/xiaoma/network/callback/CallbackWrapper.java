package com.xiaoma.network.callback;

import com.xiaoma.thread.Priority;

/**
 * Created by LKF on 2018/9/25 0025.
 * 回调代理
 */
public abstract class CallbackWrapper<M> extends ModelCallback<M> {
    private SimpleCallback<M> mCallback;

    public CallbackWrapper() {
        this(null);
    }

    public CallbackWrapper(SimpleCallback<M> callback) {
        this(Priority.NORMAL, callback);
    }

    public CallbackWrapper(Priority priority, SimpleCallback<M> callback) {
        super(priority);
        mCallback = callback;
    }

    @Override
    public void onSuccess(M model) {
        if (mCallback != null)
            mCallback.onSuccess(model);
    }

    @Override
    public void onError(int code, String msg) {
        if (mCallback != null)
            mCallback.onError(code, msg);
    }
}
