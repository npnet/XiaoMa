package com.xiaoma.skin.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewSwitcher;

import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatSupportable;

/**
 * author: andy、JF
 * date:   2019/5/29 11:25
 * desc:
 */
public class XmViewSwitcher extends ViewSwitcher implements SkinCompatSupportable {


    private SkinCompatBackgroundHelper mBackgroundTintHelper;

    public XmViewSwitcher(Context context) {
        this(context, null);
    }

    public XmViewSwitcher(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XmViewSwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
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
