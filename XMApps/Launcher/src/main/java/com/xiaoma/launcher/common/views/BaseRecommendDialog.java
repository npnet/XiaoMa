package com.xiaoma.launcher.common.views;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.common.views
 *  @file_name:      BaseRecommendDialog
 *  @author:         Rookie
 *  @create_time:    2019/3/28 16:50
 *  @description：   TODO             */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.xiaoma.launcher.R;

public abstract class BaseRecommendDialog extends Dialog {

    protected CountDownTimer mDismissTimer;

    public Context mContext;

    protected static final int DIALOG_WIDTH = 520;
    protected static final int DIALOG_HEIGHT = 600;
    private static final int DIALOG_X = 256;
    private static final int DIALOG_Y = 96;

    private static final int COUNT_TOTAL_TIME = 5000;
    protected static final int COUNT_TIME_INTERVAL = 1000;


    public BaseRecommendDialog(@NonNull Context context) {
        super(context, R.style.recommend_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(getContentViewId());
        initView();
        //重新设置宽高和坐标信息
        initWindow();
    }

    public abstract int getContentViewId();

    public abstract void initView();

    protected void initWindow() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.START | Gravity.TOP;
        lp.width = DIALOG_WIDTH;
        lp.height = DIALOG_HEIGHT;
        lp.x = DIALOG_X;
        lp.y = DIALOG_Y;
        getWindow().setAttributes(lp);
    }

    protected long getTotalTime() {
        return COUNT_TOTAL_TIME;
    }

    protected CountDownTimer getDismissTimer() {
        return new CountDownTimer(getTotalTime(), COUNT_TIME_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (isShowing()) {
                    dismiss();
                }
            }
        };
    }

    protected void startDismissTimer() {
        cancelDismissTimer();
        mDismissTimer = getDismissTimer();
        mDismissTimer.start();
    }

    protected void cancelDismissTimer() {
        if (mDismissTimer != null) {
            mDismissTimer.cancel();
        }
    }

}
