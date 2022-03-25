package com.xiaoma.component.base;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentController;
import android.support.v4.app.FragmentHostCallback;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

/**
 * Created by LKF on 2018/10/31 0031.<br/>
 * <br/>
 * 支持使用{@link Fragment}的{@link Dialog},用法和{@link FragmentActivity}类似<br/>
 * 通过{@link #getSupportFragmentManager()}获取当前的Support{@link FragmentManager}<br/>
 */
public class FragmentDialog extends Dialog {
    private static final String LOG_TAG = "FragmentDialog";
    private final Context mContext;
    private final Handler mHandler;
    private final FragmentController mFragments;
    private final FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks;
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;

    public FragmentDialog(@NonNull Context context) {
        this(context, 0);
    }

    public FragmentDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        if (context instanceof Activity) {
            final Activity act = (Activity) context;
            act.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks = new InnerActivityLifecycleCallbacks() {
                @Override
                public void onActivityStopped(Activity activity) {
                    if (activity == act) {
                        mFragments.dispatchStop();
                    }
                }
            });
        }
        mHandler = new Handler(Looper.getMainLooper());
        mFragmentLifecycleCallbacks = new InnerFragmentLifecycleCallbacks();
        mFragments = FragmentController.createController(new HostCallbacks());
        mFragments.attachHost(null);
        //这里要先设置对Window的LayoutInflater.Factory,否则无法将布局中的fragment标签解析成supportV4的Fragment
        final Window window = getWindow();
        if (window != null) {
            window.getLayoutInflater().setFactory2((LayoutInflater.Factory2) mFragments.getSupportFragmentManager());
        }
    }

    /**
     * 获取构造时传进来的{@link Context};<br/>
     * {@link Dialog}构造时会将原始的{@link Context}构造成一个{@link android.view.ContextThemeWrapper};<br/>
     * 因此,如果调用{@link Dialog#getContext()}返回的是一个{@link android.view.ContextThemeWrapper}包装类型,而不是原始的{@link Context}<br/>
     *
     * @return 构造时传递的{@link Context}
     */
    public Context getInitialContext() {
        return mContext;
    }

    public FragmentManager getSupportFragmentManager() {
        return mFragments.getSupportFragmentManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFragments.getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, true);
        mFragments.dispatchCreate();
        mFragments.dispatchStart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mFragments.dispatchResume();
        } else {
            mFragments.dispatchPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFragments.dispatchStop();
        mFragments.dispatchDestroy();
        mFragments.getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks);
        if (mContext instanceof Activity && mActivityLifecycleCallbacks != null) {
            ((Activity) mContext).getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        }
    }

    class HostCallbacks extends FragmentHostCallback<FragmentDialog> {
        HostCallbacks() {
            super(mContext, mHandler, 0);
        }

        @Override
        public boolean onShouldSaveFragmentState(Fragment fragment) {
            return !isShowing();
        }

        @NonNull
        @Override
        public LayoutInflater onGetLayoutInflater() {
            return getLayoutInflater().cloneInContext(mContext);
        }

        @Nullable
        @Override
        public FragmentDialog onGetHost() {
            return FragmentDialog.this;
        }

        @Override
        public boolean onHasWindowAnimations() {
            return getWindow() != null;
        }

        @Override
        public int onGetWindowAnimations() {
            final Window w = getWindow();
            return w == null ? 0 : w.getAttributes().windowAnimations;
        }

        @Nullable
        @Override
        public View onFindViewById(int id) {
            return FragmentDialog.this.findViewById(id);
        }

        @Override
        public boolean onHasView() {
            final Window w = getWindow();
            return w != null && w.peekDecorView() != null;
        }
    }

    static class InnerFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {
        @Override
        public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentPreAttached(fm, f, context);
            Log.i(LOG_TAG, "onFragmentPreAttached: " + f.getClass().getName());
        }

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            Log.i(LOG_TAG, "onFragmentAttached: " + f.getClass().getName());
        }

        @Override
        public void onFragmentPreCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentPreCreated(fm, f, savedInstanceState);
            Log.i(LOG_TAG, "onFragmentPreCreated: " + f.getClass().getName());
        }

        @Override
        public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentCreated(fm, f, savedInstanceState);
            Log.i(LOG_TAG, "onFragmentCreated: " + f.getClass().getName());
        }

        @Override
        public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentActivityCreated(fm, f, savedInstanceState);
            Log.i(LOG_TAG, "onFragmentActivityCreated: " + f.getClass().getName());
        }

        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
            Log.i(LOG_TAG, "onFragmentViewCreated: " + f.getClass().getName());
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            super.onFragmentStarted(fm, f);
            Log.i(LOG_TAG, "onFragmentStarted: " + f.getClass().getName());
        }

        @Override
        public void onFragmentResumed(FragmentManager fm, Fragment f) {
            super.onFragmentResumed(fm, f);
            Log.i(LOG_TAG, "onFragmentResumed: " + f.getClass().getName());
        }

        @Override
        public void onFragmentPaused(FragmentManager fm, Fragment f) {
            super.onFragmentPaused(fm, f);
            Log.i(LOG_TAG, "onFragmentPaused: " + f.getClass().getName());
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            super.onFragmentStopped(fm, f);
            Log.i(LOG_TAG, "onFragmentStopped: " + f.getClass().getName());
        }

        @Override
        public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
            super.onFragmentSaveInstanceState(fm, f, outState);
            Log.i(LOG_TAG, "onFragmentSaveInstanceState: " + f.getClass().getName());
        }

        @Override
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
            super.onFragmentViewDestroyed(fm, f);
            Log.i(LOG_TAG, "onFragmentViewDestroyed: " + f.getClass().getName());
        }

        @Override
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            super.onFragmentDestroyed(fm, f);
            Log.i(LOG_TAG, "onFragmentDestroyed: " + f.getClass().getName());
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            Log.i(LOG_TAG, "onFragmentDetached: " + f.getClass().getName());
        }
    }

    private static class InnerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}