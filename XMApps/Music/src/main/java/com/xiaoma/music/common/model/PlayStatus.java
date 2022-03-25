package com.xiaoma.music.common.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiaoma.music.common.model.PlayStatus.BUFFERING;
import static com.xiaoma.music.common.model.PlayStatus.FAILED;
import static com.xiaoma.music.common.model.PlayStatus.INIT;
import static com.xiaoma.music.common.model.PlayStatus.PAUSE;
import static com.xiaoma.music.common.model.PlayStatus.PLAYING;
import static com.xiaoma.music.common.model.PlayStatus.STOP;

/**
 * 播放状态
 *
 * @author zs
 * @date 2018/10/11 0011.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({INIT, BUFFERING, PLAYING, PAUSE, STOP, FAILED})
public @interface PlayStatus {
    /**
     * 初始状态
     */
    int INIT = 0;
    /**
     * 缓冲中
     */
    int BUFFERING = 1;
    /**
     * 正在播放
     */
    int PLAYING = 2;
    /**
     * 暂停
     */
    int PAUSE = 3;
    /**
     * 停止播放
     */
    int STOP = 4;
    /**
     * 播放出错
     */
    int FAILED = 5;
}
