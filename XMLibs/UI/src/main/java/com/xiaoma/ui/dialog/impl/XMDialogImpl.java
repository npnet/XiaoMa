package com.xiaoma.ui.dialog.impl;

import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ui.R;
import com.xiaoma.ui.dialog.AbsDialogFragment;
import com.xiaoma.ui.dialog.XmDialog;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/21
 */
public abstract class XMDialogImpl extends AbsDialogFragment {

    @Override
    protected View getDialogView() {
        return null;
    }

    @Override
    public XmDialog show() {
        return null;
    }

    @Override
    public int getDialogWidth() {
        return getPxDimension(getDialogWidthDimenRes());
    }

    public void showDialog(FragmentManager fragmentManager) {
        if (!isVisible()) {
            show(fragmentManager, getDialogTag());
        }
    }


    protected boolean isValidResId(int strRes) {
        return strRes != 0;
    }

    protected void setText(TextView view, @StringRes int strRes) {
        if (view != null && isValidResId(strRes)) {
            view.setText(strRes);
        }
    }

    protected void setText(TextView view, String str) {
        if (view != null && !TextUtils.isEmpty(str)) {
            view.setText(str);
        }
    }

    protected void setImage(ImageView view, @DrawableRes int drawId) {
        if (view != null && isValidResId(drawId)) {
            view.setImageResource(drawId);
        }
    }

    protected int getDialogWidthDimenRes() {
        return R.dimen.width_dialog;
    }

    protected abstract String getDialogTag();

    protected int getPxDimension(@DimenRes int dimenId) {
        return getContext().getResources().getDimensionPixelOffset(dimenId);
    }
}
