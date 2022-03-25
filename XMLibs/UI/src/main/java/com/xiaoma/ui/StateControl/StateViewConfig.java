package com.xiaoma.ui.StateControl;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;


import com.xiaoma.ui.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class StateViewConfig {
    private List<IStateViewDataChange> dataChangeListeners = new ArrayList<>();

    private int emptyViewResId = R.layout.state_empty_view;
    private int errorViewResId = R.layout.state_error_view;
    private int loadingViewResId = R.layout.state_loading_view;
    private int noNetworkViewResId = R.layout.state_no_network_view;
    private int errorImage = R.drawable.state_error;
    private int emptyImage = R.drawable.state_empty;
    private int noNewWorkImage = R.drawable.state_no_network;

    private int retryId = R.id.tv_retry;
    private int tipsId = R.id.tv_tips;

    private String errorRetryText;
    private String noNetWorkRetryText;
    private String emptyRetryText;

    private String noNetWorkText;
    private String emptyText;
    private String errorText;

    private int textColor;
    private float textSize;
    private int reTryTextColor;
    private float reTryTextSize;
    public boolean defLoading = false;

    StateViewConfig() {

    }

    public void init(Context context) {
        errorRetryText = context.getString(R.string.state_view_retry_text);
        noNetWorkRetryText = context.getString(R.string.state_view_retry_text);
        emptyRetryText = context.getString(R.string.state_view_retry_text);

        noNetWorkText = context.getString(R.string.state_view_no_network_text);
        emptyText = context.getString(R.string.state_view_empty_text);
        errorText = context.getString(R.string.state_view_loading_error_text);

        textSize = context.getResources().getDimension(R.dimen.font_state_view_text_size);
        textColor = context.getResources().getColor(R.color.state_view_text_color);

        reTryTextSize = context.getResources().getDimension(R.dimen.font_state_view_retry_text_size);
        reTryTextColor = context.getResources().getColor(R.color.state_view_retry_text_color);

    }

    public int getEmptyViewResId() {
        return emptyViewResId;
    }

    public StateViewConfig setEmptyViewResId(@LayoutRes int emptyViewResId) {
        this.emptyViewResId = emptyViewResId;
        return this;
    }

    public int getErrorViewResId() {
        return errorViewResId;
    }

    public StateViewConfig setErrorViewResId(@LayoutRes int errorViewResId) {
        this.errorViewResId = errorViewResId;
        return this;
    }

    public int getLoadingViewResId() {
        return loadingViewResId;
    }

    public StateViewConfig setLoadingViewResId(@LayoutRes int loadingViewResId) {
        this.loadingViewResId = loadingViewResId;
        return this;
    }

    public int getNoNetworkViewResId() {
        return noNetworkViewResId;
    }

    public StateViewConfig setNoNetworkViewResId(@LayoutRes int noNetworkViewResId) {
        this.noNetworkViewResId = noNetworkViewResId;
        return this;
    }

    public StateViewConfig setEmptyImage(@DrawableRes int resId) {
        emptyImage = resId;
        notifyChange();
        return this;
    }

    public StateViewConfig setErrorText(String value) {
        errorText = value;
        notifyChange();
        return this;
    }

    public StateViewConfig setErrorRetryText(String text) {
        errorRetryText = text;
        notifyChange();
        return this;
    }

    public StateViewConfig setTextColor(@ColorInt int color) {
        textColor = color;
        notifyChange();
        return this;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
        notifyChange();
    }


    public void addDataChangeListener(IStateViewDataChange istateChange) {
        if (istateChange != null) {
            dataChangeListeners.add(istateChange);
        }
    }

    public void removestateChange(IStateViewDataChange istateChange) {
        if (istateChange != null) {
            dataChangeListeners.remove(istateChange);
        }
    }

    public void notifyChange() {
        Iterator<IStateViewDataChange> iterator = dataChangeListeners.iterator();
        while (iterator.hasNext()) {
            IStateViewDataChange next = iterator.next();
            next.dataChange();
        }
    }

    public StateViewConfig setNoNetWorkRetryText(String text) {
        noNetWorkRetryText = text;
        notifyChange();
        return this;
    }

    public StateViewConfig setEmptyRetryText(String text) {
        emptyRetryText = text;
        notifyChange();
        return this;
    }

    public StateViewConfig setNoNetWorkText(String text) {
        noNetWorkText = text;
        notifyChange();
        return this;
    }

    public int getErrorImage() {
        return errorImage;
    }

    public StateViewConfig setErrorImage(int mErrorImage) {
        this.errorImage = mErrorImage;
        notifyChange();
        return this;
    }

    public int getEmptyImage() {
        return emptyImage;
    }


    public int getNoNewWorkImage() {
        return noNewWorkImage;
    }

    public StateViewConfig setNoNewWorkImage(int mNoNewWorkImage) {
        this.noNewWorkImage = mNoNewWorkImage;
        notifyChange();
        return this;
    }

    public int getTextColor() {
        return textColor;
    }


    public String getErrorRetryText() {
        return errorRetryText;
    }


    public String getNoNetWorkRetryText() {
        return noNetWorkRetryText;
    }


    public String getEmptyRetryText() {
        return emptyRetryText;
    }

    public String getNoNetWorkText() {
        return noNetWorkText;
    }

    public String getEmptyText() {
        return emptyText;
    }


    public String getErrorText() {
        return errorText;
    }


    public StateViewConfig setTextSize(float px) {
        textSize = px;
        notifyChange();
        return this;
    }

    public float getTextSize() {
        return textSize;
    }

    public int getRetryId() {
        return retryId;
    }

    public void setRetryId(int retryId) {
        this.retryId = retryId;
    }

    public int getTipsId() {
        return tipsId;
    }

    public void setTipsId(int tipsId) {
        this.tipsId = tipsId;
    }

    public StateViewConfig setReTryTextColor(int color) {
        reTryTextColor = color;
        notifyChange();
        return this;
    }

    public StateViewConfig setReTryTextSize(float px) {
        reTryTextSize = px;
        notifyChange();
        return this;
    }

    public int getReTryTextColor() {
        return reTryTextColor;
    }

    public float getReTryTextSize() {
        return reTryTextSize;
    }

//    public LoadingLayout setButtonTextColor(@ColorInt int color) {
//        mButtonTextColor = color;
//        return this;
//    }
//    public LoadingLayout setButtonTextSize(@ColorInt int dp) {
//        mButtonTextColor = dp2px(dp);
//        return this;
//    }
//    public LoadingLayout setButtonBackground(Drawable drawable) {
//        mButtonBackground = drawable;
//        return this;
//    }
}
