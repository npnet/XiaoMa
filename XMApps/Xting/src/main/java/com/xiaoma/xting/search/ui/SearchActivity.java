package com.xiaoma.xting.search.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.skin.views.XmSkinButton;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.SearchVoiceView;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.constant.IatError;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.dispatch.annotation.Command;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vr.utils.VrUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.AudioCarEventSignal;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingNodeHelper;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.view.FlowLayout;
import com.xiaoma.xting.sdk.bean.XMHotWord;
import com.xiaoma.xting.search.model.SearchBean;
import com.xiaoma.xting.search.vm.SearchVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import skin.support.widget.SkinCompatSupportable;

@PageDescComponent(EventConstants.PageDescribe.ACTIVITY_SEARCH)
public class SearchActivity extends BaseActivity implements View.OnClickListener, OnRetryClickListener, SkinCompatSupportable, AudioCarEventSignal.ReversingCallback {
    private static final int NOT_OPERATING_TIME = 6 * 1000;
    private static final int NO_OPERATING_TIME = 10 * 1000;
    public static final int TYPE_HISTORY = 0;
    public static final int TYPE_HOT = 1;
    private String TAG = SearchActivity.class.getSimpleName();
    public CountDownTimer countDownTimer;
    public CountDownTimer autoCountDownTimer;
    private SearchVoiceView mSearchVoiceView;
    private TextView mTvClear;
    private TextView mTvChange;
    private SearchVM mSearchVM;
    private FlowLayout mLlSearchHistory;
    private FlowLayout mLlRecommend;
    private String mKeyword;
    private RelativeLayout mRlSearchHistory;
    private LinearLayout mLlContent;
    private StateView mStateView;
    private EditText inputEt;
    private Button mButton;
    private boolean hasStartRecord = false;
    private boolean isClickCancel = false;

    public static void launcher(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xt_search);
        XmCarEventDispatcher.getInstance().registerEvent(AudioCarEventSignal.getInstance());
        AudioCarEventSignal.getInstance().setReversingCallback(this);
        initView();
        initData();
        registerFocusLossReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(focusLossReceiver);
        XmCarEventDispatcher.getInstance().unregisterEvent(AudioCarEventSignal.getInstance());
        hasStartRecord = false;
        //销毁时停止定时
        cancelCountDown(countDownTimer);
        cancelCountDown(autoCountDownTimer);
        EventTtsManager.getInstance().destroy();
        RemoteIatManager.getInstance().release();
    }

    private void initView() {
        mSearchVoiceView = findViewById(R.id.search_voice_view);
        mLlSearchHistory = findViewById(R.id.ll_search_history);
        mRlSearchHistory = findViewById(R.id.rl_search_history);
        mLlRecommend = findViewById(R.id.ll_recommend);
        mTvClear = findViewById(R.id.tv_clear);
        mTvChange = findViewById(R.id.tv_change);
        mLlContent = findViewById(R.id.ll_content);
        mLlContent = findViewById(R.id.ll_content);
        mStateView = findViewById(R.id.stateView);
        mStateView.setOnRetryClickListener(this);
        mSearchVoiceView.setOnVoiceClickListener(this);
        mSearchVoiceView.setOnRecordingCancelClickListener(this);
        mTvClear.setOnClickListener(this);
        mTvChange.setOnClickListener(this);
        mSearchVoiceView.setNormal();
        mSearchVoiceView.setHint(getString(R.string.search_view_hide_text));
        initEt();
        addSearchNowButton();
    }

    /**
     * 添加立即搜索按钮
     */
    private void addSearchNowButton() {
        ViewGroup ll = mSearchVoiceView.findViewById(R.id.ll_normal);
        if (ll == null) return;
        mButton = new XmSkinButton(this);
        mButton.setId(R.id.search_now);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(-15, 0, 0, 8);
        mButton.setLayoutParams(lp);
        mButton.setBackgroundResource(R.drawable.selector_search_now);
        mButton.setText(getString(R.string.search_now));
        mButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
        mButton.setPadding(0, 0, 0, 6);
        mButton.setGravity(Gravity.CENTER);
        ll.addView(mButton, mSearchVoiceView.getChildCount() + 1);
        mButton.setOnClickListener(this);
    }

    private void initData() {
        mSearchVM = ViewModelProviders.of(this).get(SearchVM.class);
        mSearchVM.getSearchHistoryList().observe(this, new Observer<List<SearchBean>>() {
            @Override
            public void onChanged(@Nullable List<SearchBean> responses) {
                if (ListUtils.isEmpty(responses)) {
                    return;
                }
                List<String> list = new ArrayList<>();
                list.add(EventConstants.PageDescribe.TAG_SEARCH_HISTORY);
                for (SearchBean bean : responses) {
                    list.add(bean.name);
                }
                updateFlowView(list, mLlSearchHistory, TYPE_HISTORY);
            }
        });
        subscriberRecommendList();
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
                            XMToast.showToast(getApplication(), R.string.empty_search_text);
                            return false;
                        }
                        mKeyword = text;
                        getSearchResult(mKeyword);

                        XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.TAG_SEARCH_INPUT, mKeyword, SearchActivity.this.getClass().getName(), EventConstants.PageDescribe.ACTIVITY_SEARCH);
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
                    if (hasStartRecord) {
                        return;
                    }

                    mSearchVoiceView.setVoiceContent(getString(R.string.need_me_to_do));
                    EventTtsManager.getInstance().startSpeaking(getString(R.string.need_me_to_do), new OnTtsListener() {
                        @Override
                        public void onCompleted() {
                            mSearchVoiceView.setVoiceContent(getString(R.string.is_listener));
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

    private void cancelCountDown(CountDownTimer countDownTimer) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void startRecord() {
        RemoteIatManager.getInstance().startListeningRecord(6000, 700);
        isClickCancel = false;
        hasStartRecord = true;
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

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgress();
        //当activity不在前台是停止定时
        cancelCountDown(countDownTimer);
        RemoteIatManager.getInstance().cancelListening();
        RemoteIatManager.getInstance().release();
        mSearchVoiceView.setNormal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchVM.getSearchHistoryList().setValue(mSearchVM.getHistoryList());
        setFirstTime();
        mSearchVoiceView.clearEditText();
        initVoiceEngine();
    }


    private void initVoiceEngine() {
        RemoteIatManager.getInstance().init(this);
        RemoteIatManager.getInstance().setOnIatListener(new OnIatListener() {
            @Override
            public void onComplete(String voiceText, String parseText) {
                KLog.d("voice onComplete " + voiceText);
                if (!TextUtils.isEmpty(voiceText)) {
                    String word = VrUtils.replaceFilter(voiceText);
                    mKeyword = word;
                    mSearchVoiceView.setNormalContentText(word);
                    getSearchResult(mKeyword);
                    XmAutoTracker.getInstance().onEvent(EventConstants.ACTIVITY_RECORD_COMPLETED_EVENT, mKeyword,
                            TAG, EventConstants.PageDescribe.ACTIVITY_SEARCH);
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
                mSearchVoiceView.setNormal();
                if (IatError.ERROR_MEDIA_FOCUS_LOSS == errorCode
                        || IatError.ERROR_MIC_FOCUS_LOSS == errorCode) {
                    XMToast.toastException(SearchActivity.this, R.string.mic_dismiss);
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

    private void handleVoiceEmpty() {
        if (isClickCancel) {
            return;
        }

        mSearchVoiceView.setVoiceContent(getString(R.string.search_voice_tip));
        RemoteIatManager.getInstance().stopListening();
        EventTtsManager.getInstance().startSpeaking(getString(R.string.search_voice_tip), new OnTtsListener() {
            @Override
            public void onCompleted() {
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
        Boolean isFirstTime = TPUtils.get(this, XtingConstants.FIRST_ENTER_SEARCH, true);
        if (isFirstTime || ListUtils.isEmpty(mSearchVM.getHistoryList())) {
            mRlSearchHistory.setVisibility(View.GONE);
            mLlSearchHistory.setVisibility(View.GONE);
        } else {
            mRlSearchHistory.setVisibility(View.VISIBLE);
            mLlSearchHistory.setVisibility(View.VISIBLE);
        }
        TPUtils.put(this, XtingConstants.FIRST_ENTER_SEARCH, false);
    }

    @NormalOnClick({EventConstants.NormalClick.ACTION_START_VOICE_SEARCH,
            EventConstants.NormalClick.ACTION_CLEAR_SEARCH_HISTORY,
            EventConstants.NormalClick.ACTION_CANCEL_VOICE_SEARCH,
            EventConstants.NormalClick.ACTION_SEARCh_NOW})
    @ResId({R.id.tv_start_voice_search, R.id.tv_clear, R.id.tv_recording_cancel, R.id.search_now})
    @Override
    public void onClick(View v) {
        cancelCountDown(autoCountDownTimer);
        switch (v.getId()) {
            case R.id.tv_start_voice_search:
                if (NetworkUtils.isConnected(SearchActivity.this)) {
                    mSearchVoiceView.setVoiceContent(getString(R.string.is_listener));
                    startRecord();
                } else {
                    XMToast.showToast(SearchActivity.this, R.string.net_not_connect);
                }
                break;
            case R.id.tv_clear:
                XtingUtils.getDBManager().deleteAll(SearchBean.class);
                mSearchVM.getSearchHistoryList().setValue(null);
                mLlSearchHistory.removeAllViews();
                mRlSearchHistory.setVisibility(View.GONE);
                mLlSearchHistory.setVisibility(View.GONE);
                break;
            case R.id.tv_recording_cancel:
                isClickCancel = true;
                cancelRecordVoice();
                break;
            case R.id.search_now:
                if (inputEt == null) return;
                mKeyword = inputEt.getText().toString();
                if (TextUtils.isEmpty(mKeyword)) {
                    XMToast.showToast(getApplication(), R.string.empty_search_text);
                    return;
                }
                getSearchResult(mKeyword);
                break;
        }
    }


    private void cancelRecordVoice() {
        mSearchVoiceView.setNormal();
        RemoteIatManager.getInstance().cancelListening();
    }


    private void setContentView() {
        mLlContent.setVisibility(View.VISIBLE);
        mStateView.setVisibility(View.GONE);
        subscriberRecommendList();
    }

    private void subscriberRecommendList() {
        mSearchVM.getRecommendList().observe(this, new Observer<XmResource<List<XMHotWord>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<XMHotWord>> listXmResource) {
                listXmResource.handle(new OnCallback<List<XMHotWord>>() {
                    @Override
                    public void onSuccess(final List<XMHotWord> data) {
                        List<String> list = new ArrayList<>();
                        list.add(EventConstants.PageDescribe.TAG_SEARCH_HOT);
                        for (XMHotWord bean : data) {
                            list.add(bean.getSearchword());
                        }
                        updateFlowView(list, mLlRecommend, TYPE_HOT);
                    }

                    @Override
                    public void onError(int code, String message) {
                        setNoNetView();
                    }


                });
            }
        });
    }

    private String searchContent;

    private void updateFlowView(@Nullable List<String> list, FlowLayout flowLayout, final int type) {
        flowLayout.removeAllViews();
        if (ListUtils.isEmpty(list) || list.size() == 1) {
            return;
        }
        flowLayout.setViews(list, new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(String content) {
                getSearchResult(content);
                String event = type == 0 ? EventConstants.searchHistoryEvent : EventConstants.searchHotEvent;
                XmAutoTracker.getInstance().onEvent(event, content, TAG, EventConstants.PageDescribe.ACTIVITY_SEARCH);
            }
        });
    }

    private void setNoNetView() {
        mLlContent.setVisibility(View.GONE);
        mStateView.setVisibility(View.VISIBLE);
        mStateView.showNoNetwork();
    }

    private void getSearchResult(final String keyword) {
        Log.d("Jir_Code", String.format("getSearchResult[%1$s , %2$s]", searchContent, keyword));
        if (Objects.equals(searchContent, keyword)) {
            return;
        }
        searchContent = keyword;
        showProgressDialog(R.string.loading);
        XmAutoTracker.getInstance().onEvent(
                EventConstants.PageDescribe.TAG_SEARCH_CONTENT,
                keyword,
                SearchActivity.this.getClass().getName(),
                EventConstants.NormalClick.ACTION_NET_NONE_RETRY);

        mSearchVM.insertHistory2DB(keyword);
        mSearchVoiceView.setNormalContentText(keyword);
        if (NetworkUtils.isConnected(SearchActivity.this)) {
            mSearchVoiceView.setNormal();
            SearchResultActivity.launch(SearchActivity.this, keyword);
        } else {
            searchContent = null;
            setNoNetView();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Jir_Code", String.format("onStop[%1$s]", searchContent));
        searchContent = null;
    }

    @Override
    public void onRetryClick(View view, Type type) {
        XmAutoTracker.getInstance().onEvent(
                EventConstants.PageDescribe.ACTIVITY_SEARCH,
                SearchActivity.this.getClass().getName(),
                EventConstants.NormalClick.ACTION_NET_NONE_RETRY);
        // 重新加载网络
        if (NetworkUtils.isConnected(this)) {
            setContentView();
        }
    }


    @Override
    public String getThisNode() {
        return NodeConst.Xting.ACT_SEARCH;
    }

    @Command("(打开(我的)?|我的)收藏")
    public void showMyCollect(String voiceCmd) {
        Log.d("VoiceTest", "{showMyCollect}-[voiceCmd] : " + voiceCmd);
        XtingNodeHelper.jump2MyCollect(this);
    }

    @Command("(打开)?(节目分类)(界面)?")
    public void showCategory(String voiceCmd) {
        Log.d("VoiceTest", "{showCategory}-[voiceCmd] : " + voiceCmd);
        XtingNodeHelper.jump2Category(this);
    }

    @Command("打开播放列表")
    public void popPlayList() {
        XtingNodeHelper.popoutPlayList(this);
    }

    @Command("(（这是|现在放的是|正在播放）什么歌)|(这歌叫什么(名字)?)|(这是谁(唱的)|(的歌))|(打开听歌识曲)")
    public void listenToRecognize() {
        XtingNodeHelper.openListenToRecognize(this);
    }

    @Override
    public void applySkin() {
        if (mButton != null) {
            mButton.setBackgroundResource(R.drawable.selector_search_now);
        }
    }

    @Override
    public void onReversing() {
        cancelRecordVoice();
    }


    private void registerFocusLossReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CenterConstants.IN_A_CALL);
        registerReceiver(focusLossReceiver, intentFilter);
    }


    private BroadcastReceiver focusLossReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CenterConstants.IN_A_CALL.equals(intent.getAction()) || CenterConstants.INCOMING_CALL.equals(intent.getAction())) {
                cancelRecordVoice();
            }
        }
    };
}
