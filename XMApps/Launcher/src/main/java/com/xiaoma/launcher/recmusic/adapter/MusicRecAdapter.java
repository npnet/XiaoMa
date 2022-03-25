package com.xiaoma.launcher.recmusic.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.recmusic.model.MusicRecord;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

/**
 * @author taojin
 * @date 2019/1/11
 */
public class MusicRecAdapter extends XMBaseAbstractBQAdapter<MusicRecord, BaseViewHolder> {
    private RoundedCorners mRoundedCorners;

    public MusicRecAdapter(int layoutResId, @Nullable List<MusicRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicRecord item) {
        ImageView musicImg = helper.itemView.findViewById(R.id.iv_music_rec_rv_album);
        helper.setText(R.id.tv_music_rec_rv_song, item.getName());
        helper.setText(R.id.tv_music_rec_rv_singer, item.getSingerName());
        ImageLoader.with(mContext)
                .load(item.getSingerCoverUrl())
                .placeholder(R.drawable.muscirec_default_cover)
                .transform(getRoundedCorners())
                .into(musicImg);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.MUSIC_REC_HISTORY_LIST, getData().get(position).getSongId());
    }

    private RoundedCorners getRoundedCorners() {
        if (mRoundedCorners == null) {
            mRoundedCorners = new RoundedCorners(10);
        }
        return mRoundedCorners;
    }
}
