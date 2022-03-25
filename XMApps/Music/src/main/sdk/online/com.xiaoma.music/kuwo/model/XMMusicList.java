package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.ListType;
import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.MusicList;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMMusicList extends XMBean<MusicList> {
    public XMMusicList(MusicList music) {
        super(music);
    }

    public XMListType getType() {
        ListType type = getSDKBean().getType();
        return XMListType.convertListType(type);
    }


    public String getShowName() {
        if (getSDKBean() == null) {
            return "";
        }
        return getSDKBean().getShowName();
    }

    public List<XMMusic> toList() {
        if (getSDKBean() == null) {
            return new ArrayList<>();
        }
        final List<Music> musicList = getSDKBean().toList();
        List<XMMusic> musics = new ArrayList<>();
        for (Music music : musicList) {
            if (music == null) {
                continue;
            }
            musics.add(new XMMusic(music));
        }
        return musics;
    }
}
