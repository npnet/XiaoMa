package com.xiaoma.music.player.model;

import com.xiaoma.music.kuwo.model.XMMusic;

/**
 * Created by ZYao.
 * Date ：2018/10/22 0022
 */
public class CollectEventBean{
    private boolean isFavorite;
    private XMMusic music;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public XMMusic getMusic() {
        return music;
    }

    public void setMusic(XMMusic music) {
        this.music = music;
    }
}
