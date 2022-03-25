package com.xiaoma.component.dispatch;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2019-07-23
 */
public class FrontDispatch {
    private static FrontDispatch instance;
    private int activityCount = 0;
    private List<OnFrontEvent> listeners = new ArrayList<>();
    private Application.ActivityLifecycleCallbacks callback = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityCount++;
            if (activityCount == 1) {
                notifyAppIntoFront();
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityCount--;
            if (activityCount == 0) {
                notifyAppIntoBackground();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    public static FrontDispatch getInstance() {
        if (instance == null) {
            synchronized (FrontDispatch.class) {
                if (instance == null) {
                    FrontDispatch.instance = new FrontDispatch();
                }
            }
        }
        return instance;
    }

    private void notifyAppIntoFront() {
        for (int index = listeners.size() - 1; index >= 0; index--) {
            OnFrontEvent impl = listeners.get(index);
            if (impl == null) {
                continue;
            }
            try {
                impl.intoFront();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyAppIntoBackground() {
        for (int index = listeners.size() - 1; index >= 0; index--) {
            OnFrontEvent impl = listeners.get(index);
            if (impl == null) {
                continue;
            }
            try {
                impl.intoBackground();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FrontDispatch init(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        Application app = (Application) context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(instance.callback);
        return instance;
    }

    public boolean addOnFrontListener(OnFrontEvent impl) {
        if (impl == null) {
            return false;
        }
        if (listeners.contains(impl)) {
            return true;
        }
        return listeners.add(impl);
    }

    public boolean removeOnFrontListener(OnFrontEvent impl) {
        if (impl == null) {
            return false;
        }
        return listeners.remove(impl);
    }

    public boolean isOnFront() {
        return activityCount > 0;
    }

    public interface OnFrontEvent {
        void intoFront();

        void intoBackground();
    }

    public class SimpleOnFrontEvent implements OnFrontEvent {
        @Override
        public void intoFront() {
            // empty
        }

        @Override
        public void intoBackground() {
            // empty
        }
    }
}
