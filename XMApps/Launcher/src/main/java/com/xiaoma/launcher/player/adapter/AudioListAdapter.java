package com.xiaoma.launcher.player.adapter;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * 音频列表adapter
 * Created by zhushi.
 * Date: 2019/1/14
 */
public class AudioListAdapter extends XMBaseAbstractBQAdapter<AudioInfo, BaseViewHolder> {
    private Animation mLoadingAnimation;
    private RoundedCorners mRoundedCorner;

    public AudioListAdapter() {
        super(R.layout.item_audio_list);
    }

    public RoundedCorners getRoundedCorner() {
        if (mRoundedCorner == null) {
            mRoundedCorner = new RoundedCorners(4);
        }
        return mRoundedCorner;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final AudioInfo item) {
        if (item == null) {
            return;
        }
        //背景
        ImageView ivCover = helper.getView(R.id.iv_play_cover);
        //播放暂停
        ImageView ivCtrol = helper.getView(R.id.iv_control);
        //蒙层
        View view = helper.getView(R.id.iv_cover);

        if (item.isCenterItem()) {
            view.setVisibility(View.GONE);
            ivCtrol.setVisibility(View.VISIBLE);
            //同步播放状态
            syncPlayState(item, ivCtrol);

        } else {
            view.setVisibility(View.VISIBLE);
            ivCtrol.setVisibility(View.GONE);
        }

        int audioType = item.getAudioType();
        //kw和usb列表背景图使用对象加载
        if (audioType == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO ||
                audioType == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            ImageLoader.with(mContext)
                    .load(item)
                    .placeholder(R.drawable.player_music)
                    .transform(getRoundedCorner())
                    .into(ivCover);

        } else {
            ImageLoader.with(mContext)
                    .load(item.getCover())
                    .placeholder(R.drawable.player_xting)
                    .transform(getRoundedCorner())
                    .into(ivCover);
        }
    }

    /**
     * 同步播放状态
     */
    private void syncPlayState(AudioInfo item, ImageView mIvControl) {
        mLoadingAnimation = getRotateAnimation(mContext);

        //本地FM不做播放控制
        if (item.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
            mIvControl.setVisibility(View.GONE);
            return;
        }

        mIvControl.clearAnimation();
        //音频状态icon
        if (item.getPlayState() == AudioConstants.AudioStatus.PLAYING) {
            //播放中状态
            mLoadingAnimation.reset();
            mLoadingAnimation.cancel();

            mIvControl.setVisibility(View.VISIBLE);
            mIvControl.setImageResource(R.drawable.selector_btn_pause);

        } else if (item.getPlayState() == AudioConstants.AudioStatus.LOADING) {
            //Loading状态
            mIvControl.setVisibility(View.VISIBLE);
            mIvControl.setImageResource(R.drawable.play_loading);
            startAnim(mIvControl);

        } else {
            //暂停中
            mLoadingAnimation.reset();
            mLoadingAnimation.cancel();
            mIvControl.setVisibility(View.VISIBLE);

            mIvControl.setImageResource(R.drawable.selector_btn_play);
        }
    }

    private Animation getRotateAnimation(Context context) {
        mLoadingAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        mLoadingAnimation.setRepeatCount(Animation.INFINITE);
        mLoadingAnimation.setRepeatMode(Animation.RESTART);

        return mLoadingAnimation;
    }

    /**
     * star加载动画
     *
     * @param ivControl
     */
    private void startAnim(ImageView ivControl) {
        if (mLoadingAnimation.hasEnded()) {
            mLoadingAnimation.reset();
        }
        ivControl.startAnimation(mLoadingAnimation);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.AUDIO_LIST_ITEM_PLAY_TYPE, "");
    }
}
