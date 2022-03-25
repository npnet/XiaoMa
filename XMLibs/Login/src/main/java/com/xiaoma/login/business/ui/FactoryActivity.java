package com.xiaoma.login.business.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.utils.ConfigFileUtils;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.R;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.login.common.RequestManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.Ignore;
import com.xiaoma.network.ErrorCodeConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.logintype.manager.VisitorLoginType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kaka
 * on 19-3-26 上午11:22
 * <p>
 * desc: 工厂模式页面
 * </p>
 */
public class FactoryActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    public static final String COM_XIAOMA_SETTING = "com.xiaoma.setting";
    private TextView mLoginTitle;
    private EditText mPhone;
    private EditText mVerifyCode;
    private Button mLogin;
    private View mPhoneError;
    private View mVerifyError;
    private View mPhoneTip;
    private View mVerifyTip;

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, FactoryActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().showBackNavi();
        setContentView(R.layout.activity_factory);
        initView();
        initListener();
        //进入 工厂模式一定要打开 数据防止没网不能登录工厂模式
        XmCarFactory.getSystemManager().setDataSwitch(true);
    }

    private void initListener() {
        mLoginTitle.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mPhone.addTextChangedListener(this);
        mVerifyCode.addTextChangedListener(this);
    }

    private void initView() {
        mLoginTitle = findViewById(R.id.login_title);
        mPhone = findViewById(R.id.phone);
        mPhoneError = findViewById(R.id.phone_error_border);
        mPhoneTip = findViewById(R.id.phone_tips);
        mVerifyCode = findViewById(R.id.verify_code);
        mVerifyError = findViewById(R.id.verify_error_border);
        mVerifyTip = findViewById(R.id.verify_tips);
        mLogin = findViewById(R.id.login);

        mPhone.addTextChangedListener(this);
        mVerifyCode.addTextChangedListener(this);
        mVerifyCode.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        checkInputs();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //如果当前是访客模式模式退出该页面需要重新关闭数据
        if (LoginTypeManager.getInstance().getLoginType() instanceof VisitorLoginType) {
            XmCarFactory.getSystemManager().setDataSwitch(false);
        }
    }

    @Override
    @Ignore
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login) {
            login();
        } else if (id == R.id.setting) {
            LaunchUtils.launchApp(this, COM_XIAOMA_SETTING);
        }
    }

    private void login() {
        showProgressDialog(R.string.base_loading);
        RequestManager.factoryLogin(mPhone.getText().toString(), mVerifyCode.getText().toString(), new ResultCallback<XMResult<User>>() {
            @Override
            public void onSuccess(XMResult<User> result) {
                if (result.isSuccess() && result.getData() != null) {
                    User user = result.getData();
                    File userFile = ConfigManager.FileConfig.getUserFile(String.valueOf(user.getId()));
                    File userDBFolder = ConfigManager.FileConfig.getUserDBFolder(FactoryActivity.this, String.valueOf(user.getId()));
                    //清空用户数据
                    ConfigFileUtils.delete(userFile);
                    ConfigFileUtils.delete(userDBFolder);
                    // 工厂模式 个人信息显示默认的 头像 同一的一个默认头像。名称显示id号前6位。性别默认为男。年龄为当天
                    user.setPicPath("");//置空图片地址，使用占位图
                    user.setBirthDayLong(TimeUtils.getNowMills());
                    user.setBirthDay(TimeUtils.date2String(new Date(), new SimpleDateFormat("yyyy-MM-dd")));
                    String id = String.valueOf(user.getId());
                    if (!TextUtils.isEmpty(id) && id.length() > 6) {
                        user.setName(id.substring(0, 6));
                    }
                    user.setGender(1);
                    user.setAge("1");
                    LoginManager.getInstance().manualLogin(user, LoginMethod.FACTORY.name());
                    setResult(RESULT_OK);
                    finish();
                } else {
                    XMToast.toastException(FactoryActivity.this, R.string.login_error);
                }
                dismissProgress();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFailure(int code, String msg) {
                if (code == ErrorCodeConstants.NEWUSER_IDENTIFY_CODE_ERROR) {
                    mPhoneError.setVisibility(View.VISIBLE);
                    mPhoneTip.setVisibility(View.VISIBLE);
                    mPhone.setTextColor(getColor(R.color.color_factory_error_tip));
                    msg = getString(R.string.account_not_exist);
                } else if (code == ErrorCodeConstants.OLDUSER_IDENTIFY_CODE_ERROR) {
                    mVerifyError.setVisibility(View.VISIBLE);
                    mVerifyTip.setVisibility(View.VISIBLE);
                    mVerifyCode.setTextColor(getColor(R.color.color_factory_error_tip));
                    msg = getString(R.string.passwd_error);
                } else {
                    mPhoneError.setVisibility(View.GONE);
                    mPhoneTip.setVisibility(View.GONE);
                    mVerifyError.setVisibility(View.GONE);
                    mVerifyTip.setVisibility(View.GONE);
                    mPhone.setTextColor(getColor(R.color.white));
                    mVerifyCode.setTextColor(getColor(R.color.white));
                    mPhone.clearFocus();
                    mVerifyCode.clearFocus();
                    if (TextUtils.isEmpty(msg) || code == -1) {
                        msg = getString(R.string.no_network);
                    }
                }
                if (!NetworkUtils.isConnected(FactoryActivity.this)) {
                    XMToast.toastException(FactoryActivity.this, getString(R.string.no_network));
                } else {
                    XMToast.toastException(FactoryActivity.this, msg);
                }
                dismissProgress();
            }
        });
    }

    private void checkInputs() {
        if (checkAccount() && checkPasswd()) {
            mLogin.setEnabled(true);
        } else {
            mLogin.setEnabled(false);
        }
    }

    private boolean checkAccount() {
        String account = mPhone.getText().toString();
        return !StringUtil.isEmpty(account);
    }

    private boolean checkPasswd() {
        String passwd = mVerifyCode.getText().toString();
        return !TextUtils.isEmpty(passwd);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        mPhoneError.setVisibility(View.GONE);
        mVerifyError.setVisibility(View.GONE);
        mPhoneTip.setVisibility(View.GONE);
        mVerifyTip.setVisibility(View.GONE);
        mPhone.setTextColor(getColor(R.color.white));
        mVerifyCode.setTextColor(getColor(R.color.white));
        checkInputs();
    }

    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;

            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }

            public char charAt(int index) {
                return '*'; // This is the important part
            }

            public int length() {
                return mSource.length(); // Return default
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    }

    ;
}
