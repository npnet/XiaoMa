package com.xiaoma.utils.log;

import android.content.Context;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.xiaoma.config.ConfigManager;

import java.io.File;

/**
 * Created by iSun on 2018/9/6 0006.
 */

public class KLog {
    private static boolean sInit;
    private static LogConfig sLogConfig = LogConfig.getDefaultConfig();

    public static void init(Context context) {
        init(context, sLogConfig);
    }

    public static void init(Context context, boolean isShowLog) {
        init(context, sLogConfig.setIsShowLog(isShowLog));
    }

    public static void init(Context context, LogConfig config) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        sInit = true;
        if (config == null) {
            config = LogConfig.getDefaultConfig();
        }
        File sdConfig = new File(config.configName);
        if (config.isChangeConfigFromSD && sdConfig.exists()) {
            sLogConfig = LogUtils.readConfigFromSDcard(sdConfig);
        } else {
            sLogConfig = config;
        }
        sLogConfig = LogUtils.checkConfigLegality(sLogConfig);
        if (isShowLog()) {
            Logger.clearLogAdapters();
            initLogcatAdapter();
            if (sLogConfig.isSaveLog) {
                initSaveAdapter(context);
            }
        }
    }

    private static void initLogcatAdapter() {
        HandlerThread ht = new HandlerThread(sLogConfig.tag, sLogConfig.threadPriority);
        ht.start();
        FormatStrategy logcatFormatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(sLogConfig.methodCount)         // (Optional) How many method line to show. Default 2
                .methodOffset(1)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag(sLogConfig.tag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(logcatFormatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                if (ConfigManager.ApkConfig.isDebug() && !sLogConfig.isDebugFilter) {
                    return true;
                }
                return priority >= sLogConfig.printLogLevel;
            }
        });
    }

    private static void initSaveAdapter(Context context) {
        XmDiskFormatStrategy xmDiskFormatStrategy = new XmDiskFormatStrategy(context, null, null, null, sLogConfig);

        Logger.addLogAdapter(new DiskLogAdapter(xmDiskFormatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                if (ConfigManager.ApkConfig.isDebug() && !sLogConfig.isDebugFilter) {
                    return true;
                }
                return priority >= sLogConfig.fileLogLevel;
            }
        });
    }

    private static boolean isShowLog() {
        return sInit && sLogConfig != null && sLogConfig.isShowLog;
    }

    /**
     * General log function that accepts all configurations as parameter
     */
    public static void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
        if (isShowLog()) {
            Logger.log(priority, tag, message, throwable);
        }
    }

    public static void d(@Nullable Object message) {
        if (isShowLog()) {
            Logger.d(message);
        }
    }

    public static void d(@Nullable String tag, @Nullable Object message) {
        if (isShowLog()) {
            Logger.t(tag).d(message);
        }
    }

    public static void e(@NonNull String message) {
        if (isShowLog()) {
            Logger.e(message);
        }
    }

    public static void e(@Nullable String tag, @NonNull String message) {
        if (isShowLog()) {
            Logger.t(tag).e(message);
        }
    }

    public static void e(@Nullable String tag, @Nullable Throwable throwable, @NonNull String message) {
        if (isShowLog()) {
            Logger.t(tag).e(throwable, message);
        }
    }

    public static void i(@Nullable String message) {
        if (isShowLog()) {
            Logger.i(message);
        }
    }

    public static void i(@Nullable String tag, @Nullable String message) {
        if (isShowLog()) {
            Logger.t(tag).i(message);
        }
    }

    public static void v(@NonNull String message) {
        if (isShowLog()) {
            Logger.v(message);
        }
    }

    public static void v(@Nullable String tag, @Nullable String message) {
        if (isShowLog()) {
            Logger.t(tag).v(message);
        }
    }

    public static void w(@NonNull String message) {
        if (isShowLog()) {
            Logger.w(message);
        }
    }

    public static void w(@Nullable String tag, @NonNull String message) {
        if (isShowLog()) {
            Logger.t(tag).w(message);
        }
    }

    public static void wtf(@NonNull String message) {
        if (isShowLog()) {
            Logger.wtf(message);
        }
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void wtf(@Nullable String tag, @NonNull String message) {
        if (isShowLog()) {
            Logger.t(tag).wtf(message);
        }
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        if (isShowLog()) {
            Logger.json(json);
        }
    }

    public static void json(@Nullable String tag, @Nullable String json) {
        if (isShowLog()) {
            Logger.t(tag).json(json);
        }
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(@Nullable String xml) {
        if (isShowLog()) {
            Logger.xml(xml);
        }
    }

    public static void xml(@Nullable String tag, @Nullable String xml) {
        if (isShowLog()) {
            Logger.t(tag).xml(xml);
        }
    }

}
