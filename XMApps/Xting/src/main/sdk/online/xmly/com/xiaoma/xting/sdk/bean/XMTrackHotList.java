package com.xiaoma.xting.sdk.bean;

import com.ximalaya.ting.android.opensdk.model.track.TrackHotList;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMTrackHotList extends XMCommonTrackList<TrackHotList> {
    public XMTrackHotList(TrackHotList trackHotList) {
        super(trackHotList);
    }

    public int getCategoryId() {
        return getSDKBean().getCategoryId();
    }

    public void setCategoryId(int categoryId) {
        getSDKBean().setCategoryId(categoryId);
    }

    public String getTagName() {
        return getSDKBean().getTagName();
    }

    public void setTagName(String tagName) {
        getSDKBean().setTagName(tagName);
    }

    public int getCurrentPage() {
        return getSDKBean().getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        getSDKBean().setCurrentPage(currentPage);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
