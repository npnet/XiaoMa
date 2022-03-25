package com.xiaoma.personal.qrcode.callback;

import com.xiaoma.model.XmResource;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/21 0021 17:10
 *   desc:
 * </pre>
 */
public abstract class WrapOnHandlepCallback<T> implements XmResource.OnHandleCallback<T> {

    private static final String TAG = WrapOnHandlepCallback.class.getSimpleName();

    @Override
    public void onLoading() {
        KLog.d(TAG, "XMResource loading.");
    }


    @Override
    public void onError(int code, String message) {
        KLog.d(TAG, "XMResource onError.");
    }

    @Override
    public void onCompleted() {
        KLog.d(TAG, "XMResource onCompleted.");
    }
}
