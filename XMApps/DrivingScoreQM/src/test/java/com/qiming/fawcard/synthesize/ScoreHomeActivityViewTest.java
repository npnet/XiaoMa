package com.qiming.fawcard.synthesize;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.base.widget.CircularView;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreDialogShieldActivity;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHistoryActivity;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHistoryDetailActivity;

import junit.framework.TestCase;

import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ScoreHomeActivityViewTest extends BaseTestCase {

    //测试上次驾驶评分显示，存在上次驾驶数据，启动APP，数据加载成功
    //弹出驾驶评分Telop，Telop消去
    //左上角显示"已驾驶：0分钟"
    //显示“本次驾驶评分 70”
    //显示4次急加速
    //显示5次急减速
    //显示6次急转弯
    @Test
    public void lastTravelViewTest(){
        super.setDriveInfo(4L,5L,6L,70.0);
        super.setUpService();
        super.setUpHomeActivity();
        super.bindService();
        super.ignition();
        super.stalled();

        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Intent act = shadowApplication.getNextStartedActivity();
        assertEquals(DriveScoreDialogShieldActivity.class.getName(), act.getComponent().getClassName());
        ActivityController<DriveScoreDialogShieldActivity> controller = Robolectric.buildActivity(DriveScoreDialogShieldActivity.class, act).create().resume();
        controller.get().findViewById(R.id.ibBackButton).performClick();
        assertTrue(controller.get().isFinishing());

        TextView tvScore = homeActivityController.get().findViewById(R.id.tvDriverScore);
        assertEquals("70",tvScore.getText().toString());

        CircularView tvRapidAccelerate = homeActivityController.get().findViewById(R.id.circularViewRapidAccelerate);
        TextView textViewAcc = tvRapidAccelerate.findViewById(R.id.tvNum);
        assertEquals(4+ homeActivityController.get().getResources().getString(R.string.count),textViewAcc.getText().toString());

        CircularView tvRapidDeceleration = homeActivityController.get().findViewById(R.id.circularViewRapidDeceleration);
        TextView textViewDec = tvRapidDeceleration.findViewById(R.id.tvNum);
        assertEquals(5+ homeActivityController.get().getResources().getString(R.string.count),textViewDec.getText().toString());

        CircularView tvSharpTurn = homeActivityController.get().findViewById(R.id.circularViewSharpTurn);
        TextView textViewSharp = tvSharpTurn.findViewById(R.id.tvNum);
        assertEquals(6 + homeActivityController.get().getResources().getString(R.string.count), textViewSharp.getText().toString());

        TextView tvTime = homeActivityController.get().findViewById(R.id.tvTime);
        assertEquals(homeActivityController.get().getResources().getString(R.string.drived)+"0"+homeActivityController.get().getResources().getString(R.string.drive_unit),tvTime.getText().toString());

    }

    //测试上次驾驶评分在历史页面中是否显示
    @Test
    public void historyList_Test(){
        super.setDriveInfo(4L,5L,6L,96.0);
        super.setUpService();
        super.setUpHomeActivity();
        super.bindService();
        super.ignition();
        super.stalled();

        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Intent act = shadowApplication.getNextStartedActivity();
        assertEquals(DriveScoreDialogShieldActivity.class.getName(), act.getComponent().getClassName());

        homeActivityController.get().findViewById(R.id.btn_history).performClick();
        act = shadowApplication.getNextStartedActivity();
        assertEquals(DriveScoreHistoryActivity.class.getName(), act.getComponent().getClassName());

        DriveScoreHistoryActivity historyActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        DriveScoreHistoryDetailActivity testActivity = jumpToHisDetailActivity(historyActivity);

        CircularView tvRapidAccelerate =  testActivity.findViewById(R.id.circularViewRapidAccelerate);
        TextView textViewAcc = tvRapidAccelerate.findViewById(R.id.tvNum);
        assertEquals(4+ testActivity.getResources().getString(R.string.count),textViewAcc.getText().toString());

        CircularView tvRapidDeceleration =  testActivity.findViewById(R.id.circularViewRapidDeceleration);
        TextView textViewDec = tvRapidDeceleration.findViewById(R.id.tvNum);
        assertEquals(5+ testActivity.getResources().getString(R.string.count),textViewDec.getText().toString());

        CircularView tvSharpTurn =  testActivity.findViewById(R.id.circularViewSharpTurn);
        TextView textViewSharp = tvSharpTurn.findViewById(R.id.tvNum);
        assertEquals(6 + testActivity.getResources().getString(R.string.count), textViewSharp.getText().toString());

        RecyclerView rlvHistoryList = historyActivity.findViewById(R.id.history_list);
        rlvHistoryList.scrollToPosition(1);
        RecyclerView.ViewHolder holder = rlvHistoryList.findViewHolderForAdapterPosition(1);
        assertNull(holder);
    }

    //测试熄火再点火上次驾驶评分在历史页面中是否显示
    @Test
    public void historyList_Test1(){
        super.setDriveInfo(3L,4L,5L,96.0);
        super.setUpService();
        super.setUpHomeActivity();
        super.bindService();
        super.ignition();
        super.stalled();
        super.ignition();

        homeActivityController.get().findViewById(R.id.btn_history).performClick();
        DriveScoreHistoryActivity historyActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        DriveScoreHistoryDetailActivity testActivity = jumpToHisDetailActivity(historyActivity);

        CircularView tvRapidAccelerate =  testActivity.findViewById(R.id.circularViewRapidAccelerate);
        TextView textViewAcc = tvRapidAccelerate.findViewById(R.id.tvNum);
        assertEquals(3+ testActivity.getResources().getString(R.string.count),textViewAcc.getText().toString());

        CircularView tvRapidDeceleration =  testActivity.findViewById(R.id.circularViewRapidDeceleration);
        TextView textViewDec = tvRapidDeceleration.findViewById(R.id.tvNum);
        assertEquals(4+ testActivity.getResources().getString(R.string.count),textViewDec.getText().toString());

        CircularView tvSharpTurn =  testActivity.findViewById(R.id.circularViewSharpTurn);
        TextView textViewSharp = tvSharpTurn.findViewById(R.id.tvNum);
        assertEquals(5 + testActivity.getResources().getString(R.string.count), textViewSharp.getText().toString());

        RecyclerView rlvHistoryList = historyActivity.findViewById(R.id.history_list);
        rlvHistoryList.scrollToPosition(1);
        RecyclerView.ViewHolder holder = rlvHistoryList.findViewHolderForAdapterPosition(1);
        assertNull(holder);
    }

    private DriveScoreHistoryDetailActivity jumpToHisDetailActivity(DriveScoreHistoryActivity historyActivity){
        RecyclerView rlvHistoryList = historyActivity.findViewById(R.id.history_list);
        LinearLayoutManager layoutManager =  (LinearLayoutManager) rlvHistoryList.getLayoutManager();
        layoutManager.findViewByPosition(0).findViewById(R.id.llItemClick).performClick();

        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Intent actual = shadowApplication.getNextStartedActivity();
        //验证是否启动了期望的Activity
        TestCase.assertEquals(DriveScoreHistoryDetailActivity.class.getName(), actual.getComponent().getClassName());

        //Intent模拟历史Activity跳转到历史详情Activity
        ActivityController<DriveScoreHistoryDetailActivity> testActivityController = Robolectric.buildActivity(DriveScoreHistoryDetailActivity.class, actual);
        DriveScoreHistoryDetailActivity testActivity = testActivityController.create().get();
        return testActivity;
    }

    //行驶小于2Km测试上次驾驶评分在历史页面中是否显示
    @Test
    public void historyList_Test2(){
        super.setDriveInfo(4L,5L,6L,96.0, 1.0);
        super.setUpService();
        super.ignition();
        super.stalled();
        super.ignition();
        super.setUpHomeActivity();
        super.bindService();
        homeActivityController.get().findViewById(R.id.btn_history).performClick();

        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Intent act = shadowApplication.getNextStartedActivity();
        assertEquals(DriveScoreHistoryActivity.class.getName(), act.getComponent().getClassName());

        DriveScoreHistoryActivity historyActivity = Robolectric.setupActivity(DriveScoreHistoryActivity.class);
        RecyclerView rlvHistoryList = historyActivity.findViewById(R.id.history_list);
        RecyclerView.ViewHolder holder = rlvHistoryList.findViewHolderForAdapterPosition(0);
        assertNull(holder);
    }
}
