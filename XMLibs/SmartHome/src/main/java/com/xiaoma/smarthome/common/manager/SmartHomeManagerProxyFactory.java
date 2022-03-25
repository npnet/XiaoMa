package com.xiaoma.smarthome.common.manager;

/**
 * Created by dc on 18-2-3.
 */

public class SmartHomeManagerProxyFactory {
    private static SmartHomeManagerProxy mGowildManagerProxy;
    private static SmartHomeManagerProxy mHuaWeiManagerProxy;

    public SmartHomeManagerProxyFactory() {
    }

    public static SmartHomeManagerProxy getGowildManagerProxy() {
        if (mGowildManagerProxy == null) {
            synchronized (SmartHomeManagerProxyFactory.class) {
                if (mGowildManagerProxy == null) {
                    GowildSmartHomeManager gowildSmartHomeManager = GowildSmartHomeManager.getInstance();
                    mGowildManagerProxy = new SmartHomeManagerProxy(gowildSmartHomeManager);
                }
            }
        }
        return mGowildManagerProxy;
    }

    public static SmartHomeManagerProxy getHuaWeiManagerProxy() {
        if (mHuaWeiManagerProxy == null) {
            synchronized (SmartHomeManagerProxyFactory.class) {
                if (mHuaWeiManagerProxy == null) {
                    HuaWeiSmartHomeManager huaWeiSmartHomeManager = HuaWeiSmartHomeManager.getInstance();
                    mHuaWeiManagerProxy = new SmartHomeManagerProxy(huaWeiSmartHomeManager);
                }
            }
        }
        return mHuaWeiManagerProxy;
    }
}
