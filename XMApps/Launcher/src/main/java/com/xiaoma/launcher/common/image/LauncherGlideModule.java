package com.xiaoma.launcher.common.image;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.xiaoma.image.BaseAppGlideModule;
import com.xiaoma.player.AudioInfo;

import java.io.InputStream;

@GlideModule
public class LauncherGlideModule extends BaseAppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.append(AudioInfo.class, InputStream.class, new KwImageLoader.Factory())
                .append(AudioInfo.class, InputStream.class, new UsbImageLoader.Factory());
    }
}