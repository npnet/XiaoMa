package com.xiaoma.music.common.model;

import com.xiaoma.music.common.adapter.IGalleryData;

import java.io.Serializable;

/**
 * 推荐列表专辑信息
 *
 * @author zs
 * @date 2018/10/10 0010.
 */
public class Album implements Serializable, IGalleryData {

    private String title;
    private String coverUrl;

    public Album(String title, String coverUrl) {
        this.title = title;
        this.coverUrl = coverUrl;
    }

    @Override
    public String getCoverUrl() {
        return coverUrl;
    }

    @Override
    public String getTitleText() {
        return title;
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
