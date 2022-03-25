package com.qiming.fawcard.synthesize.core.drivescore.presenter;

import com.qiming.fawcard.synthesize.base.application.QmApplication;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDao;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDetailDao;

public class DriveRecordDataUtils {
    private static DriveRecordDataUtils INSTANCE = new DriveRecordDataUtils();
    private DriveScoreHistoryDao mHistoryDao;
    private DriveScoreHistoryDetailDao mHistoryDetailDao;

    private DriveRecordDataUtils(){
        mHistoryDao = new DriveScoreHistoryDao(QmApplication.getContext());
    }

    public static DriveRecordDataUtils getInstance(){
        return INSTANCE;
    }


}
