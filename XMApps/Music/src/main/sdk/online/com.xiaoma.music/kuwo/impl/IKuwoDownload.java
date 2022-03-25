package com.xiaoma.music.kuwo.impl;

import com.xiaoma.music.kuwo.model.XMDownloadTask;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public interface IKuwoDownload {
    void addTask(XMMusic xmMusic, @IKuwoConstant.IMusicQuality int var2, boolean var3);

    void addTasks(List<XMMusic> var1, @IKuwoConstant.IMusicQuality int var2);

    boolean deleteTask(XMDownloadTask var1);

    boolean deleteAllTasks();

    List<XMDownloadTask> getAllTasks();

    int getTaskCount();

    void startTask(XMDownloadTask var1, boolean var2);

    boolean startAllTasks(boolean var1);

    boolean pauseTask(XMDownloadTask var1);

    boolean pauseAllTasks();

    XMMusicList getFinishedList();
}
