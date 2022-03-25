package com.xiaoma.music.callback;

import com.xiaoma.music.model.UsbMusic;

import java.util.ArrayList;

/**
 * @author zs
 * @date 2018/11/15 0015.
 */
public interface UsbFileSearchListener {

    void onUsbMusicScanFinished(ArrayList<UsbMusic> musicList);

    void onUsbMusicAnalyticFinished( ArrayList<UsbMusic> musicList);
}
