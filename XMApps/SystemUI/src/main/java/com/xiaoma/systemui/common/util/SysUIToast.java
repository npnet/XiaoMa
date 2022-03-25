package com.xiaoma.systemui.common.util;

import android.annotation.StringRes;
import android.app.Service;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.systemui.R;

import java.lang.ref.WeakReference;

/**
 * Created by LKF on 2019-3-13 0013.
 * 因为系统UI覆盖优先级比较高,普通的Toast无法覆盖通知栏,所以这里单独实现和通知栏同优先级的Toast
 */
public class SysUIToast {
    private static final long SHORT_DURATION_TIMEOUT = 4000;
    private static final long LONG_DURATION_TIMEOUT = 7000;
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static WeakReference<View> sLastToastRef;

    private static void removeLastToast(Context context) {
        final WeakReference<View> ref = sLastToastRef;
        if (ref != null) {
            final View last = ref.get();
            if (last != null && last.getParent() != null) {
                final WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
                wm.removeViewImmediate(last);
            }
        }
    }

    public static void makeTextAndShow(Context context, @StringRes int resId, @Toast.Duration int duration) {
        makeTextAndShow(context, context.getString(resId), duration);
    }

    public static void makeTextAndShow(final Context context, final CharSequence text, @Toast.Duration final int duration) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            sHandler.removeCallbacksAndMessages(null);
            removeLastToast(context);
            final int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL,
                    flags,
                    PixelFormat.TRANSLUCENT);
            lp.token = new Binder();
            lp.gravity = Gravity.CENTER;
            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
            lp.setTitle("Panel");
            lp.packageName = context.getPackageName();
            lp.windowAnimations = android.R.style.Animation_Toast;
            final long timeout = duration == Toast.LENGTH_LONG ? LONG_DURATION_TIMEOUT : SHORT_DURATION_TIMEOUT;
            lp.hideTimeoutMilliseconds = timeout;

            final View v = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
            final TextView tv = v.findViewById(R.id.tv_text);
            tv.setText(text);

            final WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
            wm.addView(v, lp);
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeLastToast(context);
                }
            }, timeout);
            sLastToastRef = new WeakReference<>(v);
        } else {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    makeTextAndShow(context, text, duration);
                }
            });
        }
    }
}
