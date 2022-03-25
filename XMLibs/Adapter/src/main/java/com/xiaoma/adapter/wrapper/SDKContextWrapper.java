package com.xiaoma.adapter.wrapper;

import android.content.Context;

import com.xiaoma.adapter.base.ISDK;

/**
 * Created by youthyj on 2018/9/19.
 */
public abstract class SDKContextWrapper implements ISDK {
    public abstract boolean init(Context context);
}
