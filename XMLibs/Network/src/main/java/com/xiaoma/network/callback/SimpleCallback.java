package com.xiaoma.network.callback;

/**
 * Created by LKF on 2018/9/26 0026.
 */
public interface SimpleCallback<M> {
    void onSuccess(M model);

    void onError(int code, String msg);
}