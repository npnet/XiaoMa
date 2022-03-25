package com.xiaoma.vr.understand;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 * Desc:语义理解监听
 */

public interface IUnderstandListener {
    void onResult(UnderstandResult result);

    void onError();
}
