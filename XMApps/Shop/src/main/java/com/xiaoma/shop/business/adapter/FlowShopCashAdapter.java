package com.xiaoma.shop.business.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.FlowItemForCash;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.util.LanUtils;
import com.xiaoma.shop.common.util.UnitConverUtils;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.ListUtils;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/09
 * @Describe: .
 */

public class FlowShopCashAdapter extends XMBaseAbstractBQAdapter<FlowItemForCash, BaseViewHolder> {

    private int mSelectedPos = -1;


    public FlowShopCashAdapter() {
        super(R.layout.item_flow);
    }


    @Override
    protected void convert(BaseViewHolder helper, FlowItemForCash item) {
        adapterName(helper, item);
        adapterPrice(helper, item);
        adapterClickEffect(helper, item);
    }

    private void adapterClickEffect(BaseViewHolder helper, FlowItemForCash item) {
        // 点击效果
        ViewGroup container = helper.getView(R.id.rl_flow_item);
        if (item.isSelected()) {
            container.setSelected(true);
        } else {
            container.setSelected(false);
        }
    }

    private void adapterPrice(BaseViewHolder helper, FlowItemForCash item) {
        switch (item.getPayType()) {
            case ShopContract.Pay.DEFAULT://免费
                helper.setGone(R.id.ll_price, true)
                        .setGone(R.id.ll_coin_price, false)
                        .setText(R.id.tv_discount_price, R.string.state_free);
                break;
            case ShopContract.Pay.RMB://现金
                helper.setGone(R.id.ll_price, true)
                        .setGone(R.id.ll_coin_price, false);
                setPrice(helper, item);
                break;
        }

    }

    private void setPrice(BaseViewHolder helper, FlowItemForCash item) {
        String price = UnitConverUtils.moreThanToConvert(item.getMarketPrice() + "");
        helper.setText(R.id.tv_discount_price, "￥" + price);
    }

    private void adapterName(BaseViewHolder helper, FlowItemForCash item) {
        String commoName = LanUtils.isEnglish() ? item.getCommoNameEn() : item.getCommoName();
        String name = TextUtils.isEmpty(commoName) ? "" : commoName;
        helper.setText(R.id.tv_flow, name);
    }

    /**
     * 重置选择
     */
    private void resetSelect() {
        for (FlowItemForCash flowItemBean : getData()) {
            flowItemBean.setSelected(false);
        }
    }

    /**
     * 选择
     *
     * @param position
     */
    public void selected(int position) {
        if (position < getData().size()) {
            resetSelect();
            getData().get(position).setSelected(true);
            notifyDataSetChanged();
            mSelectedPos = position;
        }
    }

    public void setSelectedPos(int selectedPos) {
        mSelectedPos = selectedPos;
    }

    public int getSelectedPos() {
        return mSelectedPos;
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getItem(position).getCommoName(), position + "");
    }

    // TODO: 2019/8/12 0012 暂时用价格去查找对应的商城，不是很稳妥，但是后台那边无法提供出其他字段给前端查询，建议用次字段
    public int searchItemByProductPrice(String productPrice) {
        if (ListUtils.isEmpty(getData())) return -1;
        for (int i = 0; i < getData().size(); i++) {
            if (isTheSamePrice(productPrice, i)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isTheSamePrice(String productPrice, int i) {
        float remotePrice = UnitConverUtils.string2Float(productPrice);
        float localPrice = UnitConverUtils.string2Float(getData().get(i).getMarketPrice() + "");
        return remotePrice == localPrice;
    }
}
