package com.xiaoma.xting.sdk;

import android.content.Context;

/**
 * @author youthyJ
 * @date 2018/9/29
 */
public class OnlineFMManager {
    private static OnlineFMManager instance;
    private OnlineFM onlineFM;

    public static OnlineFMManager getInstance() {
        if (instance == null) {
            synchronized (OnlineFMManager.class) {
                if (instance == null) {
                    instance = new OnlineFMManager();
                }
            }
        }
        return instance;
    }

    public OnlineFM getSDK(Context context) {
        if (onlineFM == null) {
            onlineFM = new TestOnlineFMImpl();
            onlineFM.init(context);
        }
        return onlineFM;
    }

    private static class TestOnlineFMImpl implements OnlineFM {

        @Override
        public void init(Context context) {

        }
    }

}
