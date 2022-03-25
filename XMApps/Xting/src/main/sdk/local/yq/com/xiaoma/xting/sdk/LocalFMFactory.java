package com.xiaoma.xting.sdk;

/**
 * @author youthyJ
 * @date 2018/10/22
 */
public class LocalFMFactory {
    private LocalFMFactory() throws Exception {
        throw new Exception();
    }

    public static LocalFM getSDK() {
        return YqSDK.getInstance();
    }
}
