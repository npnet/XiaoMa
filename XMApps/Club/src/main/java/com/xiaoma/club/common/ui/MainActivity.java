package com.xiaoma.club.common.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaoma.club.R;
import com.xiaoma.club.common.constant.ClubConstants;
import com.xiaoma.club.common.constant.TabIndexMainFragment;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.hyphenate.callback.EMCallBackImpl;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.common.viewmodel.MainViewModel;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.StringUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import static com.xiaoma.club.common.util.LogUtil.logE;
import static com.xiaoma.club.common.util.LogUtil.logI;

/**
 * Created by LKF on 2018/10/9 0009.
 */
@PageDescComponent(ClubEventConstants.PageDescribe.mainActivity)
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final int REQ_CODE_LOGIN = 1;
    private MainFragment mMainFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
            @Override
            public void doWork(Object lastResult) {
                if (isDestroy())
                    return;
                if (!LoginManager.getInstance().isUserLogin()) {
                    showToastException(R.string.account_permission_prompt);
                    finish();
                    return;
                }
                long uid = UserUtil.getCurrentUid();
                if (uid <= 0 || LoginConstants.TOURIST_USER_ID == uid) {
                    showToastException(R.string.account_permission_prompt);
                    finish();
                    return;
                }
                doNext();
            }
        }).next(new Work() {
            @Override
            public void doWork(Object lastResult) {
                if (isDestroy())
                    return;
                handleIntent(getIntent());
                setupMain();
                dismissProgress();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logI(TAG, StringUtil.format("onActivityResult -> [ requestCode: %d, resultCode: %d, data: %s ]", requestCode, resultCode, data));
        if (REQ_CODE_LOGIN == requestCode) {
            if (RESULT_OK == resultCode) {
                logI(TAG, StringUtil.format("onActivityResult -> Login Success, do Hx login...", requestCode, resultCode, data));
                loginHx();
            } else {
                logI(TAG, StringUtil.format("onActivityResult -> Login cancel, finish !", requestCode, resultCode, data));
                finish();
            }
        }
    }

    private void handleIntent(@NonNull Intent intent) {
        final Uri data = intent.getData();
        if (data == null) {
            logI(TAG, "handleIntent -> data is null");
            return;
        }
        logI(TAG, "handleIntent -> data: %s", data);
        final String path = data.getPath();
        if (getString(R.string.path_discover).equals(path)) {
            ViewModelProviders.of(this).get(MainViewModel.class).getTabSelectIndex().setValue(TabIndexMainFragment.TAB_DISCOVER);
        } else if (getString(R.string.path_msg).equals(path)) {
            ViewModelProviders.of(this).get(MainViewModel.class).getTabSelectIndex().setValue(TabIndexMainFragment.TAB_MSG);
        } else if (getString(R.string.path_contacts).equals(path)) {
            ViewModelProviders.of(this).get(MainViewModel.class).getTabSelectIndex().setValue(TabIndexMainFragment.TAB_CONTACT);
        }
    }

    /**
     * 登陆环信
     */
    private void loginHx() {
        final User user = UserUtil.getCurrentUser();
        if (user == null) {
            logE(TAG, "loginHx -> User is null !");
            return;
        }
        showProgressDialog(R.string.hx_connecting);
        IMUtils.loginHx(user.getHxAccount(), user.getHxPassword(), new EMCallBackImpl() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing())
                            return;
                        setupMain();
                        dismissProgress();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                super.onError(code, message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing())
                            return;
                        dismissProgress();
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            moveTaskToBack(true);
        }
    }

    private void setupMain() {
        if (mMainFragment != null) {
            logI(TAG, "setupMain -> MainFragment has added");
            return;
        }
        logI(TAG, "setupMain -> Add MainFragment");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fmt_container, mMainFragment = new MainFragment())
                .commitAllowingStateLoss();
    }

    @Subscriber(tag = ClubConstants.MOVE_TASK_TO_BACK)
    private void moveTaskToBack(String event) {
        moveTaskToBack(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*private void setupNetworkDisable() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fmt_container);
        if (f instanceof NoNetworkFragment) {
            logI(TAG, "setupMain -> NoNetworkFragment has added");
            return;
        }
        logI(TAG, "setupMain -> Add NoNetworkFragment");
        final NoNetworkFragment noNetworkFragment = new NoNetworkFragment();
        noNetworkFragment.setCallback(new NoNetworkFragment.Callback() {
            @Override
            public void onRetryClick(Fragment f, View v) {
                // TODO:
                if (EMClient.getInstance().isConnected()) {
                    setupMain();
                } else {
                    loginHx();
                }
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fmt_container, noNetworkFragment)
                .commitAllowingStateLoss();
    }*/
}