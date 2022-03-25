package com.xiaoma.motorcade;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.business.ui.ChooseUserActivity;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.motorcade.common.constants.MotorcadeConstants;
import com.xiaoma.motorcade.common.im.IMUtils;
import com.xiaoma.motorcade.common.manager.RequestManager;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.model.TeamCountModel;
import com.xiaoma.motorcade.common.utils.EMCallBackImpl;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.motorcade.main.ui.InitialMotorcadeFragment;
import com.xiaoma.motorcade.main.ui.MainFragment;
import com.xiaoma.motorcade.main.vm.MainVM;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.apptool.AppObserver;

import java.util.List;

@PageDescComponent(MotorcadeConstants.PageDesc.mainActivity)
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_LOGIN = 0x12;
    private MainFragment mMainFragment;
    private MainVM mainVM;
    InitialMotorcadeFragment initialMotorcadeFragment;

    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.activity_main);
        initVM();
        if (!LoginManager.getInstance().isUserLogin()) {
            if (!isDestroy()) {
                ChooseUserActivity.start(this, true);
                finish();
            }
        } else {
            // 已经登录。
            // 断网:数据库中数据不为空，打开MainFragment。
            // 联网:网络请求车队数量
            if (!NetworkUtils.isConnected(this)) {
                if (carRepoIsAvailable()) {
                    setupMain(false);
                } else {
                    showNoNetView();
                }
            } else {
                requestTeamCount();
            }
        }


    }

    private void initVM() {
        mainVM = ViewModelProviders.of(this).get(MainVM.class);
    }

    // 判断数据库中是否有数据可用
    private boolean carRepoIsAvailable() {
        List<GroupCardInfo> list = mainVM.getInfosFromRepo();
        if (list != null && !list.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_LOGIN == requestCode) {
            if (RESULT_OK == resultCode) {
                loginHx();
            } else {
                finish();
            }
        }
    }

    /**
     * 登陆环信
     */
    private void loginHx() {
        final User user = UserUtil.getCurrentUser();
        if (user == null) {
            return;
        }

        IMUtils.loginHx(user.getHxAccountService(), user.getHxPasswordService(), new EMCallBackImpl() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing())
                            return;
                        dismissProgress();
                        requestTeamCount();
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

    private void requestTeamCount() {
        RequestManager.getTeamCount(new CallbackWrapper<TeamCountModel>() {

            @Override
            public TeamCountModel parse(String data) throws Exception {
                XMResult<TeamCountModel> result = GsonHelper.fromJson(data, new TypeToken<XMResult<TeamCountModel>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    return null;
                }
                return result.getData();
            }

            @Override
            public void onSuccess(TeamCountModel model) {
                super.onSuccess(model);
                if (model.getCount() > 0) {
                    setupMain(false);
                } else {
                    setUpInitial();
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(MainActivity.this, getString(R.string.request_error));
                if (carRepoIsAvailable()) {
                    setupMain(false);
                } else {
                    showNoNetView();
                }
            }
        });
    }


    public void setupMain(boolean isOpen) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isOpenShare", isOpen);
        mMainFragment = MainFragment.newInstance(bundle);
        mMainFragment.setCallBack(callBack);
        replaceContent(mMainFragment);
    }

    public void setUpInitial() {
        if (initialMotorcadeFragment == null) {
            initialMotorcadeFragment = InitialMotorcadeFragment.newInstance();
            initialMotorcadeFragment.setCallBack(callBack);
        }
        replaceContent(initialMotorcadeFragment);
    }

    public void replaceContent(BaseFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fmt_container, fragment)
                .commitAllowingStateLoss();
    }

    ReplaceMainCallBack callBack = new ReplaceMainCallBack() {

        @Override
        public void replaceMain(boolean isOpenShare) {
            setupMain(isOpenShare);
        }

        @Override
        public void replaceInitial() {
            setUpInitial();
        }
    };

    public interface ReplaceMainCallBack {
        void replaceMain(boolean isOpenShare);

        void replaceInitial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppObserver.getInstance().closeAllActivitiesAndExit();
    }
}
