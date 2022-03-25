package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/25
 */
public class XMRelativeAlbums extends XMBean<RelativeAlbums> {
    public XMRelativeAlbums(RelativeAlbums relativeAlbums) {
        super(relativeAlbums);
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

    public List<XMAlbum> getRelativeAlbumList() {
        List<Album> relativeAlbumList = getSDKBean().getRelativeAlbumList();
        List<XMAlbum> xmRelativeAlbumList = new ArrayList<>();
        if (relativeAlbumList != null && !relativeAlbumList.isEmpty()) {
            for (Album album : relativeAlbumList) {
                if (album == null) {
                    continue;
                }
                xmRelativeAlbumList.add(new XMAlbum(album));
            }
        }
        return xmRelativeAlbumList;
    }

    public void setRelativeAlbumList(List<XMAlbum> xmRelativeAlbumList) {
        if (xmRelativeAlbumList == null) {
            getSDKBean().setRelativeAlbumList(null);
            return;
        }
        List<Album> relativeAlbumList = new ArrayList<>();
        for (XMAlbum xmAlbum : xmRelativeAlbumList) {
            if (xmAlbum == null) {
                continue;
            }
            relativeAlbumList.add(xmAlbum.getSDKBean());
        }
        getSDKBean().setRelativeAlbumList(relativeAlbumList);

    }

    public String toString() {
        return getSDKBean().toString();
    }
}
