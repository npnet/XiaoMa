package com.xiaoma.event;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by ZhangYao.
 * Date ：2017/1/17 0017
 */

@Table("eventinfo")
public class EventInfo implements Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    private long od;
    private long uid;
    private String t;
    private String c;

    public EventInfo() {
    }

    public EventInfo(long od, String c, String t, long uid) {
        this.od = od;
        this.c = c;
        this.t = t;
        this.uid = uid;
    }

    public long getOd() {
        return od;
    }

    public void setOd(long od) {
        this.od = od;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }
}
