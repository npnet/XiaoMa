package com.xiaoma.music.common.manager;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.model.UploadMusicModel;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.utils.GsonHelper;

import cn.kuwo.base.bean.Music;

/**
 * Created by ZYao.
 * Date ：2019/1/2 0002
 */
public class UploadPlayManager {
    private long startPlayTime = 0;
    @AudioSource
    private int currentAudioSource = AudioSource.NONE;
    private long mPlayTime = 0;

    public static UploadPlayManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final UploadPlayManager instance = new UploadPlayManager();
    }

    public synchronized void initPlayTime() {
        startPlayTime = System.currentTimeMillis();
        currentAudioSource = AudioSourceManager.getInstance().getCurrAudioSource();
    }

    public synchronized void uploadPlayTime(String type) {
        long endPlayTime = System.currentTimeMillis();
        final long playTime = endPlayTime - startPlayTime;
        mPlayTime = mPlayTime + playTime;
        if (playTime > 2 * 1000 && currentAudioSource == AudioSourceManager.getInstance().getCurrAudioSource()) {
            XmAutoTracker.getInstance().onEventPlayTime(mPlayTime, type);
            // 埋点上报
            XmTracker.getInstance().uploadEvent(mPlayTime, TrackerCountType.MUSICTIME.getType());
            startPlayTime = System.currentTimeMillis();
            mPlayTime = 0;
        }
    }

    public void uploadBtPlayInfo(BTMusic music) {
        if (music == null) {
            return;
        }
        UploadMusicModel musicModel = new UploadMusicModel();
        musicModel.setMusicName(music.getTitle());
        musicModel.setArtist(music.getArtist());
        musicModel.setDuration(music.getDuration());
        musicModel.setType("bluetooth");
        XmAutoTracker.getInstance().onEventListenInfo(GsonHelper.toJson(musicModel));
    }

    public void uploadUsbPlayInfo(UsbMusic music) {
        if (music == null) {
            return;
        }
        UploadMusicModel musicModel = new UploadMusicModel();
        musicModel.setMusicName(music.getName());
        musicModel.setArtist(music.getArtist());
        musicModel.setDuration(music.getDuration());
        musicModel.setType("usb");
        XmAutoTracker.getInstance().onEventListenInfo(GsonHelper.toJson(musicModel));
    }

    public void uploadKwPlayInfo(Music music) {
        if (music == null) {
            return;
        }
        UploadMusicModel musicModel = new UploadMusicModel();
        musicModel.setId(music.rid);
        musicModel.setMusicName(music.name.replace("&nbsp;", " "));
        musicModel.setArtist(music.artist.replace("&nbsp;", " "));
        musicModel.setDuration(music.duration);
        musicModel.setType("kuwo");
        XmAutoTracker.getInstance().onEventListenInfo(GsonHelper.toJson(musicModel));
    }

    public void uploadCollectMusicCount() {
        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.MUSICCOLLECT.getType());
    }

}
