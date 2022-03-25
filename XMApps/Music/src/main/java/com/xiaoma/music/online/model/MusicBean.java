package com.xiaoma.music.online.model;

import com.xiaoma.music.kuwo.model.XMMusic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zs
 * @date 2018/10/18 0018.
 */
public class MusicBean implements Serializable {

    private String musicName;
    private String singer;
    private String albumName;
    private String iconUrl;

    public MusicBean(String musicName, String singer, String albumName, String iconUrl) {
        this.musicName = musicName;
        this.singer = singer;
        this.albumName = albumName;
        this.iconUrl = iconUrl;
    }

    public static List<MusicBean> convert2Music(List<XMMusic> list) {
        List<MusicBean> musicBeans = new ArrayList<>(list.size());
        for (XMMusic xmMusic : list) {
            musicBeans.add(new MusicBean(xmMusic.getSDKBean().name, xmMusic.getSDKBean().artist, xmMusic.getSDKBean().album, xmMusic.getSDKBean()
                    .mvIconUrl));
        }
        return musicBeans;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
