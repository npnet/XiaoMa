package com.xiaoma.launcher.recmusic.model;


import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.xiaoma.player.AudioInfo;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author zs
 * @date 2018/3/2 0002.
 * 识别记录
 */
@Table("musicrecord")
public class MusicRecord implements Serializable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long _id;

    private String singerName;

    private String name;
    private String singerCoverUrl;
    private String songId;
    private String recId;
    private boolean saveType;
    private AudioInfo audioInfo ;

    public String getRecId() {
        return recId;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }

    public boolean isSaveType() {
        return saveType;
    }
    //true为后台识别，false为rec识别
    public void setSaveType(boolean saveType) {
        this.saveType = saveType;
    }

    public AudioInfo getAudioInfo() {
        return audioInfo;
    }

    public void setAudioInfo(AudioInfo audioInfo) {
        this.audioInfo = audioInfo;
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

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicRecord that = (MusicRecord) o;
        return saveType == that.saveType &&
                Objects.equals(singerName, that.singerName) &&
                Objects.equals(name, that.name) &&
                Objects.equals(singerCoverUrl, that.singerCoverUrl) &&
                Objects.equals(songId, that.songId) &&
                Objects.equals(recId, that.recId) &&
                Objects.equals(audioInfo, that.audioInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(singerName, name, singerCoverUrl, songId, recId, saveType, audioInfo);
    }
}
