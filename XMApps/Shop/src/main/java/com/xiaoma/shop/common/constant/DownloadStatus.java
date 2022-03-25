package com.xiaoma.shop.common.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/4/10 0010 11:38
 *       desc：下载状态
 * </pre>
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({DownloadStatus.NONE,
        DownloadStatus.WAITING,
        DownloadStatus.LOADING,
        DownloadStatus.PAUSE,
        DownloadStatus.ERROR,
        DownloadStatus.FINISH})
public @interface DownloadStatus {

    int NONE = 0;         //无状态
    int WAITING = 1;      //等待
    int LOADING = 2;      //下载中
    int PAUSE = 3;        //暂停
    int ERROR = 4;        //错误
    int FINISH = 5;       //完成
}
