package com.xiaoma.network.callback;

import com.xiaoma.network.model.Response;

/**
 * Created by LKF on 2019-4-24 0024.
 */
public interface BaseCallback<T> {
    void onStart();

    void onSuccess(Response<T> response);

    void onError(Response<T> response);

    void onFinish();
}
