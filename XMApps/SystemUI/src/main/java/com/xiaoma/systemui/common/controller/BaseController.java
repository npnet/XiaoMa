package com.xiaoma.systemui.common.controller;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import com.xiaoma.systemui.common.ui.ISystemUI;
import com.xiaoma.systemui.common.util.LogUtil;


/**
 * Created by LKF on 2018/11/8 0008.
 */
public abstract class BaseController implements ISystemUI {
    private Context mContext;
    private WindowManager mWindowManager;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    protected abstract WindowManager.LayoutParams makeLp();

    protected abstract View getSystemUIView();

    protected void logI(String format, Object... args) {
        LogUtil.logI(getClass().getSimpleName(), format, args);
    }

    public void init(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
    }

    @Override
    public void show() {
        logI("show()");
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                final View v = doGetSystemUIView();
                if (v != null) {
                    if (v.getParent() == null) {
                        getWindowManager().addView(v, doMakeLp());
                    } else {
                        update();
                    }
                }
            }
        });
    }

    @Override
    public void update() {
        logI("update()");
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                final View v = doGetSystemUIView();
                if (v != null && v.getParent() != null) {
                    final WindowManager.LayoutParams newLp;
                    final WindowManager.LayoutParams oldLp = (WindowManager.LayoutParams) v.getLayoutParams();
                    if (oldLp != null) {
                        newLp = new WindowManager.LayoutParams();
                        newLp.copyFrom(oldLp);
                    } else {
                        newLp = doMakeLp();
                    }
                    getWindowManager().updateViewLayout(v, newLp);
                }
            }
        });
    }

    @Override
    public void dismiss() {
        logI("dismiss()");
        final View v = doGetSystemUIView();
        if (v != null && v.getParent() != null) {
            getWindowManager().removeViewImmediate(v);
        }
    }

    @Override
    public boolean isShowing() {
        final View v = doGetSystemUIView();
        return v != null && v.getParent() != null;
    }

    public void release() {
        dismiss();
        mWindowManager = null;
        mContext = null;
    }

    public Context getContext() {
        return mContext;
    }

    protected WindowManager getWindowManager() {
        return mWindowManager;
    }

    private View doGetSystemUIView() {
        final View v = getSystemUIView();
        if (v != null && View.LAYER_TYPE_HARDWARE != v.getLayerType()) {
            v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        return v;
    }

    private WindowManager.LayoutParams doMakeLp() {
        final WindowManager.LayoutParams lp = makeLp();
        lp.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        return lp;
    }

    protected void runOnUIThread(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            mMainHandler.post(r);
        }
    }
}