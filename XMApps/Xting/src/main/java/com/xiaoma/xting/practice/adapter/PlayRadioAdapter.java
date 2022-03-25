package com.xiaoma.xting.practice.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.pratice.PlayRadioBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.xting.R;

import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/05
 *     desc   :
 * </pre>
 */
public class PlayRadioAdapter extends XMBaseAbstractBQAdapter<PlayRadioBean, BaseViewHolder> {


    public PlayRadioAdapter(List<PlayRadioBean> data) {
        super(R.layout.item_play_radio, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlayRadioBean item) {

        ImageLoader.with(mContext).load(item.getCoverUrlSmall())
                .placeholder(R.drawable.audio_search_default_bg)
                .into((ImageView) helper.getView(R.id.iv_search_icon));
        helper.setText(R.id.tv_search_name, item.getTitle());
        helper.addOnClickListener(R.id.btn_search_delete);
        helper.setVisible(R.id.iv_search_sure, item.isSelected());
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
