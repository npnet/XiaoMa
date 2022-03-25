package com.xiaoma.xting.sdk;

/**
 * @author youthyJ
 * @date 2018/10/10
 */
public class OnlineFMFactory {
    private static OnlineFMFactory instance;
    private OnlineFM sdkWrapper;

    public static OnlineFMFactory getInstance() {
        if (instance == null) {
            synchronized (OnlineFMFactory.class) {
                if (instance == null) {
                    instance = new OnlineFMFactory();
                }
            }
        }
        return instance;
    }

    private OnlineFMFactory() {

    }

    public OnlineFM getSDK() {
        if (sdkWrapper == null) {
            sdkWrapper = new XmlySDK();
        }
        return sdkWrapper;
    }

}
