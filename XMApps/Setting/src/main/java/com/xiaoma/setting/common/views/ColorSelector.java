package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiaoma.setting.R;

/**
 * @author: iSun
 * @date: 2018/11/21 0021
 */
public class ColorSelector extends RadioGroup {
    String buttonNames = getContext().getString(R.string.color_select_opt);
    String[] btnNamesArray = buttonNames.split("\\|");
    private float mTextSize = 25;

    public ColorSelector(Context context) {
        super(context);
        init(context);
    }

    public ColorSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        removeAllViews();
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        for (int i = 0; i < btnNamesArray.length; i++) {
            String btnName = btnNamesArray[i];
            RadioButton btn = new RadioButton(context);
            btn.setId(i);
            btn.setText(btnName);
            btn.setGravity(Gravity.CENTER);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            ColorStateList color = getResources().getColorStateList(R.color.color_selector_text_color);
            btn.setTextColor(color);
            btn.setBackground(new ColorDrawable(Color.TRANSPARENT));
            btn.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn.setPadding(0, 0, 0, 0);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(getResources().getDimensionPixelSize(R.dimen.color_select_width), ViewGroup.LayoutParams.WRAP_CONTENT);
//            int marginRight = (i == btnNamesArray.length - 1) ? 0 : mButtonMarginRight;
//            params.setMargins(0, mButtonMarginTop, marginRight, 0);
            addView(btn, params);
        }

        check(getChildAt(0).getId());
    }
}
