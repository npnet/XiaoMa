package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.base.bean.quku.AlbumInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMAlbumInfo extends XMBean<AlbumInfo> {
    public XMAlbumInfo(AlbumInfo albumInfo) {
        super(albumInfo);
    }

    public long getArtistID() {
        return getSDKBean().getArtistID();
    }

    public void setArtistID(long id) {
        getSDKBean().setArtistID(id);
    }

    public String getArtist() {
        return getSDKBean().getArtist();
    }

    public void setArtist(String artist) {
        getSDKBean().setArtist(artist);
    }

    public int getHot() {
        return getSDKBean().getHot();
    }

    public void setHot(String hot) {
        getSDKBean().setHot(hot);
    }

    public String getCompany() {
        return getSDKBean().getCompany();
    }

    public void setCompany(String company) {
        getSDKBean().setCompany(company);
    }

    public String getPublish() {
        return getSDKBean().getPublish();
    }

    public void setPublish(String publish) {
        getSDKBean().setPublish(publish);
    }

    public String getDigest() {
        return getSDKBean().getDigest();
    }

    public void setDigest(String digest) {
        getSDKBean().setDigest(digest);
    }

    public int getMusicCount() {
        return getSDKBean().getMusicCount();
    }

    public void setMusicCount(String count) {
        getSDKBean().setMusicCount(count);
    }

    public void setMusicCount(int count) {
        getSDKBean().setMusicCount(count);
    }
}
