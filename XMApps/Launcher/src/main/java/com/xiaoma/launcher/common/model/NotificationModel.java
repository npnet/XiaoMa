package com.xiaoma.launcher.common.model;

import android.content.Intent;

/**
 * @author taojin
 * @date 2019/3/27
 */
public class NotificationModel {
    private String id;

    private String picUrl;

    private String title;

    private String content;

    private Intent jumpIntent;
    private String packageName;
    private long time;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Intent getJumpIntent() {
        return jumpIntent;
    }

    public void setJumpIntent(Intent jumpIntent) {
        this.jumpIntent = jumpIntent;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
