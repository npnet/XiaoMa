package com.xiaoma.pet.adapter;

import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.pet.R;
import com.xiaoma.pet.model.StoreGoodsInfo;

/**
 * Created by Gillben on 2018/12/25 0025
 * <p>
 * desc: 商店商品
 */
public class StoreGoodsAdapter extends BasePetAdapter<StoreGoodsInfo> {

    @Override
    protected void convert(BaseViewHolder helper, StoreGoodsInfo item) {
        ImageLoader.with(mContext)
                .load(item.getGoodsIcon())
                .apply(RequestOptions.placeholderOf(R.drawable.goods_list_placeholder))
                .apply(RequestOptions.centerCropTransform())
                .into((ImageView) helper.getView(R.id.iv_pet_food));

        helper.setText(R.id.tv_desc_number, String.valueOf(item.getGoodsPrice()));
        helper.setText(R.id.tv_name_text, item.getGoodsName());
    }
}
