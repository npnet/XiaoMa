package com.xiaoma.login.business.ui.password.forget;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.R;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.UserUtil;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.FragmentUtils;

public class ForgetPasswdActivity extends BaseActivity {
    public static final String TAG_VERIFY_PHONE = "tag_verify_phone";
    public static final String TAG_INPUT_PASSWD = "tag_input_passwd";
    public static final String TAG_RESET = "tag_reset";
    public ForgetPasswdVM mForgetPasswdVM;
    private Observer<String> mObserver;
    private User mUser;

    public static void startForResult(Activity activity, User user, int requestCode) {
        Intent intent = new Intent(activity, ForgetPasswdActivity.class);
        intent.putExtra(LoginConstants.IntentKey.USER, (Parcelable) user);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = getIntent().getParcelableExtra(LoginConstants.IntentKey.USER);
        if (mUser == null) {
            XMToast.toastException(this, R.string.user_invalid);
            finish();
            return;
        }
        getNaviBar().showBackNavi();
        replaceFragment(TAG_VERIFY_PHONE);
        mForgetPasswdVM = ViewModelProviders.of(this).get(ForgetPasswdVM.class);
        mForgetPasswdVM.setUser(mUser);
        mForgetPasswdVM.getPageTag().observeForever(mObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String pageTag) {
                if (TAG_RESET.equals(pageTag)) {
                    changePasswd();
                } else {
                    replaceFragment(pageTag);
                }
            }
        });
    }

    private void changePasswd() {
        showProgressDialog(R.string.base_loading);
        mForgetPasswdVM.resetPasswd(String.valueOf(mUser.getId()), mForgetPasswdVM.getPasswd().getValue(), new SimpleCallback<String>() {
            @Override
            public void onSuccess(String model) {
                dismissProgress();
                mUser.setPassword(UserUtil.getPasswdHash(mForgetPasswdVM.getPasswd().getValue()));
                if (String.valueOf(mUser.getId()).equals(LoginManager.getInstance().getLoginUserId())) {
                    UserManager.getInstance().notifyUserUpdate(mUser);
                } else {
                    UserBindManager.getInstance().saveCacheUser(mUser);
                }
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(int code, String msg) {
                dismissProgress();
                if (code == -1 || TextUtils.isEmpty(msg)) {
                    msg = getString(R.string.net_work_error);
                }
                XMToast.toastException(ForgetPasswdActivity.this, msg);
            }
        });
    }

    public void replaceFragment(String tag) {
        Fragment fragment = FragmentUtils.findFragment(getSupportFragmentManager(), tag);
        if (fragment == null) {
            switch (tag) {
                case TAG_INPUT_PASSWD:
                    fragment = new NewPasswdFragment();
                    break;
                case TAG_VERIFY_PHONE:
                    fragment = new VerifyVinFragment();
                    break;
                default:
                    return;
            }
        }
        FragmentUtils.replace(getSupportFragmentManager(), fragment, R.id.view_content, tag, true);
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mForgetPasswdVM.getPageTag().removeObserver(mObserver);
    }
}
