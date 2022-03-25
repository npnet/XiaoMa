package com.xiaoma.dualscreen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author: iSun
 * @date: 2019/3/7 0007
 */
public abstract class BaseView extends FrameLayout {
    private int redId;

    public BaseView(Context context) {
        super(context);
        initView();
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public BaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public abstract void onRefresh();

    public void attach(FrameLayout frameLayout) {
        if (frameLayout != null) {
            frameLayout.addView(this);
        }
    }

    public abstract int contentViewId();

    public void onViewCreated(){

    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestory() {

    }


    public void initView() {
        redId = contentViewId();
        if (redId != 0) {
            inflate(getContext(), redId, this);
        }

        onViewCreated();
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }
}
