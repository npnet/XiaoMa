package com.qiming.fawcard.synthesize.base.system.callback;

import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import java.util.List;

public interface DriverInfoCallback {
    void dataChange(DriveScoreHistoryDetailEntity driverInfo);

    void onDriverInfosNotify(List<DriveScoreHistoryDetailEntity> driverInfoList);

    void onDriverScore(DriveScoreHistoryEntity data);

    void onRequestFailed(QMConstant.RequestFailMessage msg);

    void onDriveStart();

    void onBindSuccess();

    void onDriveTime(long driveTime);

    void hideLoading();
}
