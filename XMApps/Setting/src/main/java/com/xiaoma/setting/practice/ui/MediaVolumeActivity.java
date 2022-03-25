package com.xiaoma.setting.practice.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      MediaVolumeActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/4 16:45
 *  @description：   TODO             */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.views.TextTackSeekBar;
import com.xiaoma.utils.LaunchUtils;

public class MediaVolumeActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    public static final String TAG="[MediaVolumeActivity]";
    private TextTackSeekBar mMediaVolumeSeekBar;
    private int currentValues;
    private int mActionPosition;
    private int mRequestCode;
    private BroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_volume);
        initView();
        initData();
        registerExit();
    }

    @Override
    protected void onDestroy() {
        unRegisterExit();
        super.onDestroy();
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
    private void unRegisterExit() {
        if (mBroadcastReceiver == null) return;
        unregisterReceiver(mBroadcastReceiver);
    }
    private void exit() {
        finish();
    }
    private void initView() {
         mMediaVolumeSeekBar = findViewById(R.id.media_volume_seekbar);
        mMediaVolumeSeekBar.setOnSeekBarChangeListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(LaunchUtils.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            return;
        }
        String text = bundleExtra.getString(VrPracticeConstants.ACTION_JSON);
        if (!TextUtils.isEmpty(text)) {
            mMediaVolumeSeekBar.setProgress(Integer.parseInt(text));
        }
        mActionPosition = bundleExtra.getInt(VrPracticeConstants.ACTION_POSITION, 0);
        mRequestCode = bundleExtra.getInt(VrPracticeConstants.SKILL_REQUEST_CODE, 0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentValues=progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        currentValues=seekBar.getProgress();
    }

    @SingleClick
    public void click(View view){
        Bundle bundle = new Bundle();
        bundle.putString(VrPracticeConstants.ACTION_JSON, String.valueOf(currentValues));
        bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
        bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
        LaunchUtils.launchAppWithData(MediaVolumeActivity.this, VrPracticeConstants.PACKAGE_NAME, VrPracticeConstants.SKILL_CLASS_NAME, bundle);
        finish();
    }
}
