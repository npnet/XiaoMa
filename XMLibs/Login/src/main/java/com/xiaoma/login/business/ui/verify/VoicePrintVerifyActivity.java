package com.xiaoma.login.business.ui.verify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.R;
import com.xiaoma.ui.view.UnderLineTextView;
import com.xiaoma.voiceprint.VoicePrintManager;
import com.xiaoma.voiceprint.model.ICallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author KY
 * @date 11/23/2018
 */
public class VoicePrintVerifyActivity extends BaseActivity {

    public static final int VERIFY_COUNT = 3;
    public static final String PARAM_USER_ID = "userId";

    private UnderLineTextView verifyText;
    private Button button;
    private List<String> verifyTexts = new ArrayList<>(VERIFY_COUNT);
    private List<String> fileIds = new ArrayList<>(VERIFY_COUNT);
    private String userId;

    public static void startActivity(Context context, String userId) {
        Intent intent = new Intent(context, VoicePrintVerifyActivity.class);
        intent.putExtra(PARAM_USER_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_print_verify);
        initView();
        initData();
    }

    private void initView() {
        verifyText = findViewById(R.id.verify_text);
        button = findViewById(R.id.button);
    }

    private void initData() {
        userId = getIntent().getStringExtra(PARAM_USER_ID);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        fetchVerifyText();
    }

    private void fetchVerifyText() {
        showProgressDialog(R.string.base_loading);
        VoicePrintManager.getInstance().getTrainingText(new ICallback.GetTextCallback() {
            @Override
            public void onGetTextSuccess(List<String> texts) {
                for (String text : texts) {
                    verifyTexts.add(text.replace(" ", ""));
                }
                startVerifyRecord();
                dismissProgress();
            }

            @Override
            public void onFailure(String tag, String message) {
                dismissProgress();
                Toast.makeText(VoicePrintVerifyActivity.this, "获取验证文本失败", Toast.LENGTH_SHORT).show();
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
                            startServerVerify();
                        }
                    }

                    @Override
                    public void onFailure(String tag, String message) {
                        Toast.makeText(VoicePrintVerifyActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
//        .run();
    }

    private void startServerVerify() {
        VoicePrintManager.getInstance().verifyVoicePrint(userId, fileIds, new ICallback.VoicePrintVerifyCallback() {
            @Override
            public void onVerifySuccess() {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(String tag, String message) {
                Toast.makeText(VoicePrintVerifyActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
