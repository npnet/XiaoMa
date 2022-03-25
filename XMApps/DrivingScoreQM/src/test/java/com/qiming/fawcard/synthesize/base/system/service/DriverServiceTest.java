package com.qiming.fawcard.synthesize.base.system.service;

import android.app.Application;
import android.content.Intent;

import com.qiming.fawcard.synthesize.BaseTestCase;
import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.CommonInfoHolder;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreDialogShieldActivity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public class DriverServiceTest extends BaseTestCase {

    private DriverService mDriverService;
    private Long lastTime = 0L;             // 上次油耗车速取得时间（测试用）

    @Before
    public void setUp() {
        super.setUp();
        super.setUpService();
        mDriverService = driverServiceController.get();
        super.setUpHomeActivity();
        super.bindService();
    }

    @Test
    public void updateToken() {
        String tolenActure = "token123";
        mDriverService.updateToken(tolenActure);
        String tolenExpect = CommonInfoHolder.getInstance().getToken();
        assertEquals(tolenActure,tolenExpect);
        assertEquals(tolenActure, tolenExpect);
    }

    @Test
    public void updateDriveInfo() {
        assertEquals(0,mDriverService.getCache().size());
        DriveScoreHistoryDetailEntity data = getFakeDriveInfo();
        mDriverService.updateDriveInfo(data);
        assertEquals(1,mDriverService.getCache().size());
        assertSame(data,mDriverService.getCache().get(0));
    }

    @Test
    public void updateDriveScore_dataInvalid() {
        DriveScoreHistoryDetailEntity dataDetail = getFakeDriveInfo();
        mDriverService.updateDriveInfo(dataDetail);
        assertEquals(1,mDriverService.getCache().size());

        DriveScoreHistoryEntity data = new DriveScoreHistoryEntity();
        data.travelDist = 1.0;
        mDriverService.updateDriveScore(data);

        // 验证保存到数据库的mDriveScorePresenter.saveDriveScore函数没有被调用过
        verify(driveScorePresenter, never()).updateOrDeleteTRavelRecord(data, mDriverService.getCache());

        // 验证没显示驾驶评分对话框(没进行页面跳转)
        Intent nextIntent = ShadowApplication.getInstance().getNextStartedActivity();
        // 校验Intent的正确性
        assertNull(nextIntent);

        assertEquals(0,mDriverService.getCache().size());
    }

    @Test
    public void updateDriveScore_dataValid() {
        DriveScoreHistoryDetailEntity dataDetail = getFakeDriveInfo();
        mDriverService.updateDriveInfo(dataDetail);
        assertEquals(1,mDriverService.getCache().size());

        DriveScoreHistoryEntity data = getFakeDriveScore();
        mDriverService.updateDriveScore(data);

        // 验证保存到数据库的mDriveScorePresenter.saveDriveScore函数只被调用过一次
        verify(driveScorePresenter, only()).updateOrDeleteTRavelRecord(data, mDriverService.getCache());

        // 验证页面跳转
        Intent nextIntent = ShadowApplication.getInstance().getNextStartedActivity();
        // 校验Intent的正确性
        assertEquals(nextIntent.getComponent().getClassName(), DriveScoreDialogShieldActivity.class.getName());

        assertEquals(0,mDriverService.getCache().size());
    }

    @Test
    public void showDriverScoreDialog_100() {
        Intent nextIntent = getDialogShieldIntent(100.00);
        assertEquals(nextIntent.getIntExtra("curScore", 0), 100);
        assertEquals(nextIntent.getStringExtra("driverCommon"), getApplication().getString(R.string.pop_score_100));
        assertTrue(nextIntent.getBooleanExtra("isForeground", false));

        // 验证上传车币到服务器的mDriveScorePresenter.reportDriveScore函数被调用
        verify(driveScorePresenter, only()).reportDriveScore();
    }

    @Test
    public void showDriverScoreDialog_90() {
        Intent nextIntent = getDialogShieldIntent(90.00);
        assertEquals(nextIntent.getIntExtra("curScore", 0), 90);
        assertEquals(nextIntent.getStringExtra("driverCommon"), getApplication().getString(R.string.pop_score_99));
        assertTrue(nextIntent.getBooleanExtra("isForeground", false));

        // 验证上传车币到服务器的mDriveScorePresenter.reportDriveScore函数没被调用
        verify(driveScorePresenter, never()).reportDriveScore();
    }

    @Test
    public void showDriverScoreDialog_80() {
        Intent nextIntent = getDialogShieldIntent(80.00);
        assertEquals(nextIntent.getIntExtra("curScore", 0), 80);
        assertEquals(nextIntent.getStringExtra("driverCommon"), getApplication().getString(R.string.pop_score_89));
        assertTrue(nextIntent.getBooleanExtra("isForeground", false));

        // 验证上传车币到服务器的mDriveScorePresenter.reportDriveScore函数没被调用
        verify(driveScorePresenter, never()).reportDriveScore();
    }

    @Test
    public void showDriverScoreDialog_70() {
        Intent nextIntent = getDialogShieldIntent(70.00);
        assertEquals(nextIntent.getIntExtra("curScore", 0), 70);
        assertEquals(nextIntent.getStringExtra("driverCommon"), getApplication().getString(R.string.pop_score_79));
        assertTrue(nextIntent.getBooleanExtra("isForeground", false));

        // 验证上传车币到服务器的mDriveScorePresenter.reportDriveScore函数没被调用
        verify(driveScorePresenter, never()).reportDriveScore();
    }

    @Test
    public void showDriverScoreDialog_60() {
        Intent nextIntent = getDialogShieldIntent(60.00);
        assertEquals(nextIntent.getIntExtra("curScore", 0), 60);
        assertEquals(nextIntent.getStringExtra("driverCommon"), getApplication().getString(R.string.pop_score_69));
        assertTrue(nextIntent.getBooleanExtra("isForeground", false));

        // 验证上传车币到服务器的mDriveScorePresenter.reportDriveScore函数没被调用
        verify(driveScorePresenter, never()).reportDriveScore();
    }

    @Test
    public void showDriverScoreDialog_59() {
        Intent nextIntent = getDialogShieldIntent(59.80);
        assertEquals(nextIntent.getIntExtra("curScore", 0), 59);
        assertEquals(nextIntent.getStringExtra("driverCommon"), getApplication().getString(R.string.pop_score_59));
        assertTrue(nextIntent.getBooleanExtra("isForeground", false));

        // 验证上传车币到服务器的mDriveScorePresenter.reportDriveScore函数没被调用
        verify(driveScorePresenter, never()).reportDriveScore();
    }

    private Intent getDialogShieldIntent(Double score) {
        mDriverService.showDriverScoreDialog(/*score*/);
        // 验证页面跳转
        Intent nextIntent = ShadowApplication.getInstance().getNextStartedActivity();
        // 校验Intent的正确性
        assertEquals(nextIntent.getComponent().getClassName(), DriveScoreDialogShieldActivity.class.getName());
        return nextIntent;
    }

    // 测试用：生成一个max-min之间的随机数
    public static int randomRange(int min, int max) {
        Random rand = new Random();
        int result = rand.nextInt(max - min + 1) + min;
        return result;
    }

    // 测试用：模拟生成一条驾驶记录数据
    public DriveScoreHistoryDetailEntity getFakeDriveInfo() {
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
    public DriveScoreHistoryEntity getFakeDriveScore() {
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

    public Application getApplication() {
        return RuntimeEnvironment.application;
    }

}