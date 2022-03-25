package com.xiaoma.setting.common.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/13
 */
public class EditDialog extends Dialog implements View.OnClickListener {

    private EditText bltNameEt;
    private TextView confirm;
    private TextView cancel;
    private OnConfirmClickedListener listener;
    private TextView titleTv;
    private int title;

    public EditDialog(Context context) {
        super(context, R.style.CommonSettingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_edit_blt_name);
        initView();
        initEvent();
    }

    private void initEvent() {
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void initView() {
        bltNameEt = findViewById(R.id.blt_name_et);
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        titleTv = findViewById(R.id.title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.confirm:
                confirm();
                break;
        }
    }

    public void setTitle(@StringRes int title) {
        titleTv.setText(title);
        this.title = title;
    }

    public void setEditHit(@StringRes int hint) {
        bltNameEt.setHint(hint);
    }

    public void setEditText(String text){
        bltNameEt.setText(text);
    }

    private void confirm() {
        String name = bltNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            if (title != 0) {
                Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.please_input_blt_name, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (listener != null) {
            listener.onConfirmClicked(name);
        }
    }

    public void setOnConfirmClickedListener(OnConfirmClickedListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmClickedListener {
        void onConfirmClicked(String inputText);
    }
}
