package com.xiaoma.drivingscore.main.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.drivingscore.R;
import com.xiaoma.drivingscore.common.view.ChartView;
import com.xiaoma.drivingscore.historyRecord.ui.HistoryRecordActivity;
import com.xiaoma.drivingscore.main.vm.MainVM;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    private static final int MSG_UPDATE_INFO = 0x110;
    private ArrayList<ChartView.Units> mUnitList1 = new ArrayList();
    private ArrayList<ChartView.Units> mUnitList2 = new ArrayList();
    private TextView mTvLeft;
    private TextView mTvRight;
    private TextView mTvHistory;
    private ChartView mChartView;
    private MainVM mMainVM;
    private TextView mTvCenter;
    private int lastIndex = 20;
    private HandlerThread mHandlerThread;
    private Handler mCheckMsgHandler;
    private boolean isUpdateInfo;
    //5分钟查询数据
    private int DELAY_MILLIS = 5 * 60 * 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainVM = ViewModelProviders.of(this).get(MainVM.class);
        initView();
        initBackThread();
        initData();
    }

    private void initView() {
        mTvLeft = (TextView) findViewById(R.id.tv_left);
        mTvLeft.setOnClickListener(this);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mTvRight.setOnClickListener(this);
        mTvHistory = (TextView) findViewById(R.id.tv_history);
        mTvHistory.setOnClickListener(this);
        mChartView = (ChartView) findViewById(R.id.chartView);
        mChartView.setVisibility(View.VISIBLE);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvCenter.setOnClickListener(this);
    }

    private void initBackThread() {
        mHandlerThread = new HandlerThread("update_data");
        mHandlerThread.start();
        mCheckMsgHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                mMainVM.checkForUpdate();
                if (isUpdateInfo) {
                    //每隔五分钟刷一次数据
                    mCheckMsgHandler.sendEmptyMessageDelayed(MSG_UPDATE_INFO, DELAY_MILLIS);
                }
            }
        };
    }

    private void initData() {

        for (int i = 0; i < lastIndex; i++) {
            mUnitList1.add(new ChartView.Units(String.valueOf(i + ":00"), (float) (Math.random() * 181 + 60)));
            mUnitList2.add(new ChartView.Units(String.valueOf(i + ":00"), (float) (Math.random() * 181 + 60)));
        }

        mChartView.setValue(mUnitList1, mUnitList2);
        mChartView.setYLimit(240, 4);
        mChartView.setSelectIndex(3);
        mChartView.setChartLineValueListener(new ChartView.OnChartLineValueListener() {
            @Override
            public void onChartValue(float lineOneValue, float lineTweValue) {
                Log.i("page", "onChartValue: " + lineOneValue + " / " + lineTweValue);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandlerThread.quit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_left:
                mChartView.slide(100, 200, true);
                break;
            case R.id.tv_right:
                mChartView.slide(100, 200, false);
                break;
            case R.id.tv_history:
                HistoryRecordActivity.launch(this);
                break;
            case R.id.tv_center:
                int i = lastIndex++;
                mChartView.addValue(new ChartView.Units(String.valueOf(i + ":00"), (float) (Math.random() * 181 + 60)),
                        new ChartView.Units(String.valueOf(i + ":00"), (float) (Math.random() * 181 + 60)));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isUpdateInfo = false;
        mCheckMsgHandler.removeMessages(MSG_UPDATE_INFO);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isUpdateInfo = true;
        mCheckMsgHandler.sendEmptyMessage(MSG_UPDATE_INFO);
    }
}
