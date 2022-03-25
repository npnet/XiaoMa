package com.xiaoma.xting.common.adapter;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.control.IPlayerControl;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.utils.Transformations;

import java.lang.ref.WeakReference;

/**
 * 请使用bindToRecyclerView绑定Adapter
 *
 * @author KY
 * @date 2018/10/10
 */
public class GalleryAdapter<T extends IGalleryData> extends XMBaseAbstractBQAdapter<T, BaseViewHolder> {

    protected WeakReference<Fragment> fragment;
    protected AutoScrollTextView lastMarqueeView;
    protected boolean mAntiDoubleClickT = true;
    private AlbumItemClickListener itemClickListener;

    public GalleryAdapter(Fragment fragment) {
        super(R.layout.item_gallery, null);
        this.fragment = new WeakReference<>(fragment);
    }

    protected int mCurPlayPos = -1, mLastPlayPos = -1;

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        //避免 点击的时候 跑马效果 文字可能会出现专辑名,所以全部复写
        int position = helper.getAdapterPosition();
        boolean isPentonRadio = (position == 0 && item.getSourceType() == PlayerSourceType.KOALA);

        if (fragment.get() != null && !fragment.get().isDetached()) {
            ImageLoader.with(fragment.get())
                    .load(isPentonRadio ? R.drawable.icon_penton : item.getCoverUrl())
                    .placeholder(R.drawable.fm_default_cover)
                    .transform(Transformations.getRoundedCorners())
                    .into((ImageView) helper.getView(R.id.iv_cover));
        }

        CharSequence title = item.getTitleText(mContext);
        if (TextUtils.isEmpty(title)) {
            helper.setVisible(R.id.title, false);
        } else {
            helper.setVisible(R.id.title, true);
            AutoScrollTextView autoScrollTextView = helper.getView(R.id.title);
            autoScrollTextView.setText(isPentonRadio ? mContext.getString(R.string.penton_radio) : String.valueOf(title));
            autoScrollTextView.stopMarquee();
            if (isMarquee(helper)) {
                if (!autoScrollTextView.isStarting) {
                    Log.d("REC", "Marquee index " + position);
                    mCurPlayPos = position;
                    lastMarqueeView = autoScrollTextView;
                    autoScrollTextView.startMarquee();
                }
            }
        }

        CharSequence footer = item.getFooterText(mContext);
        if (TextUtils.isEmpty(footer)) {
            helper.setVisible(R.id.footer, false);
        } else {
            helper.setVisible(R.id.footer, true);
            helper.setText(R.id.footer, footer);
        }

        CharSequence bottom = item.getBottomText(mContext);
        if (TextUtils.isEmpty(bottom)) {
            helper.setVisible(R.id.bottom, false);
        } else {
            helper.setVisible(R.id.bottom, true);
            helper.setText(R.id.bottom, bottom);
        }
    }

    public boolean isMarquee(BaseViewHolder holder) {
        return holder.getAdapterPosition() == mCurPlayPos;
    }

    @Override
    public void setOnItemClick(View v, int position) {
        mClickToRunF = true;
        //避免重复点击刷新数据
        if (mAntiDoubleClickT) {
            PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
            if (playerInfo != null) {
                T item = getItem(position);
                if (playerInfo.getType() == item.getSourceType()
                        && playerInfo.getAlbumId() == item.getUUID()) {
                    PlayerSourceFacade.newSingleton().setSourceType(playerInfo.getType());
                    IPlayerControl playerControl = PlayerSourceFacade.newSingleton().getPlayerControl();
                    if (playerControl != null && playerControl.isCurPlayerInfoAlive()) {
                        if (!playerControl.isPlaying()) {
                            playerControl.play();
                        }
                        setCurPlayIndex(position);
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick();
                        }
                        return;
                    }
                }
            }
        }

        super.setOnItemClick(v, position);
        if (NetworkUtils.isConnected(mContext)) {
            setCurPlayIndex(position);
        }
    }

    protected boolean mClickToRunF = false;

    public void setClickToRun(boolean clickToRun) {
        mClickToRunF = clickToRun;
    }

    private void setCurPlayIndex(int playPos) {
        if (lastMarqueeView != null && lastMarqueeView.isStarting && mCurPlayPos == playPos) return;
        if (mCurPlayPos != -1) {
            mLastPlayPos = mCurPlayPos;
        }
        if (mLastPlayPos != -1) {
            notifyItemChanged(mLastPlayPos);
        }
        mCurPlayPos = playPos;
        notifyItemChanged(playPos);
    }

    /**
     * 如果当前列表有正在滚动的文字，停止滚动
     */
    public void stopMarquee() {
        if (lastMarqueeView != null && lastMarqueeView.isStarting) {
            lastMarqueeView.stopMarquee();
        }
    }

    public void updatePlayIndex(int playIndex) {
        this.mCurPlayPos = playIndex;
        notifyItemChanged(playIndex);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getTitleText(mContext).toString(), getData().get(position).getUUID() + "");
    }

    public interface AlbumItemClickListener {
        void onItemClick();
    }

    /**
     * 将 mAntiDoubleClickT = true 时 也将点击事件返回 用于新手引导弹框的消失时机
     *
     * @param clickListener
     */
    public void setAlbumItemClickListener(AlbumItemClickListener clickListener) {
        this.itemClickListener = clickListener;
    }
}
