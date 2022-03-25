package com.xiaoma.music.common.constant;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/7/19 0019 20:38
 *   desc:   快速播放状态
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({FastPlayStatus.NONE, FastPlayStatus.NEXT, FastPlayStatus.REPLAY})
public @interface FastPlayStatus {

    int NONE = 0;   //正常更新进度
    int NEXT = 1;   //一首歌快进完毕，释放后播放下一首
    int REPLAY = 2; // 一首歌回退起点，重新播放
}
