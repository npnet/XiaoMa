package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.ranks.RankAlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMRankAlbumList extends XMBean<RankAlbumList> {
    public XMRankAlbumList(RankAlbumList rankAlbumList) {
        super(rankAlbumList);
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

    public List<XMAlbum> getRankAlbumList() {
        List<Album> rankAlbumList = getSDKBean().getRankAlbumList();
        List<XMAlbum> xmAlbums = new ArrayList<>();
        if (rankAlbumList != null && !rankAlbumList.isEmpty()) {
            for (Album album : rankAlbumList) {
                if (album == null || album.isPaid()) {
                    continue;
                }
                xmAlbums.add(new XMAlbum(album));
            }
        }
        return xmAlbums;
    }

    public void setRankAlbumList(List<XMAlbum> xmRankAlbumList) {
        if (xmRankAlbumList == null) {
            getSDKBean().setRankAlbumList(null);
            return;
        }
        List<Album> rankAlbumList = new ArrayList<>();
        for (XMAlbum xmAlbum : xmRankAlbumList) {
            if (xmAlbum == null) {
                continue;
            }
            rankAlbumList.add(xmAlbum.getSDKBean());
        }
        getSDKBean().setRankAlbumList(rankAlbumList);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
