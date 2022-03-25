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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.setting.R;

public class OTAUpdatingDialog extends Dialog {

    private TextView mTvUpdateContent;
    private RelativeLayout mOneButtonLayout;

    public OTAUpdatingDialog(@NonNull Context context) {
        super(context);
    }

    public OTAUpdatingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_updating);
        initView();
        setCancelable(false);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = 788;
        p.height = 548;
        getWindow().setAttributes(p);
    }

    private void initView(){
        mTvUpdateContent = findViewById(R.id.tv_update_content);
    }

    public void setContent(CharSequence content) {
        mTvUpdateContent.setText(content);
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
