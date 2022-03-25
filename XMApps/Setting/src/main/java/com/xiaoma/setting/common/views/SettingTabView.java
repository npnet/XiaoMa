package com.xiaoma.setting.common.views;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.xiaoma.setting.R;

public class SettingTabView extends FrameLayout {
    private MagicTextView tab;
    private String tabName;

    public SettingTabView(Context context, String tabName) {
        super(context);
        initView(context, tabName);
    }

    public SettingTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, null);
    }

    public SettingTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, null);
    }

    public SettingTabView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, null);
    }

    public void initView(Context context, String tabName) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_setting_tab, this);
        tab = view.findViewById(R.id.tv_tab);
        this.tabName = tabName;
        setText();
        setTextColor(getResources().getColor(R.color.setting_tab_unselect));
    }

    public void setSelect(boolean isSelected) {
        if (tab == null) {
            return;
        }
        if (isSelected) {
            tab.setShine(true);
            setTextColor(getResources().getColor(R.color.setting_tab_select));
        } else {
            tab.setShine(false);
            setTextColor(getResources().getColor(R.color.setting_tab_unselect));
        }
    }

    public void setText(int resId) {
        setText(getContext().getResources().getString(resId));
    }

    public void setText(String text) {
        tabName = text;
        setText();
    }

    public void setTextColor(int color) {
        if (tab != null) {
            tab.setTextColor(color);
        }
    }

    private void setText() {
        if (tab != null && tabName != null) {
            tab.setText(tabName);
        }
        tab.invalidate();
    }
}
