package com.xiaoma.assistant.model;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.RecommendTrack;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMRecommendTrack extends XMBean<RecommendTrack> {
    public XMRecommendTrack(RecommendTrack recommendTrack) {
        super(recommendTrack);
    }

    public long getUid() {
        return getSDKBean().getUid();
    }

    public void setUid(long uid) {
        getSDKBean().setUid(uid);
    }

    public long getTrackId() {
        return getSDKBean().getTrackId();
    }

    public void setTrackId(long trackId) {
        getSDKBean().setTrackId(trackId);
    }

    public String getRealTitle() {
        return getSDKBean().getRealTitle();
    }

    public void setRealTitle(String realTitle) {
        getSDKBean().setRealTitle(realTitle);
    }

    public String getTrackTitle() {
        return getSDKBean().getTrackTitle();
    }

    public void setTrackTitle(String tackTitle) {
        getSDKBean().setTrackTitle(tackTitle);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
