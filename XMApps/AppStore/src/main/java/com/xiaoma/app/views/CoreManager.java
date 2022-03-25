package com.xiaoma.app.views;

import android.content.Context;
import android.os.Looper;

import com.xiaoma.thread.IDispatcher;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * Created by Administrator on 2016/12/27 0027.
 */

public enum CoreManager {
    INSTANCE;

    private static Context appContext;
    private final IDispatcher threadDispatcher;

    CoreManager() {
        threadDispatcher = ThreadDispatcher.getDispatcher();
    }

    public static void init(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }

    public static Context getContext() {
        return appContext;
    }

    public static IDispatcher getThreadDispatcher() {
        return INSTANCE.threadDispatcher;
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
