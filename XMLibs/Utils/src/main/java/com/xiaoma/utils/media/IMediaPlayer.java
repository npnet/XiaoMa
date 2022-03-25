package com.xiaoma.utils.media;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/4
 * Desc:基于系统MediaPlayer功能行为接口的抽象， 可以根据已经有的进行扩展
 */
public interface IMediaPlayer {

    void start();

    void stop();

    void pause();

    void release();

    void reset();

    void prepare() throws IOException;

    void prepareAsync();

    boolean isPlaying();

    int getCurrentPosition();

    int getDuration();

    void seekTo(int msec);

    void setVolume(float leftVolume, float rightVolume);

    void setDataSource(String path) throws IOException;

    void setDataSource(FileDescriptor fileDescriptor) throws IOException;

    void setAudioStreamType(int streamType);

    void setOnCompletionListener(OnCompletionListener listener);

    void setOnSeekCompleteListener(OnSeekCompleteListener listener);

    void setOnPreparedListener(OnPreparedListener listener);

    void setOnBufferingUpdateListener(OnBufferingUpdateListener listener);

    void setOnErrorListener(OnErrorListener listener);

    void setOnInfoListener(OnInfoListener listener);

}
