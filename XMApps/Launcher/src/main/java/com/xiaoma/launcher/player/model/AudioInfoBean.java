package com.xiaoma.launcher.player.model;

import java.util.List;

/**
 * 桌面音频列表bean
 * Created by zhushi.
 * Date: 2019/2/13
 */
public class AudioInfoBean {
    //音乐分类列表
    private List<LauncherAudioInfo> music;
    //电台分类
    private List<LauncherAudioInfo> radio;

    public List<LauncherAudioInfo> getMusic() {
        return music;
    }

    public void setMusic(List<LauncherAudioInfo> music) {
        this.music = music;
    }

    public List<LauncherAudioInfo> getRadio() {
        return radio;
    }

    public void setRadio(List<LauncherAudioInfo> radio) {
        this.radio = radio;
    }
}
