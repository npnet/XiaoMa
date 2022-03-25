package com.iflytek.cata;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface CataSearchInst {
    int create(String var1, ICataListener var2);

    int createEx(String var1, int var2, ICataListener var3);

    String searchSync(String var1);

    int searchAsync(String var1);

    int destroy();

    int setParam(int var1, int var2);
}
