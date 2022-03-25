package com.xiaoma.launcher.travel.scenic.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

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
public class AttractionsCollectAdapter extends XMBaseAbstractBQAdapter<SearchStoreBean, BaseViewHolder> {

    private String action1;
    private String action2;


    public AttractionsCollectAdapter(int layoutResId, @Nullable List<SearchStoreBean> data, String action1, String action2) {
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
        helper.setText(R.id.tv_detail, String.format(mContext.getString(R.string.item_four_attraction_detail),
                StringUtil.isNotEmpty(item.getArea()) ? item.getArea() : mContext.getString(R.string.not_area), StringUtil.keep2Decimal(item.getDistance()), item.getAvgprice()));
        helper.addOnClickListener(R.id.collection_linear);
        helper.addOnClickListener(R.id.tv_action1);
        helper.addOnClickListener(R.id.tv_action2);
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_action1, action1);
        helper.setText(R.id.tv_action2, action2);
        ImageLoader.with(mContext)
                .load(item.getFrontimg())
                .placeholder(R.drawable.attractions_not_data)
                .error(R.drawable.attractions_not_data)
                .into((ImageView) helper.getView(R.id.iv_cover));

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
