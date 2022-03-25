package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.setting.R;

public class SettingCheckView extends RelativeLayout {
    private boolean isShowLine = false;
    private TextView tvSwitch;
    private CheckBox checkBox;
    private CheckChangeListener listener;
    private View line;
    private String text;

    public SettingCheckView(Context context) {
        super(context);
        initView(context, null);
    }

    public SettingCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SettingCheckView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public SettingCheckView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingCheckView);
            text = ta.getString(R.styleable.SettingCheckView_desc);
            isShowLine = ta.getBoolean(R.styleable.SettingCheckView_line, false);
        }
        inflate(getContext(), R.layout.view_setting_check, this);
        tvSwitch = (TextView) findViewById(R.id.tv_title);
        checkBox = (CheckBox) findViewById(R.id.cb_check);
        line = findViewById(R.id.line);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onCheckChanged(getId(),isChecked);
                }
            }
        });
        if (isShowLine && line != null) {
            line.setVisibility(VISIBLE);
        }
        if (tvSwitch != null && text != null) {
            tvSwitch.setText(text);
        }
    }


    public void setTitle(CharSequence title) {
        if (title != null) {
            tvSwitch.setText(title);
        }
    }

    public void setCheckEnabled(boolean enabled) {
        checkBox.setEnabled(enabled);
    }

    public void setCheckState(boolean flag) {
        checkBox.setChecked(flag);
    }

    public boolean getCheckState() {
        return checkBox.isChecked();
    }

    public void setListener(SettingCheckView.CheckChangeListener listener) {
        this.listener = listener;
    }

    public SettingCheckView.CheckChangeListener getListener() {
        return listener;
    }


    public interface CheckChangeListener {
        void onCheckChanged(int viewId, boolean isCheck);
    }
}
