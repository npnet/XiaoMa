package com.xiaoma.club.personal.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubBaseResult;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.msg.redpacket.ui.SimpleTextWatcher;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;

/**
 * Author: loren
 * Date: 2018/10/11 0011
 */

public class PersonalEditSignActivity extends BaseActivity {

    public static final int MAX_TEXT_LENGTH = 30;
    public static final String EXTRA_SIGN = "sign";
    private EditText editSign;
    private TextView maxText;
    private View btnSave;

    public static void start(Context context, String sign) {
        Intent intent = new Intent(context, PersonalEditSignActivity.class);
        intent.putExtra(EXTRA_SIGN, sign);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_personal_sign_edit);
        initView();
    }

    private void initView() {
        TextView title = findViewById(R.id.sign_title);
        title.setText(getString(R.string.personal_sign_title));
        editSign = findViewById(R.id.edit_user_sign);
        maxText = findViewById(R.id.user_sign_max);
        editSign.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = v.getText().toString().trim();
                    commitUserSign(text);
                    return true;
                }
                return false;
            }
        });
        editSign.addTextChangedListener(new SimpleTextWatcher() {
            int start;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                LogUtil.logI("LKF", "onTextChanged -> s:%s, start: %s, before: %s, count: %s",
                        s, start, before, count);
                this.start = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_TEXT_LENGTH) {
                    s.delete(start, start + (s.length() - MAX_TEXT_LENGTH));
                }
                if (s.length() < MAX_TEXT_LENGTH) {
                    maxText.setTextColor(getColor(R.color.club_white));
                } else {
                    maxText.setTextColor(getColor(R.color.text_sign_number));
                }
                maxText.setText(s.length() + "/" + MAX_TEXT_LENGTH);
            }
        });

        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitUserSign(editSign.getText().toString().trim());
            }
        });

        if (getIntent() != null) {
            String oldSign = getIntent().getStringExtra(EXTRA_SIGN);
            if (!TextUtils.isEmpty(oldSign)) {
                editSign.setText(oldSign);
                editSign.requestFocus();
            }
        }
    }

    private void commitUserSign(final String text) {
        if (!ClubNetWorkUtils.isConnected(this)) {
            return;
        }
        showProgressDialog(R.string.is_commit_user_sign);
        ClubRequestManager.editUserSign(text, new CallbackWrapper<ClubBaseResult>() {

            @Override
            public ClubBaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, ClubBaseResult.class);
            }

            @Override
            public void onSuccess(ClubBaseResult model) {
                super.onSuccess(model);
                dismissProgress();
                if (model != null && model.isSuccess()) {
                    showToast(R.string.commit_user_sign_succss);
                    User user = UserUtil.getCurrentUser();
                    user.setPersonalSignature(text);
                    UserManager.getInstance().notifyUserUpdate(user);
                    finish();
                } else {
                    XMToast.toastException(PersonalEditSignActivity.this, R.string.commit_user_sign_failed);
                }
            }


            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                dismissProgress();
                XMToast.toastException(PersonalEditSignActivity.this, R.string.commit_user_sign_failed);
            }
        });
    }
}
