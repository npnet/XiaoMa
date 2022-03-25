package com.xiaoma.xting.sdk;

import android.content.Context;

import com.xiaoma.xting.sdk.listener.OnRadioResultListener;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/12/05
 *     desc   :
 * </pre>
 */
public interface IAcrRadio {

    void init(Context context);

    void requestRadioMetadata(final int type, String lat, String lon, final int frequency, final OnRadioResultListener listener);

    void requestBatchRadioMetadata(final int type, String lat, String lon, String frequencys, final OnRadioResultListener listener);

    void release();
}
