//package com.xiaoma.music.kuwo.manager;
//
//import java.util.List;
//
//import cn.kuwo.base.bean.Music;
//import cn.kuwo.base.bean.MusicList;
//import cn.kuwo.base.bean.MusicQuality;
//import cn.kuwo.mod.download.DownloadMgrImpl;
//import cn.kuwo.mod.download.IDownloadMgr;
//import cn.kuwo.mod.download.bean.DownloadTask;
//
///**
// * <pre>
// *  author : Jir
// *  date : 2018/8/31
// *  description :
// * </pre>
// */
//public class KuwoDownLoad implements IDownloadMgr {
//
//    private DownloadMgrImpl mKuwoDownload;
//
//    private KuwoDownLoad() {
//        mKuwoDownload = new DownloadMgrImpl();
//        mKuwoDownload.init();
//    }
//
//    public static KuwoDownLoad newSingleton() {
//        return Holder.sINSTANCE;
//    }
//
//    interface Holder {
//        KuwoDownLoad sINSTANCE = new KuwoDownLoad();
//    }
//
//    @Override
//    public void addTask(Music music, MusicQuality musicQuality, boolean b) {
//        mKuwoDownload.addTask(music, musicQuality, b);
//    }
//
//    @Override
//    public void addTasks(List<Music> list, MusicQuality musicQuality) {
//        mKuwoDownload.addTasks(list, musicQuality);
//    }
//
//    @Override
//    public boolean deleteTask(DownloadTask downloadTask) {
//        return mKuwoDownload.deleteTask(downloadTask);
//    }
//
//    @Override
//    public boolean deleteAllTasks() {
//        return mKuwoDownload.deleteAllTasks();
//    }
//
//    @Override
//    public List<DownloadTask> getAllTasks() {
//        return mKuwoDownload.getAllTasks();
//    }
//
//    @Override
//    public int getTaskCount() {
//        return mKuwoDownload.getTaskCount();
//    }
//
//    @Override
//    public void startTask(DownloadTask downloadTask, boolean b) {
//        mKuwoDownload.startTask(downloadTask, b);
//    }
//
//    @Override
//    public boolean startAllTasks(boolean b) {
//        return mKuwoDownload.startAllTasks(b);
//    }
//
//    @Override
//    public boolean pauseTask(DownloadTask downloadTask) {
//        return mKuwoDownload.pauseTask(downloadTask);
//    }
//
//    @Override
//    public boolean pauseAllTasks() {
//        return mKuwoDownload.pauseAllTasks();
//    }
//
//    @Override
//    public MusicList getFinishedList() {
//        return mKuwoDownload.getFinishedList();
//    }
//
//
//    @Override
//    public void init() {
//        mKuwoDownload.init();
//    }
//
//    public void release() {
//        mKuwoDownload.release();
//    }
//}
