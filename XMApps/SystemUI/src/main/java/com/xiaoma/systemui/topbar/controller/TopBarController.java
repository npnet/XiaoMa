package com.xiaoma.systemui.topbar.controller;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.IWindowManager;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManagerGlobal;

import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.common.constant.PackageName;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.common.util.PkgUtil;
import com.xiaoma.systemui.navigationbar.NavConstant;
import com.xiaoma.systemui.navigationbar.NavigationBarController;
import com.xiaoma.systemui.topbar.view.DraggableParent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LKF on 2018/11/6 0006.
 */
public class TopBarController {
    @SuppressLint("StaticFieldLeak")
    private static TopBarController sInstance;
    private static final long STATUS_BAR_HIDE_DELAY = 2250;
    private static final int STATUS_OR_NAV_TRANSIENT =
            View.STATUS_BAR_TRANSIENT | View.NAVIGATION_BAR_TRANSIENT;

    public static TopBarController getInstance() {
        if (sInstance == null) {
            synchronized (TopBarController.class) {
                if (sInstance == null) {
                    sInstance = new TopBarController();
                }
            }
        }
        return sInstance;
    }

    private TopBarController() {
    }

    private Context mContext;
    private IWindowManager mWms;
    private StatusBar mStatusBar;
    private float mLastVelocityY;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mHideStatusBarTask = new Runnable() {
        @Override
        public void run() {
            // 此Flag表示当前的SystemBar只是短暂显示,去除该Flag让系统再次隐藏SystemBar
            final int requested = mStatusBar.mSystemUiVisibility & ~STATUS_OR_NAV_TRANSIENT;
            if (requested != mStatusBar.mSystemUiVisibility) {
                try {
                    mWms.statusBarVisibilityChanged(requested);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void delayHideStatusBar() {
        mHandler.removeCallbacks(mHideStatusBarTask);
        mHandler.postDelayed(mHideStatusBarTask, STATUS_BAR_HIDE_DELAY);
    }

    private void logI(String format, Object... args) {
        LogUtil.logI(getClass().getSimpleName(), format, args);
    }

    public void init(Context context) {
        mContext = context;
        mWms = WindowManagerGlobal.getWindowManagerService();
        // 注册SystemUI服务
        mStatusBar = new StatusBar();
        final List<String> iconSlots = new ArrayList<>();
        final List<StatusBarIcon> icons = new ArrayList<>();
        final int[] switches = new int[9];
        final List<IBinder> binders = new ArrayList<>();
        final Rect fullscreenStackBounds = new Rect();
        final Rect dockedStackBounds = new Rect();
        try {
            final IStatusBarService statusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService(Context.STATUS_BAR_SERVICE));
            statusBarService.registerStatusBar(mStatusBar, iconSlots, icons, switches, binders, fullscreenStackBounds, dockedStackBounds);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initStatusBar(context, iconSlots, icons);
        initPanel(context);
    }

    private void initStatusBar(final Context context, List<String> iconSlots, List<StatusBarIcon> icons) {
        final StatusBarController statusBarController = StatusBarController.getInstance();
        statusBarController.init(context);

        // 设置初始图标
        logI("initStatusBar( IconSlots: %s )", iconSlots);
        logI("initStatusBar( Icons: %s )", icons);
        statusBarController.removeAllIcons();
        final int count = Math.min(iconSlots.size(), icons.size());
        for (int i = 0; i < count; i++) {
            statusBarController.setIcon(iconSlots.get(i), icons.get(i));
        }

        //拖动处理
        statusBarController.setDragCallback(new BarDragCallback("StatusBar") {
            private boolean mStatusBarVisibleOnCaptured;
            private boolean mInDragMode;

            @Override
            public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
                logI("StatusBar onViewCaptured( capturedChild: %s, activePointerId: %s )", capturedChild, activePointerId);
                mStatusBarVisibleOnCaptured = statusBarController.isVisible();
                mInDragMode = false;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                logI("StatusBar clampViewPositionVertical( top: %s, dy: %s )", top, dy);
                if (!mInDragMode) {
                    if (Math.abs(top) >= Math.sqrt(ViewConfiguration.get(context).getScaledTouchSlop())) {
                        // 准备进入面板拖拽模式
                        mInDragMode = true;
                    }
                }
                if (mStatusBarVisibleOnCaptured && mInDragMode) {
                    return super.clampViewPositionVertical(child, top, dy);
                }
                return 0;
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
                if (mInDragMode) {
                    if (mStatusBarVisibleOnCaptured) {
                        super.onViewReleased(releasedChild, xVel, yVel);
                    } else {
                        logI("StatusBar onViewReleased( xVel: %s, yVel: %s ) StatusBar [Not] visible on captured", xVel, yVel);
                        if (Math.abs(yVel) > Math.abs(xVel) && yVel > 0) {
                            // 延时自动隐藏
                            delayHideStatusBar();
                        }
                    }
                }
                mInDragMode = false;
            }
        });
    }

    private void initPanel(Context context) {
        final PanelController panelController = PanelController.getInstance();
        panelController.init(context);
        panelController.setCallback(new BarDragCallback("Panel"));
    }

    private void offsetPanelTranslationY(float distanceY) {
        final PanelController controller = PanelController.getInstance();
        final float curY = controller.getTranslationY();
        final float maxY = controller.getPanelHeight();
        float newY = curY - distanceY;
        if (newY < 0) {
            newY = 0;
        } else if (newY > maxY) {
            newY = maxY;
        }
        if (newY != curY) {
            controller.setTranslationY(newY);
        }
    }

    private void handlePanelAdsorb() {
        final PanelController controller = PanelController.getInstance();
        final float yVel = mLastVelocityY;
        // 手势向下
        if (yVel > 0) {
            controller.animateExpandPanel();
        } else if (yVel < 0) {
            controller.animateCollapsePanel();
        } else {
            if (controller.getTranslationY() >= controller.getPanelHeight() / 2) {
                controller.animateExpandPanel();
            } else {
                controller.animateCollapsePanel();
            }
        }
    }

    /*public void release() {
        StatusBarController.getInstance().release();
        PanelController.getInstance().release();
    }*/

    private Context getContext() {
        return mContext;
    }

    public void showStatusBar() {
        final StatusBarController controller = StatusBarController.getInstance();
        controller.show();
    }

    private class BarDragCallback implements DraggableParent.DragCallback {
        private String mName;

        BarDragCallback(String name) {
            mName = name;
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            logI(mName + "# onViewCaptured( capturedChild: %s, activePointerId: %s )", capturedChild, activePointerId);
            // 拖拽开始
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            logI(mName + "# clampViewPositionVertical( child: %s, top: %s, dy: %s )", child, top, dy);
            final PanelController controller = PanelController.getInstance();
            if (!controller.isShowing()) {
                controller.show();
                controller.setTranslationY(0);
            }
            offsetPanelTranslationY(-dy);
            return 0;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
            logI(mName + "# onViewReleased( releasedChild: %s, xVel: %s, yVel: %s )", releasedChild, xVel, yVel);
            // 注意:RecyclerView回调的速度和View是反的
            if (releasedChild instanceof RecyclerView) {
                yVel = -yVel;
            }
            if ((yVel < 0 && !releasedChild.canScrollVertically(1))
                    || (yVel > 0 && !releasedChild.canScrollVertically(-1))) {
                mLastVelocityY = yVel;
            }
            handlePanelAdsorb();
        }

        @Override
        public boolean shouldConsumeNestedScroll(@NonNull View target, int dx, int dy) {
            final PanelController controller = PanelController.getInstance();
            if (controller.isShowing()) {
                final float y = controller.getTranslationY();
                final float h = controller.getPanelHeight();
                logI("shouldConsumeNestedScroll() y: %s, h: %s", y, h);
                return y > 0 && y < h;
            }
            return false;
        }
    }

    public IStatusBar getStatusBar() {
        return mStatusBar;
    }

    private class StatusBar extends SimpleStatusBar {
        private int mSystemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
        //private boolean mLastStatusBarUnHide;

        // 此方法是在子线程执行,不要直接操作UI
        @Override
        public void topAppWindowChanged(boolean menuVisible) {
            super.topAppWindowChanged(menuVisible);
            final ActivityManager am = (ActivityManager) getContext().getSystemService(Service.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(1);
            if (taskInfoList == null || taskInfoList.isEmpty()) {
                logI("topAppWindowChanged( menuVisible: %s ) [ TaskInfoList is empty: %s ]",
                        menuVisible, taskInfoList);
                return;
            }
            final ComponentName topAct = taskInfoList.get(0).topActivity;
            final String topApk = topAct.getPackageName();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    final NavigationBarController controller = NavigationBarController.getInstance();
                    boolean dismissNavBar = false;
                    if (!topApk.equals(getContext().getPackageName())
                            &&
                            (topApk.startsWith("com.xiaoma") || NavConstant.HIDE_NAV_BAR_APP.contains(topApk))) {
                        // 小马应用或指定APP不显示导航栏
                        dismissNavBar = true;
                    } else if (PkgUtil.isSystemPackage(topApk) && !PackageName.SYSTEM_LAUNCHER.equals(topApk)) {
                        // 除了原生桌面,其他系统APP都隐藏导航栏
                        dismissNavBar = true;
                    }

                    if (dismissNavBar) {
                        controller.dismiss();
                    } else {
                        if (!controller.isShowing()) {
                            controller.show();
                        }
                    }
                }
            });
            logI("topAppWindowChanged( menuVisible: %s ) [ packageName: %s, Activity: %s ]",
                    menuVisible, topAct.getPackageName(), topAct.getClassName());
        }

        @Override
        public void setIcon(String slot, StatusBarIcon icon) {
            super.setIcon(slot, icon);
            StatusBarController.getInstance().setIcon(slot, icon);
            StatusBarController.getInstance().update();
        }

        @Override
        public void removeIcon(String slot) {
            super.removeIcon(slot);
            StatusBarController.getInstance().removeIcon(slot);
            StatusBarController.getInstance().update();
        }

        @Override
        public void animateCollapsePanels() {
            super.animateCollapsePanels();
            PanelController.getInstance().animateCollapsePanel();
            logI("animateCollapsePanels()");
        }

        @Override
        public void animateExpandSettingsPanel(String subPanel) {
            super.animateExpandSettingsPanel(subPanel);
            PanelController.getInstance().animateExpandPanel();
            logI("animateExpandSettingsPanel( subPanel: %s )", subPanel);
        }

        @Override
        public void animateExpandNotificationsPanel() {
            super.animateExpandNotificationsPanel();
            PanelController.getInstance().animateExpandPanel();
            logI("animateExpandNotificationsPanel");
        }

        private void dumpVisFlags(int vis) {
            final List<String> flagList = new ArrayList<>();
            if ((vis & View.STATUS_BAR_UNHIDE) != 0) {
                flagList.add("STATUS_BAR_UNHIDE");
            }
            if ((vis & View.NAVIGATION_BAR_UNHIDE) != 0) {
                flagList.add("NAVIGATION_BAR_UNHIDE");
            }
            if ((vis & View.STATUS_BAR_TRANSPARENT) != 0) {
                flagList.add("STATUS_BAR_TRANSPARENT");
            }
            if ((vis & View.NAVIGATION_BAR_TRANSPARENT) != 0) {
                flagList.add("NAVIGATION_BAR_TRANSPARENT");
            }
            if ((vis & View.SYSTEM_UI_TRANSPARENT) != 0) {
                flagList.add("SYSTEM_UI_TRANSPARENT");
            }
            if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                flagList.add("SYSTEM_UI_FLAG_LOW_PROFILE");
            }
            if ((vis & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) {
                flagList.add("SYSTEM_UI_FLAG_HIDE_NAVIGATION");
            }
            if ((vis & View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) != 0) {
                flagList.add("SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION");
            }
            if ((vis & View.STATUS_BAR_TRANSIENT) != 0) {
                flagList.add("STATUS_BAR_TRANSIENT");
            }
            if ((vis & View.NAVIGATION_BAR_TRANSIENT) != 0) {
                flagList.add("NAVIGATION_BAR_TRANSIENT");
            }
            if ((vis & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) {
                flagList.add("SYSTEM_UI_FLAG_FULLSCREEN");
            }
            if ((vis & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) != 0) {
                flagList.add("SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN");
            }
            final StringBuilder sb = new StringBuilder();
            final String div = " | ";
            for (String flagText : flagList) {
                sb.append(flagText).append(div);
            }
            if (sb.length() > 0)
                sb.delete(sb.length() - div.length(), sb.length());
            logI("Visibility -> VisFlags : " + sb);
        }

        @Override
        public void setSystemUiVisibility(final int vis, int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenBounds, Rect dockedBounds) {
            dumpVisFlags(vis);
            final int oldVal = mSystemUiVisibility;
            final int newVal = (oldVal & ~mask) | (vis & mask);
            final int diff = newVal ^ oldVal;
            logI("Visibility -> setSystemUiVisibility -> [ vis: %s, fullscreenStackVis: %s, dockedStackVis: %s, mask: %s, fullscreenBounds: %s, dockedBounds: %s ] Diff: %s",
                    vis, fullscreenStackVis, dockedStackVis, mask, fullscreenBounds, dockedBounds, diff);
            if (diff != 0) {
                mSystemUiVisibility = newVal;
                // 处于全屏状态下,下拉顶部,会回调此Flag告诉SystemUI显示状态栏
                if ((vis & View.STATUS_BAR_UNHIDE) != 0) {
                    //mLastStatusBarUnHide = true;
                    mSystemUiVisibility &= ~View.STATUS_BAR_UNHIDE;
                    StatusBarController.getInstance().update();
                    delayHideStatusBar();
                }

                // 与STATUS_BAR_UNHIDE类似
                if ((vis & View.NAVIGATION_BAR_UNHIDE) != 0) {
                    mSystemUiVisibility &= ~View.NAVIGATION_BAR_UNHIDE;
                    NavigationBarController.getInstance().update();
                }

                boolean statusBarTransparent = false;
                boolean navigationBarTransparent = false;
                // 透明状态栏
                if ((vis & View.STATUS_BAR_TRANSPARENT) != 0) {
                    mSystemUiVisibility &= ~View.STATUS_BAR_TRANSPARENT;
                    StatusBarController.getInstance().setStatusBarBackgroundColor(Color.TRANSPARENT);
                    statusBarTransparent = true;
                }
                // 透明导航栏
                if ((vis & View.NAVIGATION_BAR_TRANSPARENT) != 0) {
                    mSystemUiVisibility &= ~View.NAVIGATION_BAR_TRANSPARENT;
                    //NavigationBarController.getInstance().setNavigationBarBackgroundColor(Color.TRANSPARENT);
                    navigationBarTransparent = true;
                }
                // 透明 状态栏+导航栏
                if ((vis & View.SYSTEM_UI_TRANSPARENT) != 0) {
                    mSystemUiVisibility &= ~View.SYSTEM_UI_TRANSPARENT;
                    StatusBarController.getInstance().setStatusBarBackgroundColor(Color.TRANSPARENT);
                    //NavigationBarController.getInstance().setNavigationBarBackgroundColor(Color.TRANSPARENT);
                    statusBarTransparent = true;
                    navigationBarTransparent = true;
                }

                final Resources res = getContext().getResources();
                final Resources.Theme theme = getContext().getTheme();
                if (!statusBarTransparent) {
                    StatusBarController.getInstance().setStatusBarBackgroundColor(res.getColor(R.color.status_bar_bg, theme));
                    StatusBarController.getInstance().update();
                }
                if (!navigationBarTransparent) {
                    //NavigationBarController.getInstance().setNavigationBarBackgroundColor(res.getColor(R.color.navigation_bar_bg, theme));
                    NavigationBarController.getInstance().update();
                }
            }
        }
    }
}