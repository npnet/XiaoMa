package com.xiaoma.music.search.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.image.KwImage;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.player.ui.PlayerActivity;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/25 0025
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private Context mContext;
    private List<XMMusic> mData;
    private WeakReference<Fragment> fragment;

    public MusicAdapter(Fragment fragment, List<XMMusic> mData) {
        this.mContext = fragment.getContext();
        this.mData = mData;
        this.fragment = new WeakReference<>(fragment);
    }

    public void setData(List<XMMusic> data) {
        mData = data;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kw_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, final int position) {
        final XMMusic music = mData.get(position);
        try {
            if (fragment.get() != null && !fragment.get().isDetached()) {
                ImageLoader.with(fragment.get())
                        .load(new KwImage(music.getSDKBean(), IKuwoConstant.IImageSize.SIZE_120))
                        .placeholder(R.drawable.iv_default_cover)
                        .transform(Transformations.getRoundedCorners())
                        .into(holder.mKwImageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.mKwName.setText(StringUtil.optString(music.getName()));
        holder.mKwArtist.setText(StringUtil.optString(music.getArtist()));
        holder.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(holder.mKwName.getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.toastException(mContext, mContext.getString(R.string.net_error));
                    return;
                }
                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                OnlineMusicFactory.getKWPlayer().play(mData.get(position));
                PlayerActivity.launch(mContext);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {

        private ImageView mKwImageView;
        private TextView mKwName;
        private TextView mKwArtist;

        MusicViewHolder(View itemView) {
            super(itemView);
            mKwImageView = itemView.findViewById(R.id.item_kwmusic_iv);
            mKwName = itemView.findViewById(R.id.item_kwmusic_tv);
            mKwArtist = itemView.findViewById(R.id.music_tv_singer);
        }
    }

}
