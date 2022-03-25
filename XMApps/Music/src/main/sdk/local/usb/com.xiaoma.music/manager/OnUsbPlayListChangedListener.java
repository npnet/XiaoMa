package com.xiaoma.music.manager;

import com.xiaoma.music.model.UsbMusic;

import java.util.ArrayList;

/**
 * Created by ZYao.
 * Date ：2018/12/21 0021
 */
public interface OnUsbPlayListChangedListener {
    void onPlayMusicListChanged(ArrayList<UsbMusic> musicList);
}
