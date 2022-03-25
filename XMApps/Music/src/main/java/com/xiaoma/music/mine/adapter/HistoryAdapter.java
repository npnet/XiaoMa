package com.xiaoma.music.mine.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.image.KwImage;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.mine.model.LocalModel;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/19 0019
 */
public class HistoryAdapter extends BaseQuickAdapter<LocalModel, BaseViewHolder> {

    private boolean isDeleteStatus = false;
    private WeakReference<Fragment> fragment;

    public HistoryAdapter(Fragment fragment, @Nullable List<LocalModel> data) {
        super(R.layout.item_history_music, data);
        this.fragment = new WeakReference<>(fragment);
    }

    public void recoverNormal() {
        if (isDeleteStatus) {
            isDeleteStatus = false;
            notifyDataSetChanged();
        }
    }

    public boolean isDeleteStatus() {
        return isDeleteStatus;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final LocalModel item) {
        if (item == null || XMMusic.isEmpty(item.getXmMusic())) {
            return;
        }
        AutoScrollTextView marqueeTextView = helper.getView(R.id.tv_music_name);
        marqueeTextView.setText(item.getTitleText());
        setMarquee(marqueeTextView, item);
        final int position = helper.getAdapterPosition();
        if (isDeleteStatus) {
            helper.setVisible(R.id.iv_delete_history, true);
            startAnimation(helper);
        } else {
            helper.setVisible(R.id.iv_delete_history, false);
            clearAnimation(helper);
        }
        try {
            if (fragment.get() != null && !fragment.get().isDetached()) {
                ImageLoader.with(fragment.get())
                        .load(new KwImage(item.getXmMusic().getSDKBean(),
                                IKuwoConstant.IImageSize.SIZE_240))
                        .placeholder(R.drawable.iv_default_cover)
                        .transform(Transformations.getRoundedCorners())
                        .into((ImageView) helper.getView(R.id.img_history_music));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView albumTv = helper.getView(R.id.history_tv_album);
        TextView singerTv = helper.getView(R.id.history_tv_singer);
//        albumTv.setText(TextUtils.isEmpty(item.getXmMusic().getAlbum()) ? mContext.getString(R.string.unknow) : item.getXmMusic().getAlbum());
        singerTv.setText(TextUtils.isEmpty(item.getXmMusic().getArtist()) ? mContext.getString(R.string.unknow) : item.getXmMusic().getArtist());
        handleEvent(helper, item, position);
    }

    private void setMarquee(AutoScrollTextView textView, LocalModel item) {
        String id = getAlbumId(item);
        final int currentType = KwPlayInfoManager.getInstance().getCurrentType();
        if (currentType == KwPlayInfoManager.AlbumType.HISTORY){
            XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
            if (!XMMusic.isEmpty(nowPlayingMusic)&&nowPlayingMusic.getRid() == item.getXmMusic().getRid()){
                textView.startMarquee();
                KLog.d("HistoryAdapter","setMarquee: "+id);
            }else {
                textView.stopMarquee();
            }
        }else {
            textView.stopMarquee();
        }
    }

    private String getAlbumId(LocalModel item) {
        XMMusic xmMusic = item.getXmMusic();
        return xmMusic.getSDKBean().rid + xmMusic.getName();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.setVisible(R.id.iv_delete_history, false);
        clearAnimation(holder);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isDeleteStatus) {
            holder.setVisible(R.id.iv_delete_history, true);
            startAnimation(holder);
        }
    }

    private void startAnimation(RecyclerView.ViewHolder holder) {
        Animation animation = holder.itemView.getAnimation();
        if (animation == null) {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        }
        holder.itemView.startAnimation(animation);
    }

    private void clearAnimation(RecyclerView.ViewHolder holder) {
        holder.itemView.clearAnimation();
    }

    private void handleEvent(BaseViewHolder helper, final LocalModel item, final int position) {
        FrameLayout delete = helper.itemView.findViewById(R.id.iv_delete_history);
        delete.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.delete + " - " + item.getTitleText(), String.valueOf(item.getXmMusic().getRid()));
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                XMMusic favoriteMusic = item.getXmMusic();
                if (!XMMusic.isEmpty(favoriteMusic)) {
                    MusicDbManager.getInstance().deleteHistoryMusic(favoriteMusic, true);
                    getData().remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
            }
        });

        helper.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(item.getTitleText(), String.valueOf(item.getXmMusic().getRid()));
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                if (isDeleteStatus) {
                    isDeleteStatus = false;
                    notifyDataSetChanged();
                    return;
                }
                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.toastException(mContext, mContext.getString(R.string.net_error));
                    return;
                }
                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                OnlineMusicFactory.getKWPlayer().play(wrapperXmMusics(mData),position);
                KwPlayInfoManager.getInstance().setCurrentPlayInfo(getAlbumId(item), KwPlayInfoManager.AlbumType.HISTORY);
                notifyDataSetChanged();
            }
        });
        helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isDeleteStatus) {
                    isDeleteStatus = false;
                    notifyDataSetChanged();
                    return true;
                }
                isDeleteStatus = true;
                notifyDataSetChanged();
                return true;
            }
        });
    }
    @NonNull
    private List<XMMusic> wrapperXmMusics(List<LocalModel> localModels) {
        List<XMMusic> musicList = new ArrayList<>();
        for (LocalModel localModel : localModels) {
            XMMusic xmMusic = localModel.getXmMusic();
            if (XMMusic.isEmpty(xmMusic)) {
                continue;
            }
            musicList.add(xmMusic);
        }
        return musicList;
    }
}
