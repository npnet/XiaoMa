package com.xiaoma.setting.common.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.log.LogUtils;

/**
 * @author: iSun
 * @date: 2018/10/16 0016
 */
public class TestActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_panel);
        bindView();
    }

    private void bindView() {
        findViewAndSetClick(R.id.btn1);
        findViewAndSetClick(R.id.btn2);
        findViewAndSetClick(R.id.btn3);
    }

    private void findViewAndSetClick(int id) {
        View viewById = findViewById(id);
        if (viewById != null) {
            viewById.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                boolean logcatLog = LogUtils.getLogcatLog();
                XMToast.showToast(this, getString(R.string.log_logcat_tips) + logcatLog);
                break;
            case R.id.btn2:
                LaunchUtils.launchApp(this, SettingConstants.PACKAGE_SYSTEM_SETTING);
                break;
            case R.id.btn3:
                break;
        }
    }
}
