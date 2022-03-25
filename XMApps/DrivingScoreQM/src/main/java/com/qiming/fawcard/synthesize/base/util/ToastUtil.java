package com.qiming.fawcard.synthesize.base.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.qiming.fawcard.synthesize.BuildConfig;

public class ToastUtil {
    public static void showToast(final Context context, final String messages) {
        if (BuildConfig.DEBUG) {
            if (isMainThread()) {
                Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
            } else {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
