package com.xiaoma.shop.business.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.ScoreProductBean;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.util.UnitConverUtils;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.ListUtils;

import java.util.Collection;
import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/09
 * @Describe: .
 */

public class FlowShopCoinAdapter extends XMBaseAbstractBQAdapter<ScoreProductBean.ProductInfoBean.ChildProductBean, BaseViewHolder> {

    private int mSelectedPos = -1;
    private ScoreProductBean mScoreProductBean;

    public ScoreProductBean getScoreProductBean() {
        return mScoreProductBean;
    }

    public FlowShopCoinAdapter() {
        super(R.layout.item_flow);
    }

    public void setScoreProductBean(ScoreProductBean scoreProductBean) {
        mScoreProductBean = scoreProductBean;
    }

    @Override
    protected void convert(BaseViewHolder helper, ScoreProductBean.ProductInfoBean.ChildProductBean item) {
        adapterName(helper, item);
        adapterPrice(helper, item);
        adapterTag(helper, item);
        adapterClickEffect(helper, item);
    }

    private void adapterClickEffect(BaseViewHolder helper, ScoreProductBean.ProductInfoBean.ChildProductBean item) {
        // 点击效果
        ViewGroup container = helper.getView(R.id.rl_flow_item);
        if (item.isSelected()) {
            container.setSelected(true);
        } else {
            container.setSelected(false);
        }
    }

    private void adapterTag(BaseViewHolder helper, ScoreProductBean.ProductInfoBean.ChildProductBean item) {
        String tag = TextUtils.isEmpty(item.getTagPath()) ? "" : item.getTagPath();
        // 标签
        ImageView iv = helper.getView(R.id.iv_foreground);
        if (!TextUtils.isEmpty(tag) && mScoreProductBean != null && !ListUtils.isEmpty(mScoreProductBean.getProductInfo())) {
            Long differTime = System.currentTimeMillis() - mScoreProductBean.getProductInfo().get(0).getCreateDate();
            long sevenDays = 2 * 7 * 24 * 60 * 60 * 1000;
            long differ = sevenDays - differTime;
            if (differ >= 0) {
                ImageLoader//
                        .with(mContext)//
                        .load(tag)//
                        .into(iv);//
            }
        } else {
            iv.setBackground(null);
        }
    }

    private void adapterPrice(BaseViewHolder helper, ScoreProductBean.ProductInfoBean.ChildProductBean item) {
        switch (item.getPayType()) {
            case ShopContract.Pay.DEFAULT://免费
                helper.setGone(R.id.ll_price, true)
                        .setGone(R.id.ll_coin_price, false)
                        .setText(R.id.tv_discount_price, R.string.state_free);
                break;
            case ShopContract.Pay.COIN://车币
                helper.setGone(R.id.ll_price, false)
                        .setGone(R.id.ll_coin_price, true);
                setScore(helper, item);
                break;
        }

    }

    private void setScore(BaseViewHolder helper, ScoreProductBean.ProductInfoBean.ChildProductBean item) {
        String cardCoin = UnitConverUtils.moreThanToConvert(item.getNeedScore() + "");
        helper.setText(R.id.tv_discount_coin, cardCoin);
    }

    private void adapterName(BaseViewHolder helper, ScoreProductBean.ProductInfoBean.ChildProductBean item) {
        String name = TextUtils.isEmpty(item.getName()) ? "" : item.getName();
        helper.setText(R.id.tv_flow, name);
    }

    /**
     * 重置选择
     */
    private void resetSelect() {
        for (ScoreProductBean.ProductInfoBean.ChildProductBean flowItemBean : getData()) {
            flowItemBean.setSelected(false);
        }
    }

    @Override
    public void addData(@NonNull Collection<? extends ScoreProductBean.ProductInfoBean.ChildProductBean> newData) {
        super.addData(newData);
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
        return new ItemEvent(getItem(position).getName(), position + "");
    }

    public int searchItemByProductId(long productId) {
        if (ListUtils.isEmpty(getData())) return -1;
        for (int i = 0; i < getData().size(); i++) {
            if (productId == getData().get(i).getId()) {
                return i;
            }
        }
        return -1;
    }
}
