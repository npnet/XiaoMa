package com.xiaoma.launcher;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.xiaoma.config.BuildConfig;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LauncherCrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String KEY_CRASH_COUNT = "crash_count";
    private static final int MIN_CRASH_TIME = 60_000;// 最短Crash间隔
    private static final int MAX_CRASH_COUNT = 10; // 最短Crash间隔内的最大Crash次数

    private Context mContext;
    private SharedPreferences mSp;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    LauncherCrashHandler(Context context) {
        context = context.getApplicationContext();
        mSp = context.getSharedPreferences("crash_record_config", Context.MODE_PRIVATE);
        mContext = context;
        // 超过最短Crash间隔没有发生Crash,则直接清除Crash记录
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSp.edit().clear().apply();
            }
        }, MIN_CRASH_TIME);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (BuildConfig.DEBUG) {
            // 异常Crash打印到本地文件
            try {
                File dir = new File(Environment.getExternalStorageDirectory(),
                        mContext.getPackageName().replaceAll("\\.", "_") + "_clog");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String versionName = mContext.getPackageManager()
                        .getPackageInfo(mContext.getPackageName(), 0).versionName;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US);
                File logFile = new File(dir, versionName + "_" + df.format(System.currentTimeMillis()) + ".log");
                if (logFile.exists()) {
                    logFile.delete();
                }
                logFile.createNewFile();
                e.printStackTrace(new PrintStream(logFile));
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        int totalCrashCount = mSp.getInt(KEY_CRASH_COUNT, 0);
        if (totalCrashCount >= MAX_CRASH_COUNT) {
            // 清空Crash记录
            mSp.edit().clear().apply();
            // 连续Crash后跳转到应急桌面
            Intent simpleLauncher = new Intent("com.xiaoma.systemui.SIMPLE_LAUNCHER")
                    .setPackage("com.xiaoma.systemui")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(simpleLauncher);
            // 强杀应用,避免无限重启
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityManager am = (ActivityManager) mContext.getSystemService(Service.ACTIVITY_SERVICE);
                    am.forceStopPackage(mContext.getPackageName());
                }
            }, 2000);
        } else {
            mSp.edit().putInt(KEY_CRASH_COUNT, totalCrashCount + 1).apply();
        }
    }
}
