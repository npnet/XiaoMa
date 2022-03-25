package com.xiaoma.music.player.ui;

import android.arch.lifecycle.Observer;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.player.vm.BaseThumbPlayerVM;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.ui.view.ReflectionImageView;
import com.xiaoma.utils.StringUtil;

import org.simple.eventbus.EventBus;

/**
 * Created by ZYao.
 * Date ：2018/10/10 0010
 */
public abstract class AbstractThumbPlayerFragment extends VisibleFragment implements View.OnClickListener {
    private ReflectionImageView mCoverIv;
    private ImageView mPlayStatusIv;
    private ImageView chargeTag;
    private AutoScrollTextView mMusicInfo;

    public enum Status {
        playing(R.drawable.icon_thumb_pause),
        pause(R.drawable.icon_thumb_play),
        loading(R.drawable.icon_thumb_loading);

        @DrawableRes
        private int res;

        Status(@DrawableRes int res) {
            this.res = res;
        }

        public int getRes() {
            return res;
        }
    }

    /**
     * 跳转到播放详情
     */
    protected abstract void skipToPlayer();

    /**
     * 播放/暂停切换
     */
    protected abstract void switchPlay();

    protected abstract BaseThumbPlayerVM getThumbPlayerVM();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thumb_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCoverIv = view.findViewById(R.id.fragment_thumb_iv_cover);
        mPlayStatusIv = view.findViewById(R.id.fragment_thumb_iv_status);
        chargeTag = view.findViewById(R.id.music_charge_tag);
        view.findViewById(R.id.fragment_thumb_fl_cover).setOnClickListener(this);

        mMusicInfo = view.findViewById(R.id.fragment_thumb_tv_music_info);
        mMusicInfo.setOnClickListener(this);
        mPlayStatusIv.setVisibility(View.VISIBLE);
        BaseThumbPlayerVM vm = getThumbPlayerVM();
        if (vm == null) {
            throw new NullPointerException("#getThumbPlayerVM() must return non null !!!");
        }
        vm.getPlayingTitle().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                updatePlayingTitle(StringUtil.optString(title));
            }
        });
        vm.getPlayingPicModel().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(@Nullable Object model) {
                updatePlayingPic(model);
            }
        });
        vm.getPlayingPicBitmap().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    ImageLoader.with(AbstractThumbPlayerFragment.this)
                            .load(bitmap)
                            .transform(Transformations.getRoundedCorners())
                            .into(mCoverIv);
                } else {
                    ImageLoader.with(AbstractThumbPlayerFragment.this)
                            .load(R.drawable.iv_default_cover)
                            .transform(Transformations.getRoundedCorners())
                            .into(mCoverIv);
                }
            }
        });
        vm.getPlayingStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer status) {
                if (status == null) {
                    status = PlayStatus.INIT;
                }
                updatePlayingStatus(status);
            }
        });
    }

    public void showCharge() {
        if (chargeTag != null) {
            chargeTag.setVisibility(View.VISIBLE);
        }
    }

    public void hideCharge() {
        if (chargeTag != null) {
            chargeTag.setVisibility(View.GONE);
        }
        //隐藏开通vip气泡
        EventBus.getDefault().post(false, EventBusTags.MAIN_SHOW_BUY_VIP_VIEW);
    }

    /**
     * 刷新封面
     */
    private void updatePlayingPic(Object model) {
        ImageLoader.with(this)
                .load(model)
                .placeholder(R.drawable.iv_default_cover)
                .error(R.drawable.iv_default_cover)
                .transform(Transformations.getRoundedCorners())
                .into(mCoverIv);
    }

    /**
     * 刷新标题
     */
    private void updatePlayingTitle(String title) {
        mMusicInfo.setText(title);
    }

    /**
     * 刷新播放状态
     */
    private void updatePlayingStatus(int status) {
        mPlayStatusIv.clearAnimation();
        mMusicInfo.stopMarquee();
        boolean startMarquee = false;
        boolean showLoadingAnim = false;
        Status curStatus;
        switch (status) {
            case PlayStatus.BUFFERING:
                curStatus = Status.loading;
                showLoadingAnim = true;
                break;
            case PlayStatus.PLAYING:
                curStatus = Status.playing;
                startMarquee = true;
                break;
            case PlayStatus.FAILED:
            case PlayStatus.INIT:
            case PlayStatus.PAUSE:
            case PlayStatus.STOP:
            default:
                curStatus = Status.pause;
                break;
        }
        ImageLoader.with(this)
                .load(curStatus.getRes())
                .into(mPlayStatusIv);
        if (startMarquee) {
            mMusicInfo.startMarquee();
        } else {
            mMusicInfo.stopMarquee();
        }
        if (showLoadingAnim) {
            Animation anim = mPlayStatusIv.getAnimation();
            if (anim == null) {
                anim = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
                anim.setRepeatCount(Animation.INFINITE);
                anim.setRepeatMode(Animation.RESTART);
                anim.start();
                mPlayStatusIv.startAnimation(anim);
            }
        } else {
            mPlayStatusIv.clearAnimation();
        }
    }

    @NormalOnClick({EventConstants.NormalClick.playOrPause, EventConstants.NormalClick.skipToPlayer})
    @ResId({R.id.fragment_thumb_fl_cover, R.id.fragment_thumb_tv_music_info})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_thumb_fl_cover:
                switchPlay();
                break;
            case R.id.fragment_thumb_tv_music_info:
                final int currAudioSource = AudioSourceManager.getInstance().getCurrAudioSource();
                if (currAudioSource != AudioSource.NONE) {
                    skipToPlayer();
                }
                break;
        }
    }
}
