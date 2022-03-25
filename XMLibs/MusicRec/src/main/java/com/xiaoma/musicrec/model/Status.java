package com.xiaoma.musicrec.model;

import java.io.Serializable;

/**
 * @author zs
 * @date 2018/3/2 0002.
 */

public class Status implements Serializable {

    /**
     * msg : Success
     * code : 0
     * version : 1.0
     */

    private String msg;
    private int code;
    private String version;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
