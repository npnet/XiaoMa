package com.xiaoma.bluetooth.phone.common.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.TextView;

import com.xiaoma.bluetooth.phone.R;


/**
 * @Author ZiXu Huang
 * @Data 2018/10/26
 */
public class ProgressDialog extends Dialog {

    private TextView message;

    public ProgressDialog(@NonNull Context context) {
        super(context, R.style.CommonSettingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
//        setCancelable(false);
        initView();
    }

    private void initView() {
        message = findViewById(R.id.message);
    }

    public void setMessage(@StringRes int res) {
        message.setText(getContext().getString(res));
    }

    public void setMessage(String msg) {
        message.setText(msg);
    }
}
