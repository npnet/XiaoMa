package com.xiaoma.systemui.topbar.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.UserHandle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.common.controller.BaseController;
import com.xiaoma.systemui.topbar.view.DraggableParent;
import com.xiaoma.systemui.topbar.view.StatusBarLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LKF on 2018/11/14 0014.
 */
public class StatusBarController extends BaseController {
    @SuppressLint("StaticFieldLeak")
    private static StatusBarController sInstance;

    public static StatusBarController getInstance() {
        if (sInstance == null) {
            synchronized (StatusBarController.class) {
                if (sInstance == null) {
                    sInstance = new StatusBarController();
                }
            }
        }
        return sInstance;
    }

    private StatusBarController() {
    }

    private View mStatusBarView;
    private DraggableParent mStatusBarParent;
    private StatusBarLayout mStatusBarLayout;
    private final HashMap<String, StatusBarIcon> mIconMap = new HashMap<>();

    public void init(final Context context) {
        super.init(context);
        mStatusBarView = View.inflate(context, R.layout.status_bar_view_parent, null);
        mStatusBarParent = mStatusBarView.findViewById(R.id.status_bar_parent);
        mStatusBarLayout = mStatusBarView.findViewById(R.id.status_bar);
    }

    @Override
    protected WindowManager.LayoutParams makeLp() {
        final Context context = getContext();
        final Resources res = context.getResources();
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                res.getDisplayMetrics().widthPixels,
                res.getDimensionPixelSize(R.dimen.status_bar_height),
                WindowManager.LayoutParams.TYPE_STATUS_BAR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT);
        lp.token = new Binder();
        lp.gravity = Gravity.TOP | Gravity.END;
        /*final int navigationBarPos = SystemUIUtil.getNavigationBarPosition(context);
        switch (navigationBarPos) {
            case WindowManagerPolicy.NAV_BAR_LEFT:
                lp.gravity |= Gravity.END;
                break;
            case WindowManagerPolicy.NAV_BAR_RIGHT:
                lp.gravity |= Gravity.START;
                break;
        }*/
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        lp.setTitle("StatusBar");
        lp.packageName = context.getPackageName();
        return lp;
    }

    @Override
    protected View getSystemUIView() {
        return mStatusBarView;
    }

    @Override
    public void update() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                final List<Map.Entry<String, StatusBarIcon>> entries = new ArrayList<>(mIconMap.entrySet());
                if (!entries.isEmpty()) {
                    Collections.sort(entries, new Comparator<Map.Entry<String, StatusBarIcon>>() {
                        @Override
                        public int compare(Map.Entry<String, StatusBarIcon> o1, Map.Entry<String, StatusBarIcon> o2) {
                            return o1.getValue().iconLevel - o2.getValue().iconLevel;
                        }
                    });

                    final int childCount = mStatusBarLayout.getChildCount();
                    if (childCount > entries.size()) {
                        mStatusBarLayout.removeViewsInLayout(entries.size(), childCount - entries.size());
                        mStatusBarLayout.requestLayout();
                    } else if (childCount < entries.size()) {
                        do {
                            final ImageView iv = new ImageView(getContext());
                            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            mStatusBarLayout.addViewInLayout(iv, -1, lp);
                        } while (mStatusBarLayout.getChildCount() < entries.size());
                        mStatusBarLayout.requestLayout();
                    }
                    StringBuilder sb = new StringBuilder("update -> [ ");
                    for (int i = 0; i < entries.size(); i++) {
                        ImageView iv = (ImageView) mStatusBarLayout.getChildAt(i);
                        iv.setVisibility(View.GONE);

                        Map.Entry<String, StatusBarIcon> entry = entries.get(i);
                        StatusBarIcon statusBarIcon = entry.getValue();
                        if (statusBarIcon.visible && statusBarIcon.icon != null) {
                            // 普通状态图标
                            setIconInternal(statusBarIcon, iv);
                        }
                        sb.append(String.format("{ slot: %s, level: %s } ", entry.getKey(), statusBarIcon.iconLevel));
                    }
                    sb.append("]");
                    logI(sb.toString());
                } else {
                    mStatusBarLayout.removeAllViewsInLayout();
                    mStatusBarLayout.requestLayout();
                    logI("update() -> iconList: %s", entries);
                }
            }
        });
        super.update();
    }

    private void setIconInternal(StatusBarIcon statusBarIcon, ImageView iv) {
        iv.setVisibility(View.VISIBLE);
        final Icon icon = statusBarIcon.icon;
        int userId = statusBarIcon.user.getIdentifier();
        if (UserHandle.USER_ALL == userId) {
            userId = UserHandle.USER_SYSTEM;
        }
        Drawable drawable = icon.loadDrawableAsUser(getContext(), userId);
        iv.setImageDrawable(drawable);
    }

    void setStatusBarBackgroundColor(final int color) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mStatusBarLayout.setBackgroundColor(color);
            }
        });
    }

    void setIcon(String slot, StatusBarIcon icon) {
        mIconMap.put(slot, icon);
    }

    void removeIcon(String slot) {
        mIconMap.remove(slot);
    }

    void removeAllIcons() {
        mIconMap.clear();
    }

    void setDragCallback(DraggableParent.DragCallback callback) {
        mStatusBarParent.setDragCallback(callback);
    }

    boolean isVisible() {
        return mStatusBarLayout != null
                && View.VISIBLE == mStatusBarLayout.getVisibility();
    }
}