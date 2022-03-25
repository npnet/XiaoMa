package com.xiaoma.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.bumptech.glide.module.AppGlideModule;
import com.xiaoma.utils.reflect.Reflect;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by LKF on 2018/1/6 0006.
 * <p>支持将Transform应用到placeholder和error
 * <p>如需使用,请在自己的App中创建{@link BaseAppGlideModule}的子类
 * <p>并在类声明中添加注解{@link com.bumptech.glide.annotation.GlideModule}
 * <p>并在App的gradle配置里加上注解处理器的依赖: annotationProcessor "com.github.bumptech.glide:compiler:${GLIDE_VERSION}"
 */
public class BaseAppGlideModule extends AppGlideModule {
    private static final String TAG = "BaseAppGlideModule";

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        MemorySizeCalculator calc = new MemorySizeCalculator.Builder(context).build();
        Log.e(TAG, String.format("applyOptions -> memSize: %sMB, bmpPoolSize: %sMB, arrPoolSize: %sMB",
                byte2Mb(calc.getMemoryCacheSize()), byte2Mb(calc.getBitmapPoolSize()), byte2Mb(calc.getArrayPoolSizeInBytes())));
        Reflect.on(GlideBuilder.class)
                .method("setRequestManagerFactory", RequestManagerRetriever.RequestManagerFactory.class)
                .invoke(builder, new RequestManagerRetriever.RequestManagerFactory() {
                    @NonNull
                    @Override
                    public RequestManager build(@NonNull Glide glide, @NonNull Lifecycle lifecycle, @NonNull RequestManagerTreeNode requestManagerTreeNode, @NonNull Context context) {
                        return new XMRequestManager(glide, lifecycle, requestManagerTreeNode, context);
                    }
                });
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
    }

    private static String byte2Mb(int byteLen) {
        return BigDecimal.valueOf(byteLen)
                .divide(BigDecimal.valueOf(1024 * 1024), 2, RoundingMode.HALF_UP)
                .toString();
    }
}
