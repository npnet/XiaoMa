package com.xiaoma.driver.sdk;

import com.xiaoma.drivingscore.sdk.IDataSouce;

/**
 * wutao
 * 2018.1.10
 */
public class SdkFactory {
    private static SdkFactory instance;
    private IDataSouce sdkWrapper;

    private SdkFactory() {

    }

    public static SdkFactory getInstance() {
        if (instance == null) {
            synchronized (SdkFactory.class) {
                if (instance == null) {
                    instance = new SdkFactory();
                }
            }
        }
        return instance;
    }

    public IDataSouce getSDK() {
        if (sdkWrapper == null) {
            sdkWrapper = new Sdk();
        }
        return sdkWrapper;
    }

}
