package com.xiaoma.login.business.ui.verify.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoma.login.R;

public class FaceScanView extends View {

    private AnimationDrawable mAnimDrawable;
    private Drawable mInitDrawable;

    public FaceScanView(Context context) {
        this(context, null);
    }

    public FaceScanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    private void initAnim() {
        mAnimDrawable = (AnimationDrawable) getContext().getDrawable(R.drawable.face_scan_anim);
        mInitDrawable = getContext().getDrawable(R.drawable.face_recognize_00000);
        mAnimDrawable.setOneShot(false);
        setBackground(mInitDrawable);
    }

    public void startAnim() {
        setBackground(mAnimDrawable);
        mAnimDrawable.start();
    }

    public void cancelAnim() {
        setBackground(mInitDrawable);
        mAnimDrawable.stop();
    }
}
