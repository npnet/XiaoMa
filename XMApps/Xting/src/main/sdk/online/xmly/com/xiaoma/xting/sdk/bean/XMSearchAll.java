package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.search.SearchAll;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMSearchAll extends XMBean<SearchAll> {
    public XMSearchAll(SearchAll searchAll) {
        super(searchAll);
    }

    public XMSearchTrackList getTrackList() {
        return new XMSearchTrackList(getSDKBean().getTrackList());
    }

    public void setTrackList(XMSearchTrackList xmTrackList) {
        getSDKBean().setTrackList(xmTrackList.getSDKBean());
    }

    public XMSearchAlbumList getAlbumList() {
        return new XMSearchAlbumList(getSDKBean().getAlbumList());
    }

    public void setAlbumList(XMSearchAlbumList xmAlbumList) {
        getSDKBean().setAlbumList(xmAlbumList.getSDKBean());
    }

    public XMRadioList getRadioList() {
        return new XMRadioList(getSDKBean().getRadioList());
    }

    public void setRadioList(XMRadioList xmRadioList) {
        getSDKBean().setRadioList(xmRadioList.getSDKBean());
    }
}
