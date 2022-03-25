package com.xiaoma.login.business.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.R;
import com.xiaoma.utils.LaunchUtils;

import static com.xiaoma.login.common.LoginConstants.XIAOMA_SETTING_PKG;

/**
 * Created by kaka
 * on 19-5-22 下午6:11
 * <p>
 * desc: #a
 * </p>
 */
public class OfflineActivationActivity extends BaseActivity implements View.OnClickListener {

    private View mGoTouristsMode;
    private View mRetry;
    private View mSetting;

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, OfflineActivationActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        setContentView(R.layout.off_online_mode);
        initOfflineView();
    }


    private void initOfflineView() {
        //因为动态设置的ErrorView需要在显示后才会被add到activity中
        mGoTouristsMode = findViewById(R.id.go_tourists_mode);
        mRetry = findViewById(R.id.retry);
        mSetting = findViewById(R.id.setting);

        mGoTouristsMode.setVisibility(View.GONE);
        mRetry.setOnClickListener(this);
        mSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.retry) {
            finish();
        } else if (i == R.id.setting) {
            LaunchUtils.launchApp(this, XIAOMA_SETTING_PKG);
        }
    }
}
