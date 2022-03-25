package com.xiaoma.musicrec.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author zs
 * @date 2018/3/2 0002.
 */

public class Metadata implements Serializable {

    /**
     * music : [{"external_ids":{},"play_offset_ms":18462,"release_date":"2009-01-01","artists":[{"name":"胡歌"}],"external_metadata":{},
     * "title":"美丽的神话 (《神话》电视剧片尾曲)","duration_ms":331000,"album":{"name":"神话 电视剧原声带"},"acrid":"5c00627becb76c3cbd99784e45e19c08",
     * "result_from":1,"score":73}]
     * timestamp_utc : 2018-03-02 07:38:27
     */

    private String timestamp_utc;
    private List<Music> music;

    public String getTimestamp_utc() {
        return timestamp_utc;
    }

    public void setTimestamp_utc(String timestamp_utc) {
        this.timestamp_utc = timestamp_utc;
    }

    public List<Music> getMusic() {
        return music;
    }

    public void setMusic(List<Music> music) {
        this.music = music;
    }

}
