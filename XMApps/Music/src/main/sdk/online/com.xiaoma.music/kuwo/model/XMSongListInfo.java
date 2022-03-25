package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.base.bean.quku.SongListInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMSongListInfo extends XMBean<SongListInfo> {
    public XMSongListInfo(SongListInfo songListInfo) {
        super(songListInfo);
    }

    public String getInfo() {
        return getSDKBean().getInfo();
    }

    public void setInfo(String var1) {
        getSDKBean().setInfo(var1);
    }
}
