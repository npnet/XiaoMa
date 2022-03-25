package com.xiaoma.service.common.model;

import com.xiaoma.utils.GsonHelper;

/**
 * @author taojin
 * @date 2019/4/17
 */
public class BatchInfo {
    private String id;
    private String value;
    private String h;
    private String i;
    private String j;
    private String k;


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

    public String getJ() {
        return j;
    }

    public void setJ(String j) {
        this.j = j;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String toJson(){
        return GsonHelper.toJson(this);
    }
}
