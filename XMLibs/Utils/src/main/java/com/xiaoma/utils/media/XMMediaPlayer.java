package com.xiaoma.utils.media;

import android.media.MediaPlayer;

import java.io.FileDescriptor;
import java.io.IOException;


/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/4
 * Desc:封装小马自己的统一音频播放器
 */
public class XMMediaPlayer implements IMediaPlayer {
    public static final int MEDIA_ERROR_SERVER_DIED = MediaPlayer.MEDIA_ERROR_SERVER_DIED;
    private IMediaPlayer iMediaPlayer;

    public XMMediaPlayer() {
        //如果使用系统MediaPlayer
        if (MediaConstan.useSystemMediaPlayer) {
            iMediaPlayer = new SMediaPlayer();
        } else {
            //第三方或者自定义
        }
    }

    @Override
    public void start() {
        iMediaPlayer.start();
    }

    @Override
    public void stop() {
        iMediaPlayer.stop();
    }

    @Override
    public void pause() {
        iMediaPlayer.pause();
    }

    @Override
    public void release() {
        iMediaPlayer.release();
    }

    @Override
    public void reset() {
        iMediaPlayer.reset();
    }

    @Override
    public void prepare() throws IOException {
        iMediaPlayer.prepare();
    }

    @Override
    public void prepareAsync() {
        iMediaPlayer.prepareAsync();
    }

    @Override
    public boolean isPlaying() {
        return iMediaPlayer.isPlaying();
    }

    @Override
    public int getCurrentPosition() {
        return iMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return iMediaPlayer.getDuration();
    }

    @Override
    public void seekTo(int msec) {
        iMediaPlayer.seekTo(msec);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        iMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    @Override
    public void setDataSource(String path) throws IOException {
        iMediaPlayer.setDataSource(path);
    }

    @Override
    public void setDataSource(FileDescriptor fileDescriptor) throws IOException {
        iMediaPlayer.setDataSource(fileDescriptor);
    }

    @Override
    public void setAudioStreamType(int streamType) {
        iMediaPlayer.setAudioStreamType(streamType);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        iMediaPlayer.setOnCompletionListener(listener);
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        iMediaPlayer.setOnSeekCompleteListener(listener);
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        iMediaPlayer.setOnPreparedListener(listener);
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        iMediaPlayer.setOnBufferingUpdateListener(listener);
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        iMediaPlayer.setOnErrorListener(listener);
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        iMediaPlayer.setOnInfoListener(listener);
    }
}
