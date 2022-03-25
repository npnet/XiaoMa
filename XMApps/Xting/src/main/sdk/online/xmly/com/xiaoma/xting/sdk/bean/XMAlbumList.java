package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMAlbumList extends XMBean<AlbumList> {

    public XMAlbumList(AlbumList albumList) {
        super(albumList);
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

    public List<XMAlbum> getAlbums() {
        ArrayList<XMAlbum> xmAlbums = new ArrayList<>();
        List<Album> albums = getSDKBean().getAlbums();
        if (albums != null && !albums.isEmpty()) {
            for (Album album : albums) {
                if (album == null) {
                    continue;
                }
                xmAlbums.add(new XMAlbum(album));
            }
        }
        return xmAlbums;
    }

    public void setAlbums(List<XMAlbum> xmAlbums) {
        if (xmAlbums == null) {
            getSDKBean().setAlbums(null);
            return;
        }
        List<Album> albums = new ArrayList<>();
        for (XMAlbum xmAlbum : xmAlbums) {
            if (xmAlbum == null) {
                continue;
            }
            albums.add(xmAlbum.getSDKBean());
        }
        getSDKBean().setAlbums(albums);
    }


}
