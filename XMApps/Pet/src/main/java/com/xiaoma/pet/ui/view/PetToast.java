package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.pet.R;
import com.xiaoma.pet.common.annotation.PetToastType;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/23 0023 10:58
 *   desc:
 * </pre>
 */
public class PetToast {

    private static final String TAG = PetToast.class.getSimpleName();
    private static AlertDialog alertDialog;

    public static void showToast(Context context, String text) {
        showToastInternal(context, text, false, false, PetToastType.NORMAL);
    }

    public static void showToast(Context context, String text, boolean isLong) {
        showToastInternal(context, text, isLong, false, PetToastType.NORMAL);
    }


    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }


    public static void showToast(Context context, int resId, boolean isLong) {
        showToast(context, context.getString(resId), isLong);
    }


    public static void showGravityTop(Context context, int resId) {
        showToastInternal(context, context.getString(resId), false, true, PetToastType.NORMAL);
    }


    public static void showException(Context context, String text) {
        showToastInternal(context, text, false, false, PetToastType.EXCEPTION);
    }

    public static void showException(Context context, int resId) {
        showToastInternal(context, context.getString(resId), false, false, PetToastType.EXCEPTION);
    }


    public static void showLoading(Context context, String text) {
        showToastInternal(context, text, false, false, PetToastType.LOADING);
    }


    public static void showLoading(Context context, int resId) {
        showLoading(context, context.getString(resId));
    }


    public static void dismissLoading() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
            KLog.d(TAG, "loading end");
        }
    }


    private static void showToastInternal(final Context context,
                                          String text,
                                          final boolean isLong,
                                          final boolean gravity,
                                          @PetToastType final int type) {
        final View view = View.inflate(context, R.layout.pet_toast_view, null);
        final ImageView icon = view.findViewById(R.id.iv_pet_toast_icon);
        TextView message = view.findViewById(R.id.pet_toast_text);
        message.setText(text);
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case PetToastType.NORMAL:
                        icon.setImageLevel(PetToastType.NORMAL);
                        icon.setVisibility(View.GONE);
                        break;

                    case PetToastType.EXCEPTION:
                        icon.setImageLevel(PetToastType.EXCEPTION);
                        break;

                    case PetToastType.LOADING:
                        icon.setImageLevel(PetToastType.LOADING);
                        handleLoading(context, view);
                        return;
                }

                Toast toast = new Toast(context);
                toast.setView(view);
                if (gravity) {
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 70);
                } else {
                    toast.setGravity(Gravity.CENTER, 0, 0);
                }
                toast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    private static void handleLoading(Context context, View view) {
        KLog.d(TAG, "loading start.");
        if (alertDialog != null) {
            return;
        }

        final ImageView icon = view.findViewById(R.id.iv_pet_toast_icon);
        final Animation iconAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_rorate);
        iconAnimation.setInterpolator(new LinearInterpolator());
        icon.startAnimation(iconAnimation);
        alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        icon.clearAnimation();
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = context.getResources().getDimensionPixelOffset(R.dimen.width_pet_toast);
            layoutParams.height = context.getResources().getDimensionPixelOffset(R.dimen.height_pet_toast);
            window.setAttributes(layoutParams);
        }
    }

}
