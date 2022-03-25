package com.xiaoma.music.export.model;

/**
 * Created by ZYao.
 * Date ：2019/3/19 0019
 */
public class SearchInfo {
    private int id;
    private long createDate;
    private long modifyDate;
    private int catalog;
    private int kwSongId;
    private int songId;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public int getCatalog() {
        return catalog;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }

    public int getKwSongId() {
        return kwSongId;
    }

    public void setKwSongId(int kwSongId) {
        this.kwSongId = kwSongId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SearchInfo{" +
                "id=" + id +
                ", createDate=" + createDate +
                ", modifyDate=" + modifyDate +
                ", catalog=" + catalog +
                ", kwSongId=" + kwSongId +
                ", songId=" + songId +
                ", status=" + status +
                '}';
    }
}
