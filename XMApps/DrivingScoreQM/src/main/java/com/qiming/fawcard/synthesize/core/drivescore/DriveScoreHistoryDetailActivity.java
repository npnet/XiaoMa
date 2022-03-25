package com.qiming.fawcard.synthesize.core.drivescore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.DriveScoreLineChartManager;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.util.ConvertUtil;
import com.qiming.fawcard.synthesize.base.util.MathUtils;
import com.qiming.fawcard.synthesize.base.widget.BackButton;
import com.qiming.fawcard.synthesize.base.widget.CircularView;
import com.qiming.fawcard.synthesize.base.widget.DriverScoreLineChart;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreHistoryDetailContract;
import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScoreHistoryDetailPresenter;
import com.qiming.fawcard.synthesize.dagger2.component.ComponentHolder;
import com.qiming.fawcard.synthesize.dagger2.module.DriveScoreHistoryDetailModule;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.utils.log.KLog;

import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DriveScoreHistoryDetailActivity extends BaseActivity implements DriveScoreHistoryDetailContract.View {
    @BindView(R.id.tvTime)
    TextView tvStartTime;
    @BindView(R.id.tvDriverScoreTitle)
    TextView tvDriverScoreTitle;
    @BindView(R.id.tvDriverScore)
    TextView tvDriverScore;
    @BindView(R.id.circularViewRapidAccelerate)
    CircularView circularViewRapidAccelerate;
    @BindView(R.id.circularViewRapidDeceleration)
    CircularView circularViewRapidDeceleration;
    @BindView(R.id.circularViewSharpTurn)
    CircularView circularViewSharpTurn;
    @BindView(R.id.lineChart)
    DriverScoreLineChart mLineChart;
    @BindView(R.id.ibBackButton)
    BackButton ibBackButton;
    @BindView(R.id.btn_history)
    Button btnHistory;

    private int mDriverInfoID;
    @Inject
    DriveScoreLineChartManager mDriveScoreLineChartManager;
    @Inject
    DriveScoreHistoryDetailPresenter mPresenter;
    private Long mStartTime;
    private Long mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_score_history_detail);
        ButterKnife.bind(this);

        // UI默认显示内容
        initView();
        ComponentHolder.getInstance()
                .getDriveScoreHistoryDetailComponent(new DriveScoreHistoryDetailModule(this, this))
                .inject(this);


        Intent intent = getIntent();
        mDriverInfoID = intent.getIntExtra("id", 0);
        mStartTime = intent.getLongExtra("startTime", 0);
        mEndTime = intent.getLongExtra("endTime", 0);

        // 初始化折线图
        mDriveScoreLineChartManager.initLineChart();

        getDriveInfoFromLocalDB();
    }

    @Override
    public void onPageDataUpdate(DriveScoreHistoryEntity driveScoreHistoryEntity) {
        if (driveScoreHistoryEntity == null) {
            return;
        }
        String startTime = "";
        try {
            String formatType = "yyyy.MM.dd   HH:mm";
            startTime = ConvertUtil.longToString(driveScoreHistoryEntity.startTime, formatType);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvStartTime.setText(getResources().getString(R.string.start_time) + startTime);
        tvDriverScore.setText(Math.round(driveScoreHistoryEntity.score) + "");
        circularViewRapidAccelerate.setTvNum(driveScoreHistoryEntity.accNum + getString(R.string.count));
        circularViewRapidDeceleration.setTvNum(driveScoreHistoryEntity.decNum + getString(R.string.count));
        circularViewSharpTurn.setTvNum(driveScoreHistoryEntity.turnNum + getString(R.string.count));
    }

    @Override
    public void onLineChartUpdate(List<DriveScoreHistoryDetailEntity> detailEntities) {
        if (detailEntities == null || detailEntities.size() == 0) {
            return;
        }

        for (int i = 0; i < detailEntities.size(); ++i) {
            DriveScoreHistoryDetailEntity curDetailEntity = detailEntities.get(i);
            Double aveSpeed = MathUtils.round(curDetailEntity.avgSpeed, 1);
            Double avgFuel = MathUtils.round(curDetailEntity.avgFuel, 1);
            mDriveScoreLineChartManager.addEntry(aveSpeed, avgFuel, curDetailEntity.time);
        }
    }

    private void initView() {
        tvStartTime.setText("");
        tvDriverScoreTitle.setText(getResources().getString(R.string.last_score));
        btnHistory.setVisibility(View.GONE);
    }

    private void getDriveInfoFromLocalDB() {
        if (mDriverInfoID <= 0) {
            return;
        }
        Long startTime = System.currentTimeMillis();
        KLog.e(QMConstant.TAG,"startTime = "+startTime);
        if (mPresenter != null) {
            mPresenter.getDriveScore(mDriverInfoID);
            mPresenter.getDriveScoreDetail(mStartTime,mEndTime);
        }
        Long endTime = System.currentTimeMillis();
        KLog.e(QMConstant.TAG,"endTime = "+endTime);
        KLog.e(QMConstant.TAG,"spendTime = "+(endTime-startTime));
    }

    @OnClick(R.id.ibBackButton)
    public void onViewClicked() {
        finish();
    }

    public DriverScoreLineChart getLineChart() {
        return mLineChart;
    }
}
