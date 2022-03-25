package com.xiaoma.shop.business.ui.theme;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks;
import android.util.Log;
import android.view.View;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.utils.GsonHelper;

/**
 * Created by LKF on 2019-6-28 0028.
 */
public class TrialEndActivity extends BaseActivity {
    private static final String TAG = "TrialEndActivity";
    private FragmentLifecycleCallbacks mCallbacks;
    private Handler mHandler = new Handler();
    private Runnable mFinishAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        mFinishAction = new Runnable() {
            @Override
            public void run() {
                if (!isFinishing())
                    finish();
            }
        };
        String skinJson = getIntent().getStringExtra("skin");
        SkinVersionsBean skin = GsonHelper.fromJson(skinJson, SkinVersionsBean.class);
        if (skin == null) {
            finish();
            return;
        }
        TrialUI.showTrialEnd(this, skin);
        mCallbacks = new FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                mHandler.removeCallbacks(mFinishAction);
            }

            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                Log.i(TAG, String.format("onFragmentViewDestroyed: fmt: %s", f.getClass().getName()));
                mHandler.postDelayed(mFinishAction, 300);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(mCallbacks, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(mCallbacks);
    }
}
