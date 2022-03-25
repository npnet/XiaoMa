package com.xiaoma.skin.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import skin.support.widget.SkinCompatSpinner;

/**
 * Created by Thomas on 2018/12/19 0019
 */

public class XmSkinSpinner extends SkinCompatSpinner {
    public XmSkinSpinner(Context context) {
        super(context);
    }

    public XmSkinSpinner(Context context, int mode) {
        super(context, mode);
    }

    public XmSkinSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XmSkinSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XmSkinSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public XmSkinSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }
}
