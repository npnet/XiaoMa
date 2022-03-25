package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaoma.setting.R;

public class SettingSwitch extends LinearLayout {

    public static final String TAG = "SettingSwitch";
    private StateListener listener;
    private TextView tvTitle;
    private CheckBox checkBox;
    private int titleColor;
    private CharSequence titleText = "";
    private int titleSize;
    private int titleWidth;
    private int paddingLeft;
    private volatile boolean hasCallBack = true;

    public SettingSwitch(Context context) {
        this(context, null);
    }

    public SettingSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context,attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        View view = inflate(getContext(), R.layout.view_switch, this);
        checkBox = view.findViewById(R.id.radioButton);
        tvTitle = view.findViewById(R.id.tv_name);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingSwitch);
            titleColor = ta.getColor(R.styleable.SettingSwitch_titleColor, context.getResources().getColor(R.color.default_font));
            titleText = ta.getText(R.styleable.SettingSwitch_titleTexts);
            titleSize = ta.getDimensionPixelSize(R.styleable.SettingSwitch_titleSize, 30);
            titleWidth = ta.getDimensionPixelSize(R.styleable.SettingSwitch_titleWidth, 360);
            paddingLeft = ta.getDimensionPixelSize(R.styleable.SettingSwitch_marginLeft, 105);
            ta.recycle();
        }

        initView();
    }

    public void initView() {
        tvTitle.setText(titleText);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        tvTitle.setTextColor(titleColor);

        LinearLayout.LayoutParams layoutParams = (LayoutParams) tvTitle.getLayoutParams();
        layoutParams.width=titleWidth;
        tvTitle.setLayoutParams(layoutParams);
        tvTitle.setPadding(paddingLeft,0,0,0);

        initControl();
    }

    private void initControl() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean checkedId) {
                if (listener != null && hasCallBack) {
                    listener.onCheck(getId(), checkedId);
                }
            }
        });
    }

    public void setChecked(boolean isChecked) {
        hasCallBack = true;
        if (checkBox != null) {
            checkBox.setChecked(isChecked);
        }
    }

    //此check没有回调
    public void setCheckNoCallBack(boolean isChecked) {
        hasCallBack = false;
        if (checkBox != null) {
            checkBox.setChecked(isChecked);
        }
    }

    private int getHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view.getMeasuredHeight();
    }

    public void setListener(StateListener listener) {
        this.listener = listener;
    }

    public interface StateListener {
        void onCheck(int viewId, boolean isChecked);
    }
}
