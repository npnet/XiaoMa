package com.xiaoma.systemui.common.controller;

import android.support.annotation.NonNull;

public interface OnConnectStateListener<T> {
    void onConnected(@NonNull T t);

    void onDisconnected();
}
