package com.qiming.fawcard.synthesize.core.drivescore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.DriveScoreLineChartManager;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.dialog.LoadingFailDialog;
import com.qiming.fawcard.synthesize.base.dialog.LoadingNormalDialog;
import com.qiming.fawcard.synthesize.base.system.callback.MyLifecycleHandler;
import com.qiming.fawcard.synthesize.base.util.CalendarUtils;
import com.qiming.fawcard.synthesize.base.util.ConvertUtil;
import com.qiming.fawcard.synthesize.base.util.MathUtils;
import com.qiming.fawcard.synthesize.base.widget.BackButton;
import com.qiming.fawcard.synthesize.base.widget.CircularView;
import com.qiming.fawcard.synthesize.base.widget.DriverScoreLineChart;
import com.qiming.fawcard.synthesize.base.widget.HomeButton;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreHomeContract;
import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScoreHomePresenter;
import com.qiming.fawcard.synthesize.dagger2.component.ComponentHolder;
import com.qiming.fawcard.synthesize.dagger2.module.DriveScoreHomeModule;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.utils.log.KLog;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DriveScoreHomeActivity extends BaseActivity implements DriveScoreHomeContract.View, LoadingFailDialog.PriorityListener {
    @BindView(R.id.lineChart)
    DriverScoreLineChart mLineChart;
    @BindView(R.id.tvTime)
    TextView tvDriverTime;
    @BindView(R.id.tvDriverScore)
    TextView tvDriverScore;
    @BindView(R.id.circularViewRapidAccelerate)
    CircularView circularViewRapidAccelerate;
    @BindView(R.id.circularViewRapidDeceleration)
    CircularView circularViewRapidDeceleration;
    @BindView(R.id.circularViewSharpTurn)
    CircularView circularViewSharpTurn;
    @BindView(R.id.tvAvgSpeed)
    TextView tvAvgSpeed;
    @BindView(R.id.tvAvgFuelConsumer)
    TextView tvAvgFuelConsumer;
    @BindView(R.id.btn_history)
    Button btnHistory;
    @BindView(R.id.ibBackButton)
    BackButton ibBackButton;
    @BindView(R.id.ibHomeButton)
    HomeButton ibHomeButton;

    // ??????
    @Inject
    LoadingNormalDialog mDialog;
    @Inject
    LoadingFailDialog mDialogFail;

    @Inject
    DriveScoreLineChartManager mDriveScoreLineChartManager;
    @Inject
    DriveScoreHomePresenter mDriveScoreHomePresenter;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QMConstant.MESSAGE_REQUEST_FAILED:
                    hideLoading();
                    showLoadingFail((String) msg.obj);
                    break;
                case QMConstant.MESSAGE_DRIVE_START:
                    clearPageData();
                    break;
                case QMConstant.MESSAGE_DRIVE_INFO_UPDATE:
                    hideLoading();
                    hideLoadingFail();
                    updateLineChart((DriveScoreHistoryDetailEntity) msg.obj);
                    break;
                case QMConstant.MESSAGE_DRIVE_TIME_UPDATE:
                    updateDriveTime((long) msg.obj);
                    break;
                case QMConstant.MESSAGE_DRIVE_INFO_LIST_UPDATE:
                    hideLoading();
                    hideLoadingFail();
                    updateLineChartList((List<DriveScoreHistoryDetailEntity>) msg.obj);
                    break;
                case QMConstant.MESSAGE_DRIVE_SCORE_UPDATE:
                    updateDriveScore((DriveScoreHistoryEntity) msg.obj);
                    break;
                case QMConstant.MESSAGE_LOADING_HIDE:
                    hideLoading();
                    hideLoadingFail();
                    //showMessage(getResources().getString(R.string.loading_timeout));
                    break;
                default:
                    break;
            }
        }
    };

    private void showMessage(String msg) {
        Boolean result = MyLifecycleHandler.isApplicationInForeground();
        if (result) {
            Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
            LinearLayout layout = (LinearLayout) toast.getView();
            TextView tv = (TextView) layout.getChildAt(0);
            tv.setTextSize(25);
            toast.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.activity_drive_score_home);
        ButterKnife.bind(this);

        ComponentHolder.getInstance()
                .getDriveScoreHomeComponent(new DriveScoreHomeModule(this, this, this))
                .inject(this);
        initPageData();

        mDriveScoreHomePresenter.bindDriverService();

        // ??????????????????
        mDriveScoreLineChartManager.initLineChart();

        showLoading();
    }

    @Override
    protected void onStop() {
        super.onStop();
        KLog.d(QMConstant.TAG, "onStop: ");

//        mDriveScoreHomePresenter.retryGetDriveInfo(QMConstant.ERROR_CODE_GET_DRIVE_SNAP);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KLog.d(QMConstant.TAG, "onDestroy: ");
        mDriveScoreHomePresenter.unbindDriverService();
    }

    /**
     * ????????????????????????
     *
     * @param driverInfo ??????????????????
     */
    @Override
    public void onDriverInfoUpdate(DriveScoreHistoryDetailEntity driverInfo) {
        KLog.d(QMConstant.TAG, "DriveScoreHomeActivity::onDriverInfoUpdate: ");
        sendMessageToUI(QMConstant.MESSAGE_DRIVE_INFO_UPDATE, driverInfo);
    }

    /**
     * ????????????????????????
     *
     * @param driveTime ????????????
     */
    @Override
    public void onDriveTimeUpdate(long driveTime) {
        KLog.d(QMConstant.TAG, "DriveScoreHomeActivity::onDriveTimeUpdate: ");
        sendMessageToUI(QMConstant.MESSAGE_DRIVE_TIME_UPDATE, driveTime);
    }

    /**
     * ????????????????????????
     *
     * @param driverInfoList ??????????????????
     */
    @Override
    public void onDriverInfosUpdate(List<DriveScoreHistoryDetailEntity> driverInfoList) {
        KLog.d(QMConstant.TAG, "DriveScoreHomeActivity::onDriverInfosUpdate: ");
        sendMessageToUI(QMConstant.MESSAGE_DRIVE_INFO_LIST_UPDATE, driverInfoList);
    }

    /**
     * ??????????????????????????????
     *
     * @param data ??????????????????
     */
    @Override
    public void onDriveScoreUpdate(DriveScoreHistoryEntity data) {
        KLog.d(QMConstant.TAG, "DriveScoreHomeActivity::onDriveScoreUpdate: ");
        sendMessageToUI(QMConstant.MESSAGE_DRIVE_SCORE_UPDATE, data);
    }

    @Override
    public void onDriveStart() {
        KLog.d(QMConstant.TAG, "DriveScoreHomeActivity::onDriveStart: ");
        sendMessageToUI(QMConstant.MESSAGE_DRIVE_START);
    }

    @Override
    public void onBindSuccess() {
        KLog.d(QMConstant.TAG, "DriveScoreHomeActivity::onBindSuccess: ");

        if (!mDriveScoreHomePresenter.isTravelStarting()) {
            sendMessageToUI(QMConstant.MESSAGE_LOADING_HIDE);
        }
    }

    @Override
    public void onRequestFailed(String errorMessage) {
        KLog.d(QMConstant.TAG, "onRequestFailed: errorMessage = " + errorMessage);

        sendMessageToUI(QMConstant.MESSAGE_REQUEST_FAILED, errorMessage);
    }

    @Override
    public void hideLoadingView() {
        KLog.d(QMConstant.TAG, "hideLoadingView:");
        sendMessageToUI(QMConstant.MESSAGE_LOADING_HIDE);
    }

    @Override
    public void setResult(Boolean result) {
        if (result) {
            retryGetDriveInfo();
        }
    }

    private void sendMessageToUI(int messageType) {
        Message message = mHandler.obtainMessage();
        message.what = messageType;
        mHandler.sendMessage(message);
    }

    private void sendMessageToUI(int messageType, Object obj) {
        Message message = mHandler.obtainMessage();
        message.what = messageType;
        message.obj = obj;
        mHandler.sendMessage(message);
    }

    /**
     * ????????????????????????
     */
    private void showLoading() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    /**
     * ????????????????????????
     */
    private void hideLoading() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * ???????????????????????????
     *
     * @param message ?????????????????????????????????
     */
    private void showLoadingFail(String message) {
        if (mDialogFail != null && !mDialogFail.isShowing()) {
            mDialogFail.showDialog(message);
        }
    }

    /**
     * ???????????????????????????
     */
    private void hideLoadingFail() {
        if (null != mDialogFail && mDialogFail.isShowing()) {
            mDialogFail.dismiss();
        }
    }

    /**
     * ??????????????????????????????
     */
    private void retryGetDriveInfo() {
        showLoading();
        mDriveScoreHomePresenter.retryGetDriveInfo(QMConstant.ERROR_CODE_GET_DRIVE_SNAP);
    }

    /**
     * ?????????????????????
     */
    private void initPageData() {
        // ????????????
        tvDriverTime.setText(getResources().getString(R.string.travel_time));
        tvDriverTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastWeekMonday = CalendarUtils.getLastWeekMonday();
                String lastWeekSunday = CalendarUtils.getLastWeekSunday();
                KLog.d("test", "onClick: ????????????" + lastWeekMonday + " ~ ?????????" + lastWeekSunday);
            }
        });

        //????????????
        tvDriverScore.setText(getResources().getString(R.string.default_score));

        //?????????
        circularViewRapidAccelerate.setTvNum(getResources().getString(R.string.default_num));

        //?????????
        circularViewRapidDeceleration.setTvNum(getResources().getString(R.string.default_num));

        //?????????
        circularViewSharpTurn.setTvNum(getResources().getString(R.string.default_num));
    }

    /**
     * ???????????????????????????????????????
     */
    private void clearPageData() {
        KLog.d(QMConstant.TAG, "clearPageData: ");
        initPageData();
        // ??????????????????
        mDriveScoreLineChartManager.initLineChart();
    }

    /**
     * ?????????????????????
     *
     * @param data ??????????????????
     */
    private void updateLineChart(DriveScoreHistoryDetailEntity data) {
        Double avgSpeed;
        Double avgFuelConsumer;

        //????????????
        avgSpeed = MathUtils.round(data.avgSpeed, 1);

        //????????????
        avgFuelConsumer = MathUtils.round(data.avgFuel, 1);

        mDriveScoreLineChartManager.addEntry(avgSpeed, avgFuelConsumer, data.time);
    }

    /**
     * ?????????????????????
     *
     * @param driverInfoList ??????????????????
     */
    private void updateLineChartList(List<DriveScoreHistoryDetailEntity> driverInfoList) {
        for (int i = 0; i < driverInfoList.size(); i++) {
            Double avgSpeed;
            Double avgFuelConsumer;

            //????????????
            avgSpeed = MathUtils.round(driverInfoList.get(i).avgSpeed, 1);

            //????????????
            avgFuelConsumer = MathUtils.round(driverInfoList.get(i).avgFuel, 1);

            mDriveScoreLineChartManager.addEntry(avgSpeed, avgFuelConsumer, driverInfoList.get(i).time);
        }
    }

    private void updateDriveTime(long driveTime) {
        if (driveTime < 0) driveTime = 0;
        // ????????????
        tvDriverTime.setText(getResources().getString(R.string.drived) + driveTime + getResources().getString(R.string.drive_unit));
    }

    private void updateDriveScore(DriveScoreHistoryEntity data) {
        // ????????????
        tvDriverTime.setText(getResources().getString(R.string.drived) + ConvertUtil.secondToMinute(data.travelTime) + getResources().getString(R.string.drive_unit));

        //????????????
        tvDriverScore.setText(Math.round(data.score) + "");

        //?????????
        circularViewRapidAccelerate.setTvNum(data.accNum + getResources().getString(R.string.count));

        //?????????
        circularViewRapidDeceleration.setTvNum(data.decNum + getResources().getString(R.string.count));

        //?????????
        circularViewSharpTurn.setTvNum(data.turnNum + getResources().getString(R.string.count));
    }

    @OnClick({R.id.ibBackButton, R.id.ibHomeButton, R.id.btn_history})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ibBackButton:
                finish();
                break;
            case R.id.ibHomeButton:
                intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                break;
            case R.id.btn_history:
                intent = new Intent(DriveScoreHomeActivity.this, DriveScoreHistoryActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public DriverScoreLineChart getLineChart() {
        return mLineChart;
    }

}
