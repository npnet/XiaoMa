package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.ranks.RankItem;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMRankItem extends XMBean<RankItem> {
    public XMRankItem(RankItem rankItem) {
        super(rankItem);
    }


    public long getDataId() {
        return getSDKBean().getDataId();
    }

    public void setDataId(long dataId) {
        getSDKBean().setDataId(dataId);
    }

    public String getTitle() {
        return getSDKBean().getTitle();
    }

    public void setTitle(String title) {
        getSDKBean().setTitle(title);
    }

    public String getContentType() {
        return getSDKBean().getContentType();
    }

    public void setContentType(String contentType) {
        getSDKBean().setContentType(contentType);
    }

    public String toString() {
        return getSDKBean().toString();
    }

}
