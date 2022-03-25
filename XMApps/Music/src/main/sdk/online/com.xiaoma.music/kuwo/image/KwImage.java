package com.xiaoma.music.kuwo.image;

import com.bumptech.glide.util.Util;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;

import cn.kuwo.base.bean.Music;

/**
 * Created by LKF on 2018/10/24 0024.
 */
public class KwImage {
    private final Music mMusic;
    private final int mImageSize;

    public KwImage(Music music) {
        this(music, IKuwoConstant.IImageSize.SIZE_70);
    }

    public KwImage(Music music, @IKuwoConstant.IImageSize int imageSize) {
        if (music == null) {
            throw new IllegalArgumentException("< Music > model cannot be null");
        }
        mMusic = music;
        mImageSize = imageSize;
    }

    public Music getMusic() {
        return mMusic;
    }

    public int getImageSize() {
        return mImageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof KwImage) {
            KwImage other = (KwImage) o;
            return mMusic.rid == other.mMusic.rid
                    && mImageSize == other.mImageSize;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Util.hashCode((int) mMusic.rid, mImageSize);
    }
}