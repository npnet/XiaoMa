package com.xiaoma.smarthome.login.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.login.ui
 *  @file_name:      XiaoMiLoginActivity
 *  @author:         Rookie
 *  @create_time:    2019/1/23 15:38
 *  @description：   TODO             */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.model.MiUserInfo;
import com.xiaoma.smarthome.common.utils.QrCodeUtil;
import com.xiaoma.smarthome.common.vm.XiaoMiVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

public class XiaoMiLoginActivity extends BaseActivity implements View.OnClickListener {

    private static final int CHECK_INTERVAL = 5000;
    private static final int MAX_CHECK_COUNT_NUM = 450;
    private int checkCount;
    private LinearLayout llLogin, llLoginOut;
    private TextView tvName, tvNotify;
    private ImageView imageAvator;
    private Button btnUnbind;
    private ImageView imageLoginQrCode;
    private String loginUrl;

    private XiaoMiVM mXiaoMiVM;

    private Runnable checkBindStateRunnable = new Runnable() {
        @Override
        public void run() {
            if (checkCount < MAX_CHECK_COUNT_NUM) {
                checkCount++;
                mXiaoMiVM.checkUserBindMi();

            } else {
                ThreadDispatcher.getDispatcher().remove(this);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaomi_login);
        initView();
        initData();
        initViewModel();
    }


    private void initView() {
        llLogin = findViewById(R.id.ll_mi_login);
        llLoginOut = findViewById(R.id.ll_mi_login_out);
        imageLoginQrCode = findViewById(R.id.image_qr_code);
        tvName = findViewById(R.id.tv_name);
        tvNotify = findViewById(R.id.tv_notify);
        imageAvator = findViewById(R.id.image_head);
        btnUnbind = findViewById(R.id.btn_unbind);
        btnUnbind.setOnClickListener(this);
        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);

    }

    private void initData() {
        String miUserJson = TPUtils.get(this, SmartConstants.KEY_MI_USER_INFO, "");
        MiUserInfo userInfo = GsonHelper.fromJson(miUserJson, MiUserInfo.class);
        if (userInfo != null) {
            showLogin(false);
            showMiUserInfo(userInfo);
        } else {
            showLogin(true);
        }
    }

    private void initViewModel() {
        mXiaoMiVM = ViewModelProviders.of(this).get(XiaoMiVM.class);

        mXiaoMiVM.getXmBindStatus().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        showToast(R.string.bind_success_fetch_data);
                        showLogin(false);
                        ThreadDispatcher.getDispatcher().remove(checkBindStateRunnable);
                        //绑定成功，获取信息
                        mXiaoMiVM.fetchMiUserInfo();
                        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.LOGINSMARTACCOUNT.getType());
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.d("onFailure", "getXmBindStatus msg:" + msg);
                        TPUtils.put(XiaoMiLoginActivity.this, SmartConstants.KEY_MI_USER_INFO, "");
                        if (checkCount == 0) {
                            mXiaoMiVM.fetchMiAuthInfo();
                            showToast(R.string.fetch_xiaomi_auth);
                        } else {
                            ThreadDispatcher.getDispatcher().postDelayed(checkBindStateRunnable, CHECK_INTERVAL);
                        }
                    }
                });
            }
        });

        mXiaoMiVM.getMiAuthInfo().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        showToast(R.string.auth_success);
                        loginUrl = data.getData();
                        showLogin(true);
                        ThreadDispatcher.getDispatcher().postDelayed(checkBindStateRunnable, CHECK_INTERVAL);
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.d("onFailure", "getMiAuthInfo msg:" + msg);
                        showToast(getString(R.string.auth_fail_msg, msg));
                    }
                });
            }
        });

        mXiaoMiVM.getMiUserInfo().observe(this, new Observer<XmResource<MiUserInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<MiUserInfo> miUserInfoXmResource) {

                miUserInfoXmResource.handle(new OnCallback<MiUserInfo>() {
                    @Override
                    public void onSuccess(MiUserInfo data) {
                        showToast(R.string.fetch_user_data_success);
                        if (data != null) {
                            TPUtils.put(XiaoMiLoginActivity.this, SmartConstants.KEY_MI_USER_INFO, GsonHelper.toJson(data));
                            showMiUserInfo(data);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.d("onFailure", "getMiUserInfo msg:" + msg);
                        showToast(R.string.fetch_user_data_fail);
                        showToast(msg);
                    }
                });
            }
        });

        mXiaoMiVM.getMiParser().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        showToast(R.string.parse_success);
                        KLog.d("parserresult: " + data.getData());
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.d("onFailure", "getMiParser msg:" + msg);
                        showToast(R.string.parse_fail);
                    }
                });
            }
        });

        mXiaoMiVM.getMiLogOut().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        showToast(R.string.logout_success);
                    }

                    @Override
                    public void onFailure(String msg) {
                        showToast(R.string.logout_fail);
                    }
                });
            }
        });

        //查询用户绑定状态
        mXiaoMiVM.checkUserBindMi();
    }

    private void showLogin(boolean login) {
        if (login) {
            llLogin.setVisibility(View.VISIBLE);
            llLoginOut.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(loginUrl)) {
                imageLoginQrCode.setImageBitmap(QrCodeUtil.generateBitmap(loginUrl, 255, 255));
            }
        } else {
            llLogin.setVisibility(View.GONE);
            llLoginOut.setVisibility(View.VISIBLE);
        }
    }

    private void showMiUserInfo(MiUserInfo userInfo) {
        if (userInfo != null) {
            if (!TextUtils.isEmpty(userInfo.getMiliaoNick())) {
                tvName.setText(String.format(getString(R.string.mi_user_name), userInfo.getMiliaoNick()));
            }
            if (!TextUtils.isEmpty(userInfo.getMiliaoIcon_320())) {
                try {
                    ImageLoader.with(this).load(userInfo.getMiliaoIcon_320()).into(imageAvator);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_unbind) {
            //退出登录
            mXiaoMiVM.logOutMi();
        } else if (i == R.id.btn_open) {
//          公司地址  22.3212    113.5656
            mXiaoMiVM.textParserMi("打开床头灯", 22.3212, 113.5656);
        } else if (i == R.id.btn_close) {
            mXiaoMiVM.textParserMi("关灯", 22.3212, 113.5656);
        }
    }


}
