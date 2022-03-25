package com.xiaoma.ui.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.R;
import com.xiaoma.ui.constract.ToastLevel;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import java.lang.ref.WeakReference;

import static com.xiaoma.utils.UtilsConstants.NEW_LINE;

/**
 * Created by youthyj on 2018/9/5.
 */
public class XMToast {
    private static final String Tag = "XMToast";
    private static WeakReference<Toast> sToastRef;

    private XMToast() throws Exception {
        throw new Exception();
    }

    public static void showToast(Context context, @StringRes int resId) {
        if (context == null) {
            return;
        }
        String msg = ResUtils.getString(context, resId);
        showToast(context, msg, false);
    }

    public static void showToast(Context context, @StringRes int resId, boolean lengthLong) {
        if (context == null) {
            return;
        }
        context = context.getApplicationContext();
        String msg = ResUtils.getString(context, resId);
        showToast(context, msg, lengthLong);
    }

    public static void showToast(Context context, String msg) {
        if (context == null) {
            return;
        }
        context = context.getApplicationContext();
        showToast(context, msg, false);
    }

    public static void showToast(Context context, final String msg, final boolean lengthLong) {
        if (context == null) {
            return;
        }
        printToastInfo(context, msg);
        toastDefault(context, msg, lengthLong);
    }

    public static void showToast(Context context, String msg, Drawable leftIcon) {
        if (context == null) {
            return;
        }
        printToastInfo(context, msg);
        showToastInternal(context, msg, ToastLevel.DEFAULT, false, leftIcon);
    }

    public static void toastDefault(Context context, String strMsg, boolean toastLong) {
        showToastInternal(context, strMsg, ToastLevel.DEFAULT, toastLong);
    }

    public static void toastDefault(Context context, @StringRes int strResId, boolean toastLong) {
        toastDefault(context, ResUtils.getString(context, strResId), toastLong);
    }

    public static void toastLoading(Context context, @StringRes int strResId, boolean toastLong) {
        toastLoading(context, ResUtils.getString(context, strResId), toastLong);
    }

    public static void toastLoading(Context context, String strMsg, boolean toastLong) {
        showToastInternal(context, strMsg, ToastLevel.LOADING, toastLong);
    }

    public static void toastSuccess(Context context, @StringRes int strResId, boolean toastLong) {
        toastSuccess(context, ResUtils.getString(context, strResId), toastLong);
    }

    public static void toastSuccess(Context context, String strMsg, boolean toastLong) {
        showToastInternal(context, strMsg, ToastLevel.SUCCESS, toastLong);
    }

    public static void toastSuccess(Context context, String strMsg) {
        showToastInternal(context, strMsg, ToastLevel.SUCCESS, false);
    }

    public static void toastSuccess(Context context, @StringRes int strResId) {
        showToastInternal(context, ResUtils.getString(context, strResId), ToastLevel.SUCCESS, false);
    }

    public static void toastException(Context context, @StringRes int strResId, boolean toastLong) {
        toastException(context, ResUtils.getString(context, strResId), toastLong);
    }

    public static void toastException(Context context, String strMsg, boolean toastLong) {
        showToastInternal(context, strMsg, ToastLevel.EXCEPTION, toastLong);
    }

    public static void toastException(Context context, @StringRes int strResId) {
        showToastInternal(context, ResUtils.getString(context, strResId), ToastLevel.EXCEPTION, false);
    }

    public static void toastException(Context context, String strMsg) {
        showToastInternal(context, strMsg, ToastLevel.EXCEPTION, false);
    }

    private static void showToastInternal(final Context context,
                                          final String strMsg, @ToastLevel final int toastLevel,
                                          final boolean toastLong) {
        showToastInternal(context, strMsg, toastLevel, toastLong, null);
    }

    @SuppressLint("MissingPermission")
    private static void showToastInternal(final Context context,
                                          String msg, @ToastLevel final int toastLevel,
                                          final boolean toastLong, final Drawable leftIcon) {
        if (context == null || TextUtils.isEmpty(msg)) {
            Log.w(Tag, logWarnMsg(toastLevel, context, msg));
            return;
        }
        //下面这段代码，为了统一访客模式下（无用户信息），使用在线功能时的异常吐司提示
        if (LoginTypeManager.getInstance().isVisitorType() &&
                !NetworkUtils.isConnected(context) &&
                toastLevel == ToastLevel.EXCEPTION &&
                msg.contains(context.getString(R.string.network))) {
            msg = context.getString(R.string.network_error_in_visitor);
        }
        final String strMsg = msg;
        printToastInfo(context, strMsg);
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                cancelToast();
                Toast toast = new Toast(context);
                View toastView = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
                toast.setView(toastView);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(toastLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                WindowManager.LayoutParams windowParams = toast.getWindowParams();
                windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ConfigManager.ApkConfig.isCarPlatform()) {
                    windowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    windowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                }
                TextView tvMsg = toastView.findViewById(R.id.tvMsg);
                tvMsg.setText(strMsg);

                ImageView ivIcon = toastView.findViewById(R.id.ivIcon);
                ivIcon.clearAnimation();
                if (leftIcon != null) {
                    ivIcon.setImageDrawable(leftIcon);
                } else {
                    int iconVis = View.VISIBLE;
                    switch (toastLevel) {
                        case ToastLevel.DEFAULT:
                            iconVis = View.GONE;
                            break;
                        case ToastLevel.LOADING:
                            Animation anim = AnimationUtils.loadAnimation(context, R.anim.rotate);
                            anim.setRepeatCount(Animation.INFINITE);
                            anim.setRepeatMode(Animation.RESTART);
                            ivIcon.startAnimation(anim);
                            break;
                        case ToastLevel.EXCEPTION:
                        case ToastLevel.SUCCESS:
                        default:
                            break;
                    }
                    ivIcon.setVisibility(iconVis);
                    ivIcon.setImageLevel(toastLevel);
                }
                sToastRef = new WeakReference<>(toast);
                toast.show();
            }
        });
    }

    public static void cancelToast() {
        WeakReference<Toast> ref = sToastRef;
        if (ref == null)
            return;
        Toast toast = ref.get();
        if (toast == null)
            return;
        toast.cancel();
        ref.clear();
    }

    private static void printToastInfo(Context context, String msg) {
        if (ConfigManager.ApkConfig.isDebug()) {
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            String stack = "";
            if (stackTrace != null) {
                for (StackTraceElement element : stackTrace) {
                    if (element == null) {
                        continue;
                    }
                    String content = element.toString();
                    if (content.contains(context.getPackageName())) {
                        stack = "at " + content;
                    }
                }
            }
            Log.d(Tag, "SHOW TOAST: "
                    + NEW_LINE + "location: [" + stack + "]"
                    + NEW_LINE + "package:  [" + context.getPackageName() + "]"
                    + NEW_LINE + "content:  [" + msg + "]"
            );
        }
    }

    private static String logWarnMsg(int toastLevel, Context context, String msg) {
        return "Toast level = " + toastLevel + " [ context = " + context + " , msg = " + msg + " ]";
    }
}
