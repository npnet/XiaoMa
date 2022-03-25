package com.xiaoma.setting.common.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.setting.R;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/17
 */
public class WrongPswDialog extends Dialog implements View.OnClickListener {

    private String name;
    private TextView msg;
    private View close;

    public WrongPswDialog(@NonNull Context context, String name) {
        super(context);
        this.name = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wrong_psw);
        initView();
        initEvent();
    }

    private void initEvent() {
        close.setOnClickListener(this);
    }

    private void initView() {
        msg = findViewById(R.id.msg);
        close = findViewById(R.id.close);
        if (!TextUtils.isEmpty(name)) {
            msg.setText(String.format(getContext().getResources().getString(R.string.name_wrong_psw), name));
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
