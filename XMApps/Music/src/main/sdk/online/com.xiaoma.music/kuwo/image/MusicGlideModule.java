package com.xiaoma.music.kuwo.image;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.xiaoma.image.BaseAppGlideModule;
import com.xiaoma.music.model.UsbMusic;

import java.io.InputStream;

/**
 * Created by LKF on 2018/10/24 0024.
 */
@GlideModule
public class MusicGlideModule extends BaseAppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        // 注册酷我音乐图片加载模块
        registry.append(KwImage.class, InputStream.class, new KwImageLoader.Factory())
                .append(UsbMusic.class, InputStream.class, new UsbImageLoader.Factory())
        ;
    }
}