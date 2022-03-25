package com.xiaoma.app.model;

import com.xiaoma.utils.GsonHelper;

/**
 * @author taojin
 * @date 2019/4/17
 */
public class BatchInfo {
    //应用id
    private String id;
    //应用名称
    private String value;
    //versioncode
    private String h;
    //versionName
    private String i;

    public BatchInfo(String id, String value, String h, String i) {
        this.id = id;
        this.value = value;
        this.h = h;
        this.i = i;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String toJson() {
        return GsonHelper.toJson(this);
    }
}
