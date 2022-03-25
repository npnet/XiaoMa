package com.xiaoma.config.bean;

/**
 * Created by youthyj on 2018/9/17.
 */
public class Env {
    private String business;  // 普通业务(URL前缀)
    private String file;      // 文件业务(URL前缀)
    private String token;     // 获取token(URL前缀)
    private String log;       // 日志上报(URL前缀)

    public Env() {

    }

    public String getBusiness() {
        return business;
    }

    public String getFile() {
        return file;
    }

    public String getToken() {
        return token;
    }

    public String getLog() {
        return log;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
