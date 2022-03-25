package com.xiaoma.music.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.mine.model.VipOptionsBean;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/25 0025 11:50
 *   desc:   vip价格选项
 * </pre>
 */
public class PriceOptionsAdapter extends BaseQuickAdapter<VipOptionsBean, BaseViewHolder> {

    public PriceOptionsAdapter() {
        super(R.layout.item_vip_price_options);
    }

    @Override
    protected void convert(BaseViewHolder helper, VipOptionsBean priceOptions) {
        helper.setText(R.id.tv_vip_validity_month, mContext.getString(R.string.vip_month_format, priceOptions.getCnt()));
        helper.setText(R.id.tv_vip_price, mContext.getString(R.string.vip_price_format, String.valueOf(priceOptions.getPrice())));

        if (priceOptions.isSelect()) {
            if (OnlineMusicFactory.getKWLogin().isUserLogon() && OnlineMusicFactory.getKWLogin().isCarVipUser()) {
                helper.setGone(R.id.tv_vip_renewal_fee, true);
            }
            helper.getView(R.id.vip_price_options_layout).setBackgroundResource(R.drawable.vip_options_selected);
        } else {
            helper.setGone(R.id.tv_vip_renewal_fee, false);
            helper.getView(R.id.vip_price_options_layout).setBackgroundResource(R.drawable.vip_options_normal);
        }
    }
}
