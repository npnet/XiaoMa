package com.iflytek.cata;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public class CataNativeHandle {
    public int err_ret;
    public long native_point;

    public CataNativeHandle() {
    }

    public int getErr_ret() {
        return this.err_ret;
    }

    public long getNative_point() {
        return this.native_point;
    }

    public void setErr_ret(int err_ret) {
        this.err_ret = err_ret;
    }

    public void setNative_point(long native_point) {
        this.native_point = native_point;
    }

    public void reSet() {
        this.err_ret = 0;
        this.native_point = 0L;
    }
}
