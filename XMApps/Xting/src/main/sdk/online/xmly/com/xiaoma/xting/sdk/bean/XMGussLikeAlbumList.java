package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/25
 */
public class XMGussLikeAlbumList extends XMBean<GussLikeAlbumList> {
    public XMGussLikeAlbumList(GussLikeAlbumList gussLikeAlbumList) {
        super(gussLikeAlbumList);
    }

    public List<XMAlbum> getAlbumList() {
        ArrayList<XMAlbum> xmAlbums = new ArrayList<>();
        List<Album> albums = getSDKBean().getAlbumList();
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

    public void setAlbumList(List<XMAlbum> xmAlbums) {
        if (xmAlbums == null) {
            getSDKBean().setAlbumList(null);
            return;
        }
        List<Album> albums = new ArrayList<>();
        for (XMAlbum xmAlbum : xmAlbums) {
            if (xmAlbum == null) {
                continue;
            }
            albums.add(xmAlbum.getSDKBean());
        }
        getSDKBean().setAlbumList(albums);
    }
}
