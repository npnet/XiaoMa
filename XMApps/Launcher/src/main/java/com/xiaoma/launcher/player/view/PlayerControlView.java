package com.xiaoma.launcher.player.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.player.callback.OnPlayControlListener;
import com.xiaoma.launcher.player.model.AudioMusicMarkInfo;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.receiver.UsbDetector;

/**
 * 桌面音频播放器view
 */
public class PlayerControlView extends ConstraintLayout implements View.OnClickListener {

    private static final int PROGRESS_MAX = 100;

    private OnPlayControlListener mPlayControlListener;

    private XmSeekBar playProgressBar;
    private ImageView ivAudioBg;
    private ImageView ivPlayPause;
    private ImageView ivCollect;
    private ImageView ivPre;
    private ImageView ivNext;
    private ImageView ivPlayList;

    private TextView tvTitle;
    private TextView tvSubTitle;

    private AudioInfo mAudioInfo;
    private ProgressInfo mProgressInfo;
    private int mDataSource;
    private int mAudioType;
    private int mPlayState;
    private boolean mFavoriteState;

    private Animation mAnimation;
    private Context mContext;

    public PlayerControlView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public PlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    public PlayerControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_player_controll, this);
        playProgressBar = findViewById(R.id.seekbar);
        playProgressBar.setMax(PROGRESS_MAX);
        playProgressBar.setDisable(true);

        ivAudioBg = findViewById(R.id.audio_bg);
        ivPlayPause = findViewById(R.id.play_pause);

        ivCollect = findViewById(R.id.iv_collect);
        ivPre = findViewById(R.id.iv_pre);
        ivNext = findViewById(R.id.iv_next);
        ivPlayList = findViewById(R.id.iv_enter_list);
        tvTitle = findViewById(R.id.tv_title);
        tvSubTitle = findViewById(R.id.tv_sub_title);

        ivPlayList.setEnabled(false);
        ivCollect.setVisibility(GONE);
        ivPlayList.setVisibility(GONE);
        ivCollect.setEnabled(false);
        ivCollect.setSelected(false);
        ivPre.setEnabled(false);
        ivNext.setEnabled(false);
        ivPlayPause.setEnabled(false);

        ivAudioBg.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
        tvSubTitle.setOnClickListener(this);

        ivPlayPause.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                AudioMusicMarkInfo audioMusicMarkInfo = new AudioMusicMarkInfo();
                if (mAudioInfo != null) {
                    audioMusicMarkInfo.id = mAudioInfo.getUniqueId() + "";
                    audioMusicMarkInfo.value = mAudioInfo.getAudioType() + "";
                }
                if (mProgressInfo != null) {
                    audioMusicMarkInfo.h = mProgressInfo.getPercent() + "";
                }
                return new ItemEvent(EventConstants.NormalClick.AUDIO_PLAYER, GsonUtil.toJson(audioMusicMarkInfo));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (mPlayControlListener != null) {
                    mPlayControlListener.onPlayOrPause(mAudioType, mPlayState);
                }
            }
        });
        ivCollect.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                AudioMusicMarkInfo audioMusicMarkInfo = new AudioMusicMarkInfo();
                if (mAudioInfo != null) {
                    audioMusicMarkInfo.id = mAudioInfo.getUniqueId() + "";
                    audioMusicMarkInfo.value = mAudioInfo.getAudioType() + "";
                }
                if (mProgressInfo != null) {
                    audioMusicMarkInfo.h = mProgressInfo.getPercent() + "";
                }
                return new ItemEvent(EventConstants.NormalClick.AUDIO_COLLECT, GsonUtil.toJson(audioMusicMarkInfo));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (mPlayControlListener != null) {
                    mPlayControlListener.onFavorite(mAudioType, mFavoriteState);
                }
            }
        });
        ivPre.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                AudioMusicMarkInfo audioMusicMarkInfo = new AudioMusicMarkInfo();
                if (mAudioInfo != null) {
                    audioMusicMarkInfo.id = mAudioInfo.getUniqueId() + "";
                    audioMusicMarkInfo.value = mAudioInfo.getAudioType() + "";
                }
                if (mProgressInfo != null) {
                    audioMusicMarkInfo.h = mProgressInfo.getPercent() + "";
                }
                return new ItemEvent(EventConstants.NormalClick.AUDIO_PREVIOUS, GsonUtil.toJson(audioMusicMarkInfo));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (mPlayControlListener != null) {
                    mPlayControlListener.onPre(mAudioType);
                }
            }
        });
        ivNext.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                AudioMusicMarkInfo audioMusicMarkInfo = new AudioMusicMarkInfo();
                if (mAudioInfo != null) {
                    audioMusicMarkInfo.id = mAudioInfo.getUniqueId() + "";
                    audioMusicMarkInfo.value = mAudioInfo.getAudioType() + "";
                }
                if (mProgressInfo != null) {
                    audioMusicMarkInfo.h = mProgressInfo.getPercent() + "";
                }
                return new ItemEvent(EventConstants.NormalClick.AUDIO_NEXT, GsonUtil.toJson(audioMusicMarkInfo));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (mPlayControlListener != null) {
                    mPlayControlListener.onNext(mAudioType);
                }
            }
        });
        ivPlayList.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                AudioMusicMarkInfo audioMusicMarkInfo = new AudioMusicMarkInfo();
                if (mAudioInfo != null) {
                    audioMusicMarkInfo.id = mAudioInfo.getUniqueId() + "";
                    audioMusicMarkInfo.value = mAudioInfo.getAudioType() + "";
                }
                if (mProgressInfo != null) {
                    audioMusicMarkInfo.h = mProgressInfo.getPercent() + "";
                }
                return new ItemEvent(EventConstants.NormalClick.AUDIO_LIST, GsonUtil.toJson(audioMusicMarkInfo));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (mPlayControlListener != null) {
                    mPlayControlListener.onStartListActivity(mPlayState);
                }
            }
        });
    }


    /**
     * 设置音源类型
     *
     * @param audioType
     */
    public void setAudioType(int audioType) {
        mAudioType = audioType;
    }

    /**
     * 设置进度信息
     *
     * @param progressInfo
     */
    public void setProgressInfo(ProgressInfo progressInfo) {
        mProgressInfo = progressInfo;
        playProgressBar.setProgress((int) (mProgressInfo.getPercent() * PROGRESS_MAX));
    }

    /**
     * 设置音频信息
     *
     * @param audioInfo
     */
    public void setAudioInfo(AudioInfo audioInfo) {
        KLog.d("XM_LOG_" + "setAudioInfo: " + (audioInfo == null));
        if (audioInfo == null) {
            setEmptview();
            mAudioInfo = null;
            return;
        }
        int type = audioInfo.getAudioType();
        if (type != mAudioType) {
            setEmptview();
            return;
        }
        ivPlayPause.setEnabled(true);
        ivPre.setEnabled(true);
        ivNext.setEnabled(true);
        playProgressBar.setVisibility(VISIBLE);
        KLog.d("XM_LOG_" + "setAudioInfo: " + type);
        showByType(type);
        //标题
        tvTitle.setTextColor(mContext.getColor(R.color.white));
        if (StringUtil.isEmpty(audioInfo.getTitle())) {
            tvTitle.setVisibility(GONE);
        } else {
            tvTitle.setText(audioInfo.getTitle());
        }
        //副标题
        if (StringUtil.isEmpty(audioInfo.getSubTitle())) {
            tvSubTitle.setVisibility(GONE);
        } else {
            tvSubTitle.setVisibility(VISIBLE);
            tvSubTitle.setText(audioInfo.getSubTitle());
        }
        RoundedCorners roundedCorners = new RoundedCorners(6);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(100, 100);
        //加载音乐封面
        if (type == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            //audioInfo有改变才重新加载图片
            try {
                if (mAudioInfo == null
                        || mAudioInfo.getUsbMusicPath() == null
                        || !mAudioInfo.getUsbMusicPath().equals(audioInfo.getUsbMusicPath())
                        || audioInfo.isHistory()) {
                    loadMusicPic(audioInfo, options);
                }
                if (UsbDetector.getInstance().isRemoveState()) {
                    setBgDefault();
                }
            } catch (Exception e) {
                e.printStackTrace();
                setBgDefault();
            }
        } else if (type == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO) {
            if (mAudioInfo == null
                    || mAudioInfo.getUniqueId() != audioInfo.getUniqueId()
                    || audioInfo.isHistory()) {
                loadMusicPic(audioInfo, options);
            }
        } else if (type == AudioConstants.AudioTypes.MUSIC_LOCAL_BT) {
            //蓝牙音乐使用音乐默认图片
            KLog.d("XM_LOG_" + "MUSIC_LOCAL_BT");
            ImageLoader.with(getContext())
                    .load(R.drawable.player_music)
                    .apply(options)
                    .into(ivAudioBg);
        } else {
            KLog.d("XM_LOG_" + "OTHER");
            ImageLoader.with(getContext())
                    .load(audioInfo.getCover())
                    .placeholder(R.drawable.player_xting)
                    .apply(options)
                    .into(ivAudioBg);
        }
        if (mAudioInfo != null && mAudioInfo.getUniqueId() != audioInfo.getUniqueId()) {
            playProgressBar.setProgress(0);
        }
        mAudioInfo = audioInfo;
    }

    private void showByType(int type) {
        switch (type) {
            //网络广播(网络FM)
            case AudioConstants.AudioTypes.XTING_NET_RADIO:
                tvTitle.setVisibility(VISIBLE);
                tvSubTitle.setVisibility(VISIBLE);
                ivCollect.setEnabled(true);
                ivPlayList.setEnabled(false);
                ivPre.setEnabled(false);
                ivNext.setEnabled(false);
                playProgressBar.setProgress(0);
                ivCollect.setVisibility(VISIBLE);
                ivPlayList.setVisibility(VISIBLE);
                break;

            //网络电台
            case AudioConstants.AudioTypes.XTING_NET_FM:
            case AudioConstants.AudioTypes.XTING_KOALA_ALBUM:
                tvTitle.setVisibility(VISIBLE);
                tvSubTitle.setVisibility(VISIBLE);
                ivPlayList.setEnabled(true);
                ivCollect.setEnabled(true);
                ivPre.setEnabled(true);
                ivNext.setEnabled(true);
                ivCollect.setVisibility(VISIBLE);
                ivPlayList.setVisibility(VISIBLE);
                break;

            //本地FM
            case AudioConstants.AudioTypes.XTING_LOCAL_FM:
                tvTitle.setVisibility(VISIBLE);
                ivCollect.setEnabled(true);
                ivPre.setEnabled(true);
                ivNext.setEnabled(true);
                ivPlayPause.setEnabled(true);
                playProgressBar.setProgress(0);
                playProgressBar.setVisibility(INVISIBLE);
                ivCollect.setVisibility(VISIBLE);
                ivPlayList.setVisibility(VISIBLE);
                break;

            //kw音乐
            case AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO:
                tvTitle.setVisibility(VISIBLE);
                tvSubTitle.setVisibility(GONE);
                if (mDataSource == AudioConstants.OnlineInfoSource.KUWO_RADIO) {
                    ivPlayList.setEnabled(false);

                } else {
                    ivPlayList.setEnabled(true);
                }
                ivCollect.setVisibility(VISIBLE);
                ivPlayList.setVisibility(VISIBLE);
                ivCollect.setEnabled(true);
                ivPre.setEnabled(true);
                ivNext.setEnabled(true);
                break;

            //蓝牙音乐
            case AudioConstants.AudioTypes.MUSIC_LOCAL_BT:
                ivPlayList.setEnabled(false);
                ivCollect.setEnabled(false);
                ivCollect.setSelected(false);
                ivCollect.setVisibility(GONE);
                ivPlayList.setVisibility(GONE);
                playProgressBar.setProgress(0);
                playProgressBar.setVisibility(INVISIBLE);
                break;

            //USB音乐
            case AudioConstants.AudioTypes.MUSIC_LOCAL_USB:
                ivCollect.setEnabled(false);
                ivCollect.setSelected(false);
                ivCollect.setVisibility(GONE);
                ivPlayList.setVisibility(VISIBLE);
                ivPlayList.setEnabled(true);
                break;
        }
    }

    private void setEmptview() {
        KLog.d("XM_LOG_" + "setEmptview: ");
        tvTitle.setText(getResources().getString(R.string.no_audio_info));
        RoundedCorners roundedCorners = new RoundedCorners(6);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(100, 100);
        ImageLoader.with(getContext())
                .load("")
                .placeholder(R.drawable.player_music)
                .apply(options)
                .into(ivAudioBg);
        tvTitle.setTextColor(mContext.getColor(R.color.gray_light));
        tvSubTitle.setVisibility(GONE);
        //enable
        ivPlayList.setEnabled(false);
        ivCollect.setEnabled(false);
        ivCollect.setSelected(false);
        ivPre.setEnabled(false);
        ivNext.setEnabled(false);
        ivPlayPause.setEnabled(false);
        if (mPlayState == AudioConstants.AudioStatus.EXIT) {
            ivPlayPause.setImageResource(R.drawable.btn_off_n);

        } else {
            ivPlayPause.setImageResource(R.drawable.selector_btn_play);
        }
        playProgressBar.setProgress(0);
    }

    private void loadMusicPic(AudioInfo audioInfo, RequestOptions options) {
        KLog.d("XM_LOG_" + "loadMusicPic: " + audioInfo.getAudioType());
        ImageLoader.with(getContext())
                .load(audioInfo)
                .placeholder(R.drawable.player_music)
                .apply(options)
                .into(ivAudioBg);
    }

    /**
     * 设置播放状态
     *
     * @param playState
     */
    public void setPlayState(int playState) {
        if (mAudioInfo == null) {
            return;
        }
        mPlayState = playState;

        switch (playState) {
            //加载中
            case AudioConstants.AudioStatus.LOADING:
                ivPlayPause.setImageResource(R.drawable.play_loading);
                ivPlayPause.setEnabled(false);
                setOperationEnable(true);
                startAnim();
                break;

            //播放状态
            case AudioConstants.AudioStatus.PLAYING:
                if (mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
                    ivPlayPause.setImageResource(R.drawable.selector_fm_btn_play);

                } else {
                    ivPlayPause.setImageResource(R.drawable.selector_btn_pause);
                }
                ivPlayPause.clearAnimation();
                ivPlayPause.setEnabled(true);
                setOperationEnable(true);
                break;

            //暂停状态
            case AudioConstants.AudioStatus.PAUSING:
            case AudioConstants.AudioStatus.STOPPED:
                //出错状态
            case AudioConstants.AudioStatus.ERROR:
                ivPlayPause.clearAnimation();
                ivPlayPause.setImageResource(R.drawable.selector_btn_play);
                if (mAudioInfo != null) {
                    ivPlayPause.setEnabled(true);
                }
                setOperationEnable(true);
                break;

            //关闭本地FM
            case AudioConstants.AudioStatus.EXIT:
                ivPlayPause.clearAnimation();
                ivPlayPause.setImageResource(R.drawable.btn_off_n);
                ivPlayPause.setEnabled(true);
                //本地电台收到状态为退出
                setOperationEnable(false);
                break;
        }

    }

    private void setOperationEnable(boolean enable) {
        ivPlayList.setEnabled(enable);
        ivCollect.setEnabled(enable);
        ivPre.setEnabled(enable);
        ivNext.setEnabled(enable);
    }

    private Animation getRotateAnimation() {
        if (mAnimation == null) {
            mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            mAnimation.setRepeatCount(Animation.INFINITE);
            mAnimation.setRepeatMode(Animation.RESTART);
        }
        return mAnimation;
    }

    private void startAnim() {
        if (getRotateAnimation().hasEnded()) {
            getRotateAnimation().reset();
        }
        ivPlayPause.startAnimation(getRotateAnimation());
    }

    /**
     * 设置收藏状态
     *
     * @param favoriteState
     */
    public void setFavoriteState(boolean favoriteState) {
        mFavoriteState = favoriteState;
        ivCollect.setSelected(favoriteState);
    }

    /**
     * 设置音源数据来源
     *
     * @param dataSource
     */
    public void setDataSource(int dataSource) {
        mDataSource = dataSource;
    }

    /**
     * 播放控制监听
     *
     * @param listener
     */
    public void setOnPlayControlListener(OnPlayControlListener listener) {
        this.mPlayControlListener = listener;
    }

    /**
     * 设置播放列表icon是否可以点击
     *
     * @param enable
     */
    public void setPlayListEnable(boolean enable) {
        this.ivPlayList.setEnabled(enable);
    }

    public void setBgDefault() {
        ivAudioBg.setImageResource(R.drawable.player_music);
    }

    @Override
    public void onClick(View v) {
        if (mAudioInfo == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.audio_bg:
            case R.id.tv_title:
            case R.id.tv_sub_title:
                if (isXtingAudio(mAudioInfo)) {
                    Intent intent = new Intent(LauncherConstants.ACTION_XTING, Uri.parse(LauncherConstants.XTING_PLAYER_URL));
                    mContext.startActivity(intent);

                } else if (isMusicAudio(mAudioInfo)) {
                    Intent intent = new Intent(LauncherConstants.ACTION_MUSIC, Uri.parse(LauncherConstants.MUSIC_PLAYER_URL));
                    mContext.startActivity(intent);
                }
                break;
        }
    }

    private boolean isXtingAudio(AudioInfo audioInfo) {
        int audioType = audioInfo.getAudioType();
        switch (audioType) {
            case AudioConstants.AudioTypes.XTING_KOALA_ALBUM:
            case AudioConstants.AudioTypes.XTING_LOCAL_FM:
            case AudioConstants.AudioTypes.XTING_NET_FM:
            case AudioConstants.AudioTypes.XTING_NET_RADIO:
                return true;

            default:
                return false;
        }
    }

    private boolean isMusicAudio(AudioInfo audioInfo) {
        int audioType = audioInfo.getAudioType();
        switch (audioType) {
            case AudioConstants.AudioTypes.MUSIC_KUWO_RADIO:
            case AudioConstants.AudioTypes.MUSIC_LOCAL_BT:
            case AudioConstants.AudioTypes.MUSIC_LOCAL_USB:
            case AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO:
                return true;

            default:
                return false;
        }
    }

//    @Override
//    public void onSyncSeek(int progress, boolean isFromUser) {
//
//    }
//
//    @Override
//    public void onOneSeekDone(int progress, boolean isFromUser) {
//        if (isFromUser && listener != null) {
//            listener.onSeekBarToProgress(progress);
//        }
//    }
//
//    @Override
//    public void onControl() {
//
//    }
//    private OnSeekBarProgressChangeListener listener;
//
//    public void setSeekBarProgressChangeListener(OnSeekBarProgressChangeListener listener) {
//        this.listener = listener;
//    }
//
//    public interface OnSeekBarProgressChangeListener {
//        void onSeekBarToProgress(int progress);
//    }
}
