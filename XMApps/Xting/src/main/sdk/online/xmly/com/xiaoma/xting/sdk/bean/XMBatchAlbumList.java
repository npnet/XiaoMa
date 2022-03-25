package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMBatchAlbumList extends XMBean<BatchAlbumList> {
    public XMBatchAlbumList(BatchAlbumList batchAlbumList) {
        super(batchAlbumList);
    }

    public List<XMAlbum> getAlbums() {
        List<Album> albums = getSDKBean().getAlbums();
        List<XMAlbum> xmAlbums = new ArrayList<>();
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

    public String toString() {
        return getSDKBean().toString();
    }

}
