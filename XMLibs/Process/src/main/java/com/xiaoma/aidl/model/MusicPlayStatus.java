package com.xiaoma.aidl.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MusicPlayStatus {

    public static final int INIT = 0;
    public static final int PLAYING = 1;
    public static final int BUFFERING = 2;
    public static final int PAUSE = 3;
    public static final int STOP = 4;
    public static final int EXIT = 5;

    @IntDef({INIT, PLAYING, BUFFERING, PAUSE, STOP, EXIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {

    }

}
