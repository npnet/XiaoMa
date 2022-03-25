package com.xiaoma.motorcade.map.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMStreamParam;
import com.xiaoma.carlib.manager.XmMicManager;
import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.motorcade.MainActivity;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.constants.MotorcadeConstants;
import com.xiaoma.motorcade.common.hyphenate.SimpleConferenceListener;
import com.xiaoma.motorcade.common.hyphenate.SimpleGroupChangeListener;
import com.xiaoma.motorcade.common.locationshare.ILocationSharer;
import com.xiaoma.motorcade.common.locationshare.LocationSharerCallback;
import com.xiaoma.motorcade.common.locationshare.LocationSharerFactory;
import com.xiaoma.motorcade.common.manager.ConferenceManager;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.model.GroupMemberInfo;
import com.xiaoma.motorcade.common.model.MeetingInfo;
import com.xiaoma.motorcade.common.model.ShareLocationParam;
import com.xiaoma.motorcade.common.ui.MapActivity;
import com.xiaoma.motorcade.common.utils.MotorcadeSetting;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.motorcade.map.model.RobMicResult;
import com.xiaoma.motorcade.map.vm.ConferenceVM;
import com.xiaoma.motorcade.setting.ui.SettingActivity;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;

@PageDescComponent(MotorcadeConstants.PageDesc.motorcadeConference)
public class MotorcadeConferenceActivity extends MapActivity {

    private MeetingInfo meetingInfo;
    private ConferenceVM mMotorcadeVM;
    private static final String TAG = "MotorcadeConferenceActivity";
    public static final String MOTORCADE_INFO = "MotorcadeInfo";
    public static final String IS_FROM_SHARE = "IsFromShare";
    private static final int COUNT_DOWN_SECOND = 10;
    private String mChatId;
    private ILocationSharer iLocationSharer;
    private List<GroupMemberInfo> groupMemberInfos;
    private EMConferenceListener conferenceListener;
    private String streamId;
    private Handler handler = new Handler();
    private CountDownTimer downTimer;
    private User current;
    private AudioManager manager;
    private boolean isSpeak = false;
    private boolean isCarLibLongPress = false;
    private boolean isVrShow = false;
    private boolean isForground = false;
    private OnWheelKeyListener carLibListener;
    private EMConferenceStream currentStream = null;

    public static void launcherMapActivity(Context context, MeetingInfo info, boolean isFromShare) {
        Intent intent = new Intent(context, MotorcadeConferenceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_FROM_SHARE, isFromShare);
        bundle.putParcelable(MOTORCADE_INFO, info);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current = UserUtil.getCurrentUser();
        initConference();
        handlerIntent();
        initVM();
        startOnlineReport();
        registerVrReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMotorcadeVM.fetchMember(String.valueOf(meetingInfo.getId()));
        registerCarLibListener();
        isForground = true;
    }

    private void registerCarLibListener() {
        XmWheelManager.getInstance().register(carLibListener = new OnWheelKeyListener.Stub() {
            @Override
            public boolean onKeyEvent(int keyAction, int keyCode) {
                if (WheelKeyEvent.KEYCODE_WHEEL_VOICE == keyCode) {
                    switch (keyAction) {
                        case WheelKeyEvent.ACTION_LONG_PRESS:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isCarLibLongPress = true;
                                    if (!NetworkUtils.isConnected(MotorcadeConferenceActivity.this)) {
                                        showNoNet();
                                        return;
                                    }
                                    startPeriodRobMic();
                                }
                            });
                            break;
                        case WheelKeyEvent.ACTION_RELEASE:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isCarLibLongPress) {
                                        return;
                                    }
                                    isCarLibLongPress = false;
                                    if (!NetworkUtils.isConnected(MotorcadeConferenceActivity.this)) {
                                        return;
                                    }
                                    if (!isSpeak) {
                                        cancleMic();
                                    } else {
                                        releaseMic();
                                    }
                                }
                            });
                            break;
                    }
                }
                return false;
            }

            @Override
            public String getPackageName() throws RemoteException {
                return "com.xiaoma.motorcade";
            }
        }, new int[]{WheelKeyEvent.KEYCODE_WHEEL_VOICE});
    }

    private void unregisterCarLibListener() {
        if (carLibListener != null) {
            XmWheelManager.getInstance().unregister(carLibListener);
        }
    }

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager
            .OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    //重新获得焦点，若之前有订阅流被取消，此时恢复订阅
                    KLog.d(TAG, "get audio focus again, subscribe stream again");
                    if (currentStream != null) {
                        subscribe(currentStream);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //失去音频焦点
                    KLog.d(TAG, "lost audio focus, cancel subscribe stream");
                    if (currentStream != null) {
                        unSubscribe(currentStream);
                    }
                    if (!isSpeak) {
                        cancleMic();
                    } else {
                        releaseMic();
                    }
                    break;
            }
        }
    };

    private boolean requestAudio() {
        synchronized (this) {
            int rlt;
            if (manager == null) {
                manager = (AudioManager) getSystemService(AUDIO_SERVICE);
            }
            rlt = manager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            KLog.i(TAG, "requestAudio: rlt: " + rlt);

            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == rlt;
        }
    }

    private void releaseAudio() {
        synchronized (this) {
            if (manager == null) {
                manager = (AudioManager) getSystemService(AUDIO_SERVICE);
            }
            manager.abandonAudioFocus(audioFocusChangeListener);
        }
    }

    private void initConference() {
        EMClient.getInstance().groupManager().addGroupChangeListener(listener);
        ConferenceManager.getInstance().addConferenceListener(conferenceListener = new SimpleConferenceListener() {
            @Override
            public void onMemberJoined(EMConferenceMember emConferenceMember) {
                super.onMemberJoined(emConferenceMember);
                refreshMemeber();
            }

            @Override
            public void onMemberExited(EMConferenceMember emConferenceMember) {
                super.onMemberExited(emConferenceMember);
                refreshMemeber();
            }

            @Override
            public void onStreamAdded(EMConferenceStream emConferenceStream) {
                super.onStreamAdded(emConferenceStream);
                KLog.d(TAG, "onStreamAdded: ");
                subscribe(emConferenceStream);
            }

            @Override
            public void onStreamRemoved(EMConferenceStream emConferenceStream) {
                super.onStreamRemoved(emConferenceStream);
                KLog.d(TAG, "onStreamRemoved: ");
                releaseAudio();
                if (emConferenceStream != null) {
                    unSubscribe(emConferenceStream);
                    if (currentStream == emConferenceStream) {
                        currentStream = null;
                    }
                }
            }
        });
    }

    private void subscribe(EMConferenceStream emConferenceStream) {
        if (!MotorcadeSetting.isReceive(String.valueOf(meetingInfo.getId()))) {
            return;
        }
        currentStream = emConferenceStream;
        if (isVrShow) {
            //语音助手弹窗起来，只记录currentStream = emConferenceStream;不订阅
            return;
        }
        if (requestAudio()) {
            EMClient.getInstance().conferenceManager().subscribe(emConferenceStream, null, new EMValueCallBack<String>() {
                @Override
                public void onSuccess(String value) {
                    KLog.d(TAG, "subscribe onSuccess: " + value);
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });
        }
    }

    private void unSubscribe(EMConferenceStream emConferenceStream) {
        EMClient.getInstance().conferenceManager().unsubscribe(emConferenceStream, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                KLog.d(TAG, "subscribe unSubscribe onSuccess: " + value);
            }

            @Override
            public void onError(int error, String errorMsg) {
                KLog.d(TAG, "subscribe unSubscribe onError: " + errorMsg);
            }
        });
    }

    //请求麦克风焦点
    private boolean requestMicFocus() {
        return XmMicManager.getInstance().requestMicFocus(micListener, XmMicManager.MIC_LEVEL_APP, XmMicManager.FLAG_NONE);
    }

    XmMicManager.OnMicFocusChangeListener micListener = new XmMicManager.OnMicFocusChangeListener() {
        @Override
        public void onMicFocusChange(int var1) {
            switch (var1) {
                case XmMicManager.MICFOCUS_LOSS:
                    KLog.d(TAG, "onMicFocusChange MICFOCUS_LOSS ");
                    if (!isVrShow && isForground) {
                        requestMicFocus();
                        return;
                    }
                    if (!isSpeak) {
                        cancleMic();
                    } else {
                        releaseMic();
                    }

                    break;
                case XmMicManager.MICFOCUS_GAIN:
                    KLog.d(TAG, "onMicFocusChange MICFOCUS_GAIN ");
                    break;
            }
        }
    };

    //释放麦克风焦点，一定要保证释放！！！
    private void releaseMicFocus() {
        XmMicManager.getInstance().abandonMicFocus(micListener);
    }

    private void startOnlineReport() {
        if (mMotorcadeVM == null) {
            mMotorcadeVM = ViewModelProviders.of(this).get(ConferenceVM.class);
        }
        mMotorcadeVM.startOnlineReport(meetingInfo.getId());
    }

    private void registerVrReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG);
        filter.addAction(CenterConstants.DISMISS_VOICE_ASSISTANT_DIALOG);
        registerReceiver(vrReceiver, filter);
    }

    private void startLocation() {
        if (mChatId == null) {
            return;
        }
        iLocationSharer = LocationSharerFactory.makeLocationSharer(this, mChatId);
        iLocationSharer.addCallback(sharerCallback);
        iLocationSharer.start();
    }

    LocationSharerCallback sharerCallback = new LocationSharerCallback() {
        @Override
        public void onSendLocation(ILocationSharer sharer, ShareLocationParam param) {
            if (current == null) {
                current = UserUtil.getCurrentUser();
            }
            createMarker(current.getName(), current.getPicPath(), current.getId(),
                    current.getHxAccountService(), new LatLng(param.getLat(), param.getLon()));
        }

        @Override
        public void onReceiveLocation(ILocationSharer sharer, String fromId, ShareLocationParam param) {
            if (groupMemberInfos != null && !fromId.equals(UserUtil.getCurrentUser().getHxAccountService())) {
                for (GroupMemberInfo groupMemberInfo : groupMemberInfos) {
                    if (groupMemberInfo.getHxAccount().equals(fromId)) {
                        createMarker(groupMemberInfo.getNickName(), groupMemberInfo.getHeader(), groupMemberInfo.getId(),
                                groupMemberInfo.getHxAccount(), new LatLng(param.getLat(), param.getLon()));
                    }
                }
            }
        }

        @Override
        public void onReceiveLocationOut(ILocationSharer sharer, String fromId) {
            if (groupMemberInfos != null && !fromId.equals(UserUtil.getCurrentUser().getHxAccountService())) {
                for (GroupMemberInfo groupMemberInfo : groupMemberInfos) {
                    if (groupMemberInfo.getHxAccount().equals(fromId)) {
                        removeMarker(groupMemberInfo.getHxAccount());
                    }
                }
            }
        }
    };

    private void initVM() {
        mMotorcadeVM = ViewModelProviders.of(this).get(ConferenceVM.class);
        mMotorcadeVM.getGroupMemberList().observe(this, new Observer<List<GroupMemberInfo>>() {
            @Override
            public void onChanged(@Nullable List<GroupMemberInfo> groupMemberInfos) {
                if (groupMemberInfos != null) {
                    MotorcadeConferenceActivity.this.groupMemberInfos = groupMemberInfos;
                    refreshMemeber();
                }
            }
        });
        mMotorcadeVM.fetchMember(String.valueOf(meetingInfo.getId()));
        mMotorcadeVM.getRobMicResult().observe(this, new Observer<RobMicResult>() {
            @Override
            public void onChanged(@Nullable RobMicResult robMicResult) {
                if (robMicResult != null) {
                    handleRobResult(robMicResult);
                }
            }
        });
    }

    private void handleRobResult(RobMicResult robMicResult) {
        if (robMicResult == null) {
            return;
        }
        if (robMicResult.getLineCount() == 0 && robMicResult.getSpeakTime() != 0) {
            //抢到了麦，开始传输音频流
            mMotorcadeVM.stopRobMic();
            showRobbingMicSuccess();
            handleCountDown(robMicResult.getSpeakTime());
            publishStream();
        } else {
            if (robMicResult.isFirst()) {
                showToastException(R.string.other_has_mic);
                releaseMic();
            }
        }
    }


    private void handlerIntent() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            XMToast.toastException(this, R.string.error_motorcade_msg);
            finish();
            return;
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            MeetingInfo info = (MeetingInfo) extras.get(MOTORCADE_INFO);
            if (info != null) {
                meetingInfo = info;
                mChatId = meetingInfo.getHxGroupId();
                startLocation();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endCountDown();
        if (iLocationSharer != null) {
            iLocationSharer.removeCallback(sharerCallback);
            iLocationSharer.stop();
        }
        if (listener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(listener);
        }
        if (conferenceListener != null) {
            ConferenceManager.getInstance().removeConferenceListener(conferenceListener);
        }
        if (mMotorcadeVM != null) {
            mMotorcadeVM.reportOnline(meetingInfo.getId(), false);
        }
        mMotorcadeVM.cancleMic(meetingInfo.getId());
        mMotorcadeVM.releaseMic(meetingInfo.getId());
        ConferenceManager.getInstance().exitConference(null);
        releaseAudio();
        releaseMicFocus();
        unregisterReceiver(vrReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterCarLibListener();
        releaseAudio();
        isForground = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMicFocus();
    }

    EMGroupChangeListener listener = new SimpleGroupChangeListener() {

        @Override
        public void onMemberJoined(String s, String s1) {
            if (s.equals(mChatId)) {
                mMotorcadeVM.fetchMember(String.valueOf(meetingInfo.getId()));
            }
        }

        @Override
        public void onMemberExited(String s, String s1) {
            if (s.equals(mChatId)) {
                mMotorcadeVM.fetchMember(String.valueOf(meetingInfo.getId()));
            }
        }
    };

    private void refreshMemeber() {
        if (meetingInfo == null) {
            return;
        }
        ConferenceManager.getInstance().getConferenceInfo(meetingInfo.getMeetingAccount(), meetingInfo.getMeetingPassword(), new EMValueCallBack<EMConference>() {
            @Override
            public void onSuccess(final EMConference emConference) {
                if (groupMemberInfos != null && !groupMemberInfos.isEmpty()) {
                    if (emConference.getMemberNum() > groupMemberInfos.size()) {
                        setOnlineText(groupMemberInfos.size() + "/" + groupMemberInfos.size());
                        return;
                    }
                    setOnlineText(emConference.getMemberNum() + "/" + groupMemberInfos.size());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void publishStream() {
        if (!requestAudio()) {
            KLog.e(TAG, "publishStream -> cannot gain audio focus");
            return;
        }
        EMStreamParam param = new EMStreamParam();
        param.setStreamType(EMConferenceStream.StreamType.NORMAL);
        param.setVideoOff(true);
        param.setAudioOff(false);
        EMClient.getInstance().conferenceManager().publish(param, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String stream) {
                KLog.d(TAG, "onSuccess: " + streamId);
                isSpeak = true;
                streamId = stream;
            }

            @Override
            public void onError(int error, String errorMsg) {
                KLog.d(TAG, "onError: " + errorMsg);
            }
        });
    }

    private void startPeriodRobMic() {
        if (mMotorcadeVM == null) {
            showRobFailed();
            return;
        }
        if (requestMicFocus()) {
            showRobbingMicLoading("_");
            if (requestAudio()) {
                mMotorcadeVM.startRobMic(meetingInfo.getId());
            } else {
                showToastException(R.string.get_audio_focus_failed);
            }
        } else {
            showToastException(R.string.get_mic_failed);
        }
    }

    private void releaseMic() {
        releaseMicFocus();
        showClickStart();
        mMotorcadeVM.releaseMic(meetingInfo.getId());
        releaseAudio();
        if (streamId == null) {
            return;
        }
        isSpeak = false;
        EMClient.getInstance().conferenceManager().unpublish(streamId, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(int i, String s) {

            }
        });
        endCountDown();
    }

    private void endCountDown() {
        try {
            handler.removeCallbacks(countDown);
            if (downTimer != null) {
                downTimer.cancel();
            }
        } catch (Exception e) {
            KLog.d(TAG, "handler remove countDown Exception");
            e.printStackTrace();
        }
    }

    private void cancleMic() {
        releaseMicFocus();
        showClickStart();
        mMotorcadeVM.cancleMic(meetingInfo.getId());
    }

    private void handleCountDown(int speakTime) {
        handler.postDelayed(countDown, (speakTime - COUNT_DOWN_SECOND) * 1000);
    }

    Runnable countDown = new Runnable() {
        @Override
        public void run() {
            downTimer = new CountDownTimer(COUNT_DOWN_SECOND * 1000, 1000) {
                @Override
                public void onTick(final long millisUntilFinished) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCountDownText((int) (millisUntilFinished / 1000));
                        }
                    });
                }

                @Override
                public void onFinish() {
                    releaseMic();
                }
            };
            downTimer.start();
        }
    };

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.click_start_rl:
            case R.id.no_network_ll:
                if (!NetworkUtils.isConnected(this)) {
                    showNoNet();
                    return;
                }
                startPeriodRobMic();
                break;
            case R.id.cancel_rob_tv:
                if (!isSpeak) {
                    cancleMic();
                } else {
                    releaseMic();
                }
                break;
            case R.id.btn_location:
                locationPosition();
                break;
            case R.id.online_display_ll:
                GroupCardInfo info = new GroupCardInfo();
                info.setHxGroupId(meetingInfo.getHxGroupId());
                info.setAdminId(meetingInfo.getAdminId());
                info.setId(meetingInfo.getId());
                info.setHxGroupId(meetingInfo.getQunKey());
                info.setNick(meetingInfo.getNick());
                info.setPicPath(meetingInfo.getPicPath());
                info.setCount(meetingInfo.getCount());
                SettingActivity.launch(this, info);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        boolean isFromShare = getIntent().getBooleanExtra(IS_FROM_SHARE, false);
        if (isFromShare) {
            MainActivity.launch(this);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private BroadcastReceiver vrReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG)) {
                KLog.d(TAG, "onReceive: SHOW_VOICE_ASSISTANT_DIALOG");
                //语音助手弹窗起来
                isVrShow = true;
            } else if (action.equals(CenterConstants.DISMISS_VOICE_ASSISTANT_DIALOG)) {
                KLog.d(TAG, "onReceive: DISMISS_VOICE_ASSISTANT_DIALOG");
                //语音助手弹窗关闭
                isVrShow = false;
                if (currentStream != null) {
                    subscribe(currentStream);
                }
            }
        }
    };

}
