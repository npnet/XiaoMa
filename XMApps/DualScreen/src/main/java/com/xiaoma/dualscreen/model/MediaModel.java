package com.xiaoma.dualscreen.model;

import com.xiaoma.dualscreen.constant.MediaType;

/**
 * @author: iSun
 * @date: 2019/1/8 0008
 */
public class MediaModel {
    public MediaType type;
    public String name;
    public String icon;
    public String singer;
    public int progress;
    public int maxProgress;

    public MediaType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getSinger() {
        return singer;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public MediaModel setName(String name) {
        this.name = name;
        return this;
    }

    public MediaModel setSinger(String singer) {
        this.singer = singer;
        return this;
    }

    public MediaModel setIcon(String icon) {
        this.icon = icon;
        return this;
    }

}
