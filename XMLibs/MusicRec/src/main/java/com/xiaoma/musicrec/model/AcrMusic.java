package com.xiaoma.musicrec.model;

import java.io.Serializable;

/**
 * Created by loren on 2018/3/2 0002.
 */
public class AcrMusic implements Serializable {

    /**
     * status : {"msg":"Success","code":0,"version":"1.0"}
     * metadata : {"music":[{"external_ids":{},"play_offset_ms":18462,"release_date":"2009-01-01","artists":[{"name":"胡歌"}],"external_metadata":{},
     * "title":"美丽的神话 (《神话》电视剧片尾曲)","duration_ms":331000,"album":{"name":"神话 电视剧原声带"},"acrid":"5c00627becb76c3cbd99784e45e19c08","result_from":1,
     * "score":73}],"timestamp_utc":"2018-03-02 07:38:27"}
     * costTime : 1.1170001029968
     * resultType : 0
     */

    private Status status;
    private Metadata metadata;
    private double costTime;
    private int resultType;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public double getCostTime() {
        return costTime;
    }

    public void setCostTime(double costTime) {
        this.costTime = costTime;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }
}
