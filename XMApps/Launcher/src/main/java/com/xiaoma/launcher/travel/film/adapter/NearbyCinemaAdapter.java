package com.xiaoma.launcher.travel.film.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.movie.response.NearbyCinemasDetailsBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;


public class NearbyCinemaAdapter extends XMBaseAbstractBQAdapter<NearbyCinemasDetailsBean, BaseViewHolder> {

    private ImageView mNearbyCinemaImg;
    private TextView mNearbyCinemaCollection;

    public NearbyCinemaAdapter() {
        super(R.layout.nearby_cinema_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, NearbyCinemasDetailsBean item) {
        initView(helper);
        initData(item);
        boolean isCollect = item.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE;
        helper.setBackgroundRes(R.id.nearby_cinema_linear,
                isCollect ? R.drawable.collect_item_type_back : R.drawable.collect_item_normal);
        helper.setImageResource(R.id.iv_nearby_cinema, isCollect ? R.drawable.collect_star_select : R.drawable.collect_star_nromal);
        helper.setText(R.id.nearby_cinema_collection, isCollect ? mContext.getString(R.string.already_collect) : mContext.getString(R.string.collect));
    }

    private void initData(NearbyCinemasDetailsBean item) {
        ImageLoader.with(mContext)
                .load(item.getImgUrl())
                .placeholder(R.drawable.not_film_img)
                .error(R.drawable.not_film_img)
                .into(mNearbyCinemaImg);
    }

    private void initView(BaseViewHolder helper) {
        mNearbyCinemaImg = helper.getView(R.id.gallery_img);
        helper.addOnClickListener(R.id.nearby_cinema_linear);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent("","");
    }
}

