package com.xiaoma.shop.business.adapter.bought;

import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.AbsChildThemeBean;

/**
 *
 */
public class BoughtVehicleSoundDetailAdapter extends BaseBoughtAdapter<AbsChildThemeBean> {

    public BoughtVehicleSoundDetailAdapter(RequestManager requestManager) {
        super(requestManager);
    }

    @Override
    protected void convert(BaseViewHolder helper, AbsChildThemeBean item) {
        super.convert(helper, item);
        helper.setVisible(R.id.iv_bought_test_play, true);
        helper.setVisible(R.id.iv_bought_icon, false);
        helper.setVisible(R.id.bought_operation_bt, false);
        helper.setText(R.id.tv_bought_name, item.getName());
    }
}
