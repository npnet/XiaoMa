package com.xiaoma.component;

import android.app.Application;
import android.content.Context;

/**
 * Created by youthyj on 2018/9/7.
 */
public class AppHolder {
    private static final String Tag = AppHolder.class.getSimpleName();
    private static AppHolder instance;
    private Application appContext;

    public static AppHolder getInstance() {
        if (instance == null) {
            synchronized (AppHolder.class) {
                if (instance == null) {
                    instance = new AppHolder();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (context == null) {
            return;
        }
        appContext = (Application) context.getApplicationContext();
    }

    public Application getAppContext() {
        if (appContext == null) {
            throw new IllegalStateException("init first!");
        }
        return appContext;
    }
}
