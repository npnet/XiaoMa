package com.xiaoma.setting.common.utils;

import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: iSun
 * @date: 2018/10/29 0029
 */
public class ToastUtils {
    public static void show(final Toast toast, int cnt) {
        final Timer timer = new Timer();
        toast.setDuration(Toast.LENGTH_LONG);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }
}
