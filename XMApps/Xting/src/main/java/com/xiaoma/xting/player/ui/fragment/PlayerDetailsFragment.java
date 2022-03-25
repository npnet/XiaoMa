package com.xiaoma.xting.player.ui.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.IPlayerInfo;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.player.ui.FMPlayerActivity;
import com.xiaoma.xting.player.view.PlayerControlView;
import com.xiaoma.xting.player.view.PlayerListPopView;
import com.xiaoma.xting.player.view.XmSeekBar;
import com.xiaoma.xting.utils.XtingFastPlayController;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/30
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_PLAYER)
public class PlayerDetailsFragment extends VisibilityFragment implements View.OnTouchListener, View.OnClickListener {

    public static final int UP_SLOOP = 24;
    public static final float UP_VALID_DEGREE_45 = 0.78f;

    protected PlayerControlView mPlayerControlView;
    private XmSeekBar mSeekBar;
    private TextView mRadioNameTV,
            mProgressTV, mDurationTV;
    private AutoScrollTextView mProgramNameTV;
    private Group mProgressGroup;
    private ImageView mShrinkIV, mShowPlayListIV, mLikeIV, mListenToRecoginizeIV;
    private PlayerInfo mPlayerInfo;

    private boolean mIsSeekBarControl;
    private PlayerListPopView mPlayerListPopView;
    private boolean mSloopF = false;
    private float mLastY, mLastX;
    //    private TextView mAudioSourceTV;
    private ImageView mAudioSourceIV;

    public static PlayerDetailsFragment newInstance() {
        return new PlayerDetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preBindView();
        bindView(view);
        afterBindView(view);
    }

    private IPlayerInfo mPlayerInfoListener = new IPlayerInfo() {

        @Override
        public void onPlayerInfoChanged(PlayerInfo playerInfo) {
            if (playerInfo != null) {
                mPlayerInfo = playerInfo;
                int type = playerInfo.getType();
                mPlayerListPopView.notifyPlayIndex();
                mPlayerControlView.notifyCover(playerInfo.getImgUrl());
                mPlayerControlView.notifyWithTypeAndSubType(playerInfo);

                if (type == PlayerSourceType.HIMALAYAN
                        || type == PlayerSourceType.RADIO_XM) {

                    showHimalayanInfo();
                    mAudioSourceIV.setImageLevel(PlayerSourceType.HIMALAYAN);
                } else if (type == PlayerSourceType.RADIO_YQ) {
                    showYQRadioInfo();
                    mAudioSourceIV.setImageLevel(PlayerSourceType.RADIO_YQ);
                } else if (type == PlayerSourceType.KOALA) {
                    showKoalaInfo();
                    mAudioSourceIV.setImageLevel(PlayerSourceType.KOALA);
                }
            } else {
                Log.d("Jir", "[ onPlayerInfoChanged Null] at " + Log.getStackTraceString(new Throwable())
                        + "\n * ");
            }
        }

        private void showHimalayanInfo() {
            mPlayerControlView.setTvHint(getString(R.string.slide_up_to_switch_function));
            String programName = mPlayerInfo.getProgramName();
            if (mPlayerInfo.getSourceSubType() == PlayerSourceSubType.RADIO) {
                mProgressGroup.setVisibility(View.INVISIBLE);
                if (!mPlayerInfo.isFromRecordF()) {
                    mRadioNameTV.setText(mContext.getString(R.string.str_radio_time_show, mPlayerInfo.getExtra1(), mPlayerInfo.getExtra2()));
                    mProgramNameTV.setText(TextUtils.isEmpty(programName) ? mPlayerInfo.getAlbumName() : programName);
                    mPlayerListPopView.notifyPlayIndex();
                } else {
                    mProgramNameTV.setText(mPlayerInfo.getAlbumName());
                }
            } else {
                mRadioNameTV.setText(mPlayerInfo.getAlbumName());
                mProgramNameTV.setText(programName);
                mProgressGroup.setVisibility(View.VISIBLE);

//                onPlayerProgress(mPlayerInfo.getProgress(), mPlayerInfo.getDuration());
            }
        }

        private void showYQRadioInfo() {
//            mAudioSourceTV.setText("");
            mProgressGroup.setVisibility(View.INVISIBLE);
            mPlayerControlView.setTvHint(getString(R.string.local_change_up));
            mRadioNameTV.setText(mPlayerInfo.getAlbumName());
            mProgramNameTV.setText(mPlayerInfo.getProgramName() + ((mPlayerInfo.getSourceSubType() == PlayerSourceSubType.YQ_RADIO_FM) ? "MHz" : "kHz"));
            if (mPlayerListPopView.isShowing()) {
                mPlayerListPopView.notifyYQPlayList();
            }
        }

        private void showKoalaInfo() {
            mAudioSourceIV.setImageLevel(PlayerSourceType.KOALA);
            mPlayerControlView.setTvHint(getString(R.string.slide_up_to_switch_function));
            mProgressGroup.setVisibility(View.VISIBLE);
            mRadioNameTV.setText(mPlayerInfo.getAlbumName());
            mProgramNameTV.setText(mPlayerInfo.getProgramName());

//            onPlayerProgress(mPlayerInfo.getProgress(), mPlayerInfo.getDuration());
        }

        @Override
        public void onPlayerStatusChanged(int status) {
            if (mPlayerInfo != null && mPlayerInfo.getType() != PlayerSourceType.RADIO_YQ) {
                mPlayerListPopView.notifyStatus(status);
                mPlayerControlView.notifyPlayState(status);
                if (status == PlayerStatus.PLAYING) {
                    mSeekBar.setDisable(false);
                    mProgramNameTV.startMarquee();
                } else {
                    mProgramNameTV.stopMarquee();
                }
            }
        }

        @Override
        public void onPlayerProgress(long progress, long duration) {
            if (mPlayerInfo.getSourceSubType() != PlayerSourceSubType.RADIO) {
                mSeekBar.setMax((int) duration);
                mSeekBar.setProgress((int) progress);

                mProgressTV.setText(TimeUtils.timeMsToMMSS(progress));
                mDurationTV.setText(TimeUtils.timeMsToMMSS(duration));

                mPlayerListPopView.notifyProgress(progress);
            }
        }

        @Override
        public void onProgramSubscribeChanged(boolean subscribe) {
            mLikeIV.setSelected(subscribe);
        }

    };

    private void bindView(View view) {
        mLikeIV = view.findViewById(R.id.ivLike);
        mSeekBar = view.findViewById(R.id.seekbar);
        mShrinkIV = view.findViewById(R.id.ivShrink);
        mDurationTV = view.findViewById(R.id.tvDuration);
        mProgressTV = view.findViewById(R.id.tvProgress);
        mRadioNameTV = view.findViewById(R.id.tvSubInfo);
        mProgramNameTV = view.findViewById(R.id.tvTitle);
        mProgressGroup = view.findViewById(R.id.groupVisible);
        mAudioSourceIV = view.findViewById(R.id.ivAudioSource);
//        mAudioSourceTV = view.findViewById(R.id.tvAudioSource);
        mShowPlayListIV = view.findViewById(R.id.ivShowPlayList);
        mPlayerControlView = view.findViewById(R.id.viewPlayControl);
        mListenToRecoginizeIV = view.findViewById(R.id.ivListenToRecognize);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlayerInfoImpl.newSingleton().unRegisterPlayerInfoListener(mPlayerInfoListener);
    }

    private void preBindView() {
        mPlayerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        if (mPlayerInfo == null) {
            return;
        }
        if (mPlayerListPopView == null) {
            mPlayerListPopView = new PlayerListPopView(getActivity());
        }
        PlayerSourceFacade.newSingleton().setSourceType(mPlayerInfo.getType());

        if (!PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
            mPlayerInfo.setAction(PlayerAction.SET_LIST);
            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(mPlayerInfo, getFetchListener());
        }
    }

    private void afterBindView(View view) {
        view.setOnTouchListener(this);
        mLikeIV.setOnClickListener(this);
        mShrinkIV.setOnClickListener(this);
        mShowPlayListIV.setOnClickListener(this);
        mListenToRecoginizeIV.setOnClickListener(this);

        mSeekBar.setDisable(!PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive());
        mSeekBar.setOnSeekListener(getSeekBarChangeListener());
        mPlayerControlView.setOnPlayControlListener(getPlayControlListener());
        PlayerInfoImpl.newSingleton().registerPlayerInfoListener(mPlayerInfoListener);

    }


    @Override
    @SingleClick(1000)
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.ivShrink) {
            manualUpdateTrack(EventConstants.NormalClick.ACTION_EXIT_PLAYER_DETAILS);
            ((FMPlayerActivity) getActivity()).goBackToMainActivity();
        } else if (vId == R.id.ivShowPlayList) {
            manualUpdateTrack(EventConstants.NormalClick.ACTION_EXIT_PLAYER_LIST);
            mPlayerListPopView.show(mPlayerInfo);
        } else if (vId == R.id.ivLike) {
            manualUpdateTrack(EventConstants.NormalClick.ACTION_SAVE_OR_NOT);
            int subscribe = PlayerSourceFacade.newSingleton().getPlayerControl().subscribe(!mLikeIV.isSelected());
            if (subscribe == PlayerOperate.DEFAULT) {
                XMToast.showToast(mContext, mLikeIV.isSelected() ? getString(R.string.stored_program) : getString(R.string.not_stored_program));
            } else if (subscribe == PlayerOperate.UNSUPPORTED) {
                XMToast.showToast(mContext, R.string.xting_hint_unable_saved);
            } else {
                PlayerInfoImpl.newSingleton().onProgramSubscribeChanged(!mLikeIV.isSelected());
                XMToast.showToast(mContext, !mLikeIV.isSelected() ? R.string.xting_save : R.string.xting_cancel_save);
                XmTracker.getInstance().uploadEvent(-1, TrackerCountType.FMCOLLECT.getType());
            }
        } else if (vId == R.id.ivListenToRecognize) {
            manualUpdateTrack(EventConstants.NormalClick.ACTION_LISTEN_TO_RECOGNIZE);
            LaunchUtils.launchApp(mContext, "com.xiaoma.launcher", "com.xiaoma.launcher.recmusic.ui.MusicRecDialogActivity");
        }
    }


    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible) {
            XtingFastPlayController.getInstance().setFastPlayObserver(newTime -> {
                mSeekBar.setProgress(newTime);
                mProgressTV.setText(TimeUtils.timeMsToMMSS(newTime));
            });
        } else {
            XtingFastPlayController.getInstance().removeFastPlayObserver();
        }
    }

    private void handleYQRadioPlay() {
        if (mPlayerInfo.getAlbumId() == -1) {
            XMToast.showToast(getActivity(), R.string.no_xmly_radio);
        } else {
            showProgressDialog(R.string.action_switch_to_himalayan);
            mPlayerInfo.setAction(PlayerAction.PLAY_LIST);
            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(mPlayerInfo, new IFetchListener() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onSuccess(Object t) {
                    //请求成功之后,主动设置播放音源为喜马拉雅
                    PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
                    dismissProgress();
                }

                @Override
                public void onFail() {
                    dismissProgress();
                    XMToast.showToast(getActivity(), R.string.no_xmly_radio);
                    mPlayerInfo.setAlbumId(-1);
                    PlayerInfoImpl.newSingleton().onPlayerInfoChanged(mPlayerInfo);
                }

                @Override
                public void onError(int code, String msg) {
                    dismissProgress();
                    XMToast.showToast(getActivity(), R.string.error_by_net);
                }
            });
        }
    }

    private void handleOnlinePlay(boolean playing) {
        if (playing) {
            PlayerSourceFacade.newSingleton().getPlayerControl().pause();
        } else {
            handlePlay(0);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float downX = event.getX();
        float downY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("Jir", "onTouch: Down");
                mSloopF = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("Jir", "onTouch: Move" + assertValidUpSlide(downX, downY));
                if (assertValidUpSlide(downX, downY)) {
                    Log.d("Jir", "onTouch: Enter");
                    mSloopF = true;
                    ((FMPlayerActivity) getActivity()).addFragment(FMAlbumFragment.newInstance());
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.ACTION_SLIDE_UP,
                            "AbsPlayDetailsFragment", EventConstants.PageDescribe.FRAGMENT_ONLINE_PLAYER);
                }
                break;
        }
        mLastX = downX;
        mLastY = downY;
        return true;
    }

    private boolean assertValidUpSlide(float downX, float downY) {
        return !mSloopF
                && (mLastY - downY) > UP_SLOOP //方向控制为向上
                && Math.atan2((mLastY - downY), Math.abs(downX - mLastX)) > UP_VALID_DEGREE_45; //角度控制为与Y轴+/- 45°范围
    }

    private void handlePlay(final int playOffset) {
        if (PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
            if (playOffset == 0) {
                PlayerSourceFacade.newSingleton().getPlayerControl().play();
            } else if (playOffset == -1) {
                if (PlayerSourceFacade.newSingleton().getPlayerControl().playPre() == PlayerOperate.FAIL) {
                    XMToast.showToast(mContext, R.string.no_pre_sound);
                }
            } else {
                if (PlayerSourceFacade.newSingleton().getPlayerControl().playNext() == PlayerOperate.FAIL) {
                    XMToast.showToast(mContext, R.string.no_next_sound);
                }
            }

        } else {
            mPlayerInfo.setType(PlayerAction.SET_LIST);
            if (!NetworkUtils.isConnected(mContext)) {
                XMToast.toastException(mContext, R.string.net_not_connect);
            } else {
                if (playOffset == 0) {
                    mPlayerInfo.setAction(PlayerAction.PLAY_LIST);
                }
                PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(mPlayerInfo, new IFetchListener() {
                    @Override
                    public void onLoading() {
                        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.LOADING);
                    }

                    @Override
                    public void onSuccess(Object t) {
                        int playingIndex = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex();
                        if (playOffset == 0) {
//                            PlayerSourceFacade.newSingleton().getPlayerControl().play();
                        } else if (playOffset == -1) {
                            if (playingIndex == 0) {
                                XMToast.showToast(mContext, R.string.no_pre_sound);
                            } else {
                                PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(playingIndex - 1);
                            }
                        } else {
                            int playListSize = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayListSize();
                            if (playingIndex + 1 == playListSize) {
                                XMToast.showToast(mContext, R.string.no_next_sound);
                            } else {
                                PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(playingIndex + 1);
                            }
                        }
                    }

                    @Override
                    public void onFail() {
                        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        XMToast.showToast(mContext, R.string.net_work_error);
                        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
                    }
                });

            }
        }
    }

    private PlayerControlView.OnPlayControlListener getPlayControlListener() {
        return new PlayerControlView.OnPlayControlListener() {
            @Override
            public void onPre() {
                manualUpdateTrack(EventConstants.NormalClick.ACTION_PLAYER_CONTROL_PLAY_PRE);
                handlePlay(-1);
            }

            @Override
            public void onNext() {
                manualUpdateTrack(EventConstants.NormalClick.ACTION_PLAYER_CONTROL_PLAY_NEXT);
                handlePlay(1);
            }

            @Override
            public void onPlay(boolean playing) {
                manualUpdateTrack(EventConstants.NormalClick.ACTION_PLAYER_CONTROL_PLAY_OR_PAUSE);
                int type = mPlayerInfo.getType();

                if (type == PlayerSourceType.RADIO_YQ) {
                    handleYQRadioPlay();
                } else {
                    handleOnlinePlay(playing);
                }
            }
        };
    }

    private XmSeekBar.onSeekListener getSeekBarChangeListener() {
        return new XmSeekBar.onSeekListener() {
            @Override
            public void onSyncSeek(int progress, boolean isFromUser) {
                // onSyncSeek会在滑动时同步的被持续调用
                if (isFromUser) {
                    mProgressTV.setText(TimeUtils.timeMsToMMSS(progress));
                }
            }

            @Override
            public void onOneSeekDone(int progress, boolean isFromUser) {
                //onOneSeek方法只会在滑动结束时触发一次
                if (isFromUser) {
                    PlayerSourceFacade.newSingleton().getPlayerControl().seekProgress(progress);
                    mPlayerInfo.setProgress(progress);
                    XtingUtils.getRecordDao().insert(BeanConverter.toRecordInfo(mPlayerInfo));
                    manualUpdateTrack(EventConstants.NormalClick.TAG_PROGRESS);
                    mIsSeekBarControl = false;
                }
            }

            @Override
            public void onControl() {
                mIsSeekBarControl = true;
            }
        };
    }

    private IFetchListener getFetchListener() {
        return new IFetchListener() {
            @Override
            public void onLoading() {
                showProgressDialog(R.string.loading_data);
            }

            @Override
            public void onSuccess(Object t) {
                dismissProgress();
            }

            @Override
            public void onFail() {
                dismissProgress();
            }

            @Override
            public void onError(int code, String msg) {
                dismissProgress();
            }
        };
    }

    private void manualUpdateTrack(String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction, EventConstants.PageDescribe.TAG_PLAY_AREA, this.getClass().getName(), EventConstants.PageDescribe.FRAGMENT_PLAYER);
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.FGT_PLAYER_ONLINE_DETAILS;
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.Xting.OPEN_PLAY_LIST:
                Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                    @Override
                    public boolean queueIdle() {
                        PrintInfo.print("Jump", "打开播放列表", mPlayerInfo.toString());
                        if (mPlayerInfo != null) {
                            mPlayerListPopView.show(mPlayerInfo);
                            Log.d("JUMP", "queueIdle: show" + mPlayerListPopView.hashCode());
                        }
                        return false;
                    }
                });
                return true;
            case NodeConst.Xting.CLOSE_PLAY_LIST:
                Log.d("CLOSE", "handleJump: 1");
                if (mPlayerListPopView != null) {
                    ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("JUMP", "queueIdle: close" + mPlayerListPopView.hashCode());
                            mPlayerListPopView.dismiss();
                        }
                    });
                }
                return true;

            default:
                return false;
        }
    }
}