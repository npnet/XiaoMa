package com.xiaoma.xting.welcome.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.business.ui.ChooseUserActivity;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.xting.MainActivity;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.welcome.consract.FirstInAppStatus;
import com.xiaoma.xting.welcome.ui.fragment.PreferenceSelectFragment;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/13
 */
@PageDescComponent(EventConstants.PageDescribe.ACTIVITY_SPLASH)
public class SplashActivity extends BaseActivity {
    public static final int REQUEST_LOGIN = 0x11;

    public static void launch(BaseActivity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToMainPage(FragmentActivity activity, @FirstInAppStatus int status) {
        if (activity == null) return;
        if (activity instanceof SplashActivity) {
            TPUtils.put(activity, XtingConstants.TP_FIRST_START_APP, status);
            MainActivity.launch(activity);
            activity.finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        handleLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == MainActivity.RESULT_OK) {
            showContentView();
            replaceFragment(PreferenceSelectFragment.newInstance());
        } else if (requestCode == REQUEST_LOGIN && resultCode == MainActivity.RESULT_CANCELED) {
            finish();
        } else {
            showErrorView();
        }
    }

    public void replaceFragment(BaseFragment fragment) {
        FragmentUtils.replace(
                getSupportFragmentManager(),
                fragment,
                R.id.view_content
        );
    }

    private void handleLogin() {
        if (!LoginManager.getInstance().isUserLogin()) {
            if (!isDestroy()) {
                ChooseUserActivity.start(this, true);
                finish();
            }
        } else {
            replaceFragment(PreferenceSelectFragment.newInstance());
        }
    }

    @Override
    protected void errorOnRetry() {
        XmAutoTracker.getInstance().onEvent(
                EventConstants.PageDescribe.ACTIVITY_SEARCH,
                SplashActivity.this.getClass().getName(),
                EventConstants.NormalClick.ACTION_NET_ERROR_RETRY);
        handleLogin();
    }
}
