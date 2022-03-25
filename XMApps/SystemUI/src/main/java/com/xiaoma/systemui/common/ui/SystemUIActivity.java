package com.xiaoma.systemui.common.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xiaoma.systemui.BuildConfig;
import com.xiaoma.systemui.R;

public class SystemUIActivity extends Activity implements View.OnClickListener {
    private Button mBtnStatusBarTest;
    private Button btnAvsOn;
    private Button btnAvsOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            finish();
            return;
        }
        setContentView(R.layout.act_system_ui);
        Toast.makeText(this, R.string.main_act_startup_toast, Toast.LENGTH_SHORT).show();
        mBtnStatusBarTest = findViewById(R.id.btn_status_bar_test);
        mBtnStatusBarTest.setOnClickListener(this);
        btnAvsOn = findViewById(R.id.btn_avs_on);
        btnAvsOn.setOnClickListener(this);
        btnAvsOff = findViewById(R.id.btn_avs_off);
        btnAvsOff.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_status_bar_test:
                startActivity(new Intent(this, StatusBarMgrTestAct.class));
                break;
            case R.id.btn_avs_on:
                sendBroadcast(new Intent("com.xiaoma.TEST_AVS")
                        .putExtra("isOn", true));
                break;
            case R.id.btn_avs_off:
                sendBroadcast(new Intent("com.xiaoma.TEST_AVS")
                        .putExtra("isOn", false));
                break;
        }
    }
}