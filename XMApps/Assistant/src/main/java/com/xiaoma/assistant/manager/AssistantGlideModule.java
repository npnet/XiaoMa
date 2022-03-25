package com.xiaoma.assistant.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.xiaoma.image.BaseAppGlideModule;
/**
 * Created by qiuboxiang on 2019/6/28 21:10
 * Desc:
 */
@GlideModule
public class AssistantGlideModule  extends BaseAppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
    }
}