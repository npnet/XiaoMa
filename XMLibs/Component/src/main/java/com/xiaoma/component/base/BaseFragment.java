package com.xiaoma.component.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.xiaoma.component.R;
import com.xiaoma.model.XmResource;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.UISupport;
import com.xiaoma.ui.navi.NavigationBar;
import com.xiaoma.ui.progress.ProgressSupport;
import com.xiaoma.ui.progress.loading.CustomProgressDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import java.lang.ref.WeakReference;


/**
 * Created by youthyj on 2018/9/6.
 */
public class BaseFragment extends Fragment implements UISupport, ProgressSupport {
    protected Context mContext;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private NavigationBar naviBar;
    protected StateView mStateView;
    protected FragmentActivity mActivity;
    private WeakReference<Context> mAppContextRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (mContext != null) {
            mAppContextRef = new WeakReference<>(mContext.getApplicationContext());
        }
        FragmentActivity activity = getActivity();
        if (activity != null) {
            naviBar = activity.findViewById(R.id.view_root_navi);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).dismissProgress();
        }
        mActivity = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Nullable
    @Override
    public Context getContext() {
        Context context = super.getContext();
        if (context == null && mAppContextRef != null) {
            context = mAppContextRef.get();
        }
        return context;
    }

    @Override
    public CustomProgressDialog getProgressDialog() {
        if (mActivity instanceof BaseActivity) {
            return ((BaseActivity) mActivity).getProgressDialog();
        }
        return null;
    }

    protected void showProgressDialog(@StringRes int resId) {
        if (isDestroy()) {
            return;
        }
        String content = ResUtils.getString(getActivity(), resId);
        showProgressDialog(content);
    }

    @Override
    public boolean isDestroy() {
        final Activity activity = getActivity();
        if (activity != null) {
            return activity.isFinishing() || activity.isDestroyed();
        }
        final Context context = getContext();
        if (context != null) {
            return !isAdded();
        }
        return true;
    }

    @Override
    public Handler getUIHandler() {
        return uiHandler;
    }

    protected void showProgressDialog(final String content) {
        if (isDestroy()) {
            return;
        }
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).showProgressDialog(content);
        }
    }

    public NavigationBar getNaviBar() {
        return naviBar;
    }

    protected void dismissProgress() {
        if (isDestroy()) {
            return;
        }
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).dismissProgress();
        }
    }

    protected void showToast(@StringRes int strRes) {
        try {
            showToast(getString(strRes));
        } catch (Exception ignore) {
        }
    }

    protected void showToast(String content) {
        if (isDestroy()) {
            return;
        }
        XMToast.showToast(getActivity(), content);
    }

    protected void showToastException(@StringRes int strRes) {
        try {
            showToastException(getString(strRes));
        } catch (Exception ignore) {
        }
    }

    protected void showToastException(String content) {
        if (isDestroy()) {
            return;
        }
        XMToast.toastException(getActivity(), content);
    }

    protected View onCreateWrapView(View childView) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_base_state, null);
        FrameLayout fullContentLayout = rootView.findViewById(R.id.full_content_layout);
        mStateView = rootView.findViewById(R.id.state_view);
        fullContentLayout.addView(childView);
        mStateView.setOnRetryClickListener(new OnRetryClickListener() {
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
        return rootView;
    }


    protected void errorOnRetry() {
    }

    protected void emptyOnRetry() {
    }

    protected void noNetworkOnRetry() {
        showContentView();
    }

    protected void setNoNetworkView(int resId) {
        if (mStateView != null && !isDestroy()) {
            mStateView.setNoNetworkView(resId);
        }
    }

    protected void showNoNetView() {
        if (mStateView != null && !isDestroy()) {
            mStateView.showNoNetwork();
        }
    }

    protected void showContentView() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (mStateView != null && !isDestroy()) {
                    mStateView.showContent();
                }
            }
        });
    }

    protected void showLoadingView() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (mStateView != null && !isDestroy()) {
                    mStateView.showLoading();
                }
            }
        });
    }

    protected void showEmptyView() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (mStateView != null && !isDestroy()) {
                    mStateView.showEmpty();
                }
            }
        });
    }

    protected void showErrorView() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (mStateView != null && !isDestroy()) {
                    mStateView.showError();
                }
            }
        });
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
            showEmptyView();
        }

        @Override
        public void onError(int code, String message) {
            KLog.e("onError: ");
            dismissProgress();
            if (!StringUtil.isEmpty(message)) {
                KLog.e(message);
            }
//            if (Pattern.matches(getString(R.string.base_data_empty), message)) {
//                //空数据
//                showEmptyView();
//            } else {
            showNoNetView();
//            }
        }

        @Override
        public void onCompleted() {
            KLog.d("onCompleted: ");
            dismissProgress();

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        onHiddenChangedForAop(hidden, this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setUserVisibleHintForAop(isVisibleToUser, this);
    }

    //aop dynamic 代理使用
    public final void onHiddenChangedForAop(boolean hidden, BaseFragment baseFragment) {

    }

    //aop dynamic 代理使用
    public final void setUserVisibleHintForAop(boolean isVisibleToUser, BaseFragment baseFragment) {

    }

}
