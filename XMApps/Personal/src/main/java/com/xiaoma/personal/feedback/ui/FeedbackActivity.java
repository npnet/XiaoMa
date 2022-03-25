package com.xiaoma.personal.feedback.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.common.util.FragmentTagConstants;
import com.xiaoma.personal.feedback.callback.OnSwitchFragmentCallback;
import com.xiaoma.personal.feedback.ui.fragment.FeedbackHomeFragment;
import com.xiaoma.personal.feedback.ui.view.RecordVoiceDialog;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.utils.FragmentUtils;

/**
 * @author Gillben
 * date: 2018/12/04
 * <p>
 * 反馈交互页
 */
@PageDescComponent(EventConstants.PageDescribe.feedbackHome)
public class FeedbackActivity extends BaseActivity implements OnSwitchFragmentCallback, RecordVoiceDialog.RecordProgressCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        replacePageContent(FeedbackHomeFragment.newFragment(), FragmentTagConstants.FEEDBACK_HOME_FRAGMENT, false);
    }

    @Override
    public void onSwitchFragment(BaseFragment fragment, String tag, boolean isAddStack) {
        replacePageContent(fragment, tag, isAddStack);
    }

    private void replacePageContent(BaseFragment fragment, String tag, boolean isAddStack) {
        FragmentUtils.replace(getSupportFragmentManager(), fragment, R.id.view_content, tag, isAddStack);
    }

    public void popFragmentFromStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }


    public void showHomeAndBack() {
        getNaviBar().showBackAndHomeNavi();
    }

    public void hideNavigation() {
        getNaviBar().hideNavi();
    }


    @Override
    public void showRecordProgress() {
        XMProgress.showProgressDialog(this, getString(R.string.loading));
    }

    @Override
    public void dismissRecordProgress() {
        XMProgress.dismissProgressDialog(this);
    }
}
