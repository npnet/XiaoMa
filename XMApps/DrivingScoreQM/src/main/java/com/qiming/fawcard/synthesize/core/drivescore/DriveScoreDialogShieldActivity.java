package com.qiming.fawcard.synthesize.core.drivescore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.util.CalendarUtils;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.utils.log.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DriveScoreDialogShieldActivity extends BaseActivity {
//    @BindView(R.id.ibBackButton)
//    BackButton ibBackButton;
//    @BindView(R.id.ibHomeButton)
//    HomeButton ibHomeButton;
    @BindView(R.id.tv_shield2)
    TextView tvShield2;
    @BindView(R.id.tv_shield3)
    TextView tvShield3;
    @BindView(R.id.tv_shield1)
    TextView tvDriveDate;
    Boolean mIsCurrentApp; // 当前App是否在前台

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRootLayout().setBackground(null);
        setContentView(R.layout.activity_dialog_shield);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int curScore = intent.getIntExtra("curScore", 0);
        if (curScore == QMConstant.DRIVE_SCORE_100) {
            tvShield2.setTextColor(getResources().getColor(R.color.custom_orange));
        }
        String driverCommon = intent.getStringExtra("driverCommon");
        mIsCurrentApp = intent.getBooleanExtra("isForeground", false);

        tvShield3.setText(curScore + getResources().getString(R.string.history_score));
        tvShield2.setText(driverCommon);
        tvDriveDate.setText(CalendarUtils.getLastWeekMonday() + " ~ " + CalendarUtils.getLastWeekSunday());
    }

//    @OnClick({R.id.ibBackButton, R.id.ibHomeButton})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.ibBackButton:
//                finish();
//                if (false == mIsCurrentApp) {
//                    KLog.d(QMConstant.TAG, "将当前运行的评分APP退到后台运行");
//                    moveTaskToBack(true);
//                }
//                break;
//            case R.id.ibHomeButton:
//                KLog.d(QMConstant.TAG, "回退到系统Home界面");
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                startActivity(intent);
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
        if (false == mIsCurrentApp) {
            KLog.d(QMConstant.TAG, "将当前运行的评分APP退到后台运行");
            moveTaskToBack(true);
        }
    }
}
