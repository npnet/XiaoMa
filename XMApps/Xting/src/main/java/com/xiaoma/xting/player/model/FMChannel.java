package com.xiaoma.xting.player.model;

import com.xiaoma.xting.sdk.bean.XMAlbum;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/10/16
 */
public class FMChannel {

    private String name;
    private String coverUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public static List<FMChannel> similarAlbum2FMChannels(List<XMAlbum> list) {
        List<FMChannel> channelList = new ArrayList<>();
        FMChannel bean;
        for (XMAlbum album : list) {
            //移除错误资源
            if (album.getCreatedAt() == 0) {
                continue;
            }
            bean = new FMChannel();
            bean.setCoverUrl(album.getValidCover());
            bean.setName(album.getAlbumTitle());

            channelList.add(bean);
        }
        return channelList;
    }
}
