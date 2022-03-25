package com.xiaoma.pet.adapter;

import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.pet.R;
import com.xiaoma.pet.model.RepositoryInfo;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/21 0021 11:10
 *   desc:   仓库商品适配器
 * </pre>
 */
public class RepositoryGoodsAdapter extends BasePetAdapter<RepositoryInfo> {

    @Override
    protected void convert(BaseViewHolder helper, RepositoryInfo item) {
        ImageLoader.with(mContext)
                .load(item.getGoodsIcon())
                .apply(RequestOptions.placeholderOf(R.drawable.goods_list_placeholder))
                .apply(RequestOptions.centerCropTransform())
                .into((ImageView) helper.getView(R.id.iv_pet_food));

        helper.setGone(R.id.iv_desc_image, false);
        helper.setText(R.id.tv_desc_number, mContext.getString(R.string.repository_goods_number, item.getGoodsNumber()));
        helper.setText(R.id.tv_name_text, item.getGoodsName());
    }
}
