package com.xiaoma.autotracker.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.xiaoma.autotracker.db.AutoTrackDBManager;
import com.xiaoma.autotracker.model.AppViewScreen;
import com.xiaoma.autotracker.model.AutoTrackInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2018/12/5
 */
public class XmDataActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private XmDataFragmentLifecycleCallbacks fragmentLifecycleCallbacks = new XmDataFragmentLifecycleCallbacks();
    private List<WeakReference<Activity>> activities = new ArrayList<>();
    private Activity currentActivity;

    public FragmentManager.FragmentLifecycleCallbacks getFragmentLifecycleCallbacks() {
        return fragmentLifecycleCallbacks;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity != null) {
            activities.add(new WeakReference<Activity>(activity));
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        autoTrackInfo.setOt(AppViewScreen.APPPAGEFOREGROUND.getEventType().getEventValue());
        autoTrackInfo.setS(AppViewScreen.APPPAGEFOREGROUND.getAppStatus());
        AutoTrackDBManager.getInstance().saveAppViewScreenEvent(activity, autoTrackInfo);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity != null) {
            for (int i = 0; i < activities.size(); i++) {
                WeakReference<Activity> weakReference = activities.get(i);
                if (weakReference.get() == activity) {
                    activities.remove(weakReference);
                    break;
                }
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }
}
