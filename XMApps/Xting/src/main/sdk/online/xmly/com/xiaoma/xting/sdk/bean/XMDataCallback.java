package com.xiaoma.xting.sdk.bean;

import android.support.annotation.Nullable;

/**
 * @author youthyJ
 * @date 2018/10/10
 */
public interface XMDataCallback<T> {
    void onSuccess(@Nullable T data);

    void onError(int code, String msg);
}
