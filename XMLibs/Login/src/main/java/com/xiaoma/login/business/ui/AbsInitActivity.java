package com.xiaoma.login.business.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.R;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.business.ui.verify.ActivationActivity;
import com.xiaoma.login.common.ActivationUtils;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.login.common.UserUtil;
import com.xiaoma.login.sdk.CarKey;
import com.xiaoma.login.sdk.CarKeyFactory;
import com.xiaoma.model.User;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.CollectionUtil;

/**
 * Created by kaka
 * on 19-6-17 上午11:56
 * <p>
 * desc: #a
 * </p>
 */
public abstract class AbsInitActivity extends BaseActivity {

    private static final String TAG = AbsInitActivity.class.getSimpleName();
    private static final int ACTIVE_REQUEST_CODE = 2333;
    protected CarKey mCarKey;
    protected User keyBoundUser;
    protected boolean hasDSMModule;
    protected Runnable mTimeOut;
    protected Work mPrepareWork;
    private boolean hasPapered;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getNaviBar().hideNavi();
        showLoadingView();
        if (ConfigManager.FileConfig.isActive()) {
            prepareLogin();
        } else {
            Log.d(TAG, "onCreate: not active go active");
            ActivationActivity.startForResult(this, ACTIVE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVE_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: active success prepare login");
            prepareLogin();
        }
    }

    private void prepareLogin() {

        //上传激活信息
        ActivationUtils.uploadActiveStatus();
        UserUtil.fetchUsers();
        if (!ConfigManager.FileConfig.isUserValid()) {
            //拉取是否实名认证
            mPrepareWork = new Work() {
                @Override
                public void doWork(Object lastResult) {
                    Log.d(UserUtil.TAG, "doWork: realPrepare");
                    realPaperLogin();
                    interrupt();
                }
            };
            Work mFetchInit = new Work() {
                @Override
                public void doWork(Object lastResult) {
                    Log.d(UserUtil.TAG, "prepareLogin: no valid user fetching");
                    UserUtil.fetchUserValid(new UserUtil.CallBack() {
                        @Override
                        public void onSuccess() {
                            XMToast.toastSuccess(AppHolder.getInstance().getAppContext(),
                                    R.string.get_user_info_success);
                            doNext();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            XMToast.toastException(AppHolder.getInstance().getAppContext(),
                                    R.string.get_user_info_fail);
                        }
                    });

                    ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(UserUtil.TAG, "fetch valid user time out,jump to tourists");
                            doNext();
                        }
                    }, 5000);
                }
            };
            SeriesAsyncWorker.create().next(mFetchInit).next(mPrepareWork).start();
        } else {
            realPaperLogin();
        }
    }

    protected void realPaperLogin() {
        if (hasPapered) return;
        hasPapered = true;
        hasDSMModule = XmCarConfigManager.hasFaceRecognition();
        keyBoundUser = UserBindManager.getInstance().getKeyBoundUser();
        mCarKey = CarKeyFactory.getSDK().getCarKey();

        if (CollectionUtil.isListEmpty(UserBindManager.getInstance().getCachedUser())) {
            Log.d(TAG, "realPaperLogin: no login user go tourists");
            onFetchResult(null, LoginMethod.TOURISTS);
        } else {
            Log.d(TAG, "realPaperLogin: has login user go check bind");
            showContentView();
            fetchBindUser();
        }
    }

    protected void initView() {
        //DO NOTHING
    }

    protected abstract void fetchBindUser();

    protected void onRecognizing() {
        //DO NOTHING
    }

    protected void onRecognizeEnd() {
        //DO NOTHING
    }

    protected void onChooseUser(User faceUser, User keyUser) {
        //DO NOTHING
    }

    protected void onFetchResult(User user, LoginMethod loginMethod) {
        if (user == null || LoginMethod.TOURISTS == loginMethod) {
            LoginManager.getInstance().touristLogin();
        } else {
            LoginManager.getInstance().manualLogin(user, loginMethod.name());
        }
        goToSystem();
    }

    protected void goToSystem() {
        setResult(RESULT_OK);
        finish();
    }
}
