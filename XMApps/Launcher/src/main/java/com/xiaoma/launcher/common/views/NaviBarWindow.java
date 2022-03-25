package com.xiaoma.launcher.common.views;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import com.xiaoma.config.ConfigConstants;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.listener.SlideListener;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Thomas on 2019/6/22 0022
 * 负一屏插件window
 */

public class NaviBarWindow {

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private MenuRelativeLayout naviBarRelativeLayout;
    private Context context;
    private Animation animationLeftIn;
    private Animation animationLeftOut;
    private static final int SCREEN_WIDTH = 1920;
    private static final int ANIMATION_SCREEN_WIDTH = -1920;
    private static final int NAVIBAR_WINDOW_WIDTH = 50;
    public static final int ANIMATION_TIME = 500;
    public boolean isSlideIn = false;
    private NaviBarControlLayout view;
    private static NaviBarWindow naviBarWindow = new NaviBarWindow();
    private View pullLeft;
    private View pullRight;

    private NaviBarWindow() {

    }

    public static NaviBarWindow getNaviBarWindow() {
        return naviBarWindow;
    }

    public synchronized void init(Context context) {
        if (context == null || this.context != null) {
            return;
        }
        KLog.e("LauncherService onCreate NaviBarWindow create success");
        this.context = context;
        initNaviBarWindowReceiver();
        initNaviBarWindow();
    }

    private void initNaviBarWindowReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConfigConstants.NAVIBARWINDOW_OPEN_ACTION);
        intentFilter.addAction(ConfigConstants.NAVIBARWINDOW_CLOSE_ACTION);
        this.context.registerReceiver(naviBarWindowReceiver, intentFilter);
    }

    @SuppressLint("InflateParams")
    private void initNaviBarWindow() {
        windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ConfigManager.ApkConfig.isCarPlatform()) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_KEYGUARD;
        }
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        params.width = NAVIBAR_WINDOW_WIDTH;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        naviBarRelativeLayout = (MenuRelativeLayout) LayoutInflater.from(this.context).inflate(R.layout.layout_navibar_window, null);
        view = naviBarRelativeLayout.findViewById(R.id.fl_content);
        pullLeft = naviBarRelativeLayout.findViewById(R.id.pull_left);
        pullRight = naviBarRelativeLayout.findViewById(R.id.pull_right);
        windowManager.addView(naviBarRelativeLayout, params);
        naviBarRelativeLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        naviBarRelativeLayout.setSlideListener(slideListener);
        view.setSlideListener(slideListener);
    }

    private SlideListener slideListener = new SlideListener() {
        @Override
        public void onSlideToRight() {
            startAnimationLeftIn();
        }

        @Override
        public void onSlideToLeft() {
            startAnimationLeftOut();
        }
    };

    private synchronized void startAnimationLeftOut() {
        if (view == null || !isSlideIn) {
            return;
        }
        KLog.d("NaviBarWindow onSlideToLeft");
        isSlideIn = false;
        view.setVisibility(View.VISIBLE);
        view.clearAnimation();
        if (animationLeftOut == null) {
            animationLeftOut = new TranslateAnimation(0, ANIMATION_SCREEN_WIDTH, 0, 0);
            animationLeftOut.setDuration(ANIMATION_TIME);
            animationLeftOut.setFillEnabled(true);
            animationLeftOut.setFillAfter(true);
            animationLeftOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    pullRight.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    pullLeft.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    params.width = NAVIBAR_WINDOW_WIDTH;
                    windowManager.updateViewLayout(naviBarRelativeLayout, params);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        view.setAnimation(animationLeftOut);
        animationLeftOut.startNow();
    }

    private synchronized void startAnimationLeftIn() {
        if (view == null || isSlideIn) {
            return;
        }
        KLog.d("NaviBarWindow onSlideToRight");
        isSlideIn = true;
        params.width = SCREEN_WIDTH;
        windowManager.updateViewLayout(naviBarRelativeLayout, params);
        view.setVisibility(View.VISIBLE);
        view.clearAnimation();
        if (animationLeftIn == null) {
            animationLeftIn = new TranslateAnimation(ANIMATION_SCREEN_WIDTH, 0, 0, 0);
            animationLeftIn.setDuration(ANIMATION_TIME);
            animationLeftIn.setFillEnabled(true);
            animationLeftIn.setFillAfter(true);
            animationLeftIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    pullLeft.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    pullRight.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        view.setAnimation(animationLeftIn);
        animationLeftIn.startNow();
        view.notifyUpdate();
    }

    private BroadcastReceiver naviBarWindowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || NaviBarWindow.this.context == null || NaviBarWindow.this.view == null) {
                return;
            }
            if (ConfigConstants.NAVIBARWINDOW_OPEN_ACTION.equals(intent.getAction())) {
                startAnimationLeftIn();
            }
            if (ConfigConstants.NAVIBARWINDOW_CLOSE_ACTION.equals(intent.getAction())) {
                startAnimationLeftOut();
            }
        }
    };

    public void closeNaviWindow() {
        startAnimationLeftOut();
    }

}
