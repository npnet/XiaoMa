package com.qiming.fawcard.synthesize.core.drivescore.presenter;

import com.qiming.fawcard.synthesize.BaseTestCase;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHistoryDetailActivity;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreHistoryDetailContract;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class DriveScoreHistoryDetailPresenterTest extends BaseTestCase {

    @Test
    public void getDriveScoreAndDetail() {
        DriveScoreHistoryDetailActivity activity = Robolectric.setupActivity(DriveScoreHistoryDetailActivity.class);
        DriveScoreHistoryDetailContract.View mockView = mock(DriveScoreHistoryDetailContract.View.class);
        DriveScoreHistoryDetailPresenter scoreDetailPre = new DriveScoreHistoryDetailPresenter(activity, mockView);
        scoreDetailPre.getDriveScore(100);
        Mockito.verify(mockView, Mockito.times(1)).onPageDataUpdate((DriveScoreHistoryEntity) any());
        scoreDetailPre.getDriveScoreDetail(100);
        Mockito.verify(mockView, Mockito.times(1)).onLineChartUpdate(ArgumentMatchers.<DriveScoreHistoryDetailEntity>anyList());
    }
}

