package com.xiaoma.launcher.player.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

/**
 * 收藏adapter
 * Created by zhushi.
 * Date: 2019/2/19
 */
public class FavoriteAudioAdapter extends XMBaseAbstractBQAdapter<AudioInfo, BaseViewHolder> {

    private Animation mAnimation;
    private Context mContext;
    private RoundedCorners mCorners;

    public FavoriteAudioAdapter(@Nullable List<AudioInfo> data, Context context) {
        super(R.layout.item_audio_play, data);
        this.mContext = context;
    }

    private RoundedCorners getCorners() {
        if (mCorners == null) {
            mCorners = new RoundedCorners(4);
        }
        return mCorners;
    }

    @Override
    protected void convert(BaseViewHolder helper, AudioInfo item) {
        //title
        TextView title = helper.getView(R.id.tv_title);
        title.setText(item.getTitle());
        ImageView ivControl = helper.getView(R.id.iv_control);
        ImageView fmIvControl = helper.getView(R.id.fm_iv_control);
        AnimationDrawable playingAnimation = (AnimationDrawable) XmSkinManager.getInstance().getDrawable(R.drawable.play_wave_anim);
        fmIvControl.setImageDrawable(playingAnimation);

        mAnimation = getRotateAnimation(mContext);
        helper.addOnClickListener(R.id.iv_control);
        helper.addOnClickListener(R.id.iv_play_cover);
        //背景
        ImageLoader.with(mContext)
                .load(item.getCover())
                .placeholder(R.drawable.player_xting)
                .transform(getCorners())
                .into((ImageView) helper.getView(R.id.iv_play_cover));

        fmIvControl.clearAnimation();
        if (item.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
            ivControl.setVisibility(View.GONE);
            if (item.getPlayState() == AudioConstants.AudioStatus.PLAYING) {
                playingAnimation.start();
                fmIvControl.setVisibility(View.VISIBLE);

            } else {
                fmIvControl.setVisibility(View.GONE);
            }

        } else {
            ivControl.setVisibility(View.VISIBLE);
            fmIvControl.setVisibility(View.GONE);
            //音频状态icon
            if (item.getPlayState() == AudioConstants.AudioStatus.PLAYING) {
                //播放中状态
                mAnimation.reset();
                mAnimation.cancel();
                ivControl.setImageResource(R.drawable.selector_btn_pause);

            } else if (item.getPlayState() == AudioConstants.AudioStatus.LOADING) {
                //Loading状态
                ivControl.setImageResource(R.drawable.play_loading);
                startAnim(ivControl);

            } else {
                //暂停中
                mAnimation.reset();
                mAnimation.cancel();
                ivControl.setImageResource(R.drawable.selector_btn_play);
            }
        }
    }

    private Animation getRotateAnimation(Context context) {
        mAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        return mAnimation;
    }

    private void startAnim(ImageView ivControl) {
        if (mAnimation.hasEnded()) {
            mAnimation.reset();
        }
        ivControl.startAnimation(mAnimation);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.XTING_FAVORITE_ITEM, "" + position);
    }
}
