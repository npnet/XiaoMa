package com.xiaoma.ui.navi;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.skin.views.XmViewSwitcher;
import com.xiaoma.ui.R;
import com.xiaoma.ui.UIConstants;
import com.xiaoma.utils.DoubleClickUtils;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.LaunchUtils;

import java.io.File;

/**
 * 左侧导航栏有三种状态
 * 1、普通(显示Back按钮 + Home按钮) --> {@link #showBackAndHomeNavi()}
 * 2、仅返回(显示Back按钮) --> {@link #showBackNavi()}
 * 3、隐藏 --> {@link #hideNavi()}
 *
 * @author Jir
 * @date 2018/9/30
 */
public class NavigationBar extends LinearLayout implements INavibarConstract, View.OnClickListener {

    public static final int DISPLAY_BACK_AND_HOME = 0;
    public static final int DISPLAY_BACK = 1;
    private XmViewSwitcher mNaviStateVS;
    private View haloView;
    private ImageView mMiddleView;
    private ImageView mIvBack;
    private ImageView mIvBackPre;
    private ImageView mIvHome;

    public NavigationBar(Context context) {
        this(context, null);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_navigation, this);

        mNaviStateVS = findViewById(R.id.vsNaviState);
        mIvBack = findViewById(R.id.ivBack);
        mIvBackPre = findViewById(R.id.ivBackPre);
        mIvHome = findViewById(R.id.ivHome);

        mIvBack.setOnClickListener(this);
        mIvBackPre.setOnClickListener(this);
        mIvHome.setOnClickListener(this);
    }

    public ImageView getMiddleView() {
        if (mMiddleView == null) {
            mMiddleView = findViewById(R.id.ivMiddle);
        }
        mIvBack.setPadding(0, 113, 0, 0);
        mIvHome.setPadding(0, 0, 0, 113);
        mMiddleView.setVisibility(VISIBLE);
        return mMiddleView;
    }

    private static boolean ignoreClick() {
        if (ConfigManager.ApkConfig.isDebug()) {
            File sdCard = Environment.getExternalStorageDirectory();
            File file = new File(sdCard, "xm_ignore.xmcfg");
            return file.exists();
        }
        return false;
    }

    public ImageView getBackPreView() {
        if (mIvBackPre == null) {
            mIvBackPre = findViewById(R.id.ivBackPre);
        }
        return mIvBackPre;
    }

    @Override
    public void showBackAndHomeNavi() {
        if (!UIConstants.SHOW_NAVI_BAR) {
            return;
        }
        setVisibility(VISIBLE);
        if (haloView != null) {
            haloView.setVisibility(VISIBLE);
        }
        mIvBack.setPadding(0, 37, 0, 0);
        mIvHome.setPadding(0, 0, 0, 45);
        mNaviStateVS.setDisplayedChild(DISPLAY_BACK_AND_HOME);
    }

    @Override
    public void showBackNavi() {
        if (!UIConstants.SHOW_NAVI_BAR) {
            return;
        }
        setVisibility(VISIBLE);
        if (haloView != null) {
            haloView.setVisibility(VISIBLE);
        }
        mNaviStateVS.setDisplayedChild(DISPLAY_BACK);
    }

    @Override
    public void hideNavi() {
        setVisibility(GONE);
        if (haloView != null) {
            haloView.setVisibility(GONE);
        }
    }

    public ImageView getBackView() {
        if (mIvBack == null) {
            mIvBack = findViewById(R.id.ivBack);
        }
        return mIvBack;
    }

    public void setHaloView(View view) {
        if (view == null) {
            return;
        }
        this.haloView = view;
    }

    @Override
    public void onClick(View v) {
        if (DoubleClickUtils.isFastDoubleClick(500)) return;
        int viewId = v.getId();
        if (viewId == R.id.ivHome) {
            if (ignoreClick()) {
                return;
            }
            if (UIConstants.isLauncherAppHome(getContext())) {
                LaunchUtils.launchApp(getContext(), UIConstants.LAUNCHER_PKG, UIConstants.LAUNCHER_MAINACTIVITY);
            } else {
                KeyEventUtils.sendKeyEvent(getContext(), KeyEvent.KEYCODE_HOME);
            }
        } else if (viewId == R.id.ivBack) {
            if (ignoreClick()) {
                return;
            }
            KeyEventUtils.sendKeyEvent(getContext(), KeyEvent.KEYCODE_BACK);
        } else if (viewId == R.id.ivBackPre) {
            if (ignoreClick()) {
                return;
            }
            KeyEventUtils.sendKeyEvent(getContext(), KeyEvent.KEYCODE_BACK);
        }
    }
}
