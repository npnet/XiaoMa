package com.xiaoma.login.business.ui.password;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xiaoma.login.R;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.business.ui.password.forget.ForgetPasswdActivity;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.PasswordPageConstants;
import com.xiaoma.login.common.UserUtil;
import com.xiaoma.model.User;
import com.xiaoma.ui.toast.XMToast;

/**
 * Created by kaka
 * on 19-1-11 下午3:21
 * <p>
 * desc: 账户密码验证，输入密码的页面
 * </p>
 */
public class PasswdVerifyActivity extends BasePasswordActivity {

    protected User mUser;
    private PasswdTexts mTexts;

    public static void startForResult(Activity activity, User user, int requestCode) {
        startForResult(activity, user, requestCode, null);
    }

    public static void startForResult(Fragment fragment, User user, int requestCode) {
        startForResult(fragment, user, requestCode, null);
    }

    public static void startForResult(Activity activity, User user, int requestCode, PasswdTexts texts) {
        startForResult(activity,user,requestCode,texts,PasswdVerifyActivity.class);
    }

    public static void startForResult(Fragment fragment, User user, int requestCode, PasswdTexts texts) {
        startForResult(fragment,user,requestCode,texts,PasswdVerifyActivity.class);
    }

    public static void startForResult(Activity activity, User user, int requestCode, PasswdTexts texts,Class aClass) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(LoginConstants.IntentKey.USER, user);
        bundle.putSerializable(LoginConstants.IntentKey.PASSWD_TEXTS, texts);
        startForResult(activity,bundle,requestCode,texts,aClass);
    }

    public static void startForResult(Fragment fragment, User user, int requestCode, PasswdTexts texts,Class aClass) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(LoginConstants.IntentKey.USER, user);
        bundle.putSerializable(LoginConstants.IntentKey.PASSWD_TEXTS, texts);
        startForResult(fragment,bundle,requestCode,texts,aClass);
    }

    public static void startForResult(Activity activity, Bundle bundle, int requestCode, PasswdTexts texts,Class aClass) {
        User user = bundle.getParcelable(LoginConstants.IntentKey.USER);
        if (user != null) {
            Intent intent = new Intent(activity, aClass);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, requestCode);
        } else {
            XMToast.toastException(activity, R.string.user_invalid);
        }
    }

    public static void startForResult(Fragment fragment, Bundle bundle, int requestCode, PasswdTexts texts,Class aClass) {
        User user = bundle.getParcelable(LoginConstants.IntentKey.USER);
        if (user != null) {
            Intent intent = new Intent(fragment.getContext(), aClass);
            intent.putExtras(bundle);
            fragment.startActivityForResult(intent, requestCode);
        } else {
            XMToast.toastException(fragment.getContext(), R.string.user_invalid);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = getIntent().getParcelableExtra(LoginConstants.IntentKey.USER);
        mTexts = (PasswdTexts) getIntent().getSerializableExtra(LoginConstants.IntentKey.PASSWD_TEXTS);
        if (mUser == null) {
            XMToast.toastException(this, R.string.user_invalid);
            finish();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        if (mTexts == null) {
            setTexts(PasswdTexts.NORMAL);
        } else {
            setTexts(mTexts);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.forget_passwd) {
            ForgetPasswdActivity.startForResult(this, mUser,
                    PasswordPageConstants.RESET_PASSWD_PAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PasswordPageConstants.RESET_PASSWD_PAGE_REQUEST_CODE
                && resultCode == RESULT_OK) {
            mUser = UserBindManager.getInstance().getCachedUserById(mUser.getId());
            cleanPasswordText();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void finishInputThenConfirm() {
        if (UserUtil.checkPasswd(getPasswordText(), mUser.getPassword())) {
            verifySuccess(mUser);
        } else {
            cleanPasswordText();
            XMToast.toastException(PasswdVerifyActivity.this, R.string.passwd_error);
        }
    }

    @Override
    protected boolean showForget() {
        return true;
    }

    protected void verifySuccess(User user) {
        Intent data = new Intent();
        User mergedUser = UserUtil.merge(user);
        data.putExtra(LoginConstants.IntentKey.USER, (Parcelable) mergedUser);
        setResult(RESULT_OK, data);
        finish();
    }
}
