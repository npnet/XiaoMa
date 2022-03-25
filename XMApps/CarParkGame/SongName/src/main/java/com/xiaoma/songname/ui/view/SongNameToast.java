package com.xiaoma.songname.ui.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.songname.R;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.constract.ToastLevel;
import com.xiaoma.utils.ResUtils;

public class SongNameToast {

    public static void toastException(Context context, @StringRes int strResId) {
        showToast(context, ResUtils.getString(context, strResId), ToastLevel.EXCEPTION);
    }

    public static void showToast(final Context context, final String msg, final int toastLevel) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(context);
                View toastView = LayoutInflater.from(context).inflate(com.xiaoma.ui.R.layout.view_toast, null);
                toastView.setBackgroundResource(R.drawable.toast);
                toast.setView(toastView);
                toast.setGravity(Gravity.CENTER, 0, 0);
                TextView tvMsg = toastView.findViewById(com.xiaoma.ui.R.id.tvMsg);
                tvMsg.setText(msg);
                ImageView ivIcon = toastView.findViewById(com.xiaoma.ui.R.id.ivIcon);
                int iconVis = View.VISIBLE;
                switch (toastLevel) {
                    case ToastLevel.DEFAULT:
                        iconVis = View.GONE;
                        break;

                    case ToastLevel.LOADING:
                        Animation anim = AnimationUtils.loadAnimation(context, com.xiaoma.ui.R.anim.rotate);
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
                toast.show();
            }
        });

    }
}
