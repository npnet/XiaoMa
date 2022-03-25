package com.xiaoma.xting.koala.callback;

import com.kaolafm.opensdk.http.core.HttpCallback;
import com.kaolafm.opensdk.http.error.ApiException;
import com.xiaoma.adapter.base.XMBean;
import com.xiaoma.xting.sdk.bean.AbsXMDataCallback;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/6
 */
public class KoalaHttpCallbackImpl<XMRESULT extends XMBean<RESULT>, RESULT> implements HttpCallback<RESULT> {

    private AbsXMDataCallback<XMRESULT> mXMDataCallback;

    public KoalaHttpCallbackImpl(AbsXMDataCallback<XMRESULT> callback) {
        mXMDataCallback = callback;
    }

    @Override
    public void onSuccess(RESULT result) {
        if (mXMDataCallback != null) {
            mXMDataCallback.onSuccess(getAlbumId(), handleConverter(result));
        }
    }

    @Override
    public void onError(ApiException e) {
        if (mXMDataCallback != null) {
            mXMDataCallback.onError(getAlbumId(), e.getCode(), e.getMessage());
        }
    }

    protected XMRESULT handleConverter(RESULT result) {
        return null;
    }

    protected long getAlbumId() {
        return -1;
    }
}
