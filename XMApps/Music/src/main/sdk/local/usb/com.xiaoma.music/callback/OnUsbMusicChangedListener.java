package com.xiaoma.music.callback;

import com.xiaoma.music.model.UsbMusic;

/**
 * @author zs
 * @date 2018/10/30 0030.
 */
public interface OnUsbMusicChangedListener {

    void onBuffering(UsbMusic usbMusic);

    void onPlay(UsbMusic usbMusic);

    void onPause();

    void onProgressChange(long progressInMs, long totalInMs);

    void onPlayFailed(int errorCode);

    void onPlayStop();

    void onCompletion();

}
