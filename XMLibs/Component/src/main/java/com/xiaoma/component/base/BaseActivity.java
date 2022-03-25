package com.xiaoma.component.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.xiaoma.component.R;
import com.xiaoma.component.dispatch.SimulateWheelDispatch;
import com.xiaoma.component.nodejump.ChildNode;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.component.nodejump.RootNode;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.model.XmResource;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.UIConstants;
import com.xiaoma.ui.UISupport;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.ui.navi.NavigationBar;
import com.xiaoma.ui.progress.ProgressSupport;
import com.xiaoma.ui.progress.loading.CustomProgressDialog;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.annotation.CommandProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import skin.support.content.res.SkinCompatResources;


/**
 * Created by youthyj on 2018/9/6.
 */
public class BaseActivity extends FragmentActivity implements UISupport, ProgressSupport, RootNode {
    private static final String TAG = "BaseActivity";
    private ViewGroup rootLayout;
    private FrameLayout contentLayout;
    public final int STATUS_BAR_MARGIN_LEFT = 260;
    private NavigationBar naviBar;
    private View naviHalo;
    private CustomProgressDialog progressDialog;
    private Handler uiHandler;
    protected StateView mBaseStateView;
    private NodeUtils.RootNodeProxy mNodeProxy;
    public final int NAVI_BAR_WIDTH = 165;
    private View statusBarDivider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLandscape();
        super.setContentView(R.layout.layout_activity);
        rootLayout = findViewById(R.id.activity_root_layout);
        contentLayout = findViewById(R.id.view_content);
        statusBarDivider = findViewById(R.id.view_status_bar_divider);
        naviBar = findViewById(R.id.view_root_navi);
        naviHalo = findViewById(R.id.view_navi_halo);
        naviBar.setHaloView(naviHalo);
        setNaviBarVisible();
        mBaseStateView = findViewById(R.id.base_state_view);
        mBaseStateView.setOnRetryClickListener(new OnRetryClickListener() {
            @Override
            public void onRetryClick(View view, Type type) {
                switch (type) {
                    case ERROR:
                        errorOnRetry();
                        break;
                    case EEMPTY:
                        emptyOnRetry();
                        break;
                    case NONETWORK:
                        noNetworkOnRetry();
                        break;
                }
            }
        });
        mNodeProxy = new NodeUtils.RootNodeProxy(this);
        setupStatusBarDivider();
        // ↓模拟用的方控按键
        // showWheelBtn();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, String.format("onConfigurationChanged -> act: %s, mcc: %s", getClass().getName(), newConfig.mcc));
        super.onConfigurationChanged(newConfig);
        getWindow().setBackgroundDrawable(SkinCompatResources.getDrawableCompat(this, R.drawable.bg_common));
    }

    private void showWheelBtn() {
        if (!ConfigManager.ApkConfig.isDebug() || ConfigManager.ApkConfig.isCarPlatform()) {
            return;
        }
        if (!"com.xiaoma.xting".equals(getPackageName())
                && !"com.xiaoma.music".equals(getPackageName())) {
            return;
        }
        ViewParent parent = rootLayout.getParent();
        ViewGroup.LayoutParams rootViewLP = rootLayout.getLayoutParams();
        ((ViewGroup) parent).removeView(rootLayout);
        final RelativeLayout wheelLayout = (RelativeLayout) View.inflate(
                this, R.layout.layout_wheel, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        wheelLayout.addView(rootLayout, lp);
        ((ViewGroup) parent).addView(wheelLayout, rootViewLP);
        findViewById(R.id.rl_wheel).bringToFront();
        findViewById(R.id.wheel_next).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        SimulateWheelDispatch.getInstance().notifyNextDownEvent();
                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        // 穿透
                    case MotionEvent.ACTION_UP:
                        SimulateWheelDispatch.getInstance().notifyNextUpEvent();
                        return false;

                }
                return false;
            }
        });

        findViewById(R.id.wheel_previous).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                boolean result;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        result = SimulateWheelDispatch.getInstance().notifyPreviousDownEvent();
                        return result;
                    case MotionEvent.ACTION_CANCEL:
                        // 穿透
                    case MotionEvent.ACTION_UP:
                        result = SimulateWheelDispatch.getInstance().notifyPreviousUpEvent();
                        return result;

                }
                return false;
            }
        });
    }

    private void setupStatusBarDivider() {
        if (statusBarDivider == null) {
            return;
        }
        final String idName = "status_bar_height";
        final String defType = "dimen";
        final String defPackage = "android";
        int statusBarId = getResources().getIdentifier(idName, defType, defPackage);
        int statusBarHeight = 0;
        if (statusBarId > 0) {
            statusBarHeight = getApplicationContext().getResources().getDimensionPixelSize(statusBarId);
        }
        if (statusBarHeight > 0) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) statusBarDivider.getLayoutParams();
            lp.topMargin = statusBarHeight;
            lp.leftMargin = statusBarDividerMarginLeft();
            statusBarDivider.setLayoutParams(lp);
            statusBarDivider.setVisibility(showStatusBarDivider() ? View.VISIBLE : View.GONE);
        }
    }

    protected int statusBarDividerMarginLeft() {
        return STATUS_BAR_MARGIN_LEFT;
    }

    protected boolean showStatusBarDivider() {
        return true;
    }

    protected void statusBarDividerGone() {
        statusBarDivider.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommandProcessor.remove(this);
    }

    private void setNaviBarVisible() {
        final boolean showNavi = UIConstants.SHOW_NAVI_BAR && isAppNeedShowNaviBar();
        if (showNavi) {
            naviBar.setVisibility(View.VISIBLE);
            naviHalo.setVisibility(View.VISIBLE);
        } else {
            naviBar.setVisibility(View.GONE);
            naviHalo.setVisibility(View.GONE);
        }
        if (showNavi) {
            // 显示应用内导航栏时,隐藏系统的导航栏
            UIUtils.hideNavigationBar(getWindow());
        }
    }

    protected boolean isAppNeedShowNaviBar() {
        return true;
    }

    private void setLandscape() {
        // 全屏不透明Activity才能设置Orientation,否则在高版本Android会Crash,复现版本为8.0
        // 错误信息: java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
        try {
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void errorOnRetry() {
    }

    protected void emptyOnRetry() {
    }

    protected void noNetworkOnRetry() {
        checkNet();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    dispatchActivityResult(fragment, requestCode, resultCode, data);
                }
            }
        }
    }

    private void dispatchActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
        List<Fragment> children = fragment.getChildFragmentManager().getFragments();
        if (children != null)
            for (Fragment child : children)
                if (child != null) {
                    dispatchActivityResult(child, requestCode, resultCode, data);
                }
    }


    @SuppressLint("MissingPermission")
    protected void checkNet() {
        if (!NetworkUtils.isConnected(this)) {
            showNoNetView();
        } else {
            showContentView();
        }
    }

    protected void showNoNetView() {
        mBaseStateView.showNoNetwork();
    }

    protected void showContentView() {
        mBaseStateView.showContent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XMProgress.dismissProgressDialog(this);
        FragmentUtils.removeAll(getSupportFragmentManager());
    }

    protected void showLoadingView() {
        mBaseStateView.showLoading();
    }

    protected void showEmptyView() {
        mBaseStateView.showEmpty();
    }

    protected void showErrorView() {
        mBaseStateView.showError();
    }


    @Override
    public boolean isDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return isDestroyed();
        }
        return false;
    }

    @Override
    public Handler getUIHandler() {
        if (uiHandler == null) {
            uiHandler = new Handler(Looper.getMainLooper());
        }
        return uiHandler;
    }

    @Override
    public CustomProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
        }
        return progressDialog;
    }

    public ViewGroup getRootLayout() {
        return rootLayout;
    }

    public NavigationBar getNaviBar() {
        return naviBar;
    }

    private ViewGroup getAndClearContentLayout() {
        contentLayout.removeAllViewsInLayout();
        return contentLayout;
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, getAndClearContentLayout());
        onContentChanged();//因为我们重写了setContentView,因此要重新把这个回调执行
    }

    @Override
    public void setContentView(View view) {
        getAndClearContentLayout().addView(view);
        onContentChanged();//因为我们重写了setContentView,因此要重新把这个回调执行
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getAndClearContentLayout().addView(view, params);
        onContentChanged();//因为我们重写了setContentView,因此要重新把这个回调执行
    }

    protected void showProgressDialog(@StringRes int resId) {
        String content = ResUtils.getString(this, resId);
        showProgressDialog(content);
    }

    protected void showProgressDialog(final String content) {
        // NaviBar不可见时直接展示
        if (naviBar == null || naviBar.getVisibility() != View.VISIBLE) {
            XMProgress.showProgressDialog(BaseActivity.this, content, 0, 0, 0, 0);
            return;
        }

        XMProgress.showProgressDialog(BaseActivity.this, content, NAVI_BAR_WIDTH, 0, 0, 0);
        // NaviBar可见时排除掉NaviBar的宽度
//        naviBar.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    int naviBarWidth = naviBar.getWidth();
//                    XMProgress.showProgressDialog(BaseActivity.this, content, naviBarWidth, 0, 0, 0);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    protected void showProgressDialogNoMsg() {
        showProgressDialog("");
    }

    protected void dismissProgress() {
        //todo 暂时延迟处理，之后在仔细研究下
        try {
            XMProgress.dismissProgressDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showToast(@StringRes int resId) {
        String content = ResUtils.getString(this, resId);
        showToast(content);
    }

    protected void showToast(String content) {
        XMToast.showToast(this, content);
    }

    protected void showToastException(@StringRes int resId) {
        String content = ResUtils.getString(this, resId);
        showToastException(content);
    }

    protected void showToastException(String content) {
        XMToast.toastException(this, content);
    }

    /**
     * 跳转页面
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    public abstract class OnCallback<T> implements XmResource.OnHandleCallback<T> {

        @Override
        public void onLoading() {
            KLog.d("onLoading: ");
            showProgressDialog(R.string.base_loading);
        }

        @Override
        public void onFailure(String msg) {
            KLog.e("onFailure: ");
            dismissProgress();
            if (!StringUtil.isEmpty(msg)) {
                KLog.e(msg);
            }
            if (mBaseStateView != null) {
                mBaseStateView.showNoNetwork();
            }
        }

        @Override
        public void onError(int code, String message) {
            KLog.e("onError: ");
            dismissProgress();
            if (StringUtil.isEmpty(message)) {
                return;
            }
            KLog.e(message);
            if (mBaseStateView != null) {
                if (Pattern.matches(getString(R.string.base_data_empty), message)) {
                    //空数据
                    mBaseStateView.showEmpty();
                } else {
                    mBaseStateView.showNoNetwork();
                }
            }
        }

        @Override
        public void onCompleted() {
            KLog.d("onCompleted: ");
            dismissProgress();
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && !fragments.isEmpty()) {
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment child = fragments.get(i);
                if (child.getChildFragmentManager().popBackStackImmediate()) {
                    KLog.d(TAG, String.format("onBackPressed() [ %s ] popup backStack !", child.getClass().getName()));
                    return;
                }
            }
        }
        if (getSupportFragmentManager().popBackStackImmediate()) {
            KLog.d(TAG, "onBackPressed() Support popup backStack !");
            return;
        }
        KLog.d(TAG, "onBackPressed() call super !");
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommandProcessor.register(this);
        String jumpNodes = getIntent().getStringExtra(NodeConst.JUMP_NODES);
        if (!TextUtils.isEmpty(jumpNodes)) {
            mNodeProxy.parsingNodes(jumpNodes);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntent().putExtra(NodeConst.JUMP_NODES, intent.getStringExtra(NodeConst.JUMP_NODES));
    }

    @Override
    public final boolean parsingNodes(String jumpNodes) {
        return mNodeProxy.parsingNodes(jumpNodes);
    }

    @Override
    @CallSuper
    public boolean handleJump(String nextNode) {
        return false;
    }

    @Override
    public final String getJumpNodes() {
        return mNodeProxy.getJumpNodes();
    }

    @Override
    public final void setJumpNodes(String mJumpNode) {
        mNodeProxy.setJumpNodes(mJumpNode);
    }

    @Override
    public final boolean isJumping() {
        return mNodeProxy.isJumping();
    }

    @Override
    public final void setJumping(boolean mJumping) {
        mNodeProxy.setJumping(mJumping);
    }

    @Override
    public String getThisNode() {
        return this.getClass().getSimpleName();
    }

    @Override
    public ChildNode getNextNode(String nextNode) {
        List<ChildNode> childNodes = getChildNodes();
        for (ChildNode childNode : childNodes) {
            if (childNode != null
                    && childNode.getThisNode().equals(nextNode)) {
                return childNode;
            }
        }
        return null;
    }

    @Override
    public final void clearInitNodes() {
        if (getIntent() != null) {
            getIntent().removeExtra(NodeConst.JUMP_NODES);
        }
    }

    @Override
    public final List<ChildNode> getChildNodes() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        List<ChildNode> childNodes = new ArrayList<>();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ChildNode) {
                childNodes.add((ChildNode) fragment);
            }
        }
        return childNodes;
    }
}
