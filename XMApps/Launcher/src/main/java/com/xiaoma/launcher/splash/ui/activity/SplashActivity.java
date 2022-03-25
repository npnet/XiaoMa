package com.xiaoma.launcher.splash.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xiaoma.ad.AdManager;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.CarServiceConnManager;
import com.xiaoma.carlib.manager.CarServiceListener;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.main.ui.MainActivity;
import com.xiaoma.launcher.splash.ui.fragment.WelcomeSplashFragment;
import com.xiaoma.login.business.ui.CarInitActivity;
import com.xiaoma.login.business.ui.CarInitProActivity;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.sdk.CarKeyFactory;
import com.xiaoma.login.sdk.FaceFactory;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

/**
 * Created by Thomas on 2019/2/20 0020
 * 启动屏
 */
@PageDescComponent(EventConstants.PageDescribe.SplashActivityPagePathDesc)
public class SplashActivity extends BaseActivity {
    /**
     * 启动进程后是否登录成功的flag
     */
    private static boolean sIsLoginOnProcess = false;

    private static final int ACTION_LOGIN = 1010;
    private boolean hasLogin;
    private CarServiceListener mCarServerListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.splash_activity);

        //如果是切换帐号回来直接进入欢迎页
        boolean switchAccount = getIntent().getBooleanExtra(LoginConstants.IntentKey.SWITCH_ACCOUNT, false);
        if (switchAccount) {
            initFragment();
            return;
        }

        //如果是注销帐号回来重走流程
        boolean cancellation = getIntent().getBooleanExtra(LoginConstants.IntentKey.CANCELLATION, false);
        if (!cancellation) {
            // 如果已经登录过,直接跳进首页,不要重复执行登录操作
            if (sIsLoginOnProcess) {
                startMain();
                return;
            }
        }
        showProgressDialog(R.string.base_loading);
        SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
            @Override
            public void doWork(Object lastResult) {
                if (isFinishing())
                    return;
                //重置登录模式
                LoginTypeManager.getInstance().resetLoginType();
                CarServiceConnManager.getInstance(SplashActivity.this).addCallBack(mCarServerListener = new CarServiceListener() {
                    @Override
                    public void onCarServiceConnected(IBinder binder) {
                        doNext();
                    }

                    @Override
                    public void onCarServiceDisconnected() {
                        // do nothiing
                    }
                });
            }
        }).next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                if (isFinishing())
                    return;
                startLogin();
                dismissProgress();
            }
        }).start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean switchAccount = intent.getBooleanExtra(LoginConstants.IntentKey.SWITCH_ACCOUNT, false);
        if (switchAccount) {
            initFragment();
        }
    }

    @Override
    protected void onDestroy() {
        if (mCarServerListener != null) {
            CarServiceConnManager.getInstance(this).removeCallBack(mCarServerListener);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_LOGIN && resultCode == RESULT_OK) {
            sIsLoginOnProcess = true;
            initFragment();
        } else {
            finish();
        }
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    private void startLogin() {
        if (hasLogin) return;
        hasLogin = true;
        //开机流程需要先打开4G
        XmCarFactory.getSystemManager().setDataSwitch(true);
        boolean hasFaceRecognition = XmCarConfigManager.hasFaceRecognition();
        Intent intent;
        if (hasFaceRecognition) {
            FaceFactory.getSDK().init(SplashActivity.this);
            intent = new Intent(this, CarInitProActivity.class);
        } else {
            intent = new Intent(this, CarInitActivity.class);
        }
        CarKeyFactory.getSDK().init(SplashActivity.this);
        startActivityForResult(intent, ACTION_LOGIN);
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, WelcomeSplashFragment.newInstance())
                .commitAllowingStateLoss();
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                initAD();
            }
        });
    }

    private void initAD() {
        AdManager.syncAd();
    }

    private void startMain() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e("SplashActivity WelcomeSplashFragment startMain activity is null, Exception...");
        }
    }
}
