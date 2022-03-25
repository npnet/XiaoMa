package com.xiaoma.xting.player.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.util.Util;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.utils.Transformations;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/13
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_PLAYER)
public class PlayerControlView extends ConstraintLayout implements View.OnClickListener {

    public static final String TAG = "PlayerControlView";
    private ImageView mPlayStateIV;
    private ImageView mPlayStateBgIV;
    private int mPlayStatus;
    private Animation mAnimation;
    private Context mContext;
    private OnPlayControlListener mControlListener;
    private ImageView mCoverRV;
    private ImageView mPreIV;
    private ImageView mNextIV;
    private TextView mTvHint;
    private ImageView mUpArrowIV;

    public PlayerControlView(Context context) {
        this(context, null);
    }

    public PlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_player_controll, this);

        mPreIV = findViewById(R.id.ivPre);
        mNextIV = findViewById(R.id.ivNext);
        mCoverRV = findViewById(R.id.ivCover);
        mPlayStateIV = findViewById(R.id.ivStateLevel);
        mPlayStateBgIV = findViewById(R.id.ivStateLevelBg);
        mTvHint = findViewById(R.id.tvHint);
        mPlayStateIV.setOnClickListener(this);
        mPreIV.setOnClickListener(this);
        mNextIV.setOnClickListener(this);

        mUpArrowIV = findViewById(R.id.ivDirection);
    }

    public void setTvHint(String text) {
        mTvHint.setText(text);
        if (text.isEmpty()) {
            mUpArrowIV.setVisibility(INVISIBLE);
        } else {
            mUpArrowIV.setVisibility(VISIBLE);
        }
    }

    public void notifyPlayState(@PlayerStatus int status) {
        mPlayStatus = status;
        mPlayStateIV.setImageLevel(status);
        if (status == PlayerStatus.LOADING) {
            startAnim();
            mPlayStateIV.setEnabled(false);
        } else {
            mPlayStateIV.setEnabled(true);
            getRotateAnimation().reset();
            getRotateAnimation().cancel();
        }
    }

    public void notifyCover(String url) {
        if (mContext == null) {
            return;
        } else if (Util.isOnMainThread() && !(mContext instanceof Application)) {
            if (mContext instanceof FragmentActivity) {
                if (((FragmentActivity) mContext).isFinishing() || ((FragmentActivity) mContext).isDestroyed()) {
                    return;
                }
            } else if (mContext instanceof Activity) {
                if (((Activity) mContext).isFinishing() || ((Activity) mContext).isDestroyed()) {
                    return;
                }
            }
        }
        ImageLoader.with(getContext())
                .load(url)
                .transform(Transformations.getRoundedCorners())
                .placeholder(R.drawable.fm_default_cover)
                .into(mCoverRV);
    }

    public void notifyPreAndNextVisibility(boolean preAndNextVisibleTF) {
        mPreIV.setVisibility(preAndNextVisibleTF ? VISIBLE : INVISIBLE);
        mNextIV.setVisibility(preAndNextVisibleTF ? VISIBLE : INVISIBLE);
        mPreIV.setEnabled(preAndNextVisibleTF);
        mNextIV.setEnabled(preAndNextVisibleTF);
    }

    public void notifyWithTypeAndSubType(PlayerInfo playerInfo) {
        int type = playerInfo.getType();
        if (type == PlayerSourceType.RADIO_YQ) {
            if (playerInfo.getAlbumId() == -1) {
                mPlayStateBgIV.setVisibility(GONE);
                mPlayStateIV.setImageLevel(PlayerStatus.NONE + 1);
                mPlayStateIV.setEnabled(false);
            } else {
                mPlayStateBgIV.setVisibility(VISIBLE);
                mPlayStateIV.setVisibility(VISIBLE);
                mPlayStateIV.setEnabled(true);
                mPlayStateIV.setImageLevel(PlayerStatus.NONE);
            }
            notifyPreAndNextVisibility(true);
        } else {
            mPlayStateBgIV.setVisibility(VISIBLE);
            mPlayStateIV.setVisibility(VISIBLE);
            mPlayStateIV.setEnabled(true);
            if (type == PlayerSourceType.HIMALAYAN) {
                notifyPreAndNextVisibility(playerInfo.getSourceSubType() == PlayerSourceSubType.TRACK);
            } else {
                notifyPreAndNextVisibility(true);
            }

        }
    }

    private Animation getRotateAnimation() {
        if (mAnimation == null) {
            mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
            mAnimation.setRepeatCount(Animation.INFINITE);
            mAnimation.setRepeatMode(Animation.RESTART);
        }
        return mAnimation;
    }

    private void startAnim() {
        if (getRotateAnimation().hasEnded()) {
            getRotateAnimation().reset();
        }
        mPlayStateIV.startAnimation(getRotateAnimation());
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.ivStateLevel) {
            if (mControlListener != null) {
                mControlListener.onPlay(mPlayStatus == PlayerStatus.PLAYING);
            }
        } else {
            if (XtingUtils.isInvalidClick()) {
                Log.d(TAG, "fast click");
                return;
            }
            if (vId == R.id.ivPre) {
                if (mControlListener != null) {
                    mControlListener.onPre();
                }
            } else if (vId == R.id.ivNext) {
                if (mControlListener != null) {
                    mControlListener.onNext();
                }
            }
        }

    }


    public interface OnPlayControlListener {

        void onPre();

        void onNext();

        void onPlay(boolean playing);
    }

    public void setOnPlayControlListener(OnPlayControlListener listener) {
        this.mControlListener = listener;
    }

}
