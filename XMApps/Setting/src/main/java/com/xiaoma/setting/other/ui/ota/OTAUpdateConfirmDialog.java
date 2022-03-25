package com.xiaoma.setting.other.ui.ota;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.setting.R;

public class OTAUpdateConfirmDialog extends Dialog {

    private TextView mTvContent;
    private Button mBtnSure, mBtnCancel;

    public OTAUpdateConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public OTAUpdateConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm_ui);
        initView();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = 564;
        p.height = 245;
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        getWindow().setAttributes(p);
    }

    private void initView(){
        mTvContent = findViewById(R.id.confirm_dialog_content);
        mBtnSure = findViewById(R.id.btn_confirm_sure);
        mBtnCancel = findViewById(R.id.btn_confirm_cancel);
    }

    public void setContent(CharSequence content) {
        mTvContent.setText(content);
    }

    public void setConfirmSureListener(String buttonText, View.OnClickListener onClickListener){
        mBtnSure.setText(buttonText);
        mBtnSure.setOnClickListener(onClickListener);
    }

    public void setConfirmCancelListener(String buttonText, View.OnClickListener onClickListener){
        mBtnCancel.setText(buttonText);
        mBtnCancel.setOnClickListener(onClickListener);
    }

    @Override
    public void show() {
        if (Settings.canDrawOverlays(getContext())) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            super.show();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getContext().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getContext().startActivity(intent);
        }
    }

}
