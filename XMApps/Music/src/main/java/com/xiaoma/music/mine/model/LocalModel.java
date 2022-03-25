package com.xiaoma.music.mine.model;

import com.xiaoma.music.common.adapter.IGalleryData;
import com.xiaoma.music.kuwo.model.XMMusic;

import java.io.Serializable;

/**
 * Created by ZYao.
 * Date ：2018/10/19 0019
 */
public class LocalModel implements Serializable, IGalleryData {
    private String musicName;
    private String albumName;
    private String singerName;
    private XMMusic xmMusic;

    public LocalModel(XMMusic xmMusic) {
        this.musicName = xmMusic.getName();
        this.albumName = xmMusic.getAlbum();
        this.singerName = xmMusic.getArtist();
        this.xmMusic = xmMusic;
    }

    public XMMusic getXmMusic() {
        return xmMusic;
    }

    @Override
    public String getCoverUrl() {
        return "";
    }

    @Override
    public String getTitleText() {
        return musicName;
    }

    @Override
    public String getFooterText() {
        return albumName;
    }

    @Override
    public String getBottomText() {
        return singerName;
    }
}
