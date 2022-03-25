package com.xiaoma.setting.common.views;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiaoma.setting.R;
import com.xiaoma.skin.views.XmSkinRadioButton;
import com.xiaoma.skin.views.XmSkinRadioGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by qiuboxiang on 2018/11/1 15:05
 * description:
 */
public class RadioButtonLayout extends XmSkinRadioGroup {

    private int mButtonMarginRight;
    private int mButtonMarginTop;
    private float mTextSize;
    private int mButtonPaddingBottom;
    //    private int mButtonWidth;
//    private int mButtonHeight;
    private String buttonNames;
    private boolean clickable = true;
    private List<Integer> mIDList = new ArrayList<>();
    private CustomOnCheckedChangeListener mListener;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            RadioButtonLayout.this.setClickable(true);
        }
    };
    OnCheckedChangeListener listener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (mListener != null) {
                int index = -1;
                for (int i = 0; i < mIDList.size(); i++) {
                    if (mIDList.get(i) == checkedId) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    mListener.onCheckedChanged(group, index);
                }
                if (isDelayUIChange && lastCheckedId != -1) {
                    RadioButtonLayout.this.setOnCheckedChangeListener(null);
                    RadioButtonLayout.this.check(lastCheckedId);
                    RadioButtonLayout.this.setOnCheckedChangeListener(listener);
                    RadioButtonLayout.this.setClickable(false);
                    handler.postDelayed(runnable, 5000);
                }
            }
        }
    };
    private boolean isShow = true;
    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (clickable) {
                        return false;
                    } else {
                        if (isShow) {
                            SettingToast.build(getContext()).setMessage(getContext().getString(R.string.security_tips)).setImage(R.drawable.icon_error).show(2000);
                        }
                        return true;
                    }
                default:
                    return false;
            }
        }
    };
    private boolean isDelayUIChange = false;
    private int lastCheckedId = -1;

    public RadioButtonLayout(Context context) {
        this(context, null);
    }

    public RadioButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyConfig(context, attrs);
    }

    private void applyConfig(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RadioButtonLayout);
        buttonNames = ta.getString(R.styleable.RadioButtonLayout_buttonNames);
        mButtonMarginRight = ta.getDimensionPixelSize(R.styleable.RadioButtonLayout_buttonMarginRight, resources.getDimensionPixelSize(R.dimen.radio_button_margin_right));
        mButtonPaddingBottom = ta.getDimensionPixelSize(R.styleable.RadioButtonLayout_buttonPaddingBottom, resources.getDimensionPixelSize(R.dimen.radio_button_padding_bottom));
        mTextSize = ta.getDimensionPixelSize(R.styleable.RadioButtonLayout_textSize, resources.getDimensionPixelSize(R.dimen.radio_button_text_size));
//        mButtonWidth = ta.getDimensionPixelSize(R.styleable.RadioButtonLayout_buttonWidth, resources.getDimensionPixelSize(R.dimen.radio_button_width));
//        mButtonHeight = ta.getDimensionPixelSize(R.styleable.RadioButtonLayout_buttonHeight, resources.getDimensionPixelSize(R.dimen.radio_button_height));
        ta.recycle();
        mButtonMarginTop = resources.getDimensionPixelSize(R.dimen.radio_button_margin_top);
        if (!TextUtils.isEmpty(buttonNames)) {
            init(context);
        }
    }

    public void setButtonNames(String names) {
        this.buttonNames = names;
        init(getContext());
    }

    /**
     * @param isDelayUIChange 设置是否等回调后才更新UI,默认false
     */
    public void setDelayUIChange(boolean isDelayUIChange) {
        this.isDelayUIChange = isDelayUIChange;
        isShow = !isDelayUIChange;
    }

    @Override
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public void isToastWhenUnclickable(boolean isShow) {
        this.isShow = isShow;
    }


    private void init(Context context) {
        removeAllViews();
        setOrientation(HORIZONTAL);

        if (TextUtils.isEmpty(buttonNames)) {
            throw new RuntimeException("Please set buttonNames");
        }
        String[] btnNamesArray = buttonNames.split("\\|");
        for (int i = 0; i < btnNamesArray.length; i++) {
            String btnName = btnNamesArray[i];
            XmSkinRadioButton btn = new XmSkinRadioButton(context);
            int id = generateViewId();
            mIDList.add(id);
            btn.setId(id);
            btn.setText(btnName);
            btn.setGravity(Gravity.CENTER);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            btn.setPadding(0, 0, 0, mButtonPaddingBottom);
            btn.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            btn.setTextAppearance(R.style.radiobutton_text_color);
            if (i == 0) {
                btn.setBackgroundResource(R.drawable.bg_radio_button_left);
            } else if (i == btnNamesArray.length - 1) {
                btn.setBackgroundResource(R.drawable.bg_radio_button_right);
            } else {
                btn.setBackgroundResource(R.drawable.bg_radio_button_middle);
            }
            btn.setOnTouchListener(onTouchListener);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int marginRight = (i == btnNamesArray.length - 1) ? 0 : mButtonMarginRight;
            params.setMargins(0, mButtonMarginTop, marginRight, 0);
            addView(btn, params);
        }

        setOnCheckedChangeListener(listener);
        checkIndex(0);
    }

    public void checkIndex(int index) {
        if (isDelayUIChange) {
            setOnCheckedChangeListener(null);
            if (index < mIDList.size() && index >= 0) {
                ((RadioButton) findViewById(mIDList.get(index))).setChecked(true);
                lastCheckedId = mIDList.get(index);
            }
            setOnCheckedChangeListener(listener);
            handler.removeCallbacks(runnable);
            setClickable(true);
        } else {
            if (index < mIDList.size() && index >= 0) {
                ((RadioButton) findViewById(mIDList.get(index))).setChecked(true);
                lastCheckedId = mIDList.get(index);
            }
        }
    }

    public int getCheckedIndex() {
        for (int i = 0; i < mIDList.size(); i++) {
            if (mIDList.get(i) == getCheckedRadioButtonId()) {
                return i;
            }
        }
        return -1;
    }

    public void setCustomOnCheckedChangeListener(CustomOnCheckedChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void applySkin() {
        super.applySkin();
        for (int i = 0; i < getChildCount(); i++) {
            XmSkinRadioButton childAt = (XmSkinRadioButton) getChildAt(i);
            childAt.setTextAppearance(R.style.radiobutton_text_color);
            if (i == 0) {
                childAt.setBackgroundResource(R.drawable.bg_radio_button_left);
            } else if (i == getChildCount() - 1) {
                childAt.setBackgroundResource(R.drawable.bg_radio_button_right);
            } else {
                childAt.setBackgroundResource(R.drawable.bg_radio_button_middle);
            }
        }
    }

    public interface CustomOnCheckedChangeListener {
        void onCheckedChanged(RadioGroup group, int index);
    }
}

