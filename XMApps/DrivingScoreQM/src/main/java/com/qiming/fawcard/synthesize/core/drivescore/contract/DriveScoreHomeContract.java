package com.qiming.fawcard.synthesize.core.drivescore.contract;

import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import java.util.List;

public class DriveScoreHomeContract {
    public interface Presenter{
        void bindDriverService();

        void unbindDriverService();

        void retryGetDriveInfo(int errorCode);

        boolean isTravelStarting();
    }

    public interface View{
        void onDriverInfoUpdate(DriveScoreHistoryDetailEntity driverInfo);

        void onDriveTimeUpdate(long driveTime);

        void onDriverInfosUpdate(List<DriveScoreHistoryDetailEntity> driverInfoList);

        void onDriveScoreUpdate(DriveScoreHistoryEntity data);

        void onDriveStart();

        void onBindSuccess();

        void onRequestFailed(String errorMessage);

        void hideLoadingView();
    }
}
