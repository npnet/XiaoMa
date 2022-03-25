package com.xiaoma.music.player.view.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.kuwo.image.KwImage;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * @author zs
 * @date 2018/10/11 0011.
 */
public class PlayerView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = "PlayerView";
    private int mPosY = 0;
    private int mCurPosY = 0;
    private int mCurPosX = 0;
    private int mPosX;

    private ImageView mPlayStatus;
    private ImageView mPlayStatusLoading;
    private ScrollImageView mScrollIv;
    private OnOperationListener mOnOperationListener;
    private Animation mAnimation;

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        inflate(getContext(), R.layout.view_player, this);
        mScrollIv = findViewById(R.id.view_player_scroll_iv);
        findViewById(R.id.view_player_pre).setOnClickListener(this);
        findViewById(R.id.view_player_next).setOnClickListener(this);
        mPlayStatus = findViewById(R.id.item_view_player_iv_status);
        mPlayStatusLoading = findViewById(R.id.item_view_player_iv_status_loading);
        TextView textView = findViewById(R.id.item_view_player_tv_slide);
        mPlayStatus.setOnClickListener(this);

        mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        if (AudioSource.ONLINE_MUSIC == AudioSourceManager.getInstance().getCurrAudioSource()) {
            textView.setVisibility(VISIBLE);
        } else {
            textView.setVisibility(GONE);
        }
    }

    public void setOnOperationListener(OnOperationListener listener) {
        mOnOperationListener = listener;
    }

    @NormalOnClick({EventConstants.NormalClick.playOrPause, EventConstants.NormalClick.preMusic, EventConstants.NormalClick.nextMusic})
    @ResId({R.id.item_view_player_iv_status, R.id.view_player_pre, R.id.view_player_next})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_view_player_iv_status:
                mOnOperationListener.musicOperate();
                break;
            case R.id.view_player_pre:
                mOnOperationListener.preMusic();
                break;
            case R.id.view_player_next:
                mOnOperationListener.nextMusic();
                break;
            default:
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPosY = (int) event.getY();
                mPosX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurPosY = (int) event.getY();
                mCurPosX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > getMeasuredHeight() / 6)
                        && Math.abs(mCurPosX - mPosX) <= getMeasuredWidth()) {
                    if (AudioSource.ONLINE_MUSIC != AudioSourceManager.getInstance().getCurrAudioSource()) {
                        break;
                    }
                    if (mOnOperationListener != null) {
                        mOnOperationListener.scrollUp();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setKwImageCover(final XMMusic music) {
        if (XMMusic.isEmpty(music)) {
            return;
        }
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    ImageLoader.with(getContext())
                            .load(new KwImage(music.getSDKBean(), IKuwoConstant.IImageSize.SIZE_240))
                            .placeholder(R.drawable.iv_default_cover)
                            .transform(Transformations.getRoundedCorners())
                            .into(mScrollIv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setUsbImageCover(final UsbMusic music) {
        if (music == null) {
            return;
        }
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    ImageLoader.with(getContext())
                            .load(music)
                            .placeholder(R.drawable.iv_default_cover)
                            .transform(Transformations.getRoundedCorners())
                            .into(mScrollIv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setBTImageCover(final BTMusic music) {
        if (music == null) {
            return;
        }
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    ImageLoader.with(getContext())
                            .load(music.getBitmap())
                            .placeholder(R.drawable.iv_default_cover)
                            .transform(Transformations.getRoundedCorners())
                            .into(mScrollIv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updatePlayStatus(@PlayStatus int status) {
        switch (status) {
            case PlayStatus.BUFFERING:
                mPlayStatus.setImageResource(R.drawable.icon_loading_selector);
                mPlayStatusLoading.setVisibility(VISIBLE);
                mPlayStatusLoading.startAnimation(mAnimation);
                break;
            case PlayStatus.PLAYING:
                mPlayStatusLoading.clearAnimation();
                mPlayStatusLoading.setVisibility(GONE);
                mPlayStatus.setImageResource(R.drawable.icon_pause_selector);
                break;
            case PlayStatus.FAILED:
            case PlayStatus.STOP:
            case PlayStatus.PAUSE:
                mPlayStatusLoading.clearAnimation();
                mPlayStatusLoading.setVisibility(GONE);
                mPlayStatus.setImageResource(R.drawable.icon_play_selector);
                break;
            default:
                break;
        }
    }

    public interface OnOperationListener {

        void musicOperate();

        void nextMusic();

        void preMusic();

        void scrollUp();
    }
}
