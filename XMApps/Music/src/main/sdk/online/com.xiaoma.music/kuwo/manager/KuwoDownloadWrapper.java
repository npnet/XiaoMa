//package com.xiaoma.music.kuwo.manager;
//
//import com.xiaoma.music.kuwo.impl.IKuwoConstant;
//import com.xiaoma.music.kuwo.impl.IKuwoDownload;
//import com.xiaoma.music.kuwo.model.XMDownloadTask;
//import com.xiaoma.music.kuwo.model.XMMusic;
//import com.xiaoma.music.kuwo.model.XMMusicList;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.kuwo.base.bean.Music;
//import cn.kuwo.base.bean.MusicList;
//import cn.kuwo.base.bean.MusicQuality;
//import cn.kuwo.mod.download.bean.DownloadTask;
//
///**
// * Created by ZYao.
// * Date ：2018/10/16 0016
// */
//public class KuwoDownloadWrapper implements IKuwoDownload {
//
//    private KuwoDownLoad kuwoDownLoad;
//
//    public KuwoDownloadWrapper() {
//        kuwoDownLoad = KuwoDownLoad.newSingleton();
//    }
//
//    public static KuwoDownloadWrapper getInstance() {
//        return InstanceHolder.instance;
//    }
//
//    private static class InstanceHolder {
//        static final KuwoDownloadWrapper instance = new KuwoDownloadWrapper();
//    }
//
//    @Override
//    public void addTask(XMMusic xmMusic, @IKuwoConstant.IMusicQuality int var2, boolean var3) {
//        Music sdkBean = xmMusic.getSDKBean();
//        MusicQuality musicQuality = IKuwoConstant.getMusicQuality(var2);
//        kuwoDownLoad.addTask(sdkBean, musicQuality, var3);
//    }
//
//    @Override
//    public void addTasks(List<XMMusic> xmMusicList, @IKuwoConstant.IMusicQuality int var2) {
//        List<Music> musicList = new ArrayList<>();
//        for (XMMusic xmMusic : xmMusicList) {
//            if (xmMusic == null) {
//                continue;
//            }
//            musicList.add(xmMusic.getSDKBean());
//        }
//        MusicQuality musicQuality = IKuwoConstant.getMusicQuality(var2);
//        kuwoDownLoad.addTasks(musicList, musicQuality);
//    }
//
//    @Override
//    public boolean deleteTask(XMDownloadTask var1) {
//        return kuwoDownLoad.deleteTask(var1.getSDKBean());
//    }
//
//    @Override
//    public boolean deleteAllTasks() {
//        return kuwoDownLoad.deleteAllTasks();
//    }
//
//    @Override
//    public List<XMDownloadTask> getAllTasks() {
//        final List<DownloadTask> downloadTasks = kuwoDownLoad.getAllTasks();
//        List<XMDownloadTask> xmDownloadTaskList = new ArrayList<>();
//        for (DownloadTask downloadTask : downloadTasks) {
//            if (downloadTask == null) {
//                continue;
//            }
//            xmDownloadTaskList.add(new XMDownloadTask(downloadTask));
//        }
//        return xmDownloadTaskList;
//    }
//
//    @Override
//    public int getTaskCount() {
//        return kuwoDownLoad.getTaskCount();
//    }
//
//    @Override
//    public void startTask(XMDownloadTask var1, boolean var2) {
//        kuwoDownLoad.startTask(var1.getSDKBean(), var2);
//    }
//
//    @Override
//    public boolean startAllTasks(boolean var1) {
//        return kuwoDownLoad.startAllTasks(var1);
//    }
//
//    @Override
//    public boolean pauseTask(XMDownloadTask var1) {
//        return kuwoDownLoad.pauseTask(var1.getSDKBean());
//    }
//
//    @Override
//    public boolean pauseAllTasks() {
//        return kuwoDownLoad.pauseAllTasks();
//    }
//
//    @Override
//    public XMMusicList getFinishedList() {
//        final MusicList finishedList = kuwoDownLoad.getFinishedList();
//        if (finishedList == null) {
//            return null;
//        }
//        return new XMMusicList(finishedList);
//    }
//}
