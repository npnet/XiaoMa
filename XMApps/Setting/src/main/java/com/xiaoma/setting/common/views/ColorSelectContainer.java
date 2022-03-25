package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiaoma.setting.R;

public class ColorSelectContainer extends LinearLayout {
    private String buttonNames;
    private String type;

    public ColorSelectContainer(Context context) {
        this(context,null);
    }

    public ColorSelectContainer(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorSelectContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttribute(context, attrs);
        initView(context);
    }

    private void getAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AtmosphereSelcetor, 0, 0);
        buttonNames = typedArray.getString(R.styleable.AtmosphereSelcetor_colorStr);
        type = typedArray.getString(R.styleable.AtmosphereSelcetor_selectorType);
        typedArray.recycle();
    }


    private void initView(Context context) {
        String[] btnNamesArray = buttonNames.split("\\|");
        removeAllViews();
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        int num = "1".equals(type) ? 0 : 6;
        for (int i = 0; i < btnNamesArray.length; i++) {
            String btnName = btnNamesArray[i];
            RadioButton btn = new RadioButton(context);
            btn.setId(i+num);
            btn.setText(btnName);
            btn.setGravity(Gravity.CENTER);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28);
            ColorStateList color = getResources().getColorStateList(R.color.color_selector_text_color);
            btn.setTextColor(color);
            btn.setBackground(new ColorDrawable(Color.TRANSPARENT));
            btn.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn.setPadding(0, 0, 0, 0);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(getResources().getDimensionPixelSize(R.dimen.color_select_width_new), ViewGroup.LayoutParams.WRAP_CONTENT);
            addView(btn, params);
        }
    }

}
