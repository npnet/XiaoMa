package com.xiaoma.skin.views;

import android.content.Context;
import android.util.AttributeSet;
import skin.support.widget.SkinCompatViewGroup;

/**
 * Created by Thomas on 2018/12/19 0019
 */

public abstract class XmSkinViewGroup extends SkinCompatViewGroup {

    public XmSkinViewGroup(Context context) {
        super(context);
    }

    public XmSkinViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XmSkinViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
