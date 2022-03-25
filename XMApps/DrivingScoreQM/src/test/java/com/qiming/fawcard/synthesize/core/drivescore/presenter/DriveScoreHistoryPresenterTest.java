package com.qiming.fawcard.synthesize.core.drivescore.presenter;

import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDao;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDetailDao;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DriveScoreHistoryPresenterTest {

    private DriveScoreHistoryDao mDriveScoreHistoryDao;
    private DriveScoreHistoryDetailDao mDriveScoreHistoryDetailDao;
    private DriveScoreHistoryPresenter mDriveScoreHistoryPresenter;
    private List<DriveScoreHistoryEntity> mDataList;

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;

        mDriveScoreHistoryDao = mock(DriveScoreHistoryDao.class);
        mDataList = new ArrayList<>();
        makeDataList();
        when(mDriveScoreHistoryDao.queryHistoryList()).thenReturn(mDataList);

        mDriveScoreHistoryDetailDao = mock(DriveScoreHistoryDetailDao.class);
        mDriveScoreHistoryPresenter = new DriveScoreHistoryPresenter(mDriveScoreHistoryDao, mDriveScoreHistoryDetailDao);
    }

    private void makeDataList() {
        DriveScoreHistoryEntity data = new DriveScoreHistoryEntity();
        data.startTime = 1234567L;
        mDataList.add(data);

        DriveScoreHistoryEntity data1 = new DriveScoreHistoryEntity();
        data.startTime = 4254568132L;
        mDataList.add(data1);
        mDataList.add(data1);
    }

    @Test
    public void delete() {
        DriveScoreHistoryEntity data = new DriveScoreHistoryEntity();
        mDriveScoreHistoryPresenter.delete(data);

        Mockito.verify(mDriveScoreHistoryDao, Mockito.times(1)).delete(data);

        Mockito.verify(mDriveScoreHistoryDetailDao, Mockito.times(1)).deleteHistoryDetail(data.id);

    }

    @Test
    public void get() {
        assertEquals(mDataList.get(0),mDriveScoreHistoryPresenter.get(0).getDriveScoreHistoryEntity());
        assertEquals(mDataList.get(1),mDriveScoreHistoryPresenter.get(1).getDriveScoreHistoryEntity());
        assertEquals(mDataList.get(2),mDriveScoreHistoryPresenter.get(2).getDriveScoreHistoryEntity());
    }

    @Test
    public void getCount() {
        assertEquals(3, mDriveScoreHistoryPresenter.getCount());
    }

    @Test
    public void getChangeAndValidPosition() {

        // 日期不同，认为有变化，返回True
        assertTrue(mDriveScoreHistoryPresenter.getChange(1));
        // 日期相同，没有变化，返回False
        assertFalse(mDriveScoreHistoryPresenter.getChange(2));
        // 无效的post，认为有变化，返回True
        assertTrue(mDriveScoreHistoryPresenter.getChange(3));
    }

}