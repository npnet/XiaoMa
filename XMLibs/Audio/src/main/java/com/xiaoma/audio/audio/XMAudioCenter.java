package com.xiaoma.audio.audio;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.aidl.model.MusicInfo;
import com.xiaoma.aidl.model.MusicPlayStatus;
import com.xiaoma.audio.base.XMBaseAudio;
import com.xiaoma.audio.constants.XMMusicConstants;
import com.xiaoma.audio.listener.IMusicStatusChangeListener;
import com.xiaoma.audio.model.ForegroundAudioApp;
import com.xiaoma.audio.model.MusicPlayMode;
import com.xiaoma.audio.processor.XMMusicCenter;
import com.xiaoma.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//音源焦点管理中心
public class XMAudioCenter {

    private static XMAudioCenter instance;
    private List<XMBaseAudio> allAudios = new ArrayList<>();
    private HashMap<XMBaseAudio, Boolean> playStatusStash = new HashMap<>();
    private XMBaseAudio currentMusicAudio;
    private XMBaseAudio mXmKWMusicAudio;
    private XMBaseAudio mXmXTingAudio;
    private XMBaseAudio mXmQQMusicAudio;
    private XMBaseAudio mSystemLocalMusicAudio;
    private ForegroundAudioApp mForegroundAudioApp = ForegroundAudioApp.NONE;
    private XMMusicCenter mXMMusicCenter;
    private ArrayList<IMusicStatusChangeListener> mIMusicStatusChangeListeners = new ArrayList<>();
    private Context context;

    public static XMAudioCenter getInstance() {
        if (instance == null) {
            synchronized (XMAudioCenter.class) {
                if (instance == null) {
                    instance = new XMAudioCenter();
                }
            }
        }
        return instance;
    }

    public void addMusicStatusChangeListener(IMusicStatusChangeListener listen){
        if (listen == null || mIMusicStatusChangeListeners.contains(listen)) {
            return;
        }
        mIMusicStatusChangeListeners.add(listen);
    }

    public void removeMusicStatusChangeListener(IMusicStatusChangeListener listen){
        if (listen == null || !mIMusicStatusChangeListeners.contains(listen)) {
            return;
        }
        mIMusicStatusChangeListeners.remove(listen);
    }

    public void onPlayStatusChanged(ForegroundAudioApp foregroundAudioApp, MusicInfo musicInfo) {
        for (IMusicStatusChangeListener iMusicStatusChangeListener : mIMusicStatusChangeListeners) {
            iMusicStatusChangeListener.onPlayStatusChanged(foregroundAudioApp, musicInfo);
        }
    }

    public boolean isSystemLocalMusicAudio() {
        if (XMMusicConstants.ALLOW_LOCAL_AUDIO) {
            return currentMusicAudio instanceof SystemLocalMusicAudio;
        }
        return false;
    }

    public void updateCurrentAudio(XMBaseAudio xmAudio) {
        this.currentMusicAudio = xmAudio;
    }

    //音源应用界面焦点获取和丢失时同步
    public void updateCurrentAudioByInForeground(ForegroundAudioApp foregroundAudioApp) {
        this.mForegroundAudioApp = foregroundAudioApp;
    }

    public void removeCurrentAudio(XMBaseAudio xmAudio) {
        currentMusicAudio = null;
        allAudios.remove(xmAudio);
        if (allAudios.size() <= 0) {
            return;
        }
        currentMusicAudio = allAudios.get(0);
    }

    public void release() {
        for (XMBaseAudio audio : allAudios) {
            audio.release();
        }
    }

    public XMMusicCenter getXMMusicCenter() {
        return mXMMusicCenter;
    }

    public synchronized void init(Context context) {
        this.context = context;
        mXMMusicCenter = new XMMusicCenter();
        mXMMusicCenter.init(context);
        allAudios.clear();
        mXmKWMusicAudio = new XMKWMusicAudio(context, this);
        mXmXTingAudio = new XMXTingAudio(context, this);
        mXmQQMusicAudio = new XMQQMusicAudio(context, this);
        mSystemLocalMusicAudio = new SystemLocalMusicAudio(context, this);
        allAudios.add(mXmKWMusicAudio);
        allAudios.add(mXmXTingAudio);
        allAudios.add(mXmQQMusicAudio);
        allAudios.add(mSystemLocalMusicAudio);
        for (XMBaseAudio audio : allAudios) {
            audio.init();
        }
    }

    public void play() {
        if (currentMusicAudio != null) {
            changeCurrentXMAudioByInForeground();
            currentMusicAudio.play();
        }
    }

    public void pause() {
        if (currentMusicAudio != null) {
            currentMusicAudio.pause();
        }
    }

    public void playNext() {
        if (currentMusicAudio != null) {
            currentMusicAudio.playNext();
        }
    }

    public void playPre() {
        if (currentMusicAudio != null) {
            currentMusicAudio.playPre();
        }
    }

    public void setPlayMode(MusicPlayMode musicPlayMode) {
        if (currentMusicAudio != null) {
            currentMusicAudio.setPlayMode(musicPlayMode);
        }
    }

    public void setPlayModeRandom() {
        if (currentMusicAudio != null) {
            currentMusicAudio.setPlayModeRandom();
        }
    }

    public void subscribeProgram() {
        if (currentMusicAudio != null) {
            currentMusicAudio.subscribeProgram();
        }
    }

    public void unSubscribeProgram() {
        if (currentMusicAudio != null) {
            currentMusicAudio.unSubscribeProgram();
        }
    }

    public XMBaseAudio getCurrentMusicAudio() {
        updateCurrentAudio(pickCurrentAudio());
        return currentMusicAudio;
    }

    //vr抢占焦点前同步状态
    public void stashPlayStatus() {
        clearStash();
        for (XMBaseAudio audio : allAudios) {
            playStatusStash.put(audio, MusicPlayStatus.PLAYING == audio.getPlayStatus());
        }
    }

    //vr释放焦点前同步状态
    public void clearStash() {
        playStatusStash.clear();
    }

    private XMBaseAudio getCurrentAudioByInForeground(ForegroundAudioApp foregroundAudioApp){
        switch (foregroundAudioApp){
            case XTingMusic:
                return mXmXTingAudio;
            case ClubMusic:
                return null;
            case KWMusic:
                return mXmKWMusicAudio;
            case QQMusic:
                return mXmQQMusicAudio;
            case SystemLocalMusic:
                return mSystemLocalMusicAudio;
            case NONE:
            default:
                return null;
        }
    }

    private XMBaseAudio getCurrentPlayingAudio() {
        for (XMBaseAudio audio : allAudios) {
            Boolean playStash = playStatusStash.get(audio);
            if (playStash != null && playStash) {
                return audio;
            }
        }
        return null;
    }

    private XMBaseAudio pickCurrentAudio() {
        //优先正在播放的音频
        XMBaseAudio audio = getCurrentPlayingAudio();
        clearStash();
        if (audio != null) {
            return audio;
        }
        //当前没有播放音源，音源应用前台时优先相应
        audio = getCurrentAudioByInForeground(mForegroundAudioApp);
        if (audio != null) {
            updateCurrentAudioByInForeground(ForegroundAudioApp.NONE);
            return audio;
        }
        //返回默认的当前音频
        audio = currentMusicAudio;
        return audio;
    }

    private void changeCurrentXMAudioByInForeground() {
        String packageName = AppUtils.getAppInForeground(context).get(0);
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        switch (packageName) {
            case XMMusicConstants.CLUB_PACKAGE_NAME :
                updateCurrentAudioByInForeground(ForegroundAudioApp.ClubMusic);
                break;
            case XMMusicConstants.XTING_PACKAGE_NAME :
                updateCurrentAudioByInForeground(ForegroundAudioApp.XTingMusic);
                break;
            case XMMusicConstants.QQMUSIC_PACKAGE_NAME :
                updateCurrentAudioByInForeground(ForegroundAudioApp.QQMusic);
                break;
            case XMMusicConstants.KWMUSIC_PACKAGE_NAME :
                updateCurrentAudioByInForeground(ForegroundAudioApp.KWMusic);
                break;
        }
        getCurrentMusicAudio();
    }

}
