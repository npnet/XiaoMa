package com.xiaoma.pet.common.callback;

import com.xiaoma.model.XmResource;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/3 0003 18:37
 *   desc:   处理宠物资源网络请求结果
 * </pre>
 */
public abstract class XmPetResourceHandleCallback<T> implements XmResource.OnHandleCallback<T> {

    private static final String TAG = XmPetResourceHandleCallback.class.getSimpleName();

    @Override
    public void onLoading() {
        KLog.d(TAG, "loading");
    }

    @Override
    public void onCompleted() {
        KLog.d(TAG, "onCompleted");
    }

    @Override
    public void onError(int code, String message) {
        KLog.d(TAG, "code: " + code + "  -----  " + "message: " + message);
    }
}
