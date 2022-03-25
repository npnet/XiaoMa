package com.xiaoma.music.online.model;

import com.xiaoma.music.common.adapter.IGalleryData;
import com.xiaoma.music.kuwo.model.XMSongListInfo;

import java.io.Serializable;

/**
 * Created by ZYao.
 * Date ：2018/10/23 0023
 */
public class CategoryDetailModel implements Serializable, IGalleryData {
    private String name;
    private String iconUrl;
    private XMSongListInfo songListInfo;

    public CategoryDetailModel(String name, String iconUrl, XMSongListInfo songListInfo) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.songListInfo = songListInfo;
    }

    public XMSongListInfo getSongListInfo() {
        return songListInfo;
    }

    @Override
    public String getCoverUrl() {
        return iconUrl;
    }

    @Override
    public String getTitleText() {
        return name;
    }

    @Override
    public String getFooterText() {
        return null;
    }

    @Override
    public String getBottomText() {
        return null;
    }
}
