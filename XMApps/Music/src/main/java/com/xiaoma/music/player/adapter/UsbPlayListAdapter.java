package com.xiaoma.music.player.adapter;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.R;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.player.model.AnimationState;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.log.KLog;

/**
 * @author zs
 * @date 2018/10/12 0012.
 */
public class UsbPlayListAdapter extends XMBaseAbstractBQAdapter<UsbMusic, BaseViewHolder> {

    private String mCurProgress;
    private String mCurrDuration;
    private int mCurPosition = -1;
    private AnimationState state = AnimationState.LOADING;
    private Animation loading;

    public UsbPlayListAdapter() {
        super(R.layout.item_playlist);
    }

    public void updateProgress(long curPos, long duration) {
        mCurProgress = TimeUtils.timeMsToMMSS(curPos);
        mCurrDuration = TimeUtils.timeMsToMMSS(duration);
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(mCurPosition);
            }
        });

    }

    public void setSelectPosition(int position) {
        if (mCurPosition == position) {
            return;
        }
        int mLastPosition = mCurPosition;
        mCurPosition = position;
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(mLastPosition);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, UsbMusic item) {
        try {
            if (item == null) {
                return;
            }
            int position = helper.getAdapterPosition();
            TextView musicName = helper.getView(R.id.item_playlist_song_name);
            ImageView anim = helper.getView(R.id.iv_anim);
            musicName.setSelected(position == mCurPosition);
            String musicNameStr = item.getName();
            String artistStr = item.getArtist();
            if (!TextUtils.isEmpty(artistStr)) {
                final String format = String.format("%s-%s", musicNameStr, artistStr);
                SpannableString spannableString = new SpannableString(format);
                spannableString.setSpan(new AbsoluteSizeSpan(30), 0, musicNameStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new AbsoluteSizeSpan(20), musicNameStr.length() + 1, format.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                musicName.setText(spannableString);
            } else {
                musicName.setText(StringUtil.optString(musicNameStr));
            }
            String duration = TimeUtils.timeSecToMMSS(item.getDuration());
            if (position == mCurPosition) {
                anim.setVisibility(View.VISIBLE);
                animation(anim);
                if (item.getDuration() == 0) {
                    duration = mCurrDuration;
                }
                if (mCurProgress == null) {
                    String s = TimeUtils.timeMsToMMSS(0L);
                    helper.setText(R.id.tv_duration, String.format(mContext.getResources().getString(R.string.item_playlist_duration), s, duration));
                } else {
                    helper.setText(R.id.tv_duration, String.format(mContext.getResources().getString(R.string.item_playlist_duration), mCurProgress,
                            duration));
                }
            } else {
                anim.clearAnimation();
                anim.setVisibility(View.INVISIBLE);
                helper.setText(R.id.tv_duration, duration);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setState(AnimationState state) {
        this.state = state;
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private synchronized void animation(ImageView view) {
        if (state == null) {
            return;
        }
        view.clearAnimation();
        if (loading == null) {
            loading = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
            loading.setRepeatCount(Animation.INFINITE);
            loading.setRepeatMode(Animation.RESTART);
        }
        switch (state) {
            case PLAY:
                getAnimationDrawable(view).start();
                KLog.d("UsbPlayListAdapter", "animation: " + "playing");
                break;
            case PAUSE:
                getAnimationDrawable(view).stop();
                KLog.d("UsbPlayListAdapter", "animation: " + "pause");
                break;
            case LOADING:
//                view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.player_list_state_loading));
                view.setImageResource(R.drawable.player_list_state_loading);
                view.startAnimation(loading);
                break;
        }
    }

    private AnimationDrawable getAnimationDrawable(ImageView view) {
        Drawable viewDrawable = view.getDrawable();
        AnimationDrawable drawable = null;
        if (viewDrawable instanceof AnimationDrawable) {
            drawable = (AnimationDrawable) viewDrawable;
        } else {
            drawable = (AnimationDrawable) XmSkinManager.getInstance().getDrawable(R.drawable.play_wave_anim);
            view.setImageDrawable(drawable);
        }
        return drawable;
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(mData.get(position).getName(), mData.get(position).getPath());
    }
}
