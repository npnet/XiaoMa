package com.xiaoma.launcher.travel.delicious.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.category.response.SearchStoreBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.StringUtil;

import java.util.List;

/**
 * @author taojin
 * @date 2019/2/3
 */
public class DeliciousCollectAdapter extends XMBaseAbstractBQAdapter<SearchStoreBean, BaseViewHolder> {

    private String action1;
    private String action2;

    public DeliciousCollectAdapter(int layoutResId, @Nullable List<SearchStoreBean> data, String action1, String action2) {
        super(layoutResId, data);
        this.action1 = action1;
        this.action2 = action2;
    }

    @Override
    protected void convert(BaseViewHolder helper, final SearchStoreBean item) {
        boolean isCollect = item.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE;
        helper.setText(R.id.tv_collection, isCollect ? mContext.getString(R.string.already_collect) : mContext.getString(R.string.collect));
        helper.setBackgroundRes(R.id.collection_linear,
                isCollect ? R.drawable.collect_item_type_back : R.drawable.collect_item_normal);
        helper.setImageResource(R.id.iv_collection, isCollect ? R.drawable.collect_star_select : R.drawable.collect_star_nromal);
        helper.setText(R.id.tv_score, item.getAvgscore() + "");
        helper.addOnClickListener(R.id.collection_linear);
        if ("0".equals(item.getAvgprice()) || "0.0".equals(item.getAvgprice())) {
            //显示暂无人均
            helper.setText(R.id.tv_detail, String.format(mContext.getString(R.string.item_four_detail_no_price), StringUtil.isNotEmpty(item.getArea()) ? item.getArea() : mContext.getString(R.string.not_area), item.getSubcate(), StringUtil.keep2Decimal(item.getDistance())));

        } else {
            helper.setText(R.id.tv_detail, String.format(mContext.getString(R.string.item_four_detail),
                    StringUtil.isNotEmpty(item.getArea()) ? item.getArea() : mContext.getString(R.string.not_area), item.getSubcate(), StringUtil.keep2Decimal(item.getDistance()), item.getAvgprice()));
        }
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_action1, action1);
        helper.setText(R.id.tv_action2, action2);
        helper.addOnClickListener(R.id.tv_action1);
        helper.addOnClickListener(R.id.tv_action2);
        ImageLoader.with(mContext)
                .load(item.getFrontimg())
                .placeholder(R.drawable.emotional_food_not_data)
                .error(R.drawable.emotional_food_not_data)
                .into((ImageView) helper.getView(R.id.iv_cover));

        //米其林餐厅舒适度
        int comfort = item.getComforLevel();
        LinearLayout comfortView = helper.getView(R.id.ll_comfort);
        if (item.getIsMiQiLin() == BaseCollectVM.IS_MI_QI_LING && comfort > 0) {
            comfortView.setVisibility(View.VISIBLE);
            showComfortView(comfortView, comfort);

        } else {
            comfortView.setVisibility(View.GONE);
        }

        ImageView ivStart = helper.getView(R.id.iv_start);
        View scoreView = helper.getView(R.id.socer_linear);
        //米其林星级
        int level = item.getStar();
        if (item.getIsMiQiLin() == BaseCollectVM.IS_MI_QI_LING && level > 0) {
            ivStart.setVisibility(View.VISIBLE);
            scoreView.setVisibility(View.GONE);
            if (level == 1) {
                ivStart.setImageResource(R.drawable.icon_star_1);

            } else if (level == 2) {
                ivStart.setImageResource(R.drawable.icon_star_2);

            } else {
                ivStart.setImageResource(R.drawable.icon_star_3);
            }

        } else {
            scoreView.setVisibility(View.VISIBLE);
            ivStart.setVisibility(View.GONE);
        }
    }

    private void showComfortView(LinearLayout confortView, int comfort) {
        confortView.removeAllViews();
        for (int i = 0; i < comfort; i++) {
            ImageView mIconComfort = new ImageView(mContext);
            mIconComfort.setImageResource(R.drawable.icon_comfort);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.delicious_comfort_margin);
            mIconComfort.setLayoutParams(params);
            confortView.addView(mIconComfort);
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }

}
