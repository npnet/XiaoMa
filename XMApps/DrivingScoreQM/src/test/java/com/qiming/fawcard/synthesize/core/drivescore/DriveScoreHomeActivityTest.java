package com.qiming.fawcard.synthesize.core.drivescore;


import android.app.Dialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.BaseTestCase;
import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.DriveScoreLineChartManager;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.system.callback.DriverInfoCallback;
import com.qiming.fawcard.synthesize.base.system.service.DriverService;
import com.qiming.fawcard.synthesize.base.widget.BackButton;
import com.qiming.fawcard.synthesize.base.widget.CircularView;
import com.qiming.fawcard.synthesize.base.widget.HomeButton;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowDialog;

import static com.qiming.fawcard.synthesize.base.system.service.DriverServiceTest.randomRange;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

public class DriveScoreHomeActivityTest extends BaseTestCase {

    Long lastTime = 0L;             // 上次油耗车速取得时间（测试用）

    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    public void onDriverInfoUpdate() {
        init();
        Dialog latestDialog = ShadowDialog.getLatestDialog();
        assertNotNull(latestDialog);
        assertFalse(latestDialog.isShowing());
        DriveScoreHistoryDetailEntity data = new DriveScoreHistoryDetailEntity();
        driverServiceController.get().updateDriveInfo(data);
        assertFalse(latestDialog.isShowing());

        //Toast toast = ShadowToast.getLatestToast();
        // 判断Toast已经弹出
        //assertNotNull(toast);
        // 获取Shadow类进行验证
//        ShadowToast shadowToast = shadowOf(toast);
//        assertEquals(Toast.LENGTH_LONG, shadowToast.getDuration());
//        assertEquals(getApplication().getString(R.string.loading_timeout), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void onDriveTimeUpdate(){
        init();
        Whitebox.setInternalState(driverServiceController.get(),"mStartTimeCount", 2L);
        DriveScoreHistoryDetailEntity data = new DriveScoreHistoryDetailEntity();
        driverServiceController.get().updateDriveInfo(data);

        //验证updateDriveTime()行驶时间更新是否正确
        TextView actualTextView = homeActivityController.get().findViewById(R.id.tvTime);
        long time = 2L + QMConstant.TIMER_MINUTE;
        String expectedTextView = getApplication().getString(R.string.drived) + time + getApplication().getString(R.string.drive_unit);
        assertEquals(expectedTextView,actualTextView.getText().toString());

    }

    @Test
    public void onDriverInfosUpdate(){
        init();
        //清除缓存
        driverServiceController.get().getCache().clear();
        //加入三条缓存数据
        driverServiceController.get().getCache().add(getFakeDriveInfo());
        driverServiceController.get().getCache().add(getFakeDriveInfo());
        driverServiceController.get().getCache().add(getFakeDriveInfo());

        DriveScoreLineChartManager mDriveScoreLineChartManager = Mockito.mock(DriveScoreLineChartManager.class);
        Whitebox.setInternalState(homeActivityController.get(), "mDriveScoreLineChartManager", mDriveScoreLineChartManager);


        DriverInfoCallback mCallback = driverServiceController.get().getCallback();
        driverServiceController.get().setDriverInfoCallback(mCallback);

        //断言 updateLineChartList
        verify(mDriveScoreLineChartManager,times(3)).addEntry(anyInt(),anyDouble(),anyLong());


    }

    @Test
    public void onDriveScoreUpdate(){
        init();
        DriveScoreHistoryEntity data = getFakeDriveScore();
        driverServiceController.get().updateDriveScore(data );

        //验证updateDriveScore()评分更新是否正确
        TextView actualTextView = homeActivityController.get().findViewById(R.id.tvDriverScore);
        String expectedTextView = Math.round(data.score) + "";
        assertEquals(expectedTextView,actualTextView.getText().toString());

    }

    @Test
    public void onDriveStart(){
        init();
        Intent intent = new Intent(homeActivityController.get(), DriverService.class);
        intent.putExtra(QMConstant.DRIVER_SERVICE_KEY, QMConstant.DRIVER_SERVICE_START);
        driverServiceController.get().onStartCommand(intent,2,1);

        //验证clearPageData()中initPageData()是否执行正确
        TextView actualTextView = homeActivityController.get().findViewById(R.id.tvTime);
        String expectedTextView = getApplication().getString(R.string.travel_time);
        assertEquals(expectedTextView,actualTextView.getText().toString());
    }

    @Test
    public void onRequestFailed(){
        init();
        //发送失败消息“网络连接不可用”
        driverServiceController.get().onRequestFailed(QMConstant.RequestFailMessage.NETWORK_DISCONNECT);
        //测试是否显示Dialog
        Dialog latestDialog = ShadowDialog.getLatestDialog();
        assertNotNull(latestDialog);
        assertTrue(latestDialog.isShowing());
        //测试Dialog内容是否正确
        TextView actualTextView = latestDialog.findViewById(R.id.tv_loading_fail);
        String expectedTextView = getApplication().getString(R.string.loading_fail);
        assertEquals(expectedTextView,actualTextView.getText().toString());
    }

    @Test
    public void HomeActivityBackButtonClickTest(){
        init();
        ShadowActivity myActivityShadow = shadowOf(homeActivityController.get());
        assertEquals(false, myActivityShadow.isFinishing());

        BackButton backButton = homeActivityController.get().findViewById(R.id.ibBackButton);
        backButton.performClick();

        assertEquals(true, myActivityShadow.isFinishing());
    }

    @Test
    public void HomeActivityHomeButtonClickTest(){
        init();
        HomeButton homeButton = homeActivityController.get().findViewById(R.id.ibHomeButton);
        homeButton.performClick();

        ShadowActivity myActivityShadow = shadowOf(homeActivityController.get());
        Intent actual = myActivityShadow.getNextStartedActivity();
        Intent expectedIntent = new Intent(Intent.ACTION_MAIN);
        expectedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        expectedIntent.addCategory(Intent.CATEGORY_HOME);

        //验证是否启动了期望的Activity
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    @Test
    public void HomeActivityHistoryButtonClickTest(){
        init();
        Button historyButton = homeActivityController.get().findViewById(R.id.btn_history);
        historyButton.performClick();

        ShadowActivity myActivityShadow = shadowOf(homeActivityController.get());

        Intent actual = myActivityShadow.getNextStartedActivity();
        Intent expectedIntent = new Intent(homeActivityController.get(), DriveScoreHistoryActivity.class);

        //验证是否启动了期望的Activity
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    @Test
    public void AppStartNoDriveStartTest(){
        setUpService();
        setUpHomeActivity();
        bindService();

        //验证加载界面是否弹出
        Dialog latestDialog = ShadowDialog.getLatestDialog();
        assertNotNull(latestDialog);
        //验证加载页面是否消失
        assertFalse(latestDialog.isShowing());

        //验证页面显示是否正确
        //初始欢迎语
        TextView actualTextView = homeActivityController.get().findViewById(R.id.tvTime);
        String expectedTextView = getApplication().getString(R.string.travel_time);
        assertEquals(expectedTextView,actualTextView.getText().toString());

        AppStartDriveInfoTest();
    }

    @Test
    public void AppStartAndDriveStartTest(){
        init();

        //验证加载界面是否弹出
        Dialog latestDialog = ShadowDialog.getLatestDialog();
        assertNotNull(latestDialog);
        //验证加载页面是否消失
        assertFalse(latestDialog.isShowing());

        //验证页面显示是否正确

        //验证updateDriveTime()行驶时间更新是否正确
        TextView actualTextView = homeActivityController.get().findViewById(R.id.tvTime);
        long time = 0L ;
        String expectedTextView = getApplication().getString(R.string.drived) + time + getApplication().getString(R.string.drive_unit);
        assertEquals(expectedTextView,actualTextView.getText().toString());
        AppStartDriveInfoTest();
    }

    private void AppStartDriveInfoTest(){
        //评分
        TextView actualScore = homeActivityController.get().findViewById(R.id.tvDriverScore);
        String expectedScore = getApplication().getString(R.string.default_score);
        assertEquals(expectedScore,actualScore.getText().toString());
        //急加速
        CircularView actualRapidAccelerate = homeActivityController.get().findViewById(R.id.circularViewRapidAccelerate);
        TextView textRapidAccTvNum = actualRapidAccelerate.findViewById(R.id.tvNum);
        String expectedRapidAccelerate = getApplication().getString(R.string.default_num);
        assertEquals(expectedRapidAccelerate,textRapidAccTvNum.getText().toString());
        //急减速
        CircularView actualRapidDeceleration = homeActivityController.get().findViewById(R.id.circularViewRapidDeceleration);
        TextView textRapidDecTvNum = actualRapidDeceleration.findViewById(R.id.tvNum);
        String expectedRapidDeceleration = getApplication().getString(R.string.default_num);
        assertEquals(expectedRapidDeceleration,textRapidDecTvNum.getText().toString());
        //急转弯
        CircularView actualSharpTurn = homeActivityController.get().findViewById(R.id.circularViewSharpTurn);
        TextView textSharpTvNum = actualSharpTurn.findViewById(R.id.tvNum);
        String expectedSharpTurn = getApplication().getString(R.string.default_num);
        assertEquals(expectedSharpTurn,textSharpTvNum.getText().toString());

        //平均车速标题显示
        TextView actualAvgSpeedTextView = homeActivityController.get().findViewById(R.id.tvAvgSpeed);
        String expectedAvgSpeedTextView = getApplication().getString(R.string.avg_speed);
        assertEquals(expectedAvgSpeedTextView,actualAvgSpeedTextView.getText().toString());

        //平均油耗标题显示
        TextView actualAvgFuelConsumerTextView = homeActivityController.get().findViewById(R.id.tvAvgFuelConsumer);
        String expectedAvgFuelConsumerTextView = getApplication().getString(R.string.avg_fuel_consumer);
        assertEquals(expectedAvgFuelConsumerTextView,actualAvgFuelConsumerTextView.getText().toString());
    }
    private void init() {
        setUpService();
        ignition();
        setUpHomeActivity();
        bindService();
    }

    // 测试用：模拟生成一条驾驶记录数据
    private DriveScoreHistoryDetailEntity getFakeDriveInfo() {
        DriveScoreHistoryDetailEntity data = new DriveScoreHistoryDetailEntity();
        if (lastTime == 0L) {
            data.time = System.currentTimeMillis();              // 第一次调用时使用系统时间
        } else {
            data.time = lastTime + 5 * 60 * 1000;                // 取得时间每次增加5分钟
        }
        data.avgSpeed = Double.valueOf(randomRange(0, 150));     // 车速在0-150之间
        data.avgFuel = Double.valueOf(randomRange(5, 20));       // 油耗在5-20之间

        // 更新上次取得时间
        lastTime = data.time;

        return data;
    }

    // 测试用：模拟生成一条驾驶详情数据
    private DriveScoreHistoryEntity getFakeDriveScore() {
        DriveScoreHistoryEntity data = new DriveScoreHistoryEntity();
        data.travelTime = Long.valueOf(randomRange(0, 300) * 60);   // 行驶时间在0-300分钟
        data.endTime = System.currentTimeMillis();                  // 结束时间是当前时间
        data.startTime = data.endTime - data.travelTime;            // 开始时间 = 结束时间 - 行驶时间
        data.travelDist = 10.0;                                     // 行驶距离固定为10公里
        data.accNum = Long.valueOf(randomRange(0, 3));              // 急加速次数在0-3次
        data.decNum = Long.valueOf(randomRange(0, 3));              // 急减速次数在0-3次
        data.turnNum = Long.valueOf(randomRange(0, 3));             // 急转弯次数在0-3次
        data.score = Double.valueOf(100 - (data.accNum + data.decNum + data.turnNum)); // 计算得分
        //data.score = 100.0;
        data.avgSpeed = Double.valueOf(randomRange(0, 150));        // 车速在0-150之间
        data.avgFuel = Double.valueOf(randomRange(5, 20));           // 油耗在5-20之间

        return data;
    }


}