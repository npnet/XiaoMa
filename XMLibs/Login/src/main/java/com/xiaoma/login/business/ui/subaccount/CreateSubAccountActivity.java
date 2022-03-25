package com.xiaoma.login.business.ui.subaccount;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.R;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.business.ui.subaccount.fragment.InputPasswdFragment;
import com.xiaoma.login.business.ui.subaccount.fragment.UserInitFragment;
import com.xiaoma.login.business.ui.subaccount.fragment.VerifyPhoneFragment;
import com.xiaoma.model.User;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.FragmentUtils;

import java.util.List;

/**
 * Created by kaka
 * on 19-5-24 下午4:55
 * <p>
 * desc: 由于创建子账户的流程发生改变，将创建子账户的各个流程拆分为不同的fragment并统一由创建子账户的activity管理
 * </p>
 */
public class CreateSubAccountActivity extends BaseActivity {

    public static final String TAG_INPUT_PASSWD = "inputPasswd";
    public static final String TAG_VERIFY_PHONE = "verifyPhone";
    public static final String TAG_USER_INFO = "userInfo";
    public static final String TAG_CREATE_SUCCESS = "createSuccess";
    public static final String TAG_FINISH = "finish";
    public CreateSubAccountVM mCreateSubAccountVM;
    private Observer<String> mObserver;

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, CreateSubAccountActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        checkSubAccountCount();
        super.onCreate(savedInstanceState);
        getNaviBar().showBackNavi();
        replaceFragment(TAG_INPUT_PASSWD);
        mCreateSubAccountVM = ViewModelProviders.of(this).get(CreateSubAccountVM.class);
        mCreateSubAccountVM.getPageTag().observeForever(mObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String pageTag) {
                if (TAG_CREATE_SUCCESS.equals(pageTag)) {
                    setResult(RESULT_OK);
                    finish();
                } else if (TAG_FINISH.equals(pageTag)) {
                    finish();
                } else {
                    replaceFragment(pageTag);
                }
            }
        });
    }

    private void checkSubAccountCount() {
        List<User> cachedSubUser = UserBindManager.getInstance().getCachedSubUser();
        if (cachedSubUser != null && cachedSubUser.size() >= 5) {
            XMToast.toastException(this, R.string.no_more_sub_account);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCreateSubAccountVM.getPageTag().removeObserver(mObserver);
    }

    public void replaceFragment(String tag) {
        Fragment fragment = FragmentUtils.findFragment(getSupportFragmentManager(), tag);
        if (fragment == null) {
            switch (tag) {
                case TAG_INPUT_PASSWD:
                    fragment = new InputPasswdFragment();
                    break;
                case TAG_VERIFY_PHONE:
                    fragment = new VerifyPhoneFragment();
                    break;
                case TAG_USER_INFO:
                    fragment = new UserInitFragment();
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
}
