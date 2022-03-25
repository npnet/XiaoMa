package com.qiming.fawcard.synthesize.core.drivescore;

import android.content.Intent;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.widget.BackButton;
import com.qiming.fawcard.synthesize.base.widget.HomeButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class DriveScoreDialogShieldActivityTest {

    private DriveScoreDialogShieldActivity mShieldActivity;
    private final int mCurScore = 100;
    private final String mDriverCommon = RuntimeEnvironment.application.getString(R.string.pop_score_100);

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;
        createAndStartShieldActivity();
    }

    private void createAndStartShieldActivity() {
        Intent intent = new Intent();
        intent.putExtra("curScore", mCurScore);
        intent.putExtra("driverCommon", mDriverCommon);
        ActivityController<DriveScoreDialogShieldActivity> activityController = Robolectric.buildActivity(DriveScoreDialogShieldActivity.class, intent);
        mShieldActivity = activityController.create().start().visible().get();
    }

    //驾驶评分界面,测试评分显示
    @Test
    public void ShieldActivityShowTest(){

        TextView tvScore = mShieldActivity.findViewById(R.id.tv_shield3);
        assertEquals(mCurScore + "分", tvScore.getText().toString());
        TextView tvDriverCommon = mShieldActivity.findViewById(R.id.tv_shield2);
        assertEquals(mDriverCommon, tvDriverCommon.getText().toString());
    }


    //显示驾驶评分界面，验证点击Back按钮处理逻辑
    @Test
    public void ShieldActivityBackButtonClickTest(){
        BackButton backButton = mShieldActivity.findViewById(R.id.ibBackButton);
        backButton.performClick();

        ShadowActivity myActivityShadow = shadowOf(mShieldActivity);

        assertTrue(myActivityShadow.isFinishing());
    }


    //显示驾驶评分界面，验证点击Home按钮处理逻辑
    @Test
    public void ShieldActivityHomeButtonClickTest(){
        HomeButton homeButton = mShieldActivity.findViewById(R.id.ibHomeButton);
        homeButton.performClick();

        ShadowActivity myActivityShadow = shadowOf(mShieldActivity);

        Intent actual = myActivityShadow.getNextStartedActivity();

        Intent expectedIntent = new Intent(Intent.ACTION_MAIN);
        expectedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        expectedIntent.addCategory(Intent.CATEGORY_HOME);

        //验证是否启动了期望的Activity
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }
}
