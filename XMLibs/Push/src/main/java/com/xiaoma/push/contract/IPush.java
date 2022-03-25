package com.xiaoma.push.contract;

import android.content.Context;

import com.xiaoma.push.callback.RegisterCallback;


/**
 * Created by LKF on 2018/3/31 0031.
 */

public interface IPush {

    void register(Context context, String account, RegisterCallback callback);

    void unregister(Context context, String account, RegisterCallback callback);

    void setIsDebug(Context context, boolean debug);
}
