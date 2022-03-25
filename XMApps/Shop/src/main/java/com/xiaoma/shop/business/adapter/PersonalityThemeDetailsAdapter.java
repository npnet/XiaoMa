package com.xiaoma.shop.business.adapter;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.shop.R;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/6
 */
public class PersonalityThemeDetailsAdapter extends XMBaseAbstractBQAdapter<String, BaseViewHolder> {
    public PersonalityThemeDetailsAdapter() {
        super(R.layout.item_theme_thumbnail, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        RequestOptions transform = new RequestOptions()
                .placeholder(R.drawable.skin_details_place_holder)
                .transform(new RoundedCorners(10));
        ImageView thumbnailIV = helper.getView(R.id.ivThumbnail);
        ImageLoader.with(mContext)
                .load(item)
                .apply(transform)
                .into(thumbnailIV);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getItem(position), position + "");
    }
}
