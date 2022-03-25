package com.xiaoma.login.sdk;

import android.content.Context;

public interface ICarKey {
    void init(Context context);

    CarKey getCarKey();

    /**
     * 用于测试设置
     */
    void setCarKey(String key);
}
