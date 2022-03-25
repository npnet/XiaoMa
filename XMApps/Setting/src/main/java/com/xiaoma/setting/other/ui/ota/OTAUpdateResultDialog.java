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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.setting.R;

public class OTAUpdateResultDialog extends Dialog {

    private TextView mTvTitle, mTvContent, mTvTip, mTvButton;
    private RelativeLayout mOneButtonLayout;

    public OTAUpdateResultDialog(@NonNull Context context) {
        super(context);
    }

    public OTAUpdateResultDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_result_small);
        initView();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = 730;
        p.height = 745;
        getWindow().setAttributes(p);
    }

    private void initView(){
        mTvTitle = findViewById(R.id.tv_title);
        mTvContent = findViewById(R.id.tv_content);
        mTvTip = findViewById(R.id.tv_tip);
        mOneButtonLayout = findViewById(R.id.one_button);
        mTvButton = findViewById(R.id.tv_button);
    }

    public void setContent(CharSequence title, CharSequence content, CharSequence tip) {
        mTvTitle.setText(title);
        mTvContent.setText(content);
        mTvTip.setText(tip);
        if (tip.equals("")){
            mTvTip.setVisibility(View.GONE);
        }else{
            mTvTip.setVisibility(View.VISIBLE);
        }
    }

    public void setOneButon(CharSequence buttonText, View.OnClickListener listener, int buttonVisible){
        mTvButton.setText(buttonText);
        mTvButton.setVisibility(buttonVisible);
        mTvButton.setOnClickListener(listener);
        if (buttonVisible == View.GONE){
            setCancelable(false);
        }else{
            setCancelable(true);
        }
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
