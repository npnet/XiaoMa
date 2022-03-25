package com.xiaoma.xting.sdk;

/**
 * wutao
 * 2018.12.5
 */
public class AcrFactory {
    private static AcrFactory instance;
    private IAcrRadio sdkWrapper;

    private AcrFactory() {

    }

    public static AcrFactory getInstance() {
        if (instance == null) {
            synchronized (AcrFactory.class) {
                if (instance == null) {
                    instance = new AcrFactory();
                }
            }
        }
        return instance;
    }

    public IAcrRadio getSDK() {
        if (sdkWrapper == null) {
            sdkWrapper = new AcrSDK();
        }
        return sdkWrapper;
    }

}
