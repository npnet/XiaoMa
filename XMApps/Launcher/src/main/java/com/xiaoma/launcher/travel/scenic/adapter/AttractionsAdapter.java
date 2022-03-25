package com.xiaoma.launcher.travel.scenic.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.category.response.SearchStoreBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;


public class AttractionsAdapter extends XMBaseAbstractBQAdapter<SearchStoreBean, BaseViewHolder> {

    private ImageView mAttractionsImg;
    private TextView mAttractionSocer;

    public AttractionsAdapter() {
        super(R.layout.attractions_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchStoreBean item) {
        initView(helper);
        initData(item);
        boolean isCollect = item.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE;
        helper.setBackgroundRes(R.id.attractions_linear,
                isCollect ? R.drawable.collect_item_type_back : R.drawable.collect_item_normal);
        helper.setImageResource(R.id.iv_attractions, isCollect ? R.drawable.collect_star_select : R.drawable.collect_star_nromal);
        helper.setText(R.id.attraction_collection, isCollect ? mContext.getString(R.string.already_collect) : mContext.getString(R.string.collect));
    }

    private void initData(SearchStoreBean item) {
        mAttractionSocer.setText(item.getAvgscore() + "");
        ImageLoader.with(mContext)
                .load(item.getFrontimg())
                .placeholder(R.drawable.not_scenic_img)
                .error(R.drawable.not_scenic_img)
                .into(mAttractionsImg);
    }

    private void initView(BaseViewHolder helper) {
        mAttractionsImg = helper.getView(R.id.gallery_img);
        mAttractionSocer = helper.getView(R.id.attraction_socer);
        helper.addOnClickListener(R.id.attractions_linear);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent("", "");
    }
}

