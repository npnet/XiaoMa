package com.qiming.fawcard.synthesize.core.drivescore;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.system.callback.HistoryRegistrationCenter;
import com.qiming.fawcard.synthesize.core.drivescore.adapter.HistoryAdapter;
import com.xiaoma.component.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DriveScoreHistoryActivity extends BaseActivity {

    @BindView(R.id.history_list)
    RecyclerView rlvHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_score_history);
        getNaviBar().showBackNavi();
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HistoryRegistrationCenter.clear();
    }

    /**
     * 初始化.
     */
    private void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rlvHistoryList.setLayoutManager(layoutManager);
        HistoryAdapter mDriveAdviceAdapter = new HistoryAdapter(this);
        mDriveAdviceAdapter.activity = this;
        rlvHistoryList.setAdapter(mDriveAdviceAdapter);
    }


}
