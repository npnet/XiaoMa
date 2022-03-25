package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMQueryResult extends XMBean<QueryResult> {
    public XMQueryResult(QueryResult queryResult) {
        super(queryResult);
    }

    public long getQueryId() {
        return getSDKBean().getQueryId();
    }

    public void setQueryId(long queryId) {
        getSDKBean().setQueryId(queryId);
    }

    public String getKeyword() {
        return getSDKBean().getKeyword();
    }

    public void setKeyword(String keyword) {
        getSDKBean().setKeyword(keyword);
    }

    public String getHighlightKeyword() {
        return getSDKBean().getHighlightKeyword();
    }

    public void setHighlightKeyword(String highlightKeyword) {
        getSDKBean().setHighlightKeyword(highlightKeyword);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
