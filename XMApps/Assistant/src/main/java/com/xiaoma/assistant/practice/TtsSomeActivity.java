package com.xiaoma.assistant.practice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:
 *  @author:         Rookie
 *  @create_time:    2019/6/4 16:34
 *  @description：   TODO             */

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.dialog.VpRecordDialog;
import com.xiaoma.utils.LaunchUtils;

import java.lang.ref.WeakReference;


public class TtsSomeActivity extends BaseActivity implements View.OnClickListener {

    private EditText etVoice;
    private ImageView ivVoice;
    private TextView tvCount;
    private Button btnSure;
    private int mActionPosition;
    private int mRequestCode;

    private WeakReference<VpRecordDialog> mVoiceRecordDlgRef;

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_some);
        initView();
        initData();
        registerExit();
    }

    @Override
    protected void onDestroy() {
        unRegisterExit();
        super.onDestroy();
    }


    private void unRegisterExit() {
        if (mBroadcastReceiver == null) return;
        unregisterReceiver(mBroadcastReceiver);
    }

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exit();
            }
        }, intentFilter);
    }

    private void exit() {
        finish();
    }

    private void initView() {
        etVoice = findViewById(R.id.et_voice);
        ivVoice = findViewById(R.id.iv_voice);
        tvCount = findViewById(R.id.tv_count);
        btnSure = findViewById(R.id.btn_sure);

        setEditTextInhibitInputSpace(etVoice, 30);
        etVoice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String word = s.toString();
                tvCount.setText(word.length() + "/30");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ivVoice.setOnClickListener(this);
        btnSure.setOnClickListener(this);
    }


    private void initData() {
        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(LaunchUtils.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            return;
        }
        String text = bundleExtra.getString(VrPracticeConstants.ACTION_JSON);
        if (!TextUtils.isEmpty(text)) {
            etVoice.setText(text);
            etVoice.setSelection(text.length());
        }
        mActionPosition = bundleExtra.getInt(VrPracticeConstants.ACTION_POSITION, 0);
        mRequestCode = bundleExtra.getInt(VrPracticeConstants.SKILL_REQUEST_CODE, 0);
    }


    @Override
    @SingleClick
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice:

                if (isVoiceRecordShowing()) {
                    return;
                }

                VpRecordDialog vpRecordDialog = new VpRecordDialog(this, new VpRecordDialog.Callback() {
                    @Override
                    public void onSend(VpRecordDialog dlg, String translation) {
                        dlg.dismiss();
                        etVoice.setText(translation);
                        if (!TextUtils.isEmpty(translation)) {
                            etVoice.setSelection(translation.length());
                        }
                    }

                    @Override
                    public void onError(VpRecordDialog dlg, int errorCode) {
                        dlg.dismiss();
                        showToast(errorCode);
                    }
                });
                vpRecordDialog.show();

                mVoiceRecordDlgRef = new WeakReference<>(vpRecordDialog);
                break;
            case R.id.btn_sure:
                if (TextUtils.isEmpty(etVoice.getText().toString())) {
                    showToast(R.string.please_enter_text);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(VrPracticeConstants.ACTION_JSON, etVoice.getText().toString());
                bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
                bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
                LaunchUtils.launchAppWithData(TtsSomeActivity.this, VrPracticeConstants.PACKAGE_NAME, VrPracticeConstants.SKILL_CLASS_NAME, bundle);
                finish();
                break;
            default:
                break;
        }
    }

    private boolean isVoiceRecordShowing() {
        if (mVoiceRecordDlgRef == null)
            return false;
        Dialog lastDlg = mVoiceRecordDlgRef.get();
        return lastDlg != null && lastDlg.isShowing();
    }

    /**
     * 禁止EditText输入空格
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText, int maxLength) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength), filter});
    }
}
