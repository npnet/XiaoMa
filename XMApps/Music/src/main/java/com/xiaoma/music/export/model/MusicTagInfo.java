package com.xiaoma.music.export.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by ZYao.
 * Date ：2019/5/8 0008
 */
@Table("MusicTagInfo")
public class MusicTagInfo {
    /**
     * songId : 12345678
     * songName : 挪威的森林
     * tagId : 10
     * tag : 热闹
     */

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long id;

    private int songId;
    private String songName;
    private int tagId;
    private String tag;

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
