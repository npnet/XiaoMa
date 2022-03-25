package com.xiaoma.smarthome.common.callback;

import java.util.List;

public abstract class SMIatCallback {

    public abstract void callback(boolean result);

    //在线设备名字
    public abstract void callback(boolean result, List<String> list);
}
