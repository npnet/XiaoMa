package com.xiaoma.xting.koala;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/10
 */
public class KoalaFactory {
    private static KoalaSDK sKoalaSDK;

    public static KoalaSDK getSDK() {
        if (sKoalaSDK == null) {
            sKoalaSDK = new KoalaSDK();
        }
        return sKoalaSDK;
    }
}
