package com.xiaoma.music.player.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.music.R;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.kuwo.image.KwImage;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.ui.OnRvItemClickListener;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author zs
 * @date 2018/10/12 0012.
 */
public class SimilarAdapter extends BaseQuickAdapter<XMMusic, BaseViewHolder> {
    private WeakReference<Fragment> fragment;

    public SimilarAdapter(Fragment fragment, @Nullable List<XMMusic> data) {
        super(R.layout.item_similar, data);
        this.fragment = new WeakReference<>(fragment);
    }

    public void setData(List<XMMusic> data) {
        mData = data;
    }

    @Override
    protected void convert(final BaseViewHolder helper, XMMusic item) {
        if (!XMMusic.isEmpty(item)) {
            helper.setText(R.id.item_similar_tv, StringUtil.optString(item.getName()));
            helper.setText(R.id.similar_tv_singer, StringUtil.optString(item.getArtist()));
            try {
                if (fragment.get() != null && !fragment.get().isDetached()) {
                    ImageLoader.with(fragment.get())
                            .load(new KwImage(item.getSDKBean(), IKuwoConstant.IImageSize.SIZE_240))
                            .placeholder(R.drawable.iv_default_cover)
                            .transform(Transformations.getRoundedCorners())
                            .into((ImageView) helper.getView(R.id.item_similar_iv));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            helper.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return new ItemEvent(item.getName(), String.valueOf(item.getRid()));
                }

                @BusinessOnClick
                @Override
                public void onClick(View v) {
                    if (!ListUtils.isEmpty(mData)) {
                        clickListener.onItemClick(helper.getAdapterPosition(), mData);
                    }
                }
            });
        }
    }

    private OnRvItemClickListener<List<XMMusic>> clickListener;

    public void setClickListener(OnRvItemClickListener<List<XMMusic>> clickListener) {
        this.clickListener = clickListener;
    }

}
