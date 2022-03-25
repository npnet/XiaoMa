package com.xiaoma.xting.player.adapter;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.xting.R;
import com.xiaoma.xting.player.model.FMChannel;
import com.xiaoma.xting.utils.Transformations;


/**
 * <des>
 *
 * @author YangGang
 * @date 2018/11/6
 */
public class OnlineFuctionAdapter extends XMBaseAbstractBQAdapter<FMChannel, BaseViewHolder> {
    public OnlineFuctionAdapter() {
        super(R.layout.item_play_list_store);
    }

    @Override
    protected void convert(BaseViewHolder helper, FMChannel item) {
        ImageLoader.with(helper.itemView.getContext()).load(item.getCoverUrl())
                .placeholder(R.drawable.fm_default_cover)
                .transform(Transformations.getRoundedCorners())
                .into((ImageView) helper.getView(R.id.ivCover));
        helper.setText(R.id.tvName, item.getName());
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getName(), position + "");
    }
}
