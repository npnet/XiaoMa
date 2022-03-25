package com.xiaoma.music.player.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.player.model.AnimationState;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zs
 * @date 2018/12/17 0017.
 */
public class OnlinePlayListAdapter extends XMBaseAbstractBQAdapter<XMMusic, BaseViewHolder> {

    private String mCurProgress;
    private int mCurPosition = -1;
    private AnimationState state = AnimationState.LOADING;
    private AnimationDrawable playing;
    private Animation loading;
    private List<Integer> chargeType;

    public OnlinePlayListAdapter() {
        super(R.layout.item_playlist);
    }

    public void updateProgress(long curPos) {
        mCurProgress = TimeUtils.timeMsToMMSS(curPos);
        notifyItemChanged(mCurPosition);
    }

    public int getCurPosition() {
        return mCurPosition;
    }

    public void setSelectPosition(int position) {
        if (mCurPosition == position) {
            return;
        }
        mCurProgress = null;
        int mLastPosition = mCurPosition;
        mCurPosition = position;
        notifyItemChanged(mLastPosition);
        notifyItemChanged(position);
    }

    public void setChargeType(List<Integer> types) {
        if (types == null || types.isEmpty()) {
            return;
        }
        if (chargeType != null) {
            chargeType.clear();
        } else {
            chargeType = new ArrayList<>();
        }
        chargeType.addAll(types);
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, XMMusic item) {
        int position = helper.getAdapterPosition();
        TextView musicName = helper.getView(R.id.item_playlist_song_name);
        ImageView anim = helper.getView(R.id.iv_anim);
        musicName.setSelected(position == mCurPosition);
        String musicNameStr = item.getName();
        String artistStr = item.getSDKBean().artist;
        try {
            final String format = String.format("%s — %s", musicNameStr, artistStr);
            SpannableString spannableString = new SpannableString(format);
            spannableString.setSpan(new AbsoluteSizeSpan(30), 0, musicNameStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(23), musicNameStr.length() + 3, format.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            musicName.setText(spannableString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (chargeType != null && !chargeType.isEmpty()) {
            TextView charge = helper.getView(R.id.list_charge_tag);
            int type = 0;
            try {
                type = chargeType.get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (type) {
                case IKuwoConstant.XMMusicChargeType.NEED_VIP:
                case IKuwoConstant.XMMusicChargeType.NEED_VIP_SONG:
                case IKuwoConstant.XMMusicChargeType.NEED_VIP_ALBUM:
                    charge.setVisibility(View.VISIBLE);
                    charge.setText(mContext.getResources().getString(R.string.need_vip));
                    charge.setBackgroundResource(R.drawable.vip_tag_text_bg);
                    break;
                case IKuwoConstant.XMMusicChargeType.NEED_ALBUM:
                case IKuwoConstant.XMMusicChargeType.NEED_SONG:
                    charge.setVisibility(View.VISIBLE);
                    charge.setText(mContext.getResources().getString(R.string.need_buy));
                    charge.setBackgroundResource(R.drawable.buy_tag_text_bg);
                    break;
                default:
                    charge.setVisibility(View.GONE);
                    break;
            }
        }
        String duration = TimeUtils.timeSecToMMSS(item.getDuration());
        if (position == mCurPosition) {
            anim.setVisibility(View.VISIBLE);
            animation(anim);
            if (mCurProgress == null) {
                int currentPos = OnlineMusicFactory.getKWPlayer().getCurrentPos();
                String s = TimeUtils.timeMsToMMSS(currentPos);
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
    }

    private void animation(ImageView view) {
        if (state == null) {
            return;
        }
        view.clearAnimation();
        if (playing == null) {
            playing = (AnimationDrawable) XmSkinManager.getInstance().getDrawable(R.drawable.play_wave_anim);
        }
        if (loading == null) {
            loading = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
            loading.setRepeatCount(Animation.INFINITE);
            loading.setRepeatMode(Animation.RESTART);
        }
        switch (state) {
            case PLAY:
                view.setImageDrawable(playing);
                playing.start();
                break;
            case PAUSE:
                view.setImageDrawable(playing);
                playing.stop();
                break;
            case LOADING:
//                view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.player_list_state_loading));
//                view.setImageDrawable(XmSkinManager.getInstance().getDrawable(R.drawable.player_list_state_loading));
                view.setImageResource(R.drawable.player_list_state_loading);
                view.startAnimation(loading);
                break;
        }
    }

    public AnimationState getState() {
        return state;
    }

    public void setState(AnimationState state) {
        this.state = state;
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(mData.get(position).getName(), String.valueOf(mData.get(position).getRid()));
    }

    public void updatePlaying() {
        playing = (AnimationDrawable) XmSkinManager.getInstance().getDrawable(R.drawable.play_wave_anim);
    }
}