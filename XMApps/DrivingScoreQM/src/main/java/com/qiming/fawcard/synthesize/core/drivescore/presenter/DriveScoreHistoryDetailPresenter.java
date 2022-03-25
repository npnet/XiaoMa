package com.qiming.fawcard.synthesize.core.drivescore.presenter;

import android.content.Context;

import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreHistoryDetailContract;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDao;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDetailDao;

import java.util.List;

import javax.inject.Inject;

public class DriveScoreHistoryDetailPresenter implements DriveScoreHistoryDetailContract.Presenter {
    private Context mContext;
    private DriveScoreHistoryDetailContract.View mView;

    @Inject
    public DriveScoreHistoryDetailPresenter(Context context, DriveScoreHistoryDetailContract.View view) {
        this.mContext = context;
        this.mView = view;
    }

    @Override
    public void getDriveScore(int id) {
        DriveScoreHistoryDao driveScoreHistoryDao = new DriveScoreHistoryDao(mContext);
        DriveScoreHistoryEntity driveScoreHistoryEntity = driveScoreHistoryDao.queryForId(id);

        if (mView != null) {
            mView.onPageDataUpdate(driveScoreHistoryEntity);
        }
    }

    @Override
    public void getDriveScoreDetail(int id) {
        DriveScoreHistoryDetailDao driveScoreHistoryDetailDao = new DriveScoreHistoryDetailDao(mContext);
        List<DriveScoreHistoryDetailEntity> detailEntities = driveScoreHistoryDetailDao.queryHistoryDetail(id);

        if (mView != null) {
            mView.onLineChartUpdate(detailEntities);
        }
    }

    public void getDriveScoreDetail(Long startTime, Long endTime) {
        DriveScoreHistoryDetailDao driveScoreHistoryDetailDao = new DriveScoreHistoryDetailDao(mContext);
        List<DriveScoreHistoryDetailEntity> details = driveScoreHistoryDetailDao.getDetailsByHistoryStartEndTime(startTime, endTime);
        if (mView != null) {
            mView.onLineChartUpdate(details);
        }
    }
}
