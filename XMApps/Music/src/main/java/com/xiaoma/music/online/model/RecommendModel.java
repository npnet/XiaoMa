package com.xiaoma.music.online.model;

import com.xiaoma.music.common.adapter.IGalleryData;
import com.xiaoma.music.kuwo.model.XMBaseQukuItem;

import java.io.Serializable;

/**
 * Created by ZYao.
 * Date ：2018/10/24 0024
 */
public class RecommendModel implements Serializable, IGalleryData {
    private String name;
    private String iconUrl;
    private XMBaseQukuItem songListInfo;

    public RecommendModel(String name, String iconUrl, XMBaseQukuItem songListInfo) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.songListInfo = songListInfo;
    }

    public XMBaseQukuItem getBaseQukuInfo() {
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
