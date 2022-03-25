package com.xiaoma.bluetooth.phone.contacts.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoma.bluetooth.phone.R;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/21
 */
public class BluetoothTabView extends FrameLayout {

    private String tableName;
    private TextView tab;
    private Context context;

    public BluetoothTabView(@NonNull Context context,String tableName) {
        super(context);
        initView(context, tableName);
    }

    public BluetoothTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, null);
    }

    public BluetoothTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, null);
    }

    public BluetoothTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, null);
    }

    private void initView(Context context, String tabName) {
        this.context = context;
        View inflate = LayoutInflater.from(context).inflate(R.layout.bluetooth_phone_tab_layout, this);
        tab = inflate.findViewById(R.id.tv_tab);
        this.tableName = tabName;
        setText();

    }

    private void setText() {
        if (!TextUtils.isEmpty(tableName)) {
            tab.setText(tableName);
        }
    }

    public void setSelect(boolean isSelected) {
        if (isSelected) {
            tab.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            tab.setTextColor(context.getResources().getColor(R.color.bluetooth_phone_unselected_tab_color));
        }
    }
}
