package com.qiming.fawcard.synthesize.core.drivescore.contract;

import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import java.util.List;

public class DriveScoreHistoryDetailContract {
    public interface Presenter{
        void getDriveScore(int id);

        void getDriveScoreDetail(int id);
    }

    public interface View{
        void onPageDataUpdate(DriveScoreHistoryEntity driveScoreHistoryEntity);

        void onLineChartUpdate(List<DriveScoreHistoryDetailEntity> detailEntities);
    }
}
