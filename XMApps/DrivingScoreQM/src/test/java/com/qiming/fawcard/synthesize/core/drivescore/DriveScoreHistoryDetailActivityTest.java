package com.qiming.fawcard.synthesize.core.drivescore;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.BaseTestCase;
import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.DriveScoreLineChartManager;
import com.qiming.fawcard.synthesize.base.widget.BackButton;
import com.qiming.fawcard.synthesize.base.widget.CircularView;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDao;
import com.qiming.fawcard.synthesize.data.source.local.DriveScoreHistoryDetailDao;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowActivity;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.robolectric.Shadows.shadowOf;

public class DriveScoreHistoryDetailActivityTest extends BaseTestCase {

    private List<DriveScoreHistoryDetailEntity> makeDetailEntityList(int listCount) {
        List<DriveScoreHistoryDetailEntity> dataList = new ArrayList<>();

        for (int i = 0; i < listCount; i++) {
            dataList.add(new DriveScoreHistoryDetailEntity());
        }

        return dataList;
    }

    @Test
    public void onLineChartUpdateTest_oneData() {

        DriveScoreHistoryDetailActivity testActivity = new DriveScoreHistoryDetailActivity();
        DriveScoreLineChartManager myMock = Mockito.mock(DriveScoreLineChartManager.class);
        Whitebox.setInternalState(testActivity, "mDriveScoreLineChartManager", myMock);
        testActivity.onLineChartUpdate(makeDetailEntityList(1));

        // 有一条情报的场合
        Mockito.verify(myMock).addEntry(anyInt(), anyDouble(), anyLong());
        Mockito.verify(myMock, Mockito.atLeastOnce()).addEntry(anyInt(), anyDouble(), anyLong());
        Mockito.verify(myMock, Mockito.times(1)).addEntry(anyInt(), anyDouble(), anyLong());

    }

    @Test
    public void onLineChartUpdateTest_moreData() {

        DriveScoreHistoryDetailActivity testActivity = new DriveScoreHistoryDetailActivity();
        DriveScoreLineChartManager myMock = Mockito.mock(DriveScoreLineChartManager.class);
        Whitebox.setInternalState(testActivity, "mDriveScoreLineChartManager", myMock);

        // 有三条情报的场合
        testActivity.onLineChartUpdate(makeDetailEntityList(3));
        Mockito.verify(myMock, Mockito.atLeast(3)).addEntry(anyInt(), anyDouble(), anyLong());
        Mockito.verify(myMock, Mockito.times(3)).addEntry(anyInt(), anyDouble(), anyLong());

    }

    @Test
    public void onPageDataUpdateTest() {

        DriveScoreHistoryEntity anyObject = new DriveScoreHistoryEntity();
        anyObject.accNum = 1L;
        anyObject.decNum = 2L;
        anyObject.turnNum = 3L;
        anyObject.score = 100D;
        anyObject.startTime = 1558942409L;


        DriveScoreHistoryDetailActivity testActivity = Robolectric.setupActivity(DriveScoreHistoryDetailActivity.class);
        testActivity.onPageDataUpdate(anyObject);
        TextView tvStartTime = testActivity.findViewById(R.id.tvTime);
        Assert.assertEquals("上车时间：1970.01.19   09:02", tvStartTime.getText().toString());
        TextView tvDriverScore = testActivity.findViewById(R.id.tvDriverScore);
        Assert.assertEquals("100", tvDriverScore.getText().toString());
        CircularView circularViewRapidAccelerate = testActivity.findViewById(R.id.circularViewRapidAccelerate);
        TextView textViewAccTvNum = circularViewRapidAccelerate.findViewById(R.id.tvNum);
        Assert.assertEquals("1次", (textViewAccTvNum.getText().toString()));

        CircularView circularViewRapidDeceleration = testActivity.findViewById(R.id.circularViewRapidDeceleration);
        TextView textViewDecTvNum = circularViewRapidDeceleration.findViewById(R.id.tvNum);
        Assert.assertEquals("2次", textViewDecTvNum.getText().toString());

        CircularView circularViewSharpTurn = testActivity.findViewById(R.id.circularViewSharpTurn);
        TextView textViewSharpTvNum = circularViewSharpTurn.findViewById(R.id.tvNum);
        Assert.assertEquals("3次", textViewSharpTvNum.getText().toString());
    }

    @Test
    public void testOnClickFromHistoryToHistoryDetail() {

        Activity activity = Robolectric.setupActivity(Activity.class);
        //历史数据库文件
        DriveScoreHistoryDao driveScoreHistoryDao = new DriveScoreHistoryDao(activity);
        //向历史数据库插入数据
        insertDateToHistoryDB(driveScoreHistoryDao);
        //历史详情数据库文件
        DriveScoreHistoryDetailDao driveScoreHistoryDetailDao = new DriveScoreHistoryDetailDao(activity);
        //向历史详情数据库插入数据
        insertDateToHistoryDetailDB(driveScoreHistoryDetailDao);

        //启动历史Acitvity并模拟点击其中一条数据
        ActivityController<DriveScoreHistoryActivity> activityController = Robolectric.buildActivity(DriveScoreHistoryActivity.class);
        DriveScoreHistoryActivity driveScoreHistoryActivity = activityController.get();
        activityController.create().start().visible();
        RecyclerView currentRecyclerView = driveScoreHistoryActivity.findViewById(R.id.history_list);
        LinearLayoutManager layoutManager =  (LinearLayoutManager) currentRecyclerView.getLayoutManager();
        layoutManager.findViewByPosition(0).findViewById(R.id.llItemClick).performClick();
        ShadowActivity myActivityShadow = shadowOf(driveScoreHistoryActivity);
        //       currentRecyclerView.getChildAt(0).performClick();
        Intent expectedIntent = new Intent(driveScoreHistoryActivity, DriveScoreHistoryDetailActivity.class);
        Intent actual = myActivityShadow.getNextStartedActivity();

        //验证是否启动了期望的Activity
        assertEquals(expectedIntent.getComponent(), actual.getComponent());

        //Intent模拟历史Activity跳转到历史详情Activity
        ActivityController<DriveScoreHistoryDetailActivity> testActivityController = Robolectric.buildActivity(DriveScoreHistoryDetailActivity.class, actual);
        DriveScoreHistoryDetailActivity testActivity = testActivityController.create().get();

        //验证历史详情Activity取得的数据是否正确
        TextView tvStartTime = testActivity.findViewById(R.id.tvTime);
        assertEquals("上车时间：2019.05.31   14:06", tvStartTime.getText().toString());
        TextView tvDriverScore = testActivity.findViewById(R.id.tvDriverScore);
        assertEquals("30", tvDriverScore.getText().toString());

        CircularView circularViewRapidAccelerate = testActivity.findViewById(R.id.circularViewRapidAccelerate);
        TextView textViewAccTvNum = circularViewRapidAccelerate.findViewById(R.id.tvNum);
        assertEquals("1次", textViewAccTvNum.getText().toString());

        CircularView circularViewRapidDeceleration = testActivity.findViewById(R.id.circularViewRapidDeceleration);
        TextView textViewDecTvNum = circularViewRapidDeceleration.findViewById(R.id.tvNum);
        assertEquals("2次", textViewDecTvNum.getText().toString());

        CircularView circularViewSharpTurn = testActivity.findViewById(R.id.circularViewSharpTurn);
        TextView textViewSharpTvNum = circularViewSharpTurn.findViewById(R.id.tvNum);
        assertEquals("1次", textViewSharpTvNum.getText().toString());

        assertEquals(260.0, testActivity.mLineChart.getData().getDataSetByIndex(0).getEntryForIndex(0).getY(),0.0);
        assertEquals(30.0, testActivity.mLineChart.getData().getDataSetByIndex(1).getEntryForIndex(0).getY(),0.1);
        assertEquals(2, testActivity.mLineChart.getData().getDataSetByIndex(1).getEntryForIndex(0).getX(),0.0);
    }

    @Test
    public void testOnClickFromHistoryToHistoryDetail_moreDate() {

        Activity activity = Robolectric.setupActivity(Activity.class);
        //历史数据库文件
        DriveScoreHistoryDao driveScoreHistoryDao = new DriveScoreHistoryDao(activity);
        //向历史数据库插入数据
        insertDateToHistoryDB_moreDate(driveScoreHistoryDao);
        //历史详情数据库文件
        DriveScoreHistoryDetailDao driveScoreHistoryDetailDao = new DriveScoreHistoryDetailDao(activity);
        //向历史详情数据库插入数据
        insertDateToHistoryDetailDB_moreDate(driveScoreHistoryDetailDao);

        //启动历史Acitvity并模拟点击其中一条数据
        ActivityController<DriveScoreHistoryActivity> activityController = Robolectric.buildActivity(DriveScoreHistoryActivity.class);
        DriveScoreHistoryActivity driveScoreHistoryActivity = activityController.get();
        activityController.create().start().visible();
        RecyclerView currentRecyclerView = driveScoreHistoryActivity.findViewById(R.id.history_list);
        LinearLayoutManager layoutManager =  (LinearLayoutManager) currentRecyclerView.getLayoutManager();
        layoutManager.scrollToPosition(2);
        layoutManager.findViewByPosition(2).findViewById(R.id.llItemClick).performClick();
        //  currentRecyclerView.getChildAt(1).findViewById(R.id.llItemClick).performClick();
        ShadowActivity myActivityShadow = shadowOf(driveScoreHistoryActivity);
        Intent expectedIntent = new Intent(driveScoreHistoryActivity, DriveScoreHistoryDetailActivity.class);
        Intent actual = myActivityShadow.getNextStartedActivity();

        //验证是否启动了期望的Activity
        assertEquals(expectedIntent.getComponent(), actual.getComponent());

        //Intent模拟历史Activity跳转到历史详情Activity
        ActivityController<DriveScoreHistoryDetailActivity> testActivityController = Robolectric.buildActivity(DriveScoreHistoryDetailActivity.class, actual);
        DriveScoreHistoryDetailActivity testActivity = testActivityController.create().get();

        //验证历史详情Activity取得的数据是否正确
        TextView tvStartTime = testActivity.findViewById(R.id.tvTime);
        assertEquals("上车时间：2019.05.30   14:02", tvStartTime.getText().toString());
        TextView tvDriverScore = testActivity.findViewById(R.id.tvDriverScore);
        assertEquals("30", tvDriverScore.getText().toString());
        CircularView circularViewRapidAccelerate = testActivity.findViewById(R.id.circularViewRapidAccelerate);
        TextView textViewAccTvNum = circularViewRapidAccelerate.findViewById(R.id.tvNum);
        assertEquals("1次", textViewAccTvNum.getText().toString());
        CircularView circularViewRapidDeceleration = testActivity.findViewById(R.id.circularViewRapidDeceleration);
        TextView textViewDecTvNum = circularViewRapidDeceleration.findViewById(R.id.tvNum);
        assertEquals("2次", textViewDecTvNum.getText().toString());
        CircularView circularViewSharpTurn = testActivity.findViewById(R.id.circularViewSharpTurn);
        TextView textViewSharpTvNum = circularViewSharpTurn.findViewById(R.id.tvNum);
        assertEquals("1次", textViewSharpTvNum.getText().toString());

        assertEquals(260.0, testActivity.mLineChart.getData().getDataSetByIndex(0).getEntryForIndex(0).getY(),0.0);
        assertEquals(30, testActivity.mLineChart.getData().getDataSetByIndex(1).getEntryForIndex(0).getY(),0.1);
        assertEquals(2, testActivity.mLineChart.getData().getDataSetByIndex(1).getEntryForIndex(0).getX(),0.0);
    }

    @Test
    public void onViewClickedTest() {
        ActivityController<DriveScoreHistoryDetailActivity> detailActivityActivityController = Robolectric.buildActivity(DriveScoreHistoryDetailActivity.class).create().start();
        DriveScoreHistoryDetailActivity activity = detailActivityActivityController.get();
        BackButton textBackButton = activity.findViewById(R.id.ibBackButton);
        textBackButton.performClick();
        assertTrue(activity.isFinishing());
    }

    private void insertDateToHistoryDB(DriveScoreHistoryDao driveScoreHistoryDao) {
        DriveScoreHistoryEntity driveScoreHistoryEntity = new DriveScoreHistoryEntity();
        driveScoreHistoryEntity.travelTime = 1559542409L;
        driveScoreHistoryEntity.travelDist = 400000D;
        driveScoreHistoryEntity.startTime = 1559282769000L;
        driveScoreHistoryEntity.endTime = 1559942409L;
        driveScoreHistoryEntity.avgSpeed = 300.0;
        driveScoreHistoryEntity.avgFuel = 5.6;
        driveScoreHistoryEntity.score = 30.0;
        driveScoreHistoryEntity.turnNum = 1L;
        driveScoreHistoryEntity.decNum = 2L;
        driveScoreHistoryEntity.accNum = 1L;
        driveScoreHistoryEntity.id = 1;
        driveScoreHistoryDao.create(driveScoreHistoryEntity);
    }

    private void insertDateToHistoryDetailDB(DriveScoreHistoryDetailDao driveScoreHistoryDetailDao) {
        DriveScoreHistoryDetailEntity driveScoreHistoryDetailEntity = new DriveScoreHistoryDetailEntity();
        driveScoreHistoryDetailEntity.time = 1559282528000L;
        driveScoreHistoryDetailEntity.avgFuel = 50.0;
        driveScoreHistoryDetailEntity.avgSpeed = 300.0;
        driveScoreHistoryDetailEntity.historyId = 1;
        driveScoreHistoryDetailEntity.id = 1;
        driveScoreHistoryDetailDao.create(driveScoreHistoryDetailEntity);
    }

    private void insertDateToHistoryDB_moreDate(DriveScoreHistoryDao driveScoreHistoryDao) {
        DriveScoreHistoryEntity driveScoreHistoryEntity = new DriveScoreHistoryEntity();
        driveScoreHistoryEntity.travelTime = 7200000L;
        driveScoreHistoryEntity.travelDist = 400000D;
        driveScoreHistoryEntity.startTime = 1559196128000L; //2019/5/30 14:2:8
        driveScoreHistoryEntity.endTime = 1559203328000L; //2019/5/30 16:2:8
        driveScoreHistoryEntity.avgSpeed = 300.0;
        driveScoreHistoryEntity.avgFuel = 5.6;
        driveScoreHistoryEntity.score = 30.0;
        driveScoreHistoryEntity.turnNum = 1L;
        driveScoreHistoryEntity.decNum = 2L;
        driveScoreHistoryEntity.accNum = 1L;
        driveScoreHistoryEntity.id = 1;
        driveScoreHistoryDao.create(driveScoreHistoryEntity);

        DriveScoreHistoryEntity driveScoreHistoryEntity1 = new DriveScoreHistoryEntity();
        driveScoreHistoryEntity1.travelTime = 7200000L;
        driveScoreHistoryEntity1.travelDist = 300000D;
        driveScoreHistoryEntity1.startTime = 1559282769000L; //2019/5/31 14:6:9
        driveScoreHistoryEntity1.endTime = 1559289969000L; //2019/5/31 16:6:9
        driveScoreHistoryEntity1.avgSpeed = 300.0;
        driveScoreHistoryEntity1.avgFuel = 5.6;
        driveScoreHistoryEntity1.score = 80.0;
        driveScoreHistoryEntity1.turnNum = 2L;
        driveScoreHistoryEntity1.decNum = 2L;
        driveScoreHistoryEntity1.accNum = 2L;
        driveScoreHistoryEntity1.id = 2;
        driveScoreHistoryDao.create(driveScoreHistoryEntity1);

        DriveScoreHistoryEntity driveScoreHistoryEntity2 = new DriveScoreHistoryEntity();
        driveScoreHistoryEntity2.travelTime = 7200000L;
        driveScoreHistoryEntity2.travelDist = 300000D;
        driveScoreHistoryEntity2.startTime = 1560302207000L; //2019/6/12 9:16:47
        driveScoreHistoryEntity2.endTime = 1560302429000L; //2019/6/12 9:20:29
        driveScoreHistoryEntity2.avgSpeed = 300.0;
        driveScoreHistoryEntity2.avgFuel = 5.6;
        driveScoreHistoryEntity2.score = 80.0;
        driveScoreHistoryEntity2.turnNum = 3L;
        driveScoreHistoryEntity2.decNum = 1L;
        driveScoreHistoryEntity2.accNum = 2L;
        driveScoreHistoryEntity2.id = 3;
        driveScoreHistoryDao.create(driveScoreHistoryEntity2);
    }

    private void insertDateToHistoryDetailDB_moreDate(DriveScoreHistoryDetailDao driveScoreHistoryDetailDao) {
        DriveScoreHistoryDetailEntity driveScoreHistoryDetailEntity = new DriveScoreHistoryDetailEntity();
        driveScoreHistoryDetailEntity.time = 1559196128000L; //2019/5/30 14:2:8
        driveScoreHistoryDetailEntity.avgFuel = 50.0;
        driveScoreHistoryDetailEntity.avgSpeed = 300.0;
        driveScoreHistoryDetailEntity.historyId = 1;
        driveScoreHistoryDetailEntity.id = 1;
        driveScoreHistoryDetailDao.create(driveScoreHistoryDetailEntity);

        DriveScoreHistoryDetailEntity driveScoreHistoryDetailEntity1 = new DriveScoreHistoryDetailEntity();
        driveScoreHistoryDetailEntity1.time = 1559282769000L; //2019/5/31 14:6:9
        driveScoreHistoryDetailEntity1.avgFuel = 5.6;
        driveScoreHistoryDetailEntity1.avgSpeed = 120.0;
        driveScoreHistoryDetailEntity1.historyId = 2;
        driveScoreHistoryDetailEntity1.id = 2;
        driveScoreHistoryDetailDao.create(driveScoreHistoryDetailEntity1);

        DriveScoreHistoryDetailEntity driveScoreHistoryDetailEntity2 = new DriveScoreHistoryDetailEntity();
        driveScoreHistoryDetailEntity2.time = 1560302207000L; //2019/6/12 9:16:47
        driveScoreHistoryDetailEntity2.avgFuel = 6.0;
        driveScoreHistoryDetailEntity2.avgSpeed = 110.0;
        driveScoreHistoryDetailEntity2.historyId = 3;
        driveScoreHistoryDetailEntity2.id = 3;
        driveScoreHistoryDetailDao.create(driveScoreHistoryDetailEntity2);
    }

}