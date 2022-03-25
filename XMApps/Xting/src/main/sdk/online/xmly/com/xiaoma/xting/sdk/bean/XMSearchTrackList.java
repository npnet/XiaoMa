package com.xiaoma.xting.sdk.bean;

import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMSearchTrackList extends XMCommonTrackList<SearchTrackList> {
    public XMSearchTrackList(SearchTrackList searchTrackList) {
        super(searchTrackList);
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

    public String toString() {
        return getSDKBean().toString();
    }

}
