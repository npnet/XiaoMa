package com.xiaoma.shop.business.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.shop.R;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/19
 * @Describe:
 */

public class ShopBaseFragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setStateViewTips();
    }
    private void setStateViewTips() {
        try {
            View noNetworkView = mStateView.getNoNetworkView();
            TextView tv = noNetworkView.findViewById(R.id.tv_tips);
            tv.setText(R.string.network_does_not_force);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
