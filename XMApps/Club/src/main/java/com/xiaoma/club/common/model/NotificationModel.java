package com.xiaoma.club.common.model;

import android.content.Intent;
import android.util.Log;

/**
 * Created by 凌艳 on 2019/3/19.
 */
public class NotificationModel {

    private String picPath;

    private String title;

    private String content;

    private Intent jumpIntent;

    private String id;

    private long messageTime;

    private boolean showHeadsUp;

    public boolean isShowHeadsUp() {
        return showHeadsUp;
    }

    public void setShowHeadsUp(boolean showHeadsUp) {
        this.showHeadsUp = showHeadsUp;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }


    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
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
        Log.d("intent", "getJumpIntent: " + jumpIntent.getStringExtra("hx_chat_id"));
        return jumpIntent;
    }

    public void setJumpIntent(Intent jumpIntent) {
        this.jumpIntent = jumpIntent;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
