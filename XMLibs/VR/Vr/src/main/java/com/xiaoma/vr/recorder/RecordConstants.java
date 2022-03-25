package com.xiaoma.vr.recorder;

import android.media.AudioFormat;

/**
 * Created by LKF
 * 2018/3/15 0015.
 */

public class RecordConstants {
    public static final int DEFAULT_SAMPLE_RATE = 16 * 1000;
    public static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int DEFAULT_BUFFER_MULTIPLE = 3;
    public static final int DEFAULT_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    public static final int TRACK = 64;

    public static final int STEREO_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;//    AudioFormat.CHANNEL_IN_STEREO
    public static final int STEREO_TRACK = 128;


    public static final String BUFFER_ALL = "buffer";
    public static final String BUFFER_LEFT = "left_buffer";
    public static final String BUFFER_RIGHT = "right_buffer";

}
