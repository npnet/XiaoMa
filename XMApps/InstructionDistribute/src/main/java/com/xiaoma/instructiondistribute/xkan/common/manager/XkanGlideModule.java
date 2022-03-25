package com.xiaoma.instructiondistribute.xkan.common.manager;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.xkan.common.manager
 *  @file_name:      XkanGlideModule
 *  @author:         Rookie
 *  @create_time:    2019/3/14 15:00
 *  @description：   TODO             */

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.xiaoma.image.BaseAppGlideModule;

@GlideModule
public class XkanGlideModule extends BaseAppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
//        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
//                .setMemoryCacheScreens(2).build();
//        int memoryCacheSize = calculator.getMemoryCacheSize();
//        Log.d("XkanGlideModuleddd", "memoryCacheSize: " + memoryCacheSize);
//        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
    }
}
