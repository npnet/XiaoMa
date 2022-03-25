package com.xiaoma.guide.utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xiaoma.guide.R;
import com.xiaoma.utils.ConvertUtils;

import java.lang.ref.WeakReference;

public class GuideToast {
    private GuideToast() {
    }

    private static WeakReference<Toast> sToastRef;

    public static void showToastInternal(Context context,
                                         boolean toastLong,
                                         @LayoutRes int toastLayoutId) {
        if (context == null) {
            return;
        }
        cancelToast();
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(toastLayoutId, null);
        View container = toastView.findViewById(R.id.ll_container);
        container.setLayoutParams(new LinearLayout.LayoutParams((int) ConvertUtils.px2dp(context, 654), (int) ConvertUtils.px2dp(context, 220)));
        toast.setView(toastView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(toastLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        sToastRef = new WeakReference<>(toast);
        toast.show();
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
}
