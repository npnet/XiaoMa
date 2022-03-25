package com.xiaoma.songname.ui.fragment;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.songname.R;
import com.xiaoma.songname.common.manager.AudioFocusManager;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

/**
 * 右侧播放音乐页面
 */
public class PlayMusicFragment extends BaseFragment implements View.OnClickListener,
        AudioFocusManager.IAudioFocusListener {
    public static final int PLAYING_STATE = 1;//播放
    public static final int PAUSE_STATE = 2;//暂停
    public static final int OVER_STATE = 3;//结束

    private int mPlayState = PAUSE_STATE;

    public static final int ENTER_PLAY_MUSIC = 1;
    public static final int ENTER_ANSWER = 2;

    private TextView tvTitle;
    private ImageView ivPlayer;//唱片
    private ImageView ivState;//状态
    private ImageView ivNeedle;//指针
    private ImageView ivNeedleXY;
    private TextView tvGameOver;
    //左侧答题页面状态
    private int mAnswerPageState;
    //唱片旋转动画
    private ObjectAnimator mPlayerAnimation;
    //指针移到唱片位置
    private ObjectAnimator playingAnimator;
    //指针离开唱片位置
    private ObjectAnimator pauseAnimator;
    private MediaPlayer mediaPlayer;
    private NotifyAnswerPageListener answerPageListener;
    private boolean pauseByClick;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_music, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        ivPlayer = view.findViewById(R.id.iv_player);
        ivState = view.findViewById(R.id.iv_state);
        ivNeedle = view.findViewById(R.id.iv_needle);
        ivNeedleXY = view.findViewById(R.id.iv_needle_ding);
        tvGameOver = view.findViewById(R.id.tv_game_over);
        ivState.setOnClickListener(this);
        ivState.setEnabled(false);
        AudioFocusManager.getInstance().registerListener(this);
    }

    private void initData() {
        //唱片旋转动画
        mPlayerAnimation = ObjectAnimator.ofFloat(ivPlayer, "rotation", 0.0f, 360.0f);
        mPlayerAnimation.setDuration(15000);//设定转一圈的时间
        mPlayerAnimation.setRepeatCount(Animation.INFINITE);//设定无限循环
        mPlayerAnimation.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        mPlayerAnimation.setInterpolator(new LinearInterpolator());// 匀速
        //旋转基准点
        ivNeedle.setPivotX(15);
        ivNeedle.setPivotY(0);
        //指针移动到唱片位置
        playingAnimator = ObjectAnimator.ofFloat(ivNeedle, "rotation", 0.0f, 35.0f);
        playingAnimator.setDuration(1000);
        //指针从唱片位置移开
        pauseAnimator = ObjectAnimator.ofFloat(ivNeedle, "rotation", 35.0f, 0.0f);
        pauseAnimator.setDuration(1000);
    }

    public void setTitleText(String titleText) {
        tvTitle.setText(titleText);
    }

    /**
     * 设置播放源
     *
     * @param playSource
     */
    public void setPlaySource(String playSource) {
        if (StringUtil.isEmpty(playSource)) {
            XMToast.toastException(getContext(), "playSource is empty");
            return;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.reset();
            //播放准备监听
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    KLog.d("music play onPrepared");
                }
            });
            //播放完成监听
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    KLog.d("music play onCompletion");
                    if (answerPageListener != null) {
                        answerPageListener.playEnd();
                    }
                    notifyPlayState(PAUSE_STATE, ENTER_PLAY_MUSIC);
                }
            });
            //播放失败监听
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    KLog.e("music play error");
                    XMToast.toastException(getContext(), "music play error");
                    return true;
                }
            });

            mediaPlayer.setDataSource(playSource);
            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新状态
     *
     * @param state
     */
    public void notifyPlayState(int state, int control) {
        if (mPlayState == state) {
            return;
        }
        this.mPlayState = state;
        switch (state) {
            case PLAYING_STATE:
                ivState.setVisibility(View.VISIBLE);
                ivNeedle.setVisibility(View.VISIBLE);
                ivNeedleXY.setVisibility(View.VISIBLE);
                tvGameOver.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                ivState.setImageResource(R.drawable.icon_pause);
                playingAnimator.start();

                if (mediaPlayer != null) {
                    if (pauseByClick) {
                        //继续播放
                        mediaPlayer.start();
                        mPlayerAnimation.resume();

                    } else {
                        //重新播放
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                        mPlayerAnimation.start();
                    }
                }
                if (answerPageListener != null) {
                    answerPageListener.playStart();
                    if (mAnswerPageState == AnswerFragment.RECORD_ANSWER_STATE ||
                            mAnswerPageState == AnswerFragment.ANSWER_FAILED_STATE) {
                        answerPageListener.playStartAgain();
                    }
                }
                break;

            case PAUSE_STATE:
                ivState.setVisibility(View.VISIBLE);
                ivNeedle.setVisibility(View.VISIBLE);
                ivNeedleXY.setVisibility(View.VISIBLE);
                tvGameOver.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                ivState.setImageResource(R.drawable.icon_play);
                mPlayerAnimation.pause();
                pauseAnimator.start();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                if (answerPageListener != null && mediaPlayer != null && mediaPlayer.isPlaying()) {
                    answerPageListener.playPause();
                }
                if (control == ENTER_ANSWER) {
                    pauseByClick = false;

                } else {
                    pauseByClick = true;
                }
                break;

            case OVER_STATE:
                ivState.setVisibility(View.GONE);
                ivNeedle.setVisibility(View.GONE);
                ivNeedleXY.setVisibility(View.GONE);
                tvTitle.setVisibility(View.INVISIBLE);
                tvGameOver.setVisibility(View.VISIBLE);
                mPlayerAnimation.cancel();
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                break;
        }
    }

    public void setPlayBtnEnable(boolean enable) {
        ivState.setEnabled(enable);
    }

    public void setAnswerPageState(int state) {
        this.mAnswerPageState = state;
    }

    @Override
    public void onClick(View v) {
        int action = -1;
        switch (v.getId()) {
            case R.id.iv_state:
                if (mPlayState == PLAYING_STATE) {
                    action = PAUSE_STATE;

                } else if (mPlayState == PAUSE_STATE) {
                    action = PLAYING_STATE;
                }
                break;
        }
        notifyPlayState(action, ENTER_PLAY_MUSIC);
    }

    public void setAnswerPageListener(NotifyAnswerPageListener listener) {
        this.answerPageListener = listener;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void stop() {
        KLog.d("stop---stop");
        notifyPlayState(PAUSE_STATE, ENTER_PLAY_MUSIC);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioFocusManager.getInstance().unRegisterListener(this);
    }

    public interface NotifyAnswerPageListener {
        /**
         * 开始播放
         */
        void playStart();

        /**
         * 重新播放
         */
        void playStartAgain();

        /**
         * 播放暂停
         */
        void playPause();

        /**
         * 播放结束
         */
        void playEnd();
    }
}
