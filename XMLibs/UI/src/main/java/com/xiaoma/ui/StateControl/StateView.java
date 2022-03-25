package com.xiaoma.ui.StateControl;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ui.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面状态控制 ，显示内容，加载中、无网络、无数据、出错
 */
public class StateView extends FrameLayout {
    private static boolean isStaticAdd = false;
    private View emptyView;
    private View errorView;
    private View loadingView;
    private View noNetworkView;
    private View contentView;

    private int emptyViewResId;
    private int errorViewResId;
    private int loadingViewResId;
    private int noNetworkViewResId;
    private int contentViewResId;

    private OnClickListener onErrorClickListener;
    private OnClickListener onEmptyClickListener;
    private OnClickListener onNoNetWorkClickListener;

    private LayoutInflater mInflater;
    private OnRetryClickListener mOnRetryClickListener;
    private Map<Integer, View> mResId = new HashMap<>();
    private StateViewConfig defConfig = new StateViewConfig();
    private StateViewConfig mConfig;


//    public static StateView wrap(Activity activity) {
//        return wrap(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0));
//    }
//
//    public static StateView wrap(android.support.v4.app.Fragment fragment) {
//        return wrap(fragment.getView());
//    }
//
//    public static StateView wrap(Fragment fragment) {
//        return wrap(fragment.getView());
//    }
//
//    public static StateView wrap(View view) {
//        if (view == null) {
//            throw new RuntimeException("content view can not be null");
//        }
//        ViewGroup parent = (ViewGroup) view.getParent();
//        if (view == null) {
//            throw new RuntimeException("parent view can not be null");
//        }
//        ViewGroup.LayoutParams lp = view.getLayoutParams();
//        int index = parent.indexOfChild(view);
//        parent.removeView(view);
//
//        StateView status = new StateView(view.getContext());
//        parent.addView(status, index, lp);
//        status.addView(view);
//        status.setContentView(view);
//
//        initChildViewFromWrap(view, status);
//
//        return status;
//    }
//
//    private static void initChildViewFromWrap(View view, StateView status) {
//        status.emptyView = new EmptyView(view.getContext(), defConfig);
//        status.errorView = new ErrorView(view.getContext(), defConfig);
//        status.loadingView = new LoadingView(view.getContext(), defConfig);
//        status.noNetworkView = new NoNetworkView(view.getContext(), defConfig);
//
//        status.addView(status.emptyView);
//        status.addView(status.errorView);
//        status.addView(status.loadingView);
//        status.addView(status.noNetworkView);
//
//        status.emptyView.setVisibility(GONE);
//        status.errorView.setVisibility(GONE);
//        status.loadingView.setVisibility(GONE);
//        status.noNetworkView.setVisibility(GONE);
//
//        isStaticAdd = false;
//
//        status.setChildClickForJava();
//    }

    public StateView(Context context) {
        this(context, null);
    }

    public StateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initChildViewListener();
    }

    public StateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defConfig.init(context);
        if (attrs != null) {
            mInflater = LayoutInflater.from(context);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateView, defStyleAttr, 0);
            emptyViewResId = a.getResourceId(R.styleable.StateView_emptyView, defConfig.getEmptyViewResId());
            errorViewResId = a.getResourceId(R.styleable.StateView_errorView, defConfig.getErrorViewResId());
            loadingViewResId = a.getResourceId(R.styleable.StateView_loadingView, defConfig.getLoadingViewResId());
            noNetworkViewResId = a.getResourceId(R.styleable.StateView_noNetworkView, defConfig.getNoNetworkViewResId());

            checkConfig();
            getConfig().setEmptyViewResId(emptyViewResId);
            getConfig().setErrorViewResId(errorViewResId);
            getConfig().setLoadingViewResId(loadingViewResId);
            getConfig().setNoNetworkViewResId(noNetworkViewResId);
            a.recycle();
            isStaticAdd = true;
        }
        initChildViewListener();
    }


    private View layout(int resId) {
        if (mResId.containsKey(resId)) {
            return mResId.get(resId);
        }
        if (resId == 0) {
            return null;
        }
        View view = mInflater.inflate(resId, this, false);
        view.setVisibility(GONE);
        addView(view);
        mResId.put(resId, view);
        setChildClickForXml(resId, view);
        return view;
    }

    protected void initChildViewListener() {
        onErrorClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryClick(v, Type.ERROR);
            }
        };
        onEmptyClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryClick(v, Type.EEMPTY);
            }
        };
        onNoNetWorkClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryClick(v, Type.NONETWORK);
            }
        };
    }

    private void onRetryClick(View v, Type nonetwork) {
        if (mOnRetryClickListener != null) {
            mOnRetryClickListener.onRetryClick(v, nonetwork);
        }
    }


    private void setChildClickForXml(int resId, View view) {
        if (isStaticAdd && view != null && resId != 0) {
            if (resId == emptyViewResId) {
                setChildViewClick(view, view.findViewById(R.id.tv_retry), onEmptyClickListener);
            }
            if (resId == errorViewResId) {
                setChildViewClick(view, view.findViewById(R.id.tv_retry), onErrorClickListener);
            }
            if (resId == noNetworkViewResId) {
                setChildViewClick(view, view.findViewById(R.id.tv_retry), onNoNetWorkClickListener);
            }
        }
    }

    private void setChildClickForJava() {
        if (!isStaticAdd) {
            if (emptyView != null) {
                setChildViewClick(emptyView, emptyView.findViewById(R.id.tv_retry), onEmptyClickListener);
            }
            if (errorView != null) {
                setChildViewClick(errorView, errorView.findViewById(R.id.tv_retry), onErrorClickListener);
            }
            if (noNetworkView != null) {
                setChildViewClick(noNetworkView, noNetworkView.findViewById(R.id.tv_retry), onNoNetWorkClickListener);
            }
        }
    }

    private void setContentView(View view) {
        if (isStaticAdd) {
            contentViewResId = view.getId();
            mResId.put(view.getId(), view);
        } else {
            contentView = view;
            mResId.put(view.hashCode(), view);
        }
    }

    public final void showEmpty() {
        if (isStaticAdd) {
            show(emptyViewResId);
        } else {
            show(emptyView);
        }
    }

    public void setErrorViewResId(int errorViewResId) {
        this.errorViewResId = errorViewResId;
    }

    public final void showError() {
        if (isStaticAdd) {
            show(errorViewResId);
        } else {
            show(errorView);
        }
    }

    public final void showLoading() {
        if (isStaticAdd) {
            show(loadingViewResId);
        } else {
            show(loadingView);
        }
    }

    public final void showNoNetwork() {
        if (isStaticAdd) {
            show(noNetworkViewResId);
        } else {
            show(noNetworkView);
        }
    }

    public final void showContent() {
        if (isStaticAdd) {
            show(contentViewResId);
        } else {
            show(contentView);
        }
    }

    private void show(View showView) {
        if (showView == null) {
            return;
        }
        if (!mResId.containsKey(showView.hashCode())) {
            mResId.put(showView.hashCode(), showView);
        }
        for (View view : mResId.values()) {
            view.setVisibility(GONE);
        }
        showView.setVisibility(VISIBLE);
    }

    private void show(int resId) {
        for (View view : mResId.values()) {
            view.setVisibility(GONE);
        }
        if (layout(resId) != null) {
            layout(resId).setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 0) {
            return;
        }
        if (getChildCount() > 1) {
            removeViews(1, getChildCount() - 1);
        }
        View view = getChildAt(0);
        if (isStaticAdd) {
            setContentView(view);
        }
        if (getConfig().defLoading) {
            showLoading();
        }
    }

    /**
     * @param onRetryClickListener 重试点击事件，需在调用之前设置showXX方法之前
     */
    public void setOnRetryClickListener(OnRetryClickListener onRetryClickListener) {
        this.mOnRetryClickListener = onRetryClickListener;
    }


    public StateView setEmptyText(String text) {
        checkConfig();
        getConfig().setEmptyText(text);
        if (isStaticAdd) {
            text(getConfig().getEmptyViewResId(), getConfig().getTipsId(), getConfig().getEmptyText());
        }
        return this;
    }

    public StateView setEmptyImage(@DrawableRes int resId) {
        checkConfig();
        getConfig().setEmptyImage(resId);
        if (isStaticAdd && mResId.containsKey(emptyViewResId)) {
            setImage(mResId.get(emptyViewResId), getConfig().getEmptyImage());
        }
        return this;
    }

    private void checkConfig() {
        if (mConfig == null) {
            mConfig = new StateViewConfig();
        }
    }

    public StateView setErrorImage(@DrawableRes int resId) {
        checkConfig();
        getConfig().setErrorImage(resId);
        if (isStaticAdd && mResId.containsKey(errorViewResId)) {
            setImage(mResId.get(errorViewResId), getConfig().getErrorImage());
        }
        return this;
    }

    private void setImage(View view, int id) {
        if (view != null && view.findViewById(R.id.iv_tips) != null) {
            ImageView viewById = view.findViewById(R.id.iv_tips);
            viewById.setImageResource(id);
        }
    }

    public StateView setNoNetWorkImage(@DrawableRes int resId) {
        checkConfig();
        getConfig().setNoNewWorkImage(resId);
        if (isStaticAdd && mResId.containsKey(noNetworkViewResId)) {
            setImage(mResId.get(noNetworkViewResId), getConfig().getNoNewWorkImage());
        }
        return this;
    }

    public StateView setErrorText(String text) {
        checkConfig();
        getConfig().setErrorText(text);
        if (isStaticAdd) {
            text(getConfig().getErrorViewResId(), getConfig().getTipsId(), getConfig().getErrorText());
        }
        return this;
    }

    private void text(int layoutId, int ctrlId, CharSequence value) {
        if (mResId.containsKey(layoutId)) {
            TextView view = (TextView) mResId.get(layoutId).findViewById(ctrlId);
            if (view != null) {
                view.setText(value);
                if (ctrlId == getConfig().getTipsId()) {
                    view.setTextColor(getConfig().getTextColor());
                    view.setTextSize(getConfig().getTextSize());
                } else if (ctrlId == getConfig().getRetryId()) {
                    view.setTextColor(getConfig().getReTryTextColor());
                    view.setTextSize(getConfig().getReTryTextSize());
                }
            }
        }
    }

    public StateView setErrorRetryText(String text) {
        checkConfig();
        getConfig().setErrorRetryText(text);
        if (isStaticAdd) {
            text(getConfig().getErrorViewResId(), getConfig().getRetryId(), getConfig().getErrorRetryText());
        }
        return this;
    }

    public StateViewConfig getConfig() {
        if (mConfig == null) {
            return defConfig;
        } else {
            return mConfig;
        }
    }

    public StateView setNoNetWorkRetryText(String text) {
        checkConfig();
        getConfig().setNoNetWorkRetryText(text);
        if (isStaticAdd) {
            text(getConfig().getNoNetworkViewResId(), getConfig().getRetryId(), getConfig().getNoNetWorkRetryText());
        }
        return this;
    }

    public StateView setEmptyRetryText(String text) {
        checkConfig();
        getConfig().setEmptyRetryText(text);
        if (isStaticAdd) {
            text(getConfig().getEmptyViewResId(), getConfig().getRetryId(), getConfig().getEmptyRetryText());
        }
        return this;
    }

    public StateView setTextColor(@ColorInt int color) {
        checkConfig();
        getConfig().setTextColor(color);
        if (isStaticAdd) {
            text(getConfig().getEmptyViewResId(), getConfig().getTipsId(), getConfig().getEmptyText());
            text(getConfig().getErrorViewResId(), getConfig().getTipsId(), getConfig().getErrorText());
            text(getConfig().getNoNetworkViewResId(), getConfig().getTipsId(), getConfig().getNoNetWorkText());
        }
        return this;
    }

    public StateView setRetryTextColor(@ColorInt int color) {
        checkConfig();
        getConfig().setReTryTextColor(color);
        if (isStaticAdd) {
            text(getConfig().getEmptyViewResId(), getConfig().getRetryId(), getConfig().getEmptyRetryText());
            text(getConfig().getErrorViewResId(), getConfig().getRetryId(), getConfig().getErrorRetryText());
            text(getConfig().getNoNetworkViewResId(), getConfig().getRetryId(), getConfig().getNoNetWorkRetryText());
        }
        return this;
    }

    public StateView setRetryTextSize(@ColorInt int px) {
        checkConfig();
        getConfig().setReTryTextSize(px);
        if (isStaticAdd) {
            text(getConfig().getEmptyViewResId(), getConfig().getRetryId(), getConfig().getEmptyRetryText());
            text(getConfig().getErrorViewResId(), getConfig().getRetryId(), getConfig().getErrorRetryText());
            text(getConfig().getNoNetworkViewResId(), getConfig().getRetryId(), getConfig().getNoNetWorkRetryText());
        }
        return this;
    }

    public View getEmptyView() {
        if (isStaticAdd) {
            return layout(emptyViewResId);
        } else {
            return emptyView;
        }
    }

    public void setEmptyView(int viewId) {
        updateChindView(emptyViewResId, viewId);
    }

    public void setEmptyView(View emptyView) {
        if (!isStaticAdd) {
            if (this.emptyView != null) {
                removeView(this.emptyView);
            }
            this.emptyView = emptyView;
            addView(this.emptyView);
            this.emptyView.setVisibility(GONE);
        }

    }

    public View getErrorView() {
        if (isStaticAdd) {
            return layout(errorViewResId);
        } else {
            return errorView;
        }
    }


    private void updateChindView(int oldViewId, int newViewId) {
        if (isStaticAdd) {
            checkConfig();
            if (mResId.containsKey(oldViewId)) {
                removeView(mResId.get(oldViewId));
                mResId.remove(oldViewId);
            }
            if (oldViewId == errorViewResId) {
                errorViewResId = newViewId;
                getConfig().setErrorViewResId(errorViewResId);
            } else if (oldViewId == emptyViewResId) {
                emptyViewResId = newViewId;
                getConfig().setEmptyViewResId(emptyViewResId);
            } else if (oldViewId == noNetworkViewResId) {
                noNetworkViewResId = newViewId;
                getConfig().setNoNetworkViewResId(noNetworkViewResId);
            } else if (oldViewId == loadingViewResId) {
                loadingViewResId = newViewId;
                getConfig().setLoadingViewResId(loadingViewResId);
            }
        }
    }


    /**
     * warp及xml方式均可
     *
     * @param viewId
     */
    public void setErrorView(int viewId) {
        if (isStaticAdd) {
            updateChindView(errorViewResId, viewId);
        } else {
            setErrorView(inflate(getContext(), viewId, null));
        }
    }

    /**
     * 仅限warp方式使用
     *
     * @param mErrorView
     */
    public void setErrorView(View mErrorView) {
        if (!isStaticAdd) {
            if (mErrorView != null) {
                removeView(mErrorView);
            }
            this.errorView = mErrorView;
            addView(mErrorView);
            mErrorView.setVisibility(GONE);
            setChildViewClick(mErrorView, mErrorView.findViewById(R.id.tv_retry), onErrorClickListener);
        }
    }

    private void setChildViewClick(View parent, View ctrlId, OnClickListener onClickListener) {
        if (parent != null && mOnRetryClickListener != null && onClickListener != null) {
            if (ctrlId != null) {
                ctrlId.setOnClickListener(onClickListener);
            } else {
                parent.setOnClickListener(onClickListener);
            }
        }
    }

    public View getLoadingView() {
        if (isStaticAdd) {
            return layout(loadingViewResId);
        } else {
            return loadingView;
        }
    }

    public void setLoadingView(int viewId) {
        updateChindView(loadingViewResId, viewId);
    }

    public void setLoadingView(View loadingView) {
        if (!isStaticAdd) {
            if (this.loadingView != null) {
                removeView(this.loadingView);
            }
            this.loadingView = loadingView;
            addView(errorView);
            this.loadingView.setVisibility(GONE);
        }
    }

    public View getNoNetworkView() {
        if (isStaticAdd) {
            return layout(noNetworkViewResId);
        } else {
            return noNetworkView;
        }
    }

    public void setNoNetworkView(int viewId) {
        updateChindView(noNetworkViewResId, viewId);
    }

    public void setNoNetworkView(View noNetworkView) {
        if (!isStaticAdd) {
            if (this.noNetworkView != null) {
                removeView(this.noNetworkView);
            }
            this.noNetworkView = noNetworkView;
            addView(this.noNetworkView);
            this.noNetworkView.setVisibility(GONE);
        }
    }


    public StateView setNoNetWorkText(String text) {
        checkConfig();
        getConfig().setNoNetWorkText(text);
        if (isStaticAdd) {
            text(getConfig().getNoNetworkViewResId(), getConfig().getTipsId(), getConfig().getNoNetWorkText());
        }
        return this;
    }


//    public StatusView setButtonTextColor(@ColorInt int color) {
//        return this;
//    }
//
//    public StatusView setButtonTextSize(@ColorInt int dp) {
//        return this;
//    }
//
//    public StatusView setButtonBackground(Drawable drawable) {
//        return this;
//    }

}
