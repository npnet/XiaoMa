package com.xiaoma.ui.dialog.impl;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ui.R;
import com.xiaoma.ui.constract.ToastLevel;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/21
 */
public class XMSmallTextDialog extends XMDialogImpl {
    private ImageView mStateIV;
    private TextView mMsgTV;

    @StringRes
    private int mMsgStrRes;
    @ToastLevel
    private int mLevel;
    private String mMsgStr;

    private Animation mAnimation;

    public static XMSmallTextDialog newInstance() {
        return new XMSmallTextDialog();
    }

    @Override
    protected String getDialogTag() {
        return XMSmallTextDialog.class.getSimpleName();
    }

    @Override
    public int getDialogHeight() {
        return getPxDimension(R.dimen.height_dialog_small);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_fragment_small;
    }

    @Override
    public void bindView(View view) {
        mMsgTV = view.findViewById(R.id.tvMsg);
        mStateIV = view.findViewById(R.id.ivState);

        if (isValidResId(mMsgStrRes)) {
            setText(mMsgTV, mMsgStrRes);
        } else {
            setText(mMsgTV, mMsgStr);
        }
        setLevel(mStateIV, mLevel);
    }

    public XMSmallTextDialog setMsg(@StringRes int strRes) {
        mMsgStr = null;
        mMsgStrRes = strRes;
        setText(mMsgTV, strRes);
        return this;
    }

    public XMSmallTextDialog setMsg(String msg) {
        mMsgStrRes = 0;
        mMsgStr = msg;
        setText(mMsgTV, msg);
        return this;
    }

    public void updateMsgAndState(@ToastLevel int level, @StringRes int strRes) {
        this.mLevel = level;
        setLevel(mStateIV, level);
        setMsg(strRes);
    }
    public void updateMsgAndState(@ToastLevel int level, String msg) {
        this.mLevel = level;
        setLevel(mStateIV, level);
        setMsg(msg);
    }

    private void setLevel(ImageView view, @ToastLevel int level) {
        if (view != null) {
            if (level == ToastLevel.LOADING) {
                if (getRotateAnimation(getContext()).hasEnded()) {
                    getRotateAnimation(getContext()).reset();
                }
                view.startAnimation(getRotateAnimation(getContext()));
            } else {
                getRotateAnimation(getContext()).reset();
                getRotateAnimation(getContext()).cancel();
            }
            view.setImageLevel(level);
        }
    }

    private Animation getRotateAnimation(Context context) {
        if (mAnimation == null) {
            mAnimation = AnimationUtils.loadAnimation(context, com.xiaoma.ui.R.anim.rotate);
            mAnimation.setRepeatCount(Animation.INFINITE);
            mAnimation.setRepeatCount(Animation.REVERSE);
        }
        return mAnimation;
    }
}
