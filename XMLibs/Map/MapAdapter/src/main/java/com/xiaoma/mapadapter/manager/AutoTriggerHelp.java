package com.xiaoma.mapadapter.manager;

import android.util.Log;

import com.xiaoma.thread.ThreadDispatcher;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Administrator on 2017/12/8 0008.
 */

public class AutoTriggerHelp<T> {

    private static final String TAG = "AutoTriggerHelp";

    public interface ITriggerMaxCritical {
        void triggerCritical(ArrayList data);
    }

    final long MAX_TIME;
    final int MAX_COUNT;
    ArrayBlockingQueue<T> dataCache;
    ITriggerMaxCritical iTriggerMaxCritical;
    Runnable timeAtomicRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"time trigger now, MAX TIME = "+MAX_TIME);
            triggerNow();
        }
    };

    public AutoTriggerHelp(int maxCount, long maxTime, ITriggerMaxCritical iTriggerMaxCritical) throws IllegalArgumentException {
        this.MAX_COUNT = maxCount;
        this.MAX_TIME = maxTime;
        this.iTriggerMaxCritical = iTriggerMaxCritical;
        if (MAX_COUNT <= 0) throw new IllegalArgumentException("maxCount must be >= 0");
        if (maxCount > 0) {
            dataCache = new ArrayBlockingQueue<>(MAX_COUNT);
            startAutoTime();
        }
    }

    private void startAutoTime() {
        Log.d(TAG,"AutoTriggerHelp startAutoTime,MAX_TIME = " + MAX_TIME);
        if (MAX_TIME <= 1000) return;
        ThreadDispatcher.getDispatcher().removeOnMain(timeAtomicRunnable);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(timeAtomicRunnable, MAX_TIME);
    }


    public boolean addData(T data) {
        if (data == null) {
            Log.e(TAG,"data can not be null");
            return false;
        }
        try {
            boolean result = dataCache.add(data);
            Log.d(TAG,"AutoTriggerHelp addData, current size = " + dataCache.size()+", add result = "+result);
            if(dataCache.size() >= MAX_COUNT) {
                Log.d(TAG, "count trigger now, MAX COUNT = "+MAX_COUNT);
                triggerNow();
            }
            return result;
        } catch (IllegalStateException illegalSateException) {
            triggerNow();
        } catch (NullPointerException nullException) {
            nullException.printStackTrace();
        }
        return false;
    }

    public synchronized void triggerNow() {
        if (dataCache.size() == 0) {
            startAutoTime();
            return;
        }
        ThreadDispatcher.getDispatcher().removeOnMain(timeAtomicRunnable);
        ArrayList<T> data = new ArrayList<>();
        int count = dataCache.drainTo(data, MAX_COUNT);
        if (count > 0) {
            if (iTriggerMaxCritical != null) iTriggerMaxCritical.triggerCritical(data);
        }
        Log.d(TAG, "triggerNow , drain to count = " + count + ", leave count = " + dataCache.size());
        if (count == MAX_COUNT && dataCache.size() > MAX_COUNT) {
            triggerNow();
        } else {
            startAutoTime();
        }
    }

}
