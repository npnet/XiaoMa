package com.xiaoma.music.manager;

import android.content.Context;

import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.player.model.PlayMode;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/12/21 0021
 */
public interface IUsbMusicList {

    void init(Context context);

    void addUsbMusicList(List<UsbMusic> musicList);

    void replaceUsbMusicList(List<UsbMusic> musicList);

    void removeUsbMusicList(List<UsbMusic> musicList);

    void removeAllUsbMusicList();

    List<UsbMusic> getUsbMusicList();

    List<UsbMusic> getDefaultUsbMusicList();

    void insertUsbMusic(UsbMusic music);

    void insertUsbMusic(UsbMusic music, int position);

    void deleteUsbMusic(UsbMusic music);

    void addUsbPlayListChangedListener(OnUsbPlayListChangedListener listener);

    void removeUsbPlayListChangedListener(OnUsbPlayListChangedListener listener);

    void addUsbPlayTipsListener(IUsbPlayTipsListener listener);

    void removeUsbPlayTipsListener(IUsbPlayTipsListener listener);

    boolean playPre();

    void autoPlayNext();

    boolean playNext();

    void setPlayMode(@PlayMode int playMode);

    int getPlayMode();

    void addStateChangeListen(PlayerListManager.IUsbPlayModeChangedListener listen);

    void removeStateChangeListen(PlayerListManager.IUsbPlayModeChangedListener listen);
}
