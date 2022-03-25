package com.xiaoma.login.business.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.R;

public class IccidCheckActivity extends BaseActivity {
    private TextView mTvIccid;
    private TextView mTvImei;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().showBackNavi();
        setContentView(R.layout.activity_iccid);

        mTvIccid = findViewById(R.id.tv_iccid);
        mTvImei = findViewById(R.id.tv_imei);

        mTvIccid.setText(getString(R.string.iccid, ConfigManager.DeviceConfig.getICCID(this)));
        mTvImei.setText(getString(R.string.imei, ConfigManager.DeviceConfig.getIMEI(this)));
    }
}
