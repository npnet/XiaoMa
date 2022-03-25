package com.xiaoma.setting.common.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoma.setting.R;

/**
 * @Author ZiXu Huang
 * @Data 2018/11/23
 */
public class BltPairConfirmDialog extends Dialog implements View.OnClickListener {

    private TextView title;
    private TextView msg;
    private TextView confirm;
    private TextView cancel;
    private OnConfirmClickedListener listener;

    public BltPairConfirmDialog(@NonNull Context context) {
        super(context, R.style.CommonSettingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_blt_pair_confirm);
        initView();
    }

    private void initView() {
        title = findViewById(R.id.title);
        msg = findViewById(R.id.msg);
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

    }

    @Override
    public void show() {
        setType();
        super.show();
    }

    private void setType() {
        if (Build.VERSION.SDK_INT>=26) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else{
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    public void setTitle(@StringRes int titleRes) {
        title.setText(titleRes);
    }

    public void setMsg(@StringRes int msgRes) {
        msg.setText(msgRes);
    }

    public void setMsg( String msgRes) {
        msg.setText(msgRes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
                break;
            case R.id.confirm:
//                dismiss();
                if (listener != null) {
                    listener.onConfirmClicked();
                }
                break;
        }
    }

    public void setOnConfirmClickedListener(OnConfirmClickedListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmClickedListener {
        void onConfirmClicked();

        void onCancel();
    }
}
