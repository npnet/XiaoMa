package com.xiaoma.plugin.download;

/**
 * Created by Administrator on 2016/12/12 0012.
 * 下载状态集合
 */
public enum DownLoadState {
    WAITTING(1),//队列等待中
    DOWNLOADING(2),//下载中
    PAUSE(3),//暂停中
    CANCEL(4),//取消中
    FINISH(5), //下载完成
    FAILED(6);//下载失败

    int state;

    DownLoadState(int state) {
        this.state = state;
    }
}
