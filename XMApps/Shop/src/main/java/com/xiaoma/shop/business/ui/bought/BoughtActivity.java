package com.xiaoma.shop.business.ui.bought;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.ui.bought.callback.ChangeFragmentCallback;
import com.xiaoma.utils.FragmentUtils;

public class BoughtActivity extends BaseActivity implements ChangeFragmentCallback {


    private BoughtMainFragment mBoughtMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bought);
        mBoughtMainFragment = BoughtMainFragment.newInstance();
        FragmentUtils.replace(getSupportFragmentManager(), mBoughtMainFragment, R.id.bought_content_layout);
    }


    @Override
    public void changeFragment(BaseFragment fragment) {
        addHoleContainer(fragment);
    }

    public void addHoleContainer(BaseFragment fragment) {
        FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.bought_content_layout, fragment.getClass().getSimpleName()
                , false, true);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            //查看回退栈是否有fragment
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (mBoughtMainFragment != null && count == 0 && mBoughtMainFragment.isExecutingCleanCache()) {
                mBoughtMainFragment.changeCleanCacheStatus(false);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
