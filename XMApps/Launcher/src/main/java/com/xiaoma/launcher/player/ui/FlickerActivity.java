package com.xiaoma.launcher.player.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by LKF on 2019-1-31 0031.
 * 通过打开此Activity,可以强行让上一个Activity执行onPause->onResume
 */
public class FlickerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }
}
