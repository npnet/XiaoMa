package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author youthyJ
 * @date 2018/10/18
 */
public class XMCommonTrackList<T extends CommonTrackList<Track>> extends XMBean<T> {

    public XMCommonTrackList(T t) {
        super(t);
    }

    public int size() {
        if (getTracks() != null) {
            return getTracks().size();
        }
        return 0;
    }

    public void updateCommonTrackList(XMCommonTrackList<T> commonTrackList) {
        getSDKBean().updateCommonParams(commonTrackList.getSDKBean());
    }

    public void cloneCommonTrackList(XMCommonTrackList<T> commonTrackList) {
        getSDKBean().cloneCommonTrackList(commonTrackList.getSDKBean());
    }

    public void updateCommonParams(XMCommonTrackList<T> commonTrackList) {
        getSDKBean().updateCommonTrackList(commonTrackList.getSDKBean());
    }

    public Map<String, String> getParams() {
        return getSDKBean().getParams();
    }

    public void setParams(Map<String, String> params) {
        getSDKBean().setParams(params);
    }

    public int getTotalCount() {
        return getSDKBean().getTotalCount();
    }

    public void setTotalCount(int totalCount) {
        getSDKBean().setTotalCount(totalCount);
    }

    public int getTotalPage() {
        return getSDKBean().getTotalPage();
    }

    public void setTotalPage(int totalPage) {
        getSDKBean().setTotalPage(totalPage);
    }

    public String toString() {
        return getSDKBean().toString();
    }

    public List<XMTrack> getTracks() {
        List<Track> list = getSDKBean().getTracks();
        List<XMTrack> xmList = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (Track track : list) {
                if (track == null) {
                    continue;
                }
                XMTrack xmTrack = new XMTrack(track);
                xmList.add(xmTrack);
            }
        }
        return xmList;
    }

    public void setTracks(List<XMTrack> xmList) {
        if (xmList == null) {
            getSDKBean().setTracks(null);
            return;
        }
        List<Track> list = new ArrayList<>();
        for (XMTrack xmTrack : xmList) {
            if (xmTrack == null) {
                continue;
            }
            list.add(xmTrack.getSDKBean());
        }
        getSDKBean().setTracks(list);
    }
}