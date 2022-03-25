package com.xiaoma.systemui.navigationbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.xiaoma.systemui.R;
import com.xiaoma.systemui.common.controller.BaseController;
import com.xiaoma.systemui.common.util.KeyEventUtil;

/**
 * Created by LKF on 2018/11/2 0002.
 */
public class NavigationBarController extends BaseController {
    @SuppressLint("StaticFieldLeak")
    private static NavigationBarController sInstance;

    public static NavigationBarController getInstance() {
        if (sInstance == null) {
            synchronized (NavigationBarController.class) {
                if (sInstance == null) {
                    sInstance = new NavigationBarController();
                }
            }
        }
        return sInstance;
    }

    private View mNavigationBarView;

    private NavigationBarController() {
    }

    /**
     * 远程端可以通过此方法设置远程视图
     */
    public void setRemoteViews(RemoteViews remoteViews) {
        if (remoteViews != null) {
            ((ViewGroup) mNavigationBarView).removeAllViewsInLayout();
            remoteViews.apply(getContext(), (ViewGroup) mNavigationBarView);
            update();
        } else {
            setDefaultView();
        }
    }

    public int getNavigationBarWidth() {
        return mNavigationBarView.getMeasuredWidth();
    }

    private void setDefaultView() {
        ((ViewGroup) mNavigationBarView).removeAllViewsInLayout();
        View.inflate(getContext(), R.layout.navigation_bar_view, (ViewGroup) mNavigationBarView);
        final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_home:
                        dispatchKeyEvent(KeyEvent.KEYCODE_HOME);
                        break;
                    case R.id.btn_back:
                        dispatchKeyEvent(KeyEvent.KEYCODE_BACK);
                        break;
                }
            }
        };
        mNavigationBarView.findViewById(R.id.btn_home).setOnClickListener(clickListener);
        mNavigationBarView.findViewById(R.id.btn_back).setOnClickListener(clickListener);
        update();
    }

    private void dispatchKeyEvent(int keyCode) {
        if (mKeyInterceptor == null || !mKeyInterceptor.onKeyEvent(keyCode)) {
            KeyEventUtil.sendClickKeyEvent(keyCode);
        }
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mNavigationBarView = View.inflate(context, R.layout.navigation_bar_remote_view_container, null);
        setDefaultView();
    }

    @Override
    protected WindowManager.LayoutParams makeLp() {
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_NAVIGATION_BAR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        | WindowManager.LayoutParams.FLAG_SLIPPERY,
                PixelFormat.TRANSLUCENT);
        lp.token = new Binder();
        lp.setTitle("NavigationBar");
        lp.windowAnimations = 0;
        return lp;
    }

    @Override
    protected View getSystemUIView() {
        return mNavigationBarView;
    }

    private KeyInterceptor mKeyInterceptor;

    public void setKeyInterceptor(KeyInterceptor keyInterceptor) {
        mKeyInterceptor = keyInterceptor;
    }

    public interface KeyInterceptor {
        boolean onKeyEvent(int keyCode);
    }
}