package com.xiaoma.launcher.player.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoma.player.AudioConstants;

import java.io.Serializable;

/**
 * 桌面音频model
 * Created by zhushi.
 * Date: 2019/2/21
 */
public class LauncherAudioInfo implements MultiItemEntity, Serializable {
    private int id;
    //音源类型 对应 AudioConstants.AudioTypes
    private int audioType;
    private String name;
    private Object logo;
    private int playState;
    private boolean isSelected;
    private String subTitle;
    private String nameEn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAudioType() {
        return audioType;
    }

    public void setAudioType(int audioType) {
        this.audioType = audioType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getLogo() {
        return logo;
    }

    public void setLogo(Object logo) {
        this.logo = logo;
    }

    public int getPlayState() {
        return playState;
    }

    public void setPlayState(int playState) {
        this.playState = playState;
    }

    @Override
    public int getItemType() {
        return audioType;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    @Override
    public String toString() {
        return "LauncherAudioInfo{" +
                "id=" + id +
                ", audioType=" + audioType +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", playState=" + playState +
                '}';
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public boolean isMusicItem() {
        return audioType == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO ||
                audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_USB ||
                audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_BT;
    }
}
