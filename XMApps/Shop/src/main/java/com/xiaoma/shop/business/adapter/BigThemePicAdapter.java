package com.xiaoma.shop.business.adapter;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.shop.R;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/6
 */
public class BigThemePicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public BigThemePicAdapter() {
        super(R.layout.item_theme_big);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        RequestOptions transform = new RequestOptions()
                .placeholder(R.drawable.skin_details_big_place_holder)
                .transform(new RoundedCorners(20));
        ImageView picIV = helper.getView(R.id.ivPic);
        ImageLoader.with(mContext)
                .load(item)
                .apply(transform)
                .into(picIV);
        helper.setText(R.id.tvIndex, (helper.getAdapterPosition() + 1) + " / " + getItemCount())
                .addOnClickListener(R.id.ivClose);
    }

}
