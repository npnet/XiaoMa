package com.qiming.fawcard.synthesize;

import android.content.Intent;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreDialogShieldActivity;
import com.xiaoma.utils.log.KLog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StalledBehaviorTest extends BaseTestCase {

    @Before
    public void setUp(){
        super.setUp();
        mockDrivedApi = Mockito.spy(DrivedApiMock.class);
    }

    private void superInit() {
        setUpService();
        ignition();
        setUpHomeActivity();
        bindService();
        stalled();
    }

    // 熄火测试
    @Test
    public void StalledTest_UpLoadScore() {
        setDriveInfo(1L,1L,2L,100.0, 2.1);
        superInit();
        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Intent act = shadowApplication.getNextStartedActivity();
        assertEquals(DriveScoreDialogShieldActivity.class.getName(), act.getComponent().getClassName());
        ActivityController<DriveScoreDialogShieldActivity> controller = Robolectric.buildActivity(DriveScoreDialogShieldActivity.class, act).create().resume().start();

        TextView textTvShield2 = controller.get().findViewById(R.id.tv_shield2);
        TextView textTvShield3 = controller.get().findViewById(R.id.tv_shield3);
        KLog.d("test", textTvShield2.getText().toString());
        assertTrue(textTvShield2.getText().toString().equals(getApplication().getString(R.string.pop_score_100)));
        assertTrue(textTvShield3.getText().toString().equals(getApplication().getString(R.string.pop_score)));

        // 上传车币
        verify(mockDrivedApi, times(1)).uploadScore(anyString(), anyString(), ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void StalledTest_NotUpLoadScore() {
        setDriveInfo(1L,1L,2L,80.0, 2.1);
        superInit();
        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Intent act = shadowApplication.getNextStartedActivity();
        assertEquals(DriveScoreDialogShieldActivity.class.getName(), act.getComponent().getClassName());
        ActivityController<DriveScoreDialogShieldActivity> Acontroller = Robolectric.buildActivity(DriveScoreDialogShieldActivity.class, act).create().resume().start();

        TextView textTvShield2 = Acontroller.get().findViewById(R.id.tv_shield2);
        TextView textTvShield3 = Acontroller.get().findViewById(R.id.tv_shield3);
        KLog.d("test", textTvShield2.getText().toString());
        assertTrue(textTvShield2.getText().toString().equals(getApplication().getString(R.string.pop_score_89)));
        assertTrue(textTvShield3.getText().toString().equals(80 + getApplication().getString(R.string.history_score)));

        // 不上传车币
        verify(mockDrivedApi, times(0)).uploadScore(anyString(), anyString(), ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void StalledTest_DrivedDistanceBelow2km() {
        setDriveInfo(1L,1L,2L,80.0, 1.0);
        superInit();
        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Intent act = shadowApplication.getNextStartedActivity();

        // 行驶不满足两公里，不进行保存和提醒（DriveScoreDialogShieldActivity不弹出、cache清空）
        assertEquals(null, act);
        assertEquals(0, driverServiceController.get().getCache().size());
    }
}
