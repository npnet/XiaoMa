package com.xiaoma.xting.sdk.bean;

import android.support.annotation.Nullable;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/18
 */
public class AbsXMDataCallback<T> implements XMDataCallback<T> {

    @Override
    public void onSuccess(@Nullable T data) {

    }

    @Override
    public void onError(int code, String msg) {

    }

    public void onError(long albumId, int code, String msg) {
        onError(code, msg);
    }

    public void onSuccess(long albumId, T data) {
        onSuccess(data);
    }
}
