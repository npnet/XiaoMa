package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.ranks.RankTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMRankTrackList extends XMBean<RankTrackList> {
    public XMRankTrackList(RankTrackList rankTrackList) {
        super(rankTrackList);
    }

    public int getTotalPage() {
        return getSDKBean().getTotalPage();
    }

    public void setTotalPage(int totalPage) {
        getSDKBean().setTotalPage(totalPage);
    }

    public int getTotalCount() {
        return getSDKBean().getTotalCount();
    }

    public void setTotalCount(int totalCount) {
        getSDKBean().setTotalCount(totalCount);
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

    public List<XMTrack> getTrackList() {
        List<Track> trackList = getSDKBean().getTrackList();
        List<XMTrack> xmTracks = new ArrayList<>();
        if (trackList == null && !trackList.isEmpty()) {
            for (Track track : trackList) {
                if (track == null) {
                    continue;
                }
                xmTracks.add(new XMTrack(track));
            }
        }
        return xmTracks;
    }

    public void setTrackList(List<XMTrack> xmTrackList) {
        if (xmTrackList == null) {
            getSDKBean().setTrackList(null);
            return;
        }
        List<Track> trackList = new ArrayList<>();
        for (XMTrack xmTrack : xmTrackList) {
            if (xmTrack == null) {
                continue;
            }
            trackList.add(xmTrack.getSDKBean());
        }
        getSDKBean().setTrackList(trackList);
    }
}
