package com.xiaoma.songname.ui.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.songname.R;
import com.xiaoma.songname.common.constant.SongNameConstants;
import com.xiaoma.songname.common.manager.AudioFocusManager;
import com.xiaoma.songname.model.GameResult;
import com.xiaoma.songname.model.SongNameBean;
import com.xiaoma.songname.ui.activity.SortActivity;
import com.xiaoma.songname.ui.view.SongNameToast;
import com.xiaoma.songname.vm.SongNameVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vr.utils.VrUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 左侧答题页面
 */
public class AnswerFragment extends BaseFragment implements View.OnClickListener {

    public static final int TTS_QUESTION_STATE = 1;//TTS播报状态
    public static final int START_ANSWER_QUESTION_STATE = 2;//开始作答状态
    public static final int RECORD_ANSWER_STATE = 3;//作答录音状态
    public static final int ANSWER_FAILED_STATE = 4;//作答失败状态
    public static final int ANSWER_SUCCESS_STATE = 5;//作答成功状态
    public static final int GAME_RESULT_SATE = 6;//游戏结算状态
    public static final int NO_QUESTION_STATE = 7;//已答完所有歌曲
    //无网络
    public static final int NO_NETWORK_STATE = 8;

    //第x首歌，请仔细听哦
    private static final int TTS_QUESTION_TEXT = 1;
    //你听出了什么歌曲吗
    private static final int TTS_MUSIC_PLAY_END_TEXT = 2;
    //回答错误，请重新回答
    private static final int TTS_ANSWER_ERROR_TEXT = 3;
    //恭喜您回答正确
    private static final int TTS_ANSWER_RIGHT_TEXT = 4;

    //重试获取题目
    private static final int RETRY_GET_SUBJECT = 1;
    //重试获取游戏结果
    private static final int RETRY_GET_RESULT = 2;

    private ImageView ivClose;//退出iv
    private TextView tvQuestion;//问题
    private TextView tvTip;//答案提示
    private Button btnAnswer;//开始回答btn
    private ImageView mIvVoiceAnim;//录音动画
    private TextView mTvTipAnswer;//答案提示范例
    private TextView mTvSuccessResultTips;//回答正确tips
    private TextView mTvCount;//进入下一题剩余秒数
    private View mGameOverView;//游戏结束View
    private TextView tvTotalRecord;//总分
    private TextView tvBeatRecord;//击败人数比例
    private TextView tvGameCon;//继续游戏
    private TextView tvSort;//查看排行榜
    private View mAnswerView;
    private View mNoNetworkView;
    private View mBtnRetry;
    private TextView mErrorAnswerTv;

    private AnimationDrawable mVoiceAnim;
    //重试按钮状态
    private int mRetryBtnState = RETRY_GET_SUBJECT;

    //刷新播放View接口
    private IFlushPlayView mIFlushPlayView;
    private SongNameVM mSongNameVM;
    private SongNameBean.SongSubjectBean mSongSubject;
    private GameResult mGameResult;
    private String uid;
    //总得分
    private int mTotoalRecord;
    //当前页面状态
    private int mState;

    private static final int WHAT = 101;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private static final long MAX_TIME = 3000;
    private long curTime = 0;
    private boolean isPause = false;
    private static final int COUNT_TOTAL_TIME = 3;
    private boolean isInit;
    //题目序号
    private int questionNo;
    private XmDialog comfirmExitDialog;
    //错误答案
    private String mErrorAnswerStr = "";

    //初始化timer
    public void initTimer() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (curTime == 0) {
                    curTime = MAX_TIME;
                } else {
                    //计数器，每次减一秒。
                    curTime -= 1000;
                }
                Message message = new Message();
                message.what = WHAT;
                message.obj = curTime;
                mHandler.sendMessage(message);
            }
        };
        mTimer = new Timer();
    }

    //实现更新主线程UI
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT:
                    long sRecLen = (long) msg.obj;
                    if (sRecLen <= 0) {
                        mTimer.cancel();
                        curTime = 0;
                        if (mSongSubject != null) {
                            showAnswerViewByState(TTS_QUESTION_STATE);

                        } else {
                            showAnswerViewByState(NO_QUESTION_STATE);
                        }
                        return;
                    }
                    mTvCount.setText(getString(R.string.count_down_next_question, sRecLen / 1000, questionNo));
                    break;
            }
        }
    };

    public static AnswerFragment newInstanceFragment(String uid) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putString(SongNameConstants.UID, uid);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_answer, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        ivClose = view.findViewById(R.id.iv_close);
        tvQuestion = view.findViewById(R.id.tv_question);
        tvTip = view.findViewById(R.id.tv_tip);
        btnAnswer = view.findViewById(R.id.btn_answer);
        mIvVoiceAnim = view.findViewById(R.id.iv_voice_anim);
        mTvTipAnswer = view.findViewById(R.id.tv_tip_answer);
        mTvSuccessResultTips = view.findViewById(R.id.tv_success_result);
        mTvCount = view.findViewById(R.id.tv_count);
        mGameOverView = view.findViewById(R.id.game_over_view);
        tvTotalRecord = view.findViewById(R.id.tv_total_record);
        tvBeatRecord = view.findViewById(R.id.tv_beat_record);
        tvGameCon = view.findViewById(R.id.tv_game_continue);
        tvSort = view.findViewById(R.id.tv_sort);
        mAnswerView = view.findViewById(R.id.view_answer);
        mNoNetworkView = view.findViewById(R.id.view_no_network);
        mBtnRetry = view.findViewById(R.id.tv_retry);
        mErrorAnswerTv = view.findViewById(R.id.tv_error_answer);

        mVoiceAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.anim_voice);
        mIvVoiceAnim.setBackground(mVoiceAnim);

        ivClose.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        tvGameCon.setOnClickListener(this);
        tvSort.setOnClickListener(this);
        mBtnRetry.setOnClickListener(this);
    }

    private void initData() {
        EventTtsManager.getInstance().init(mContext);
        RemoteIatManager.getInstance().init(mContext);
        RemoteIatManager.getInstance().setOnIatListener(new OnIatListener() {
            @Override
            public void onComplete(String voiceText, String parseText) {
                if (mState == GAME_RESULT_SATE) {
                    return;
                }
                KLog.d("voice onComplete: voiceText" + voiceText + ",parseText:" + parseText);
                String voiceStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(voiceText);
                    JSONObject intentJson = jsonObject.optJSONObject("intent");
                    voiceStr = intentJson.optString("text");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stopAnim();
                if (!TextUtils.isEmpty(voiceStr)) {
                    handleAnswer(VrUtils.replaceFilter(voiceStr));
                }
            }

            @Override
            public void onVolumeChanged(int volume) {
            }

            @Override
            public void onNoSpeaking() {
                KLog.d("voice onNoSpeaking ");
            }

            @Override
            public void onError(int errorCode) {
                if (mState == GAME_RESULT_SATE) {
                    return;
                }
                KLog.d("voice onError " + errorCode);
                stopAnim();
                mTvTipAnswer.setVisibility(View.GONE);
                mIvVoiceAnim.setVisibility(View.GONE);
                btnAnswer.setVisibility(View.VISIBLE);
                btnAnswer.setEnabled(true);
                SongNameToast.toastException(getContext(), R.string.record_error);
            }

            @Override
            public void onResult(String recognizerText, boolean isLast, String currentText) {
                KLog.d("voice onResult  " + recognizerText + " --- " + isLast + "  " + currentText);
            }

            @Override
            public void onWavFileComplete() {
                KLog.d("voice onWavFileComplete");
            }

            @Override
            public void onRecordComplete() {
                KLog.d("voice onRecordComplete");
            }
        });

        mSongNameVM = ViewModelProviders.of(this).get(SongNameVM.class);
        mSongNameVM.getSubject().observe(this, new Observer<XmResource<SongNameBean.SongSubjectBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<SongNameBean.SongSubjectBean> xmResource) {
                if (xmResource == null) {
                    return;
                }
                xmResource.handle(new OnCallback<SongNameBean.SongSubjectBean>() {
                    @Override
                    public void onSuccess(SongNameBean.SongSubjectBean data) {
                        mSongSubject = data;
                        if (data == null) {
                            showAnswerViewByState(NO_QUESTION_STATE);
                            return;
                        }
                        if (isInit) {
                            showAnswerViewByState(TTS_QUESTION_STATE);
                        }
                        isInit = false;
                    }

                    @Override
                    public void onFailure(String msg) {
                        KLog.d(msg);
                        mRetryBtnState = RETRY_GET_SUBJECT;
                        showAnswerViewByState(NO_NETWORK_STATE);
                    }
                });
            }
        });
        mSongNameVM.getReportResult().observe(this, new Observer<XmResource<Object>>() {
            @Override
            public void onChanged(@Nullable XmResource<Object> xmResource) {
                if (xmResource == null) {
                    return;
                }
                xmResource.handle(new OnCallback<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        //上报成功获取下一题目
                        mSongNameVM.fetchSubject(uid);
                    }
                });
            }
        });
        mSongNameVM.getGameResult().observe(this, new Observer<XmResource<GameResult>>() {
            @Override
            public void onChanged(@Nullable XmResource<GameResult> xmResource) {
                if (xmResource == null) {
                    return;
                }
                xmResource.handle(new OnCallback<GameResult>() {
                    @Override
                    public void onSuccess(GameResult data) {
                        mGameResult = data;
                        showAnswerViewByState(GAME_RESULT_SATE);
                        if (comfirmExitDialog != null) {
                            comfirmExitDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        mGameResult = null;
                        mRetryBtnState = RETRY_GET_RESULT;
                        showAnswerViewByState(NO_NETWORK_STATE);
                        if (comfirmExitDialog != null) {
                            comfirmExitDialog.dismiss();
                        }
                        mState = GAME_RESULT_SATE;
                    }
                });
            }
        });
        if (getArguments() != null) {
            uid = getArguments().getString(SongNameConstants.UID);
        }
        isInit = true;
        mSongNameVM.fetchSubject(uid);
    }

    private void showAnswerViewByState(int state) {
        if (mIFlushPlayView != null) {
            mIFlushPlayView.setAnswerPageState(state);
        }
        mState = state;
        switch (state) {
            case TTS_QUESTION_STATE:
                if (mSongSubject == null) {
                    return;
                }
                initQuestion();
                break;

            case START_ANSWER_QUESTION_STATE:
                if (mSongSubject == null) {
                    return;
                }
                musicComplete();
                break;

            case RECORD_ANSWER_STATE:
                if (mSongSubject == null) {
                    return;
                }
                answerQuestion();
                break;

            case ANSWER_FAILED_STATE:
                if (mSongSubject == null) {
                    return;
                }
                wrongAnswer();
                break;

            case ANSWER_SUCCESS_STATE:
                if (mSongSubject == null) {
                    return;
                }
                rightAnswer();
                break;

            case GAME_RESULT_SATE:
                gameFinish();
                break;

            case NO_QUESTION_STATE:
                showNoQuestionDialog();
                break;

            case NO_NETWORK_STATE:
                showNoNetWorkView();
                break;

            default:
                break;
        }
    }

    private void initQuestion() {
        mAnswerView.setVisibility(View.VISIBLE);
        mGameOverView.setVisibility(View.GONE);
        mNoNetworkView.setVisibility(View.GONE);

        questionNo = mSongSubject.getSubjectNum();
        tvQuestion.setText(getString(R.string.subject_num_listen, questionNo));
        tvQuestion.setTextColor(ContextCompat.getColor(mContext, R.color.right_color));
        String tip = getString(R.string.subject_tip, mSongSubject.getSubjectTip());
        tvTip.setText(tip);
        changeTvColor(tip, tip.length() - 3, tip.length(), tvTip);
        tvTip.setTextColor(ContextCompat.getColor(mContext, R.color.tip_color));
        tvTip.setVisibility(View.VISIBLE);
        btnAnswer.setVisibility(View.VISIBLE);
        btnAnswer.setEnabled(false);
        mIvVoiceAnim.setVisibility(View.GONE);
        mTvTipAnswer.setVisibility(View.GONE);
        mTvSuccessResultTips.setVisibility(View.GONE);
        mTvCount.setVisibility(View.GONE);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                startSpeakingQuestion(TTS_QUESTION_TEXT, tvQuestion.getText().toString());
            }
        }, 1500);
        //刷新播放页面
        if (mIFlushPlayView != null) {
            mIFlushPlayView.setPlayTitle(getString(R.string.play_num, questionNo));
            mIFlushPlayView.notifyPlayState(PlayMusicFragment.PAUSE_STATE);
            mIFlushPlayView.setPlayBtnEnable(false);
            mIFlushPlayView.setPlaySource(mSongSubject.getVoiceUrl());
        }
    }

    private void musicComplete() {
        mAnswerView.setVisibility(View.VISIBLE);
        mGameOverView.setVisibility(View.GONE);
        mNoNetworkView.setVisibility(View.GONE);

        tvQuestion.setText(R.string.do_you_know);
        tvQuestion.setTextColor(Color.WHITE);
        tvTip.setText(R.string.tip_can_replay);
        tvTip.setTextColor(ContextCompat.getColor(mContext, R.color.tip_color));
        btnAnswer.setVisibility(View.VISIBLE);
        btnAnswer.setEnabled(true);
        mIvVoiceAnim.setVisibility(View.GONE);
        mTvTipAnswer.setVisibility(View.GONE);
        mTvSuccessResultTips.setVisibility(View.GONE);
        mTvCount.setVisibility(View.GONE);
        startSpeakingQuestion(TTS_MUSIC_PLAY_END_TEXT, tvQuestion.getText().toString());
    }

    /**
     * 答题
     */
    private void answerQuestion() {
        mAnswerView.setVisibility(View.VISIBLE);
        mGameOverView.setVisibility(View.GONE);
        mNoNetworkView.setVisibility(View.GONE);

        tvTip.setText(R.string.replay_tip);
        tvQuestion.setText(R.string.say_your_answer);
        tvQuestion.setTextColor(Color.WHITE);
        mIvVoiceAnim.setVisibility(View.VISIBLE);
        mTvTipAnswer.setVisibility(View.VISIBLE);
        btnAnswer.setVisibility(View.GONE);
        mTvSuccessResultTips.setVisibility(View.GONE);
        mTvCount.setVisibility(View.GONE);
        startAnim();
        //开始收音
        RemoteIatManager.getInstance().stopListening();
        RemoteIatManager.getInstance().startListeningNormal();
        if (mIFlushPlayView != null) {
            //通知音乐暂停
            mIFlushPlayView.notifyPlayState(PlayMusicFragment.PAUSE_STATE);
        }
    }

    private void wrongAnswer() {
        mAnswerView.setVisibility(View.VISIBLE);
        mGameOverView.setVisibility(View.GONE);
        mNoNetworkView.setVisibility(View.GONE);

        tvQuestion.setText(R.string.wrong_answer_reanwser);
        tvQuestion.setTextColor(ContextCompat.getColor(mContext, R.color.wrong_color));
        shakeView(tvQuestion);
        String subTip = getString(R.string.subject_tip, mSongSubject.getSubjectTip());
        tvTip.setText(subTip);
        tvTip.setTextColor(ContextCompat.getColor(mContext, R.color.tip_color));
        changeTvColor(subTip, subTip.length() - 3, subTip.length(), tvTip);

        mIvVoiceAnim.setVisibility(View.GONE);
        mTvTipAnswer.setVisibility(View.VISIBLE);
        btnAnswer.setVisibility(View.GONE);
        mTvSuccessResultTips.setVisibility(View.GONE);
        mTvCount.setVisibility(View.GONE);
        startSpeakingQuestion(TTS_ANSWER_ERROR_TEXT, tvQuestion.getText().toString());

        //显示错误答案
        mErrorAnswerTv.setText(mErrorAnswerStr);
        mErrorAnswerTv.setVisibility(View.VISIBLE);
    }

    private void rightAnswer() {
        mAnswerView.setVisibility(View.VISIBLE);
        mGameOverView.setVisibility(View.GONE);
        mNoNetworkView.setVisibility(View.GONE);

        tvQuestion.setText(getString(R.string.con_right_answer));
        tvQuestion.setTextColor(ContextCompat.getColor(mContext, R.color.right_color));
        shakeView(tvQuestion);
        tvTip.setTextColor(ContextCompat.getColor(mContext, R.color.tip_color));
        String answer = getString(R.string.right_answer, mSongSubject.getSubjectAnswer());
        tvTip.setText(answer);
        changeTvColor(answer, answer.length() - mSongSubject.getSubjectAnswer().length(), answer.length(), tvTip);
        questionNo++;
        mTvCount.setText(getString(R.string.count_down_next_question, COUNT_TOTAL_TIME, questionNo));
        mTvSuccessResultTips.setVisibility(View.VISIBLE);
        mTvCount.setVisibility(View.VISIBLE);
        mIvVoiceAnim.setVisibility(View.GONE);
        mTvTipAnswer.setVisibility(View.GONE);
        btnAnswer.setVisibility(View.GONE);
        //回答正确后上报
        mSongNameVM.reportResult(uid, mSongSubject.getId());
        //得分累加
        mTotoalRecord += mSongSubject.getSubjectPoint();
        mSongSubject = null;
        startSpeakingQuestion(TTS_ANSWER_RIGHT_TEXT, tvQuestion.getText().toString());
        if (mIFlushPlayView != null) {
            //通知游戏暂停
            mIFlushPlayView.notifyPlayState(PlayMusicFragment.PAUSE_STATE);
        }
    }

    private void gameFinish() {
        mNoNetworkView.setVisibility(View.GONE);
        mAnswerView.setVisibility(View.VISIBLE);
        mGameOverView.setVisibility(View.VISIBLE);

        tvQuestion.setText(R.string.good_game);
        tvQuestion.setTextColor(ContextCompat.getColor(mContext, R.color.right_color));
        mIvVoiceAnim.setVisibility(View.GONE);
        mTvTipAnswer.setVisibility(View.GONE);
        btnAnswer.setVisibility(View.GONE);
        tvTip.setVisibility(View.GONE);
        mTvCount.setVisibility(View.GONE);
        mTvSuccessResultTips.setVisibility(View.GONE);
        if (mGameResult != null) {
            tvTotalRecord.setText(getString(R.string.game_total_point, mGameResult.getTotalPoint()));
            tvBeatRecord.setText(mGameResult.getRatio() + "%");

        } else {
            tvTotalRecord.setText(R.string.no_data);
            tvBeatRecord.setText(R.string.no_data);
        }

        if (mSongSubject == null) {
            tvGameCon.setEnabled(false);

        } else {
            tvGameCon.setEnabled(true);
        }
    }

    /**
     * 开始3s倒计时进入下一题
     */
    private void startTimer() {
        destroyTimer();
        initTimer();
        isPause = false;
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * view左右抖动
     *
     * @param view
     */
    public void shakeView(final View view) {
        ThreadDispatcher.getDispatcher().post(new Runnable() {
            @Override
            public void run() {
                Animation translateAnimation = new TranslateAnimation(-15, 15, 0, 0);
                translateAnimation.setDuration(40);//每次时间
                translateAnimation.setRepeatCount(4);//重复次数
                /**倒序重复REVERSE  正序重复RESTART**/
                translateAnimation.setRepeatMode(Animation.REVERSE);
                view.startAnimation(translateAnimation);
            }
        });
    }

    /**
     * tts播报题目
     *
     * @param text
     */
    private void startSpeakingQuestion(final int ttsType, String text) {
        EventTtsManager.getInstance().startSpeaking(text, new OnTtsListener() {
            @Override
            public void onCompleted() {
                KLog.d("startSpeakingQuestion onCompleted");
                switch (ttsType) {
                    case TTS_QUESTION_TEXT:
                        //播报结束就开始播放音频
                        btnAnswer.setEnabled(true);
                        if (mIFlushPlayView != null && mSongSubject != null) {
                            mIFlushPlayView.setPlayBtnEnable(true);
                            mIFlushPlayView.notifyPlayState(PlayMusicFragment.PLAYING_STATE);
                        }
                        break;

                    case TTS_MUSIC_PLAY_END_TEXT:
                        //触发回答按钮
                        btnAnswer.performClick();
                        break;

                    case TTS_ANSWER_ERROR_TEXT:
                        //隐藏错误答案
                        mErrorAnswerTv.setVisibility(View.GONE);
                        mIvVoiceAnim.setVisibility(View.VISIBLE);
                        startAnim();
                        //开始收音
                        RemoteIatManager.getInstance().stopListening();
                        RemoteIatManager.getInstance().startListeningNormal();
                        break;

                    case TTS_ANSWER_RIGHT_TEXT:
                        startTimer();
                        break;
                }
            }

            @Override
            public void onBegin() {
                KLog.d("startSpeakingQuestion onBegin");
            }

            @Override
            public void onError(int code) {
                KLog.d("startSpeakingQuestion onError code:" + code);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (mState != GAME_RESULT_SATE) {
                    if (mTimer != null && curTime != 0 && !isPause) {
                        mTimer.cancel();
                        isPause = true;
                    }
                    showExitDialog();

                } else {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
                break;

            case R.id.btn_answer:
                //开始作答
                showAnswerViewByState(RECORD_ANSWER_STATE);
                break;

            case R.id.tv_sort:
                if (!NetworkUtils.isConnected(getContext())) {
                    SongNameToast.toastException(getContext(), R.string.toast_network_exception);
                    return;
                }
                if (StringUtil.isEmpty(uid)) {
                    showToast("request params uid is null");
                    return;
                }
                Intent intent = new Intent(getActivity(), SortActivity.class);
                intent.putExtra(SongNameConstants.UID, uid);
                startActivity(intent);
                break;

            case R.id.tv_game_continue:
                if (!NetworkUtils.isConnected(getContext())) {
                    SongNameToast.toastException(getContext(), R.string.toast_network_exception);
                    return;
                }
                isInit = true;
                mSongNameVM.fetchSubject(uid);
                break;

            case R.id.tv_retry:
                if (mRetryBtnState == RETRY_GET_SUBJECT) {
                    mSongNameVM.fetchSubject(uid);

                } else {
                    mSongNameVM.getTotalPoints(uid);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 确认退出弹框
     */
    private void showExitDialog() {
        View view = View.inflate(getContext(), R.layout.dialog_exit, null);
        comfirmExitDialog = new XmDialog.Builder(getFragmentManager())
                .setView(view).setWidth(560).setHeight(300)
                .setCancelableOutside(false)
                .create();
        comfirmExitDialog.setCancelable(false);
        TextView textView = view.findViewById(R.id.tv_msg);
        textView.setText(getString(R.string.dialog_exit_msg, mTotoalRecord));

        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIFlushPlayView != null) {
                    //通知游戏结束
                    mIFlushPlayView.notifyPlayState(PlayMusicFragment.OVER_STATE);
                }
                //清空当前得分
                mTotoalRecord = 0;
                //获取游戏结算数据
                mSongNameVM.getTotalPoints(uid);
                comfirmExitDialog.dismiss();
                destroyTimer();
                EventTtsManager.getInstance().stopSpeaking();
                RemoteIatManager.getInstance().stopListening();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comfirmExitDialog.dismiss();
                pauseTimer();
            }
        });

        comfirmExitDialog.show();
    }

    /**
     * 答完全部题目弹框
     */
    private void showNoQuestionDialog() {
        View view = View.inflate(getContext(), R.layout.dialog_no_question, null);
        final XmDialog builder = new XmDialog.Builder(getFragmentManager())
                .setView(view).setWidth(560)
                .setHeight(300)
                .setCancelableOutside(false)
                .create();
        builder.setCancelable(false);
        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                //获取游戏结算数据
                mSongNameVM.getTotalPoints(uid);
                if (mIFlushPlayView != null) {
                    //通知游戏结束
                    mIFlushPlayView.notifyPlayState(PlayMusicFragment.OVER_STATE);
                }
            }
        });
        builder.show();
    }

    private void showNoNetWorkView() {
        mAnswerView.setVisibility(View.GONE);
        mGameOverView.setVisibility(View.GONE);
        mNoNetworkView.setVisibility(View.VISIBLE);
    }

    /**
     * 暂停计时器
     */
    private void pauseTimer() {
        if (mTimer != null && curTime != 0 && isPause) {
            destroyTimer();
            initTimer();
            mTimer.schedule(mTimerTask, 0, 1000);
            isPause = false;
        }
    }

    /**
     * 停止录音动画
     */
    private void stopAnim() {
        if (mVoiceAnim != null && mVoiceAnim.isRunning()) {
            mVoiceAnim.stop();
        }
    }

    /**
     * 开始录音动画
     */
    private void startAnim() {
        if (mVoiceAnim != null && !mVoiceAnim.isRunning()) {
            mVoiceAnim.start();
        }
    }

    public void setIFlushPlayView(IFlushPlayView IFlushPlayView) {
        this.mIFlushPlayView = IFlushPlayView;
    }

    /**
     * 根据收音处理答案
     *
     * @param voiceContent
     */
    private void handleAnswer(String voiceContent) {
        if (mSongSubject != null && voiceContent.toLowerCase().contains(mSongSubject.getSubjectAnswer().toLowerCase())) {
            //回答正确
            showAnswerViewByState(ANSWER_SUCCESS_STATE);

        } else {
            mErrorAnswerStr = voiceContent;
            //回答错误
            showAnswerViewByState(ANSWER_FAILED_STATE);
        }
    }

    /**
     * 修改text文字颜色
     *
     * @param text
     * @param start
     * @param end
     * @param textView
     */
    private void changeTvColor(String text, int start, int end, TextView textView) {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.right_color)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(style);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventTtsManager.getInstance().destroy();
        RemoteIatManager.getInstance().release();
    }

    public void startPlayMusic() {
        btnAnswer.setEnabled(true);
        //获取焦点
        AudioFocusManager.getInstance().requestFocus();
    }

    public void playMusicAgain() {
        //停止收音
        RemoteIatManager.getInstance().stopListening();
        stopAnim();
        mTvTipAnswer.setVisibility(View.GONE);
        mIvVoiceAnim.setVisibility(View.GONE);
        btnAnswer.setVisibility(View.VISIBLE);
        mErrorAnswerTv.setVisibility(View.GONE);
        btnAnswer.setEnabled(true);
        //获取焦点
        AudioFocusManager.getInstance().requestFocus();
    }

    /**
     * 音频播放结束
     */
    public void onPlayMusicEnd() {
        //释放焦点
        AudioFocusManager.getInstance().abandonFocus();
        //刷新view
        showAnswerViewByState(START_ANSWER_QUESTION_STATE);
    }

    public interface IFlushPlayView {
        /**
         * 通知音乐状态
         *
         * @param state
         */
        void notifyPlayState(int state);

        /**
         * 设置title
         *
         * @param title
         */
        void setPlayTitle(String title);

        /**
         * 设置播放源
         *
         * @param playSource
         */
        void setPlaySource(String playSource);

        /**
         * 设置播放按钮是否可点
         *
         * @param enable
         */
        void setPlayBtnEnable(boolean enable);

        /**
         * 当前答题页面状态
         *
         * @param state
         */
        void setAnswerPageState(int state);
    }

    public void destroyTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}
