package com.xiaoma.utils.apptool;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.utils.AppUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LKF on 2017/6/21 0021.
 */

public class AppObserver implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = AppObserver.class.getSimpleName();

    private static class InstanceHolder {
        private static AppObserver sInstance = new AppObserver();
    }

    public static AppObserver getInstance() {
        return InstanceHolder.sInstance;
    }

    private AppObserver() {
    }

    private boolean mIsInit = false;
    private boolean mIsForeground;
    private Context context;
    public void init(Context context) {
        if (mIsInit || context == null)
            return;
        mIsInit = true;
        this.context = context.getApplicationContext();
        Application app = (Application) context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(this);
    }

    public boolean isForeground() {
        return mIsForeground;
    }

    private Set<AppStateChangedListener> appStateChangedListenerSet = new HashSet<>();

    public void addAppStateChangedListener(AppStateChangedListener listener) {
        if (listener != null)
            appStateChangedListenerSet.add(listener);
    }

    public void removeAppStateChangedListener(AppStateChangedListener listener) {
        if (listener != null)
            appStateChangedListenerSet.remove(listener);
    }

    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        Log.i(TAG, String.format("ActivityLifecycle-> [ %s ] : onCreate( savedInstanceState: %s )", activity.getClass().getName(), savedInstanceState));
    }


    @Override
    public void onActivityStarted(Activity activity) {
        Log.i(TAG, String.format("ActivityLifecycle-> [ %s ] : onStart()", activity.getClass().getName()));
        if (!mIsForeground) {
            mIsForeground = true;
            dispatchForegroundChanged(true);
            Log.i(TAG, "app come to foreground");
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i(TAG, String.format("ActivityLifecycle-> [ %s ] : onResume()", activity.getClass().getName()));
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i(TAG, String.format("ActivityLifecycle-> [ %s ] : onPause()", activity.getClass().getName()));
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i(TAG, String.format("ActivityLifecycle-> [ %s ] : onStop()", activity.getClass().getName()));
        if (!AppUtils.isAppForeground()) {
            mIsForeground = false;
            dispatchForegroundChanged(false);
            Log.i(TAG, "app come to foreground");
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i(TAG, String.format("ActivityLifecycle-> [ %s ] : onSaveInstance( outState: %s )", activity.getClass().getName(), outState));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(TAG, String.format("ActivityLifecycle-> [ %s ] : onyDestroy()", activity.getClass().getName()));
    }

    private void dispatchForegroundChanged(boolean isForeground) {
        for (AppStateChangedListener l : appStateChangedListenerSet) {
            l.onForegroundChanged(isForeground);
        }
    }

    public interface AppStateChangedListener {
        void onForegroundChanged(boolean isForeground);
    }

    public void closeAllActivitiesAndExit() {
        try {
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.setAccessible(true);
            method.invoke(context.getSystemService(Context.ACTIVITY_SERVICE), context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }
}
