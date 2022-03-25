package com.xiaoma.model.pratice;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/05
 *     desc   :
 * </pre>
 */
public class PlayMusicBean implements MultiItemEntity {

    private int itemType;

    private String singerName;
    private String name;
    private String singerCoverUrl;
    private long id;
    private long songId;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSingerCoverUrl() {
        return singerCoverUrl;
    }

    public void setSingerCoverUrl(String singerCoverUrl) {
        this.singerCoverUrl = singerCoverUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    @Override
    public String toString() {
        return "PlayMusicBean{" +
                ", itemType=" + itemType +
                ", singerName='" + singerName + '\'' +
                ", name='" + name + '\'' +
                ", singerCoverUrl='" + singerCoverUrl + '\'' +
                ", id=" + id +
                ", songId=" + songId +
                '}';
    }
}
