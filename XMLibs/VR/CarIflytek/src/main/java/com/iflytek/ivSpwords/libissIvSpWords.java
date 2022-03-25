package com.iflytek.ivSpwords;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public class libissIvSpWords {
    public libissIvSpWords() {
    }

    public static synchronized native int SPWordInit(SpWordHandle var0, String var1);

    public static synchronized native int SPWordUnInit(SpWordHandle var0);

    public static synchronized native String SPWordGetResult(SpWordHandle var0, String var1);

    static {
        System.loadLibrary("SpWord");
        System.loadLibrary("spword-jni");
    }
}
