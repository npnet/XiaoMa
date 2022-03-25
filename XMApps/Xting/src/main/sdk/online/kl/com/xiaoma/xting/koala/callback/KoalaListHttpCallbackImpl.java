package com.xiaoma.xting.koala.callback;

import com.kaolafm.opensdk.http.core.HttpCallback;
import com.kaolafm.opensdk.http.error.ApiException;
import com.xiaoma.adapter.base.XMBean;
import com.xiaoma.xting.sdk.bean.XMDataCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/6
 */
public class KoalaListHttpCallbackImpl<XMRESULT extends XMBean<RESULT>, RESULT> implements HttpCallback<List<RESULT>> {

    private XMDataCallback<List<XMRESULT>> mXMListDataCallback;

    public KoalaListHttpCallbackImpl(XMDataCallback<List<XMRESULT>> callback) {
        mXMListDataCallback = callback;
    }

    @Override
    public void onSuccess(List<RESULT> results) {
        if (mXMListDataCallback != null) {
            if (results == null || results.isEmpty()) {
                mXMListDataCallback.onSuccess(null);
            } else {
                List<XMRESULT> xmResultList = new ArrayList<>(results.size());
                for (RESULT result : results) {
                    xmResultList.add(handleConverter(result));
                }
                mXMListDataCallback.onSuccess(xmResultList);
            }
        }
    }

    protected XMRESULT handleConverter(RESULT result) {
        return null;
    }

    @Override
    public void onError(ApiException e) {
        if (mXMListDataCallback != null) {
            mXMListDataCallback.onError(e.getCode(), e.getMessage());
        }
    }
}
