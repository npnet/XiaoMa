package com.iflytek.cata;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public class libisscata {
    private static final String tag = "libisscata";

    public libisscata() {
    }

    public static synchronized native void IndexCreate(CataNativeHandle var0, String var1, String var2, int var3, ICataListener var4);

    public static synchronized native void IndexCreateEx(CataNativeHandle var0, String var1, String var2, int var3, int var4, ICataListener var5);

    public static synchronized native void IndexDestroy(CataNativeHandle var0);

    public static synchronized native void IndexDropRes(CataNativeHandle var0);

    public static synchronized native void IndexAddIdxEntity(CataNativeHandle var0, String var1);

    public static synchronized native void IndexDelIdxEntity(CataNativeHandle var0, String var1);

    public static synchronized native void IndexEndIdxEntity(CataNativeHandle var0);

    public static synchronized native void SearchCreate(CataNativeHandle var0, String var1, String var2, ICataListener var3);

    public static synchronized native void SearchCreateEx(CataNativeHandle var0, String var1, String var2, int var3, ICataListener var4);

    public static synchronized native void SearchDestroy(CataNativeHandle var0);

    public static synchronized native String SearchSync(CataNativeHandle var0, String var1);

    public static synchronized native void SearchAsync(CataNativeHandle var0, String var1);

    public static synchronized native void SetParam(CataNativeHandle var0, int var1, int var2);

    static {
        System.loadLibrary("cata");
        System.loadLibrary("cataIndex");
        System.loadLibrary("cata-jni");
    }
}
