package com.xiaoma.vr.tts;

import android.util.Log;

import com.xiaoma.utils.log.KLog;

/**
 * Created by ZYao.
 * Date ：2019/7/2 0002
 */
public class TtsPriorityHelper {
    private static final String TAG = "XmTtsManager";
    private TtsPriority mPriority;

    public static TtsPriorityHelper getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final TtsPriorityHelper instance = new TtsPriorityHelper();
    }

    public TtsPriorityHelper() {
        this.mPriority = TtsPriority.DEFAULT;
    }

    public synchronized boolean interruptPriority(TtsPriority priority) {
        if (priority == null) {
            return false;
        }
        int value = priority.value();
        boolean interrupt = value <= mPriority.value();
        if (interrupt) {
            this.mPriority = priority;
            Log.d(TAG, "interrupt priority success: " + mPriority.value() + ", newPriority: " + priority.value());
        } else {
            Log.d(TAG, "interrupt priority error: " + mPriority.value() + ", newPriority: " + priority.value());
        }
        return interrupt;
    }

    public synchronized void recovery() {
        this.mPriority = TtsPriority.DEFAULT;
        Log.d(TAG, "default priority : no voice input");
    }
}
