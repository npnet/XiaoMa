package com.xiaoma.assistant.utils;

import android.os.Handler;
import android.os.Message;

/**
 * @Author ZiXu Huang
 * @Data 2019/3/28
 */
public class UpdateByTimeUtils {
    private int duration;
    private boolean isPoll = true;
    private CallBack callback;
    private Runnable runnable;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (isPoll) {
                    startUpdateByTime();
                }
            }
        }
    };

    public void startUpdateByTime(int duration, CallBack callBack) {
        this.callback = callBack;
        this.duration = duration;
        isPoll = true;
        startUpdateByTime();
    }

    private void startUpdateByTime() {
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                    callback.onCall();
                }
            };
        }
        handler.postDelayed(runnable, duration);
    }

    public void stopUpdate() {
        isPoll = false;
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public interface CallBack {
        void onCall();
    }
}
