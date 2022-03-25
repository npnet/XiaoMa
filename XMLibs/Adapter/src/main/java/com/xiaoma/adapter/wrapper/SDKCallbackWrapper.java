package com.xiaoma.adapter.wrapper;

import com.xiaoma.adapter.base.ISDK;
import com.xiaoma.adapter.base.ISDKCallbackConvert;

/**
 * Created by youthyj on 2018/9/19.
 */
public abstract class SDKCallbackWrapper implements ISDK {
    public abstract void init(ISDKCallbackConvert callback);
}
