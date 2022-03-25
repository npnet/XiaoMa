package com.xiaoma.smarthome.login.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.manager.CMDeviceManager;
import com.xiaoma.smarthome.common.manager.CMSceneDataManager;
import com.xiaoma.smarthome.common.manager.SmartHomeManager;
import com.xiaoma.smarthome.login.model.CMUserInfo;
import com.xiaoma.smarthome.login.model.XiaoMiBean;
import com.xiaoma.smarthome.login.vm.LoginVM;
import com.xiaoma.smarthome.scene.ui.SelectSceneFragment;
import com.xiaoma.smarthome.scene.ui.XiaoBaiSelectSceneFragment;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    //公子小白登录
    private static final String FRAGMENT_TAG_XIAOBAI_LOGIN = "fragment_tag_xiaobai_login";
    //公子小白场景列表
    public static final String FRAGMENT_TAG_XIAOBAI_SELECTSCENE = "fragment_tag_xiaobai_selectscene";

    //云米登录
    private static final String FRAGMENT_TAG_YUN_MI_LOGIN = "fragment_tag_yun_mi_login";
    //云米场景列表
    public static final String FRAGMENT_TAG_YUN_MI_SELECTSCENE = "fragment_tag_yun_mi_selectscene";
    //云米设置页面
    public static final String FRAGMENT_GAT_YUN_MI_SETTING = "fragment_gat_yun_mi_setting";

    //小米登录页面
    private static final String FRAGMENT_TAG_XIAOMAI_LOGIN = "fragment_tag_xiaomai_login";
    //小米登录成功页面
    public static final String FRAGMENT_TAG_XIAOMAI_LOGIN_SUCESS = "fragment_tag_xiaomai_login_success";

    private ImageView mIvCloudMi;
    private ImageView ivXiaobai;
    private ImageView ivXiaomi;
    private TextView mTvCloudMi;
    private TextView mTvXiaoBai;
    private TextView mTvXiaoMi;

    private SmartHomeManager mSmartHomeManager;

    private LoginVM mLoginVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smarthome_activity_main);
        KLog.d("SmartHome MainActivity onCreate");
        initView();
        initVM();
    }



    private void initView() {
        mSmartHomeManager = SmartHomeManager.getInstance();

        mIvCloudMi = findViewById(R.id.iv_cloudmi);
        ivXiaobai = findViewById(R.id.iv_xiaobai);
        ivXiaomi = findViewById(R.id.iv_xiaomi);
        mTvCloudMi = findViewById(R.id.tv_cloudmi);
        mTvXiaoBai = findViewById(R.id.tv_xiaobai);
        mTvXiaoMi = findViewById(R.id.tv_xiaomi);

        mIvCloudMi.setOnClickListener(this);
        ivXiaobai.setOnClickListener(this);
        ivXiaomi.setOnClickListener(this);
        mTvCloudMi.setOnClickListener(this);
        mTvXiaoBai.setOnClickListener(this);
        mTvXiaoMi.setOnClickListener(this);
    }

    private void initVM() {
        mLoginVM = ViewModelProviders.of(this).get(LoginVM.class);
        //获取云米登录状态
        mLoginVM.getCmLoginState().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                if (stringXmResource == null) {
                    return;
                }
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        //用户已授权则无需拉取二维码,直接查询用户信息
                        mLoginVM.queryCMUserInfo();
                    }

                    @Override
                    public void onError(int code, String message) {
                        dismissProgress();
                        replaceFragment(YunMiLoginFragment.newInstance(), FRAGMENT_TAG_YUN_MI_LOGIN);
                    }
                });
            }
        });
        mLoginVM.getCMUserInfo().observe(this, new Observer<XmResource<CMUserInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<CMUserInfo> cmUserInfoXmResource) {
                if (cmUserInfoXmResource == null) {
                    return;
                }
                cmUserInfoXmResource.handle(new OnCallback<CMUserInfo>() {
                    @Override
                    public void onSuccess(CMUserInfo data) {
                        dismissProgress();
                        //更新云米token
                        CMSceneDataManager.getInstance().setCmToken(data.getToken());
                        //登陆云米sdk
                        XiaoMiBean xiaomi = data.getXiaomi();
                        if (xiaomi != null) {
                            CMDeviceManager.getInstance().loginCM(xiaomi);
                        }
                        startSelectSceneFragment(data.getHeadImg(), data.getNickName());
                    }

                    @Override
                    public void onError(int code, String message) {
                        dismissProgress();
                        XMToast.toastException(MainActivity.this, getString(R.string.error_net_msg, message));
                        //登录已过期
                        if (code == SmartConstants.YUN_MI_LOGIN_EXPIRED) {
                            mLoginVM.logout();
                        }
                    }
                });
            }
        });
    }

    private void startSelectSceneFragment(String head, String nick) {
        //打开场景列表
        FragmentUtils.replace(getSupportFragmentManager(), SelectSceneFragment.newInstance(head, nick),
                R.id.container_frame, MainActivity.FRAGMENT_TAG_YUN_MI_SELECTSCENE, true);

    }

    @Override
    public void onClick(View v) {
        if (!NetworkUtils.isConnected(this)) {
            XMToast.toastException(this, getString(R.string.no_net_work));
            return;
        }
        int i = v.getId();
        if (i == R.id.tv_xiaobai || i == R.id.iv_xiaobai) {
            if (mSmartHomeManager.isLogin() && mSmartHomeManager.isBind()) {
                //跳转到管理页面
                replaceFragment(XiaoBaiSelectSceneFragment.newInstance(), FRAGMENT_TAG_XIAOBAI_SELECTSCENE);

            } else {
                //跳转到登录页面
                replaceFragment(XiaoBaiLoginFragment.newInstance(), FRAGMENT_TAG_XIAOBAI_LOGIN);
            }

        } else if (i == R.id.tv_xiaomi || i == R.id.iv_xiaomi) {
            replaceFragment(XiaoMiLoginFragment.newInstance(), FRAGMENT_TAG_XIAOMAI_LOGIN);

        } else if (i == R.id.tv_cloudmi || i == R.id.iv_cloudmi) {
            //先查询用户登录状态，再判断是否拉取二维码
            showProgressDialog(R.string.loading);
            mLoginVM.fetchCMLoginState();
        }
    }

    private void replaceFragment(BaseFragment fragment, String tag) {
        FragmentUtils.replace(getSupportFragmentManager(), fragment, R.id.container_frame, tag, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        KLog.d("SmartHome MainActivity onNewIntent");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KLog.d("SmartHome MainActivity onDestroy");
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && !fragments.isEmpty()) {
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment child = fragments.get(i);
                if (child.getChildFragmentManager().popBackStackImmediate()) {
                    return;
                }
            }
        }
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return;
        }
        moveTaskToBack(true);
    }
}
