package com.xiaoma.club.common.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.constant.IatError;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vr.utils.VrUtils;

/**
 * Author: loren
 * Date: 2018/12/25 0025
 */

public class ClubSearchVoiceView extends FrameLayout implements View.OnClickListener {

    public static final int MAX_SEARCH_TEXT_LENGTH = 32;
    public static final int OPEN_CLOSE_ANIMATION_DURTION = 500;
    private static final int NOT_OPERATING_TIME = 6 * 1000;
    private static final int AUTO_LISTENER_TIME = 10 * 1000;
    public final int MIN_ANIMATION_DISTANCE = getResources().getDimensionPixelOffset(R.dimen.width_voice_search);
    private boolean isFinish = false;
    private OnOpenAnimationFinishListener listener;
    //input
    private RelativeLayout searchLl;
    private Button searchVoiceBtn, searchNowBtn;
    private EditText searchEt;
    private ImageView searchOpen;
    //voice
    private LinearLayout recordingLl;
    private TextView recordingCancle, recordingTips;
    private ImageView recordingLeft, recordingRight;
    private AnimationDrawable mLeftDrawable, mRightDrawable;
    private CountDownTimer countDownTimer;
    private CountDownTimer autoCountDownTimer;
    private boolean isClickCancel;

    public ClubSearchVoiceView(@NonNull Context context) {
        this(context, null);
    }

    public ClubSearchVoiceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClubSearchVoiceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ClubSearchVoiceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_club_search_voice, this);
        //input
        searchLl = view.findViewById(R.id.discovery_search_ll);
        searchVoiceBtn = view.findViewById(R.id.discovery_search_voice_btn);
        searchVoiceBtn.setOnClickListener(this);
        searchNowBtn = view.findViewById(R.id.discovery_search_now_btn);
        searchNowBtn.setEnabled(false);
        searchEt = view.findViewById(R.id.discovery_search_et);
        searchEt.setMovementMethod(ScrollingMovementMethod.getInstance());
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                    final String text = v.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        searchNowBtn.performClick();
                        return true;
                    }
                }
                return false;
            }
        });
        searchOpen = view.findViewById(R.id.discovery_search_open_btn);
        searchOpen.setOnClickListener(this);
        //voice
        recordingLl = view.findViewById(R.id.discovery_ll_recording);
        recordingCancle = view.findViewById(R.id.discovery_tv_recording_cancel);
        recordingTips = view.findViewById(R.id.discovery_tv_recording_content);
        recordingCancle.setOnClickListener(this);
        recordingLeft = view.findViewById(R.id.discovery_iv_recording_left);
        recordingRight = view.findViewById(R.id.discovery_iv_recording_right);
        mLeftDrawable = (AnimationDrawable) recordingLeft.getDrawable();
        mRightDrawable = (AnimationDrawable) recordingRight.getDrawable();

        searchEt.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int dindex = 0;
                int count = 0;
                while (count <= MAX_SEARCH_TEXT_LENGTH && dindex < dest.length()) {
                    char c = dest.charAt(dindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > MAX_SEARCH_TEXT_LENGTH) {
                    return dest.subSequence(0, dindex - 1);
                }

                int sindex = 0;
                while (count <= MAX_SEARCH_TEXT_LENGTH && sindex < source.length()) {
                    char c = source.charAt(sindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > MAX_SEARCH_TEXT_LENGTH) {
                    sindex--;
                }
                return source.subSequence(0, sindex);
            }
        }});
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cancelCountDown(countDownTimer);
                cancelCountDown(autoCountDownTimer);
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchNowBtn.setEnabled(!TextUtils.isEmpty(s) && !TextUtils.isEmpty(s.toString()));
            }
        });
        initTts();
    }

    private void initTts() {
        EventTtsManager.getInstance().init(getContext());
        if (autoCountDownTimer == null) {
            autoCountDownTimer = new CountDownTimer(AUTO_LISTENER_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    KLog.d("time----" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    setVoiceContent(getContext().getString(R.string.search_need_me_to_do));
                    EventTtsManager.getInstance().startSpeaking(getContext().getString(R.string.search_need_me_to_do), new OnTtsListener() {
                        @Override
                        public void onCompleted() {
                            setVoiceContent(getContext().getString(R.string.search_is_listener));
                            startRecord();
                        }

                        @Override
                        public void onBegin() {

                        }

                        @Override
                        public void onError(int code) {

                        }
                    });
                }
            };
            autoCountDownTimer.start();
        } else {
            autoCountDownTimer.start();
        }
    }

    public void initVoiceEngine(Context context) {
        if (context == null) {
            return;
        }
        RemoteIatManager.getInstance().init(context);

    }

    public void registerIatListener() {
        RemoteIatManager.getInstance().setOnIatListener(new OnIatListener() {
            @Override
            public void onComplete(String voiceText, String parseText) {
                KLog.d("voice onComplete " + voiceText);
                if (!TextUtils.isEmpty(voiceText)) {
                    String word = VrUtils.replaceFilter(voiceText);
                    stopVoiceAnim();
                    setNormal();
                    if (!isClickCancel) {
                        searchEt.setText(word);
                        searchNowBtn.performClick();
                    }
                } else {
                    handleVoiceEmpty();
                }
            }

            @Override
            public void onVolumeChanged(int volume) {
                KLog.d("voice change " + volume);
            }

            @Override
            public void onNoSpeaking() {
                KLog.d("voice onNoSpeaking");
            }

            @Override
            public void onError(int errorCode) {
                KLog.d("voice onError " + errorCode);
                if (IatError.ERROR_MEDIA_FOCUS_LOSS == errorCode ||
                        IatError.ERROR_MIC_FOCUS_LOSS == errorCode) {
                    setNormal();
                    XMToast.toastException(getContext(), R.string.tips_audio_focus_occupied);
                } else {
                    handleVoiceEmpty();
                }
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
    }

    public void unregisterIatListener() {
        RemoteIatManager.getInstance().setOnIatListener(null);
    }

    private void handleVoiceEmpty() {
        if (isClickCancel) {
            return;
        }
        setVoiceContent(getContext().getString(R.string.search_voice_needme_tip));
        EventTtsManager.getInstance().startSpeaking(getContext().getString(R.string.search_voice_needme_tip), new OnTtsListener() {
            @Override
            public void onCompleted() {
                stopVoiceAnim();
                setNormal();
            }

            @Override
            public void onBegin() {

            }

            @Override
            public void onError(int code) {

            }
        });
    }

    public void startVoiceAnim() {
        mLeftDrawable.start();
        mRightDrawable.start();
    }

    public void stopVoiceAnim() {
        mLeftDrawable.stop();
        mRightDrawable.stop();
    }

    public void setNormal() {
        searchLl.setVisibility(VISIBLE);
        searchEt.setVisibility(VISIBLE);
        recordingLl.setVisibility(GONE);
        searchOpen.setVisibility(View.GONE);
        stopVoiceAnim();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void requestEtFocus() {
        if (searchEt != null) {
            searchEt.requestFocus();
        }
    }

    public void setHint(String text) {
        if (searchEt != null) {
            searchEt.setHint(text);
        }
    }

    public void setNormalContentText(CharSequence text) {
        searchLl.setVisibility(VISIBLE);
        recordingLl.setVisibility(GONE);
        searchEt.setVisibility(VISIBLE);
        searchOpen.setVisibility(View.GONE);
        searchEt.setText(text);
        stopVoiceAnim();
    }

    public void setVoiceContent(String text) {
        try {
            final InputMethodManager im = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchLl.setVisibility(GONE);
        recordingLl.setVisibility(VISIBLE);
        recordingCancle.setVisibility(VISIBLE);
        if (!TextUtils.isEmpty(text)) {
            recordingTips.setText(text);
        }
        recordingTips.setVisibility(VISIBLE);
        searchOpen.setVisibility(View.GONE);
        startVoiceAnim();
    }

    public void startRecord() {
        RemoteIatManager.getInstance().startListeningRecord(3000, 700);
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(NOT_OPERATING_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    KLog.d("记录时间----" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    isClickCancel = false;
                    RemoteIatManager.getInstance().cancelListening();
                }
            };
            countDownTimer.start();
        } else {
            countDownTimer.start();
        }
    }

    public void setOnSearchClickListener(@Nullable OnClickListener listener) {
        searchNowBtn.setOnClickListener(listener);
    }

    public void openWidthAnimation() {
        searchOpen.setVisibility(View.GONE);
        setNormal();
        isFinish = false;
        ValueAnimator widthAnimation = ValueAnimator.ofInt(getResources().getDimensionPixelOffset(R.dimen.height_discovery_search_voice_view), getResources().getDimensionPixelOffset(R.dimen.width_discovery_search_voice_view));
        widthAnimation.setDuration(OPEN_CLOSE_ANIMATION_DURTION);
        widthAnimation.setInterpolator(new AccelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                searchLl.getLayoutParams().width = value;
                searchLl.requestLayout();
                if (value <= MIN_ANIMATION_DISTANCE) {
                    searchVoiceBtn.setVisibility(GONE);
                    searchNowBtn.setVisibility(GONE);
                } else {
                    if (!isFinish) {
                        searchVoiceBtn.setVisibility(VISIBLE);
                        searchNowBtn.setVisibility(VISIBLE);
                        isFinish = true;
                    }
                }
            }

        });
        widthAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.onAnimationFinish();
                }
            }
        });
        widthAnimation.start();
        if (listener != null) {
            listener.onAnimationStart();
        }
    }

    public void closeWidthAnimation() {

        ValueAnimator widthAnimation = ValueAnimator.ofInt(getResources().getDimensionPixelOffset(R.dimen.size_animation_distance), getResources().getDimensionPixelOffset(R.dimen.height_discovery_search_voice_view));
        widthAnimation.setDuration(OPEN_CLOSE_ANIMATION_DURTION);
        widthAnimation.setInterpolator(new AccelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                searchLl.getLayoutParams().width = value;
                searchLl.requestLayout();
                if (value <= MIN_ANIMATION_DISTANCE) {
                    searchVoiceBtn.setVisibility(GONE);
                    searchNowBtn.setVisibility(GONE);
                    searchOpen.setVisibility(View.VISIBLE);
                    searchLl.setVisibility(View.GONE);
                    recordingLl.setVisibility(View.GONE);
                } else {
                }
            }
        });
        widthAnimation.start();

    }

    @Override
    public void onClick(View v) {
        cancelCountDown(autoCountDownTimer);
        switch (v.getId()) {
            case R.id.discovery_search_voice_btn:
                setVoiceContent(getContext().getString(R.string.search_is_listener));
                startRecord();
                break;
            case R.id.discovery_tv_recording_cancel:
                isClickCancel = true;
                RemoteIatManager.getInstance().cancelListening();
                setNormal();
                break;
            case R.id.discovery_search_open_btn:
                openWidthAnimation();
                break;
        }
    }

    public String getText() {
        String text = "";
        if (searchEt != null) {
            text = searchEt.getText().toString();
        }
        return text;
    }

    public void setEtText(String s) {
        if (searchEt != null && s != null) {
            searchEt.setText(s);
        }
    }

    public void release(boolean needDestroyTts) {
        RemoteIatManager.getInstance().cancelListening();
        RemoteIatManager.getInstance().release();
        cancelCountDown(countDownTimer);
        if (needDestroyTts) {
            cancelCountDown(autoCountDownTimer);
            EventTtsManager.getInstance().destroy();
        }
    }

    private void cancelCountDown(CountDownTimer countDownTimer) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * 当使用键盘就会执行此方法
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        cancelCountDown(countDownTimer);
        cancelCountDown(autoCountDownTimer);
        return super.dispatchKeyEvent(event);
    }

    /**
     * 当触摸就会执行此方法
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        cancelCountDown(countDownTimer);
        cancelCountDown(autoCountDownTimer);
        return super.dispatchTouchEvent(ev);
    }

    public void setOnOpenAnimationFinishListener(OnOpenAnimationFinishListener listener) {
        this.listener = listener;
    }

    public interface OnOpenAnimationFinishListener {
        void onAnimationStart();

        void onAnimationFinish();
    }
}
