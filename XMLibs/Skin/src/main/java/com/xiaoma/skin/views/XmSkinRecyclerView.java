package com.xiaoma.skin.views;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatSupportable;

/**
 * author: andy、JF
 * date:   2019/6/05 17:36
 * desc:
 */
public class XmSkinRecyclerView extends RecyclerView implements SkinCompatSupportable {
    private SkinCompatBackgroundHelper mBackgroundTintHelper;

    public XmSkinRecyclerView(Context context) {
        this(context, null);
    }

    public XmSkinRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XmSkinRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr);

    }

    @Override
    public void setBackgroundResource(int resId) {
        super.setBackgroundResource(resId);
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.onSetBackgroundResource(resId);
        }
    }

    @Override
    public void applySkin() {
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySkin();
        }
    }
}
