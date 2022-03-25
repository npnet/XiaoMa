package com.xiaoma.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author youthyJ
 * @date 2018/11/7
 */
public class PlayerConstants {
    private PlayerConstants() throws Exception {
        throw new Exception();
    }

    public static final String ACTION_PLAYER_PREPARED = "com.xiaoma.action.PLAYER_PREPARED";

    public static final class Options {
        public static final int PLAY = 1;
        public static final int PAUSE = 2;

        public static final int NEXT = 3;
        public static final int PREVIOUS = 4;

        public static final int FAVORITE = 5;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Options.PLAY,
            Options.PAUSE,
            Options.NEXT,
            Options.PREVIOUS,
            Options.FAVORITE
    })
    public @interface AudioOption {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AudioStatus.LOADING,
            AudioStatus.PLAYING,
            AudioStatus.PAUSING,
            AudioStatus.STOPPED,
            AudioStatus.EXIT,
            AudioStatus.ERROR
    })
    public @interface AudioState {
    }

    public static final class AudioStatus {
        public static final int PLAYING = 1;
        public static final int PAUSING = 2;
        public static final int LOADING = 3;
        public static final int STOPPED = 4;
        public static final int EXIT = 999;
        public static final int ERROR = 990;
    }

    /**
     * 这的常量主要用与区分不同的音频类型
     * 同时也是{@link PlayerManagerHelper.OnControlListener#onControl(int, int)}中第一个参数
     * 在初始化了{@link PlayerManagerHelper}的音频应用中用于判断当前控制的音频类型。
     * <p>
     * 如果应用只初始化了{@link PlayerManagerHelper}还没有手动播放音频，则会返回{@link AudioTypes#NONE},
     * 此时可不做处理，或播放默认的音频。
     * <p>
     * <p>
     * !!!接入时增加Type，请将其加入下方的范围限定注解中!!!
     */
    public static final class AudioTypes {
        public static final int NONE = 0;
        public static final int XTING_NET_FM = 1;
        public static final int XTING_LOCAL_FM = 2;
        public static final int MUSIC_ONLINE_KUWO = 3;
        public static final int MUSIC_LOCAL_USB = 4;
        public static final int MUSIC_LOCAL_BT = 5;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AudioTypes.NONE,
            AudioTypes.XTING_LOCAL_FM,
            AudioTypes.XTING_NET_FM,
            AudioTypes.MUSIC_ONLINE_KUWO,
            AudioTypes.MUSIC_LOCAL_USB,
            AudioTypes.MUSIC_LOCAL_BT,
    })
    public @interface AudioType {
    }
}
