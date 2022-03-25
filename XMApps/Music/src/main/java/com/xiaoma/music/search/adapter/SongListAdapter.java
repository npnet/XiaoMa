package com.xiaoma.music.search.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMSongListInfo;
import com.xiaoma.music.player.ui.PlayerActivity;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/12 0012
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
    private static final int PAGE_SIZE = 500;

    private Context mContext;
    private List<XMSongListInfo> mData;
    private WeakReference<Fragment> fragment;

    public SongListAdapter(Fragment fragment, List<XMSongListInfo> mData) {
        this.mContext = fragment.getContext();
        this.mData = mData;
        this.fragment = new WeakReference<>(fragment);
    }

    public void setData(List<XMSongListInfo> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        XMSongListInfo singerBean = mData.get(position);
        try {
            if (fragment.get() != null && !fragment.get().isDetached()) {
                ImageLoader.with(fragment.get())
                        .load(TextUtils.isEmpty(singerBean.getSDKBean().getImageUrl()) ? ""
                                : singerBean.getSDKBean().getImageUrl())
                        .placeholder(R.drawable.iv_default_cover)
                        .transform(Transformations.getRoundedCorners())
                        .into(holder.mImageView);
                ImageLoader.with(fragment.get())
                        .load(TextUtils.isEmpty(singerBean.getSDKBean().getImageUrl()) ? ""
                                : singerBean.getSDKBean().getImageUrl())
                        .transform(Transformations.getCircleCrop())
                        .into(holder.mImageViewCircle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.mName.setText(singerBean.getSDKBean().getName());
        holder.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(holder.mName.getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.toastException(mContext, mContext.getString(R.string.net_error));
                    return;
                }
                if (listener != null) {
                    listener.onProgressState(true);
                }
                OnlineMusicFactory.getKWAudioFetch().fetchSongListMusic(mData.get(position), 0, PAGE_SIZE, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                    @Override
                    public void onFetchSuccess(List<XMMusic> musicList) {
                        if (listener != null) {
                            listener.onProgressState(false);
                        }
                        AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                        OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                        PlayerActivity.launch(mContext);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        if (listener != null) {
                            listener.onProgressState(false);
                        }
                        XMToast.showToast(mContext, R.string.net_error);
                        KLog.e("switchPlay songlist failed :" + msg);
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private ImageView mImageViewCircle;
        private TextView mName;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_song_list_iv);
            mImageViewCircle = itemView.findViewById(R.id.item_song_list_circle_iv);
            mName = itemView.findViewById(R.id.item_song_list_tv);
        }
    }

    private OnProgressStateListener listener;

    public void setOnProgressStateListener(OnProgressStateListener listener) {
        this.listener = listener;
    }

    public interface OnProgressStateListener {
        void onProgressState(boolean isShow);
    }
}
