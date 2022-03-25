package com.xiaoma.ui;

import android.os.Handler;

/**
 * Created by youthyj on 2018/9/27.
 */
public interface UISupport {
    boolean isDestroy();

    Handler getUIHandler();
}
