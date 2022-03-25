package com.xiaoma.login.business.ui.verify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.R;
import com.xiaoma.utils.CountDownTimer;


/**
 * Created by kaka
 * on 19-5-31 下午2:37
 * <p>
 * desc: #a
 * </p>
 */
public class DeactivateActivity extends BaseActivity {

    private CountDownTimer mTimer;
    private static final long totalTime = 1000 * 60 * 5;// 5min
    private static final long interval = 1000 * 60;// 1min
    private TextView mDeactivateTips;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, DeactivateActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        setContentView(R.layout.activity_deactivate);
        mDeactivateTips = findViewById(R.id.deactivate_tips);
        mTimer = new CountDownTimer(totalTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                mDeactivateTips.setText(getString(R.string.deactivate_tips, getTimeByMillis(millisUntilFinished)));
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
        mTimer.start();
    }

    private String getTimeByMillis(long millis) {
        return getString(R.string.count_min, Math.round(millis / (1000 * 60f)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        //阻止用户返回
    }
}
