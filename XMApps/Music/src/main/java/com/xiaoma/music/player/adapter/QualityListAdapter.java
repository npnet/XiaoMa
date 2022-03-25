package com.xiaoma.music.player.adapter;

import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.R;
import com.xiaoma.music.player.model.MusicQualityModel;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * Author: loren
 * Date: 2019/6/29 0029
 */
public class QualityListAdapter extends XMBaseAbstractBQAdapter<MusicQualityModel, BaseViewHolder> {

    private int currentQuality;
    private boolean currentIsVip;

    public QualityListAdapter() {
        super(R.layout.item_music_quality);
    }

    public void setCurrentQuality(int quality) {
        this.currentQuality = quality;
    }

    public void setCurrentIsVip(boolean currentIsVip) {
        this.currentIsVip = currentIsVip;
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicQualityModel item) {
        Button qualityBtn = helper.getView(R.id.quality_content_btn);
        Button buyVip = helper.getView(R.id.quality_type_btn);
        if (item.getQualityText() != 0) {
            qualityBtn.setText(item.getQualityText());
            if (item.getQuality() == currentQuality) {
                qualityBtn.setSelected(true);
                qualityBtn.setTextColor(mContext.getColor(R.color.search_key_word));
            } else {
                qualityBtn.setSelected(false);
                qualityBtn.setTextColor(mContext.getColor(R.color.white));
            }
        }
        buyVip.setVisibility(item.isNeedVip() && !currentIsVip ? View.VISIBLE : View.GONE);
        qualityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((item.getQuality() != currentQuality) && (listener != null)) {
                    listener.onQualityClick(item);
                }
            }
        });
        buyVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBuyVipClick();
                }
            }
        });
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }

    private OnItemChildClickListener listener;

    public void setOnItemChildClickListener(OnItemChildClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemChildClickListener {

        void onQualityClick(MusicQualityModel model);

        void onBuyVipClick();
    }
}
