package com.xiaoma.club.msg.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubBaseResult;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;

/**
 * Author: loren
 * Date: 2019/2/15 0015
 */

public class GroupEditSignActivity extends BaseActivity {

    public static final int MAX_TEXT_LENGTH = 30;
    private static final String EXTRA_QUN_ID = "extraQunId";
    public static final String EXTRA_SIGN = "sign";
    private EditText editSign;
    private TextView maxText;
    private long qunId;

    public static void start(Context context, long qunId, String oldSign) {
        context.startActivity(new Intent(context, GroupEditSignActivity.class)
                .putExtra(EXTRA_QUN_ID, qunId)
                .putExtra(EXTRA_SIGN, oldSign));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_personal_sign_edit);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent == null) {
            XMToast.toastException(this, getString(R.string.group_id_empty));
            finish();
            return;
        }
        qunId = intent.getLongExtra(EXTRA_QUN_ID, 0);
        if (qunId == 0) {
            XMToast.toastException(this, getString(R.string.group_id_empty));
            finish();
            return;
        }
        TextView title = findViewById(R.id.sign_title);
        title.setText(getString(R.string.group_sign_title));
        editSign = findViewById(R.id.edit_user_sign);
        maxText = findViewById(R.id.user_sign_max);
        String oldSign = getIntent().getStringExtra(EXTRA_SIGN);
        if (!TextUtils.isEmpty(oldSign)) {
            maxText.setText(oldSign.length() + "/" + MAX_TEXT_LENGTH);
            editSign.setText(oldSign);
            editSign.requestFocus();
        }
        editSign.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = v.getText().toString().trim();
                    if (text == null) {
                        text = "";
                    }
                    commitUserSign(text);
                }
                return false;
            }
        });
        editSign.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int dindex = 0;
                int count = 0;
                while (count <= MAX_TEXT_LENGTH && dindex < dest.length()) {
                    char c = dest.charAt(dindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 1;
                    }
                }
                if (count > MAX_TEXT_LENGTH) {
                    return dest.subSequence(0, dindex - 1);
                }

                int sindex = 0;
                while (count <= MAX_TEXT_LENGTH && sindex < source.length()) {
                    char c = source.charAt(sindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 1;
                    }
                }
                if (count > MAX_TEXT_LENGTH) {
                    sindex--;
                }
                return source.subSequence(0, sindex);
            }
        }});
        editSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((start + count) < MAX_TEXT_LENGTH) {
                    maxText.setText((start + count) + "/" + MAX_TEXT_LENGTH);
                    maxText.setTextColor(getColor(R.color.club_white));
                } else {
                    maxText.setText(MAX_TEXT_LENGTH + "/" + MAX_TEXT_LENGTH);
                    maxText.setTextColor(getColor(R.color.text_sign_number));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void commitUserSign(final String text) {
        if (!ClubNetWorkUtils.isConnected(this)) {
            return;
        }
        if (qunId == 0) {
            XMToast.toastException(this, getString(R.string.group_id_empty));
            finish();
            return;
        }
        showProgressDialog(R.string.is_commit_user_sign);
        ClubRequestManager.editGroupSign(qunId, text, new CallbackWrapper<ClubBaseResult>() {

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
                    finish();
                } else {
                    XMToast.toastException(GroupEditSignActivity.this, R.string.commit_user_sign_failed);
                }
            }


            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                dismissProgress();
                XMToast.toastException(GroupEditSignActivity.this, R.string.commit_user_sign_failed);
            }
        });
    }
}
