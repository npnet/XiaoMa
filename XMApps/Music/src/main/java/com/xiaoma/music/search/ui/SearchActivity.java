package com.xiaoma.music.search.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.manager.AudioFocusHelper;
import com.xiaoma.music.common.util.MusicUtils;
import com.xiaoma.music.search.model.SearchBean;
import com.xiaoma.music.search.vm.SearchVM;
import com.xiaoma.skin.views.XmSkinButton;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.FlowLayout;
import com.xiaoma.ui.view.SearchVoiceView;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.constant.IatError;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vr.utils.VrUtils;

import java.util.ArrayList;
import java.util.List;

import skin.support.widget.SkinCompatSupportable;

import static com.xiaoma.music.common.constant.EventConstants.NormalClick.searchNow;

@PageDescComponent(EventConstants.PageDescribe.searchActivity)
public class SearchActivity extends BaseActivity implements View.OnClickListener, SkinCompatSupportable {
    public static final String TAG = SearchActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 0x11;
    public static final String INTENT_TEXT = "text";
    private static final int NOT_OPERATING_TIME = 6 * 1000;
    private static final int NO_OPERATING_TIME = 10 * 1000;
    public static final int DELAY_TO_FINISH = 3000;
    public static final int TYPE_HISTORY = 0;
    public static final int TYPE_HOT = 1;
    public CountDownTimer countDownTimer;
    public CountDownTimer autoCountDownTimer;
    private SearchVoiceView mSearchVoiceView;
    private SearchVM mSearchVM;
    private FlowLayout mLlSearchHistory;
    private FlowLayout mLlRecommend;
    private String mKeyword;
    private RelativeLayout mRlSearchHistory;
    private ConstraintLayout mNetWorkView;
    private LinearLayout mLlContent;
    private boolean isClickCancel;
    private EditText inputEt;
    private Button button;
    private AudioFocusHelper audioFocusHelper;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();

    }

    private void initView() {
        mSearchVoiceView = findViewById(R.id.search_voice_view);
        mLlSearchHistory = findViewById(R.id.ll_search_history);
        mRlSearchHistory = findViewById(R.id.rl_search_history);
        mLlRecommend = findViewById(R.id.ll_recommend);
        mNetWorkView = findViewById(R.id.rl_search_no_network);
        mLlContent = findViewById(R.id.ll_content);
        mSearchVoiceView.setOnVoiceClickListener(this);
        mSearchVoiceView.setOnRecordingCancelClickListener(this);
        findViewById(R.id.tv_clear).setOnClickListener(this);
        findViewById(R.id.tv_retry).setOnClickListener(this);
        mSearchVoiceView.setNormal();
        mSearchVoiceView.setHintText(getString(R.string.search_voice_hint));
        initEt();
        addSearchNowButton();
    }

    /**
     * 添加立即搜索按钮
     */
    private void addSearchNowButton() {
        ViewGroup ll = mSearchVoiceView.findViewById(R.id.ll_normal);
        if (ll == null) return;
        button = new XmSkinButton(this);
        button.setId(R.id.search_view_search_now_btn);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(-5, 0, 0, 4);
        button.setLayoutParams(lp);
        button.setBackgroundResource(R.drawable.search_view_search_now);
        button.setText(getString(R.string.search_search_now));
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
        button.setPadding(0, 0, 0, 6);
        button.setGravity(Gravity.CENTER);
        ll.addView(button, mSearchVoiceView.getChildCount() + 1);
        button.setOnClickListener(this);
        button.setEnabled(false);
    }

    private void initEt() {
        inputEt = mSearchVoiceView.findViewById(R.id.et_normal_content);
        if (inputEt != null) {
            inputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String text = v.getText().toString().trim();
                        if (TextUtils.isEmpty(text)) {
                            showToast(R.string.search_content_empty);
                            v.setText("");
                            return false;
                        }
                        mKeyword = text;
                        getSearchResult(mKeyword);
                        XmAutoTracker.getInstance().onEvent(EventConstants.inputEnterClickEvent,
                                mKeyword, TAG, EventConstants.PageDescribe.searchActivity);
                    }
                    return false;
                }
            });
            inputEt.addTextChangedListener(new TextWatcher() {
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
                    button.setEnabled(!TextUtils.isEmpty(s) && !TextUtils.isEmpty(s.toString().trim()));
                }
            });
        }
        EventTtsManager.getInstance().init(this);
        if (autoCountDownTimer == null) {
            autoCountDownTimer = new CountDownTimer(NO_OPERATING_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    KLog.d("time----" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    final boolean success = audioFocusHelper.requestAudioFocus();
                    if (!success) {
                        XMToast.toastException(SearchActivity.this, R.string.mic_dismiss);
                        mSearchVoiceView.setNormal();
                        return;
                    }
                    mSearchVoiceView.setVoiceContent(getString(R.string.need_me_to_do));
                    EventTtsManager.getInstance().startSpeaking(getString(R.string.need_me_to_do), new OnTtsListener() {
                        @Override
                        public void onCompleted() {
                            mSearchVoiceView.setVoiceContent(getString(R.string.is_listener));
                            startRecord(false);
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

    private void cancelCountDown(CountDownTimer countDownTimer) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void initData() {
        setContent();
        audioFocusHelper = new AudioFocusHelper(this, null);
        mSearchVM = ViewModelProviders.of(this).get(SearchVM.class);
        mSearchVM.getSearchHistoryList().observe(this, new Observer<List<SearchBean>>() {
            @Override
            public void onChanged(@Nullable List<SearchBean> responses) {
                if (ListUtils.isEmpty(responses)) {
                    return;
                }
                List<String> list = new ArrayList<>();
                for (SearchBean bean : responses) {
                    list.add(bean.name);
                }
                updateFlowView(list, mLlSearchHistory, TYPE_HISTORY);
            }
        });
        mSearchVM.getAllRecommendList().observe(this, new Observer<XmResource<List<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<String>> responses) {
                if (responses != null) {
                    //当前activity网络异常状态时需要自己单独处理，不使用BaseActivity的StateView
                    responses.handle(new OnCallback<List<String>>() {
                        @Override
                        public void onSuccess(List<String> data) {
                            setContent();
                            if (!data.isEmpty()) {
                                updateFlowView(data, mLlRecommend, TYPE_HOT);
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            dismissProgress();
                            setNoNet();
                        }
                    });
                }
            }
        });
        mSearchVM.fetchRecommendList();
    }

    private void updateFlowView(@Nullable List<String> list, FlowLayout flowLayout, int type) {
        flowLayout.removeAllViews();
        if (ListUtils.isEmpty(list)) {
            return;
        }
        flowLayout.setViews(list, new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(String content) {
                String event = type == 0 ? EventConstants.searchHistoryEvent : EventConstants.searchHotEvent;
                XmAutoTracker.getInstance().onEvent(event, content, TAG, EventConstants.PageDescribe.searchActivity);
                getSearchResult(content);
            }
        });
    }

    public void startRecord(boolean isClick) {
        if (isClick) {
            final boolean success = audioFocusHelper.requestAudioFocus();
            if (!success) {
                XMToast.toastException(SearchActivity.this, R.string.mic_dismiss);
                mSearchVoiceView.setNormal();
                return;
            }
        }
        RemoteIatManager.getInstance().init(getApplicationContext());
        RemoteIatManager.getInstance().setOnIatListener(iatListener);
        RemoteIatManager.getInstance().startListeningRecord(6000, 700);
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(NOT_OPERATING_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    KLog.d("time----" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    KLog.d("MrMine", "onFinish: " + "cancelListening");
//                    mSearchVoiceView.setVoiceContent(getString(R.string.search_need_help));
                    isClickCancel = false;
                    RemoteIatManager.getInstance().cancelListening();
//                    mSearchVoiceView.setNormal();
                }
            };
            countDownTimer.start();
        } else {
            countDownTimer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时停止定时
        cancelCountDown(countDownTimer);
        cancelCountDown(autoCountDownTimer);
        EventTtsManager.getInstance().destroy();
    }

    protected void setNoNet() {
        mLlContent.setVisibility(View.GONE);
        mNetWorkView.setVisibility(View.VISIBLE);
    }

    protected void setContent() {
        mLlContent.setVisibility(View.VISIBLE);
        mNetWorkView.setVisibility(View.GONE);
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

    private void getSearchResult(final String keyword) {
        mSearchVM.insertHistory2DB(keyword);
        mSearchVoiceView.setNormalContentText(keyword);
        if (!NetworkUtils.isConnected(this)) {
            showToastException(R.string.net_error);
        } else {
            audioFocusHelper.abandonAudioFocus();
            mSearchVoiceView.setNormal();
            RemoteIatManager.getInstance().setOnIatListener(null);
            SearchResultActivity.startActivity(SearchActivity.this, keyword);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //当activity不在前台是停止定时
        cancelCountDown(countDownTimer);
        RemoteIatManager.getInstance().cancelListening();
        RemoteIatManager.getInstance().release();
        EventTtsManager.getInstance().stopSpeaking();
        audioFocusHelper.abandonAudioFocus();
        mSearchVoiceView.setNormal();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventTtsManager.getInstance().stopSpeaking();
        audioFocusHelper.abandonAudioFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchVM.getSearchHistoryList().setValue(mSearchVM.getHistoryList());
        setFirstTime();
        RemoteIatManager.getInstance().init(this);
        if (inputEt != null && !TextUtils.isEmpty(inputEt.getText().toString())) {
            inputEt.setText("");
        }
    }

    private OnIatListener iatListener = new OnIatListener() {
        @Override
        public void onComplete(String voiceText, String parseText) {
            isClickCancel = false;
            if (!TextUtils.isEmpty(voiceText)) {
                String word = VrUtils.replaceFilter(voiceText);
                if (!isClickCancel) {
                    mKeyword = word;
                    preRecoverNormal();
                    mSearchVoiceView.setNormalContentText(word);
                    getSearchResult(mKeyword);
                    XmAutoTracker.getInstance().onEvent(EventConstants.activityRecordCompletedEvent, mKeyword,
                            "SearchActivity", EventConstants.PageDescribe.searchActivity);
                } else {
                    mSearchVoiceView.setNormal();
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
        }

        @Override
        public void onError(int errorCode) {
            KLog.d("voice onError " + errorCode);
            isClickCancel = false;
            if (IatError.ERROR_MEDIA_FOCUS_LOSS == errorCode
                    || IatError.ERROR_MIC_FOCUS_LOSS == errorCode){
                XMToast.toastException(SearchActivity.this, R.string.mic_dismiss);
            }
            handleVoiceEmpty();
        }

        @Override
        public void onResult(String recognizerText, boolean isLast, String currentText) {
        }

        @Override
        public void onWavFileComplete() {
            KLog.d(" isun voice onWavFileComplete");
        }

        @Override
        public void onRecordComplete() {
        }
    };

    private void preRecoverNormal() {
        audioFocusHelper.abandonAudioFocus();
        RemoteIatManager.getInstance().setOnIatListener(null);
    }


    private void handleVoiceEmpty() {
        if (isClickCancel) {
            preRecoverNormal();
            return;
        }
        mSearchVoiceView.setVoiceContent(getString(R.string.search_voice_tip));
        EventTtsManager.getInstance().startSpeaking(getString(R.string.search_voice_tip), new OnTtsListener() {
            @Override
            public void onCompleted() {
                ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        preRecoverNormal();
                    }
                }, 200);
                mSearchVoiceView.setNormal();
            }

            @Override
            public void onBegin() {
            }

            @Override
            public void onError(int code) {
            }
        });
    }

    private void setFirstTime() {
        Boolean isFirstTime = TPUtils.get(this, MusicConstants.FIRST_ENTER_SEARCH, true);
        if (isFirstTime || ListUtils.isEmpty(mSearchVM.getHistoryList())) {
            mRlSearchHistory.setVisibility(View.GONE);
            mLlSearchHistory.setVisibility(View.GONE);
        } else {
            mRlSearchHistory.setVisibility(View.VISIBLE);
            mLlSearchHistory.setVisibility(View.VISIBLE);
        }
        TPUtils.put(this, MusicConstants.FIRST_ENTER_SEARCH, false);
    }

    @NormalOnClick({EventConstants.NormalClick.startSearchVoice, EventConstants.NormalClick.clearSearchVoice, EventConstants.NormalClick.cancelSearchVoice, EventConstants.NormalClick.retryLoading})
    @ResId({R.id.tv_start_voice_search, R.id.tv_clear, R.id.tv_recording_cancel, R.id.tv_retry})
    @Override
    public void onClick(View v) {
        cancelCountDown(autoCountDownTimer);
        switch (v.getId()) {
            case R.id.tv_start_voice_search:
                if (!MusicUtils.isNetConnected(this)) {
                    XMToast.toastException(this, R.string.voice_search_is_not_available);
                    return;
                }
                mSearchVoiceView.setVoiceContent(getString(R.string.is_listener));
                startRecord(true);
                break;
            case R.id.tv_clear:
                mSearchVM.clearAllHistory();
                mLlSearchHistory.removeAllViews();
                mRlSearchHistory.setVisibility(View.GONE);
                mLlSearchHistory.setVisibility(View.GONE);
                break;
            case R.id.tv_recording_cancel:
                EventTtsManager.getInstance().stopSpeaking();
                preRecoverNormal();
                mSearchVoiceView.setNormal();
                RemoteIatManager.getInstance().cancelListening();
                isClickCancel = true;
                break;
            case R.id.tv_retry:
                // 重新加载网络
                initData();
                break;
            case R.id.search_view_search_now_btn:
                startSearch();
                break;
        }
    }

    private void startSearch() {
        if (inputEt == null) return;
        mKeyword = inputEt.getText().toString().trim();
        if (TextUtils.isEmpty(mKeyword)) {
            showToast(R.string.search_content_empty);
            return;
        }
        getSearchResult(mKeyword);
        XmAutoTracker.getInstance().onEvent(searchNow, mKeyword, TAG, EventConstants.PageDescribe.searchActivity);
    }

    @Override
    public void applySkin() {
        if (button != null) {
            button.setBackgroundResource(R.drawable.search_view_search_now);
        }
    }
}
