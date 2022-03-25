package com.xiaoma.personal.manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.manager.ui.fragment.SetupFragment;
import com.xiaoma.utils.FragmentUtils;

import static com.xiaoma.personal.common.util.FragmentTagConstants.SETUP_FRAGMENT_TAG;

/**
 * Created by Gillben on 2019/1/8 0008
 * <p>
 * desc: 账户设置
 */
@PageDescComponent(EventConstants.PageDescribe.AccountManagerHome)
public class SetupActivity extends BaseActivity {

    private static final String TAG = SetupFragment.class.getSimpleName();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        replaceFragment(SetupFragment.newInstance(), SETUP_FRAGMENT_TAG);
    }


    public void replaceFragment(BaseFragment fragment, String tag) {
        FragmentUtils.replace(getSupportFragmentManager(),fragment,R.id.setup_page_content,tag,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }
}
