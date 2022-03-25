package com.xiaoma.login.business.ui.verify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.R;
import com.xiaoma.login.business.ui.verify.view.NumberKeyboard;
import com.xiaoma.ui.toast.XMToast;

/**
 * @author KY
 * @date 11/21/2018
 */
public class ForgetActivity extends BaseActivity implements NumberKeyboard.onKeyEventListener, View.OnClickListener {

    private static final int PASSWD_COUNT = 6;
    private NumberKeyboard numberKeyboard;
    private EditText etVerifyCode;
    private EditText etPasswd;
    private Button btnSubmit;
    private TextView tvGetVerifyCode;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ForgetActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        initView();
    }

    private void initView() {
        numberKeyboard = findViewById(R.id.number_keyboard);
        etVerifyCode = findViewById(R.id.et_verify_code);
        tvGetVerifyCode = findViewById(R.id.tv_get_verify_code);
        etPasswd = findViewById(R.id.et_passwd);
        btnSubmit = findViewById(R.id.btnSubmit);

        numberKeyboard.setOnKeyEventListener(this);
        tvGetVerifyCode.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onKey(int number) {
        EditText focusedText = getFocusedEditTextView();

        int length = focusedText.getText().length();
        if (length < PASSWD_COUNT) {
            //获取Edittext光标所在位置
            int index = focusedText.getSelectionStart();
            focusedText.getText().insert(index, String.valueOf(number));
        } else {
            XMToast.showToast(this, "密码最多6位数字");
        }

    }

    @Override
    public void onBackspace() {
        EditText focusedText = getFocusedEditTextView();
        int length = focusedText.getText().length();
        if (length > 0) {
            int index = focusedText.getSelectionStart();
            if (index != 0) {
                focusedText.getText().delete(index - 1, index);
            }
        }
    }

    private EditText getFocusedEditTextView() {
        View currentFocus = getCurrentFocus();
        if (currentFocus instanceof EditText) {
            return (EditText) currentFocus;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSubmit) {
            // TODO:提交密码修改
        } else if (id == R.id.tv_get_verify_code) {
            // TODO：获取验证码
        }
    }
}
