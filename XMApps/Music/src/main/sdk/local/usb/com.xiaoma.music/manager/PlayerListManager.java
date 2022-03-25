package com.xiaoma.music.manager;

import android.content.Context;
import android.support.annotation.Nullable;

import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.model.SwitchStatus;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.player.model.PlayMode;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by ZYao.
 * Date ：2018/12/21 0021
 */
public class PlayerListManager implements IUsbMusicList {

    private ArrayList<UsbMusic> mUsbPlayList = new ArrayList<>();
    private ArrayList<UsbMusic> mDefaultUsbPlayList = new ArrayList<>();
    private CopyOnWriteArraySet<OnUsbPlayListChangedListener> listChangedListeners = new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<IUsbPlayModeChangedListener> playModeChangedListeners = new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<IUsbPlayTipsListener> playTipsListeners = new CopyOnWriteArraySet<>();
    private int playMode = PlayMode.LIST_ORDER;


    @Override
    public void addStateChangeListen(IUsbPlayModeChangedListener listen) {
        if (listen == null || playModeChangedListeners.contains(listen)) {
            return;
        }
        playModeChangedListeners.add(listen);
    }

    @Override
    public void removeStateChangeListen(IUsbPlayModeChangedListener listen) {
        if (listen == null || !playModeChangedListeners.contains(listen)) {
            return;
        }
        playModeChangedListeners.remove(listen);
    }

    @Override
    public void init(Context context) {
        IJKPlayerManager ijkPlayerManager = IJKPlayerManager.getInstance();
        ijkPlayerManager.addMusicChangeListener(new OnUsbMusicChangedListener() {
            private boolean mPlayFailed;

            @Override
            public void onBuffering(UsbMusic usbMusic) {

            }

            @Override
            public void onPlay(UsbMusic usbMusic) {
                mPlayFailed = false;
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onProgressChange(long progressInMs, long totalInMs) {

            }

            @Override
            public void onPlayFailed(int errorCode) {
                mPlayFailed = true;
            }

            @Override
            public void onPlayStop() {

            }

            @Override
            public void onCompletion() {
                if (!mPlayFailed) {
                    autoPlayNext();
                }
            }
        });
    }

    @Override
    public void addUsbMusicList(List<UsbMusic> musicList) {
        if (!ListUtils.isEmpty(musicList)) {
            mUsbPlayList.clear();
            mDefaultUsbPlayList.clear();
            mUsbPlayList.addAll(musicList);
            mDefaultUsbPlayList.addAll(musicList);
            dispatchMusicList();
        }
    }

    @Override
    public void replaceUsbMusicList(List<UsbMusic> musicList) {
        if (!ListUtils.isEmpty(musicList)) {
            mUsbPlayList.clear();
            mUsbPlayList.addAll(musicList);
            dispatchMusicList();
        }
    }

    @Override
    public void removeUsbMusicList(List<UsbMusic> musicList) {
        if (!ListUtils.isEmpty(musicList)) {
            mUsbPlayList.removeAll(musicList);
            dispatchMusicList();
        }
    }

    @Override
    public void removeAllUsbMusicList() {
        mUsbPlayList.clear();
        dispatchMusicList();
    }

    @Override
    public List<UsbMusic> getUsbMusicList() {
        return mUsbPlayList;
    }

    @Override
    public List<UsbMusic> getDefaultUsbMusicList() {
        return mDefaultUsbPlayList;
    }

    @Override
    public void insertUsbMusic(UsbMusic music) {
        if (music != null) {
            mUsbPlayList.add(0, music);
            dispatchMusicList();
        }
    }

    @Override
    public void insertUsbMusic(UsbMusic music, int position) {
        if (music != null) {
            mUsbPlayList.add(position, music);
            dispatchMusicList();
        }
    }

    @Override
    public void deleteUsbMusic(UsbMusic music) {
        if (music != null) {
            mUsbPlayList.remove(music);
            dispatchMusicList();
        }
    }

    @Override
    public void addUsbPlayListChangedListener(OnUsbPlayListChangedListener listener) {
        if (listener != null) {
            listChangedListeners.add(listener);
        }
    }

    @Override
    public void removeUsbPlayListChangedListener(OnUsbPlayListChangedListener listener) {
        if (listener != null) {
            listChangedListeners.remove(listener);
        }
    }

    @Override
    public void addUsbPlayTipsListener(IUsbPlayTipsListener listener) {
        if (listener != null) {
            playTipsListeners.add(listener);
        }
    }

    @Override
    public void removeUsbPlayTipsListener(IUsbPlayTipsListener listener) {
        if (listener != null) {
            playTipsListeners.remove(listener);
        }
    }

    private void dispatchMusicList() {
        for (OnUsbPlayListChangedListener listChangedListener : listChangedListeners) {
            listChangedListener.onPlayMusicListChanged(mUsbPlayList);
        }
    }


    @Override
    public boolean playPre() {
        switch (playMode) {
            case PlayMode.LIST_ORDER:
                return playByListOrder(false, SwitchStatus.PRE);
            case PlayMode.LIST_LOOP:
                playByListLoop(SwitchStatus.PRE);
                break;
            case PlayMode.SINGLE_LOOP:
                playBySingleLoop(false, SwitchStatus.PRE);
                break;
            case PlayMode.RANDOM:
                playByRandom();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void autoPlayNext() {
        playNext(true);
    }

    @Override
    public boolean playNext() {
        return playNext(false);
    }


    private boolean playNext(boolean isAutoPlay) {
        switch (playMode) {
            case PlayMode.LIST_ORDER:
                return playByListOrder(isAutoPlay, SwitchStatus.NEXT);
            case PlayMode.LIST_LOOP:
                playByListLoop(SwitchStatus.NEXT);
                break;
            case PlayMode.SINGLE_LOOP:
                playBySingleLoop(isAutoPlay, SwitchStatus.NEXT);
                break;
            case PlayMode.RANDOM:
                playByRandom();
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 顺序播放
     */
    private boolean playByListOrder(boolean isAutoPlay, @SwitchStatus int status) {
        switch (status) {
            case SwitchStatus.NEXT:
                UsbMusic nextMusic = getListOrderNextMusic(isAutoPlay);
                if (nextMusic != null) {
                    UsbMusicFactory.getUsbPlayerProxy().play(nextMusic);
                    return true;
                } else {
                    return false;
                }
            case SwitchStatus.PRE:
                UsbMusic preMusic = getListOrderPreMusic(isAutoPlay);
                if (preMusic != null) {
                    UsbMusicFactory.getUsbPlayerProxy().play(preMusic);
                    return true;
                } else {
                    return false;
                }
            default:
                break;
        }
        return false;
    }

    private UsbMusic getListOrderPreMusic(boolean isAutoPlay) {
        UsbMusic usbMusic = null;
        if (!ListUtils.isEmpty(mUsbPlayList)) {
            UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
            if (currUsbMusic != null) {
                int index = mUsbPlayList.indexOf(currUsbMusic);
                try {
                    if (index == 0) {
                        if (!isAutoPlay) {
                            for (IUsbPlayTipsListener playTipsListener : playTipsListeners) {
                                playTipsListener.onFirstTips();
                            }
                            return null;
                        }
                    } else {
                        usbMusic = mUsbPlayList.get(index - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return usbMusic;
    }

    @Nullable
    private UsbMusic getListOrderNextMusic(boolean isAutoPlay) {
        UsbMusic usbMusic = null;
        if (!ListUtils.isEmpty(mUsbPlayList)) {
            int size = mUsbPlayList.size();
            UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
            if (currUsbMusic != null) {
                int index = mUsbPlayList.indexOf(currUsbMusic);
                try {
                    if (index == size - 1) {
                        if (!isAutoPlay) {
                            for (IUsbPlayTipsListener playTipsListener : playTipsListeners) {
                                playTipsListener.onLastTips();
                            }
                            return null;
                        }
                    } else {
                        usbMusic = mUsbPlayList.get(index + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return usbMusic;
    }

    /**
     * 列表循环
     */
    private void playByListLoop(@SwitchStatus int status) {
        switch (status) {
            case SwitchStatus.NEXT:
                UsbMusic nextMusic = getLoopNextMusic();
                if (nextMusic != null) {
                    UsbMusicFactory.getUsbPlayerProxy().play(nextMusic);
                }
                break;
            case SwitchStatus.PRE:
                UsbMusic preMusic = getLoopPreMusic();
                if (preMusic != null) {
                    UsbMusicFactory.getUsbPlayerProxy().play(preMusic);
                }
                break;
            default:
                break;
        }
    }

    private UsbMusic getLoopPreMusic() {
        UsbMusic usbMusic = null;
        if (!ListUtils.isEmpty(mUsbPlayList)) {
            int size = mUsbPlayList.size();
            UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
            if (currUsbMusic != null) {
                int index = mUsbPlayList.indexOf(currUsbMusic);
                try {
                    if (index == 0) {
                        usbMusic = mUsbPlayList.get(size - 1);
                    } else {
                        usbMusic = mUsbPlayList.get(index - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return usbMusic;
    }

    private UsbMusic getLoopNextMusic() {
        UsbMusic usbMusic = null;
        if (!ListUtils.isEmpty(mUsbPlayList)) {
            int size = mUsbPlayList.size();
            UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
            if (currUsbMusic != null) {
                int index = mUsbPlayList.indexOf(currUsbMusic);
                try {
                    if (index == size - 1) {
                        usbMusic = mUsbPlayList.get(0);
                    } else {
                        usbMusic = mUsbPlayList.get(index + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return usbMusic;
    }

    /**
     * 单曲循环
     */
    private void playBySingleLoop(boolean isAutoPlay, @SwitchStatus int status) {
        switch (status) {
            case SwitchStatus.NEXT:
                UsbMusic nextMusic = getSingleNextMusic(isAutoPlay);
                if (nextMusic != null) {
                    UsbMusicFactory.getUsbPlayerProxy().play(nextMusic);
                }
                break;
            case SwitchStatus.PRE:
                UsbMusic preMusic = getSinglePreMusic(isAutoPlay);
                if (preMusic != null) {
                    UsbMusicFactory.getUsbPlayerProxy().play(preMusic);
                }
                break;
            default:
                break;
        }
    }

    private UsbMusic getSinglePreMusic(boolean isAutoPlay) {
        UsbMusic usbMusic = null;
        if (!ListUtils.isEmpty(mUsbPlayList)) {
            int size = mUsbPlayList.size();
            UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
            if (currUsbMusic != null) {
                int index = mUsbPlayList.indexOf(currUsbMusic);
                if (!isAutoPlay) {
                    try {
                        if (index == 0) {
                            usbMusic = mUsbPlayList.get(size - 1);
                        } else {
                            usbMusic = mUsbPlayList.get(index - 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    usbMusic = currUsbMusic;
                }
            } else {
                KLog.d("playBySingleLoop: currUsbMusic is empty");
            }
        }
        return usbMusic;
    }

    private UsbMusic getSingleNextMusic(boolean isAutoPlay) {
        UsbMusic usbMusic = null;
        if (!ListUtils.isEmpty(mUsbPlayList)) {
            int size = mUsbPlayList.size();
            UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
            if (currUsbMusic != null) {
                int index = mUsbPlayList.indexOf(currUsbMusic);
                if (!isAutoPlay) {
                    try {
                        if (index == size - 1) {
                            usbMusic = mUsbPlayList.get(0);
                        } else {
                            usbMusic = mUsbPlayList.get(index + 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    usbMusic = currUsbMusic;
                }
            } else {
                KLog.d("playBySingleLoop: currUsbMusic is empty");
            }
        }
        return usbMusic;
    }

    /**
     * 随机播放
     */
    private void playByRandom() {
        if (!ListUtils.isEmpty(mUsbPlayList)) {
            int size = mUsbPlayList.size();
            int position = (int) (Math.random() * size);
            UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
            if (currUsbMusic != null && position == mUsbPlayList.indexOf(currUsbMusic)) {
                if (position == size - 1) {
                    UsbMusicFactory.getUsbPlayerProxy().play(mUsbPlayList.get(0));
                } else {
                    UsbMusicFactory.getUsbPlayerProxy().play(mUsbPlayList.get(position + 1));
                }
            } else {
                UsbMusicFactory.getUsbPlayerProxy().play(mUsbPlayList.get(position));
            }
        }
    }

    @Override
    public void setPlayMode(int playMode) {
        this.playMode = playMode;
        for (IUsbPlayModeChangedListener playModeChangedListener : playModeChangedListeners) {
            playModeChangedListener.onPlayModeChanged(playMode);
        }
    }

    @Override
    public int getPlayMode() {
        return playMode;
    }

    public interface IUsbPlayModeChangedListener {
        void onPlayModeChanged(int mode);
    }
}
