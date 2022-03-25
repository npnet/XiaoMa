package com.iflytek.cata;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface CataIndexInst {
    int create(String var1, ICataListener var2);

    int createEx(String var1, int var2, ICataListener var3);

    int reCreate(String var1, ICataListener var2);

    int reCreateEx(String var1, int var2, ICataListener var3);

    int drop();

    int addIdxEntity(String var1);

    int delIdxEntity(String var1);

    int endIdxEntity();

    int destroy();
}
