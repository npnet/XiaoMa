package com.xiaoma.voiceprint.business.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.User;
import com.xiaoma.ui.view.UnderLineTextView;
import com.xiaoma.voiceprint.R;
import com.xiaoma.voiceprint.VoicePrintConstant;
import com.xiaoma.voiceprint.VoicePrintManager;
import com.xiaoma.voiceprint.model.ICallback;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 11/26/2018
 */
public class VoicePrintRegisterActivity extends BaseActivity {

    public static final int VERIFY_COUNT = 3;
    public static final String PARAM_USER = "user";

    private UnderLineTextView verifyText;
    private List<String> verifyTexts = new ArrayList<>(VERIFY_COUNT);
    private List<String> fileIds = new ArrayList<>(VERIFY_COUNT);
    private User mUser;

    public static void startActivityForResult(Activity activity, User user) {
        Intent intent = new Intent(activity, VoicePrintRegisterActivity.class);
        intent.putExtra(PARAM_USER, (Serializable) user);
        activity.startActivityForResult(intent, VoicePrintConstant.RequestCode.VOICE_REGISTER);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_print_register);
        initView();
        initData();
    }

    private void initView() {
        verifyText = findViewById(R.id.verify_text);
    }

    private void initData() {
        mUser = (User) getIntent().getSerializableExtra(PARAM_USER);

        showProgressDialog("加载验证本文");
        VoicePrintManager.getInstance().getTrainingText(new ICallback.GetTextCallback() {
            @Override
            public void onGetTextSuccess(List<String> texts) {
                for (String text : texts) {
                    verifyTexts.add(text.replace(" ", ""));
                }
                dismissProgress();
                startVerifyRecord();
            }

            @Override
            public void onFailure(String tag, String message) {
                dismissProgress();
                Toast.makeText(VoicePrintRegisterActivity.this, "获取验证文本失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startVerifyRecord() {
        // 更新展示的验证文本
        verifyText.setString(verifyTexts.get(fileIds.size()));

        //TODO： 开始录音
        // 模拟录音回调
        new Runnable() {
            @Override
            public void run() {
                //TODO：来自录音的录制文件
                File file = new File("");
                VoicePrintManager.getInstance().uploadVoicePrintFile(file, new ICallback.UploadCallback() {
                    @Override
                    public void onUploadSuccess(String fileId) {
                        fileIds.add(fileId);
                        if (fileIds.size() < VERIFY_COUNT) {
                            startVerifyRecord();
                        } else {
                            startServerRegister();
                        }
                    }

                    @Override
                    public void onFailure(String tag, String message) {
                        Toast.makeText(VoicePrintRegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
//        .run();
    }

    private void startServerRegister() {
        VoicePrintManager.getInstance().registerVoicePrint(mUser.getName(), fileIds, new ICallback.AddRegisterCallback() {
            @Override
            public void onUserAddSuccess(String clientId) {

            }

            @Override
            public void onRegisterSuccess(String clientId) {
                Intent intent = new Intent();
                intent.putExtra(VoicePrintConstant.ResultCode.VOICE_REGISTER_CLIENT_ID, clientId);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(String tag, String message) {
                Toast.makeText(VoicePrintRegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}
