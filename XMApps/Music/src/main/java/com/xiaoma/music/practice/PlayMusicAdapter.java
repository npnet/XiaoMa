package com.xiaoma.music.practice;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.pratice.PlayMusicBean;
import com.xiaoma.music.R;
import com.xiaoma.music.kuwo.image.KwImage;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/05
 *     desc   :
 * </pre>
 */
public class PlayMusicAdapter extends XMBaseAbstractBQAdapter<PlayMusicBean, BaseViewHolder> {

    private List<XMMusic> xmMusics;

    public PlayMusicAdapter(List<PlayMusicBean> data) {
        super(R.layout.item_play_search, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlayMusicBean item) {
        try {
            XMMusic xmMusic = getXmMusics().get(helper.getLayoutPosition());
            ImageLoader.with(mContext).load(new KwImage(xmMusic.getSDKBean(), IKuwoConstant.IImageSize.SIZE_120))
                    .placeholder(R.drawable.img_music_default)
                    .error(R.drawable.img_music_default)
                    .into((ImageView) helper.getView(R.id.iv_search_icon));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        helper.setText(R.id.tv_search_name, item.getName() + "--" + item.getSingerName());
        helper.setVisible(R.id.iv_search_sure, item.isSelected());
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }

    public List<XMMusic> getXmMusics() {
        return xmMusics == null ? new ArrayList<>() : xmMusics;
    }

    public void setXmMusics(List<XMMusic> xmMusics) {
        this.xmMusics = xmMusics;
    }
}
