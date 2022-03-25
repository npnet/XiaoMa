package com.xiaoma.xting.sdk.bean;

import com.ximalaya.ting.android.opensdk.model.track.AnnouncerTrackList;

/**
 * @author youthyJ
 * @date 2018/10/18
 */
public class XMAnnouncerTrackList extends XMCommonTrackList<AnnouncerTrackList> {
    public XMAnnouncerTrackList(AnnouncerTrackList announcerTrackList) {
        super(announcerTrackList);
    }

    public int getCurrentPage() {
        AnnouncerTrackList sdkBean = getSDKBean();
        return getSDKBean().getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        getSDKBean().setCurrentPage(currentPage);
    }
}
