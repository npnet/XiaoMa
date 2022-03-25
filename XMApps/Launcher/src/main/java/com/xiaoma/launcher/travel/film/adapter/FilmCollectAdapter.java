package com.xiaoma.launcher.travel.film.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.film.adapter
 *  @file_name:
 *  @author:         Rookie
 *  @create_time:    2019/2/22 19:38
 *  @description：   TODO             */

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.movie.response.NearbyCinemasDetailsBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;

import java.util.List;

public class FilmCollectAdapter extends XMBaseAbstractBQAdapter<NearbyCinemasDetailsBean, BaseViewHolder> {


    private String action1;
    private String action2;

    public FilmCollectAdapter(int layoutResId, @Nullable List<NearbyCinemasDetailsBean> data, String action1, String action2) {
        super(layoutResId, data);
        this.action1 = action1;
        this.action2 = action2;

    }

    @Override
    protected void convert(BaseViewHolder helper, final NearbyCinemasDetailsBean item) {
        boolean isCollect = item.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE;
        helper.setText(R.id.tv_collection, isCollect ? mContext.getString(R.string.already_collect) : mContext.getString(R.string.collect));
        helper.setVisible(R.id.socer_linear, false);
        helper.setBackgroundRes(R.id.collection_linear,
                isCollect ? R.drawable.collect_item_type_back : R.drawable.collect_item_normal);
        helper.setImageResource(R.id.iv_collection, isCollect ? R.drawable.collect_star_select : R.drawable.collect_star_nromal);

        helper.addOnClickListener(R.id.tv_collection);
        helper.addOnClickListener(R.id.tv_action1);
        helper.addOnClickListener(R.id.tv_action2);
        if (StringUtil.isNotEmpty( item.getMinPrice())){
            helper.setText(R.id.tv_detail, String.format(mContext.getString(R.string.item_three_detail2), item.getCountyName(), item.getDistance(), item.getMinPrice()));
        }else {
            helper.setText(R.id.tv_detail, String.format(mContext.getString(R.string.item_three_detail_null), item.getCountyName(), item.getDistance()));
        }

        helper.setText(R.id.tv_name, item.getCinemaName());
        helper.setText(R.id.tv_action1, action1);
        helper.setText(R.id.tv_action2, action2);

        ImageLoader.with(mContext)
                .load(item.getImgUrl())
                .placeholder(R.drawable.not_film_img)
                .error(R.drawable.not_film_img)
                .into((ImageView) helper.getView(R.id.iv_cover));

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }

}