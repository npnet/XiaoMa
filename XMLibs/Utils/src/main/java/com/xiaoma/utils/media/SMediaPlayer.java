package com.xiaoma.utils.media;

import android.media.MediaPlayer;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/4
 * Desc:基于android系统提供的媒体播放器
 */
public class SMediaPlayer implements IMediaPlayer {
    private MediaPlayer mediaPlayer;

    public SMediaPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void release() {
        mediaPlayer.release();
    }

    @Override
    public void reset() {
        mediaPlayer.reset();
    }

    @Override
    public void prepare() throws IOException {
        mediaPlayer.prepare();
    }

    @Override
    public void prepareAsync() {
        mediaPlayer.prepareAsync();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        mediaPlayer.setVolume(leftVolume, rightVolume);
    }

    @Override
    public void setDataSource(String path) throws IOException {
        mediaPlayer.setDataSource(path);
    }

    @Override
    public void setDataSource(FileDescriptor fileDescriptor) throws IOException {
        mediaPlayer.setDataSource(fileDescriptor);
    }

    @Override
    public void setAudioStreamType(int streamType) {
        mediaPlayer.setAudioStreamType(streamType);
    }

    @Override
    public void setOnCompletionListener(final OnCompletionListener listener) {
        MediaPlayer.OnCompletionListener onCompletionListener = null;
        if (listener != null) {
            onCompletionListener = new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    listener.onCompletion(SMediaPlayer.this);
                }
            };
        }
        mediaPlayer.setOnCompletionListener(onCompletionListener);
    }

    @Override
    public void setOnSeekCompleteListener(final OnSeekCompleteListener listener) {
        MediaPlayer.OnSeekCompleteListener onSeekCompleteListener = null;
        if (listener != null) {
            onSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    listener.onSeekComplete(SMediaPlayer.this);
                }
            };
        }
        mediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
    }

    @Override
    public void setOnPreparedListener(final OnPreparedListener listener) {
        MediaPlayer.OnPreparedListener onPreparedListener = null;
        if (listener != null) {
            onPreparedListener = new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    listener.onPrepared(SMediaPlayer.this);
                }
            };
        }
        mediaPlayer.setOnPreparedListener(onPreparedListener);
    }

    @Override
    public void setOnBufferingUpdateListener(final OnBufferingUpdateListener listener) {
        MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = null;
        if (listener != null) {
            onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    listener.onBufferingUpdate(SMediaPlayer.this, i);
                }
            };
        }
        mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
    }

    @Override
    public void setOnErrorListener(final OnErrorListener listener) {
        MediaPlayer.OnErrorListener onErrorListener = null;
        if (listener != null) {
            onErrorListener = new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return listener.onError(SMediaPlayer.this, i, i1);
                }
            };
        }
        mediaPlayer.setOnErrorListener(onErrorListener);
    }

    @Override
    public void setOnInfoListener(final OnInfoListener listener) {
        MediaPlayer.OnInfoListener onInfoListener = null;
        if (listener != null) {
            onInfoListener = new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    return listener.onInfo(mp, what, extra);
                }
            };
        }
        mediaPlayer.setOnInfoListener(onInfoListener);
    }
}
