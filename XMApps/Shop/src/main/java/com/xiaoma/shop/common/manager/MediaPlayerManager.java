package com.xiaoma.shop.common.manager;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/20
 * @Describe: 媒体管理类
 */

public class MediaPlayerManager {
    private MediaPlayer mMediaPlayer;

    private MediaPlayerManager() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
    }

    public static MediaPlayerManager getInstance() {
        return Holder.mMediaPlayerManager;
    }



    public static class Holder {
        private static MediaPlayerManager mMediaPlayerManager = new MediaPlayerManager();
    }

    public MediaPlayerManager setDataSource(String sourcePath) {
        if (mMediaPlayer.isPlaying()) return this;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(sourcePath);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public MediaPlayerManager addListener(final IPlayListener iPlayListener) {
        if (mMediaPlayer == null || mMediaPlayer.isPlaying()) return this;
        try {
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (iPlayListener != null) {
                        iPlayListener.onPrepare();
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (iPlayListener != null) {
                        iPlayListener.onComplete();
                    }
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (iPlayListener != null) {
                        iPlayListener.onError();
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            if (iPlayListener != null) {
                iPlayListener.onError();
            }
        }
        return this;
    }

    public void play() {
        if (mMediaPlayer == null) return;
        try {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mMediaPlayer == null) return;
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stop() {
        if (mMediaPlayer == null) return;
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isPlaying() {
        if (mMediaPlayer == null) return false;
        try {
              return   mMediaPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public interface IPlayListener {
        void onPrepare();

        void onComplete();

        void onError();
    }
}
