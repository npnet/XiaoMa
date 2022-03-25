package com.xiaoma.music.mine.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
import com.xiaoma.music.kuwo.image.KwImage;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.mine.model.LocalModel;
import com.xiaoma.ui.OnRvItemClickListener;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/24 0024
 */
public class CollectionAdapter extends BaseQuickAdapter<LocalModel, BaseViewHolder> {

    private boolean isDeleteStatus = false;
    private OnRvItemClickListener<XMMusic> listener;
    private WeakReference<Fragment> fragment;
    private boolean longClick = false;


    public CollectionAdapter(Fragment fragment, @Nullable List<LocalModel> data) {
        super(R.layout.item_collection, data);
        this.fragment = new WeakReference<>(fragment);
    }

    public void setListener(OnRvItemClickListener<XMMusic> listener) {
        this.listener = listener;
    }

    public void setLongClick(boolean longClick) {
        this.longClick = longClick;
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
        final int position = helper.getAdapterPosition();
        AutoScrollTextView marqueeTextView = helper.getView(R.id.footer);
        marqueeTextView.setText(StringUtil.optString(item.getTitleText()));
//        helper.setText(R.id.collect_tv_album,StringUtil.optString(item.getXmMusic().getAlbum()));
        helper.setText(R.id.collect_tv_singer,StringUtil.optString(item.getXmMusic().getArtist()));
        setMarquee(marqueeTextView, item);
        if (isDeleteStatus) {
            helper.setVisible(R.id.delete, true);
            startAnimation(helper);
        } else {
            helper.setVisible(R.id.delete, false);
            clearAnimation(helper);
        }
        try {
            if (fragment.get() != null && !fragment.get().isDetached()) {
                ImageLoader.with(fragment.get())
                        .load(new KwImage(item.getXmMusic().getSDKBean(), IKuwoConstant.IImageSize.SIZE_240))
                        .placeholder(R.drawable.iv_default_cover)
                        .transform(Transformations.getRoundedCorners())
                        .into((ImageView) helper.getView(R.id.cover));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handleEvent(helper, item, position);
    }

    private void setMarquee(AutoScrollTextView textView, LocalModel item) {
        String id = getAlbumId(item);
        final int currentType = KwPlayInfoManager.getInstance().getCurrentType();
        if (currentType == KwPlayInfoManager.AlbumType.COLLECTION){
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
        holder.setVisible(R.id.delete, false);
        clearAnimation(holder);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isDeleteStatus) {
            holder.setVisible(R.id.delete, true);
            startAnimation(holder);
        }
    }

    private void handleEvent(BaseViewHolder helper, final LocalModel item, final int position) {
        FrameLayout delete = helper.itemView.findViewById(R.id.delete);
        delete.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.delete + " - " + item.getTitleText(), String.valueOf(item.getXmMusic().getRid()));
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                removeMusic(item, position);
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
                callbackClickListener(position, item);
            }
        });
        if (longClick) {
            helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (CollectionAdapter.this.onLongClick()) return true;
                    return true;
                }
            });
        }
    }

    private boolean onLongClick() {
        if (isDeleteStatus) {
            isDeleteStatus = false;
            notifyDataSetChanged();
            return true;
        }
        isDeleteStatus = true;
        notifyDataSetChanged();
        return false;
    }

    private void callbackClickListener(int position, LocalModel item) {
        if (isDeleteStatus) {
            isDeleteStatus = false;
            notifyDataSetChanged();
            return;
        }
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.toastException(mContext, mContext.getString(R.string.net_error));
            return;
        }
        listener.onItemClick(position, item.getXmMusic());
    }

    private void removeMusic(LocalModel item, int position) {
        XMMusic favoriteMusic = item.getXmMusic();
        if (!XMMusic.isEmpty(favoriteMusic)) {
            MusicDbManager.getInstance().deleteCollectionMusic(favoriteMusic);
            getData().remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
            listener.onItemDelete();
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
}
