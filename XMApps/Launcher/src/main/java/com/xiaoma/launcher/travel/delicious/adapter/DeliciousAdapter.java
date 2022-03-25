package com.xiaoma.launcher.travel.delicious.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.category.response.SearchStoreBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

public class DeliciousAdapter extends XMBaseAbstractBQAdapter<SearchStoreBean, BaseViewHolder> {

    private ImageView mDeliciousImg;
    private TextView mDeliciousScore;
    private LinearLayout mDeliciousLinear;
    private LinearLayout mSocerLinear;
    private ImageView mStart;

    public DeliciousAdapter() {
        super(R.layout.delicious_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchStoreBean item) {
        initView(helper);
        initData(item);
        boolean isCollect = item.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE;
        helper.setBackgroundRes(R.id.delicious_linear,
                isCollect ? R.drawable.collect_item_type_back : R.drawable.collect_item_normal);
        helper.setImageResource(R.id.iv_delicious, isCollect ? R.drawable.collect_star_select : R.drawable.collect_star_nromal);
        helper.setText(R.id.delicious_collection, isCollect ? mContext.getString(R.string.already_collect) : mContext.getString(R.string.collect));
    }

    private void initData(SearchStoreBean item) {
        mDeliciousScore.setText(item.getAvgscore() + "");
        ImageLoader.with(mContext)
                .load(item.getFrontimg())
                .placeholder(R.drawable.not_scenic_img)
                .error(R.drawable.not_scenic_img)
                .into(mDeliciousImg);

        int level = item.getStar();
        if (item.getIsMiQiLin() == BaseCollectVM.IS_MI_QI_LING && level > 0) {
            mStart.setVisibility(View.VISIBLE);
            mSocerLinear.setVisibility(View.GONE);
            if (level == 1) {
                mStart.setImageResource(R.drawable.icon_star_1);

            } else if (level == 2) {
                mStart.setImageResource(R.drawable.icon_star_2);

            } else {
                mStart.setImageResource(R.drawable.icon_star_3);
            }

        } else {
            mSocerLinear.setVisibility(View.VISIBLE);
            mStart.setVisibility(View.GONE);
        }
    }

    private void initView(BaseViewHolder helper) {
        mDeliciousLinear = helper.getView(R.id.delicious_linear);
        mSocerLinear = helper.getView(R.id.socer_linear);
        mDeliciousImg = helper.getView(R.id.delicious_img);
        mDeliciousScore = helper.getView(R.id.tv_score);
        mStart = helper.getView(R.id.iv_start);
        helper.addOnClickListener(R.id.delicious_linear);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent("", "");
    }

    public void showText() {
        mDeliciousLinear.setVisibility(View.VISIBLE);
        mSocerLinear.setVisibility(View.VISIBLE);
    }

    public void hintText() {
        mDeliciousLinear.setVisibility(View.INVISIBLE);
        mSocerLinear.setVisibility(View.INVISIBLE);
    }
}

