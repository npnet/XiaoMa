package com.qiming.fawcard.synthesize.core.drivescore.contract;

import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScoreHistoryPresenter;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

public class DriveScoreHistoryContract {
    // define presenter API
    public interface Presenter {
        DriveScoreHistoryPresenter.ViewModel get(int index);
        void delete(DriveScoreHistoryEntity data);
        int getCount();
    }
}
