package com.xiaoma.app.model;

/**
 * Created by zhushi.
 * Date: 2018/11/7
 */
public class CancelItemEvent {
    //剩余下载任务数目
    public int downloadSize;
    //下载tag
    public String tag;

    public CancelItemEvent(int downloadSize, String tag) {
        this.downloadSize = downloadSize;
        this.tag = tag;
    }
}
