package com.xiaoma.service.common.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.manager.XmMicManager;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.ServiceConstants;
import com.xiaoma.service.common.manager.AudioAuditionHelper;
import com.xiaoma.service.common.manager.CarDataManager;
import com.xiaoma.service.common.manager.IBCallManager;
import com.xiaoma.service.common.manager.LastTimeFormatUtils;
import com.xiaoma.service.common.views.CircleCharAvatarView;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author taojin
 * @date 2019/5/28
 */
public class TboxCallWindowService extends Service implements ICarEvent {
    private final String TAG = "TboxCallWindowService";
    private WindowManager.LayoutParams dialingParams;
    private WindowManager.LayoutParams phoneParams;
    private WindowManager.LayoutParams smallParams;
    private WindowManager windowManager;
    private Context mContext;
    private RelativeLayout dialingTouchLayout;
    private RelativeLayout phoneToucherLayout;
    private RelativeLayout smallToucherLayout;
    private int callType;
    private ScheduledExecutorService mExecutorService;
    private long time;
    private TextView tvTime;
    private boolean isCalling;
    /**
     * 显示类型用于判断当前window显示的是哪个view（拨号为1，通话为2，最小化为3）
     */
    private int showType = -1;
    private boolean isStop = false;

    private AudioManager mAudioManager;
    private static final int AUDIO_FOCUS_GAIN_FLAG = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;

    private final int[] KEYCODE = new int[]{WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB, WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD, WheelKeyEvent.KEYCODE_WHEEL_MUTE};
    private final XmMicManager.OnMicFocusChangeListener onMicFocusChangeListener = new XmMicManager.OnMicFocusChangeListener() {
        @Override
        public void onMicFocusChange(int var1) {

        }
    };

    private OnWheelKeyListener mCallWheelKeyListener = new OnWheelKeyListener.Stub() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onKeyEvent(int keyAction, int keyCode) {
            List<Object> keyCodeList = Arrays.stream(KEYCODE).boxed().collect(Collectors.toList());
            if (keyAction == WheelKeyEvent.ACTION_CLICK) {
                if (keyCodeList.contains(keyCode)) {
                    KLog.d(TAG, "WheelKeyEvent.ACTION_CLICK");
                    switch (keyCode) {
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD:
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB:
                            break;
                        case WheelKeyEvent.KEYCODE_WHEEL_MUTE:
                            hangUpCall();
                            hangUpIBCall();
                            break;
                    }
                }
                return true;
            } else if (keyAction == WheelKeyEvent.ACTION_LONG_PRESS) {
                if (keyCodeList.contains(keyCode)) {
                    KLog.d(TAG, "WheelKeyEvent.ACTION_LONG_PRESS");
                    switch (keyCode) {
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD:
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB:
                            break;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public String getPackageName() throws RemoteException {
            return mContext.getPackageName();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        XmCarEventDispatcher.getInstance().registerEvent(this);
        sendBroadcast(new Intent(CenterConstants.IN_A_IBCALL));
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            callType = intent.getIntExtra("call_type", -1);
            IBCallManager.getInstance().setIBCall(true);
        } else {
            IBCallManager.getInstance().setIBCall(false);
            stopSelf();
        }
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocusForCall(callType == 1 ? AudioManager.STREAM_ICALL : AudioManager.STREAM_BCALL, AUDIO_FOCUS_GAIN_FLAG);
        }
//        initAudioAudition();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        XmWheelManager.getInstance().register(mCallWheelKeyListener, KEYCODE);
        createDialingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 拨号window
     */
    private void createDialingWindow() {
        dialingParams = new WindowManager.LayoutParams();
        if (windowManager == null) {
            windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        }
        initParams(dialingParams);
        dialingParams.width = 550;
        dialingParams.height = 594;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        dialingTouchLayout = (RelativeLayout) inflater.inflate(R.layout.layout_dialing_window, null);
        windowManager.addView(dialingTouchLayout, dialingParams);
        showViewByDialingState();

    }

    /**
     * 通话中window
     */
    private void createPhoneWindow() {
        phoneParams = new WindowManager.LayoutParams();
        if (windowManager == null) {
            windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        }
        initParams(phoneParams);
        phoneParams.width = 550;
        phoneParams.height = 594;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        phoneToucherLayout = (RelativeLayout) inflater.inflate(R.layout.layout_phone_window, null);
        windowManager.addView(phoneToucherLayout, phoneParams);
        showViewByPhoneState();

    }


    private void createSmallCallWindow() {
        smallParams = new WindowManager.LayoutParams();
        if (windowManager == null) {

            windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        }
        initParams(smallParams);
        smallParams.width = 613;
        smallParams.height = 103;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        smallToucherLayout = (RelativeLayout) inflater.inflate(R.layout.layout_call_state_window, null);
        windowManager.addView(smallToucherLayout, smallParams);
//        smallToucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        smallToucherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeViewImmediate(smallToucherLayout);
                if (isCalling) {

                    if (phoneToucherLayout == null) {
                        createPhoneWindow();
                    } else {
                        windowManager.addView(phoneToucherLayout, phoneParams);
                        showViewByPhoneState();
                    }

                } else {
                    windowManager.addView(dialingTouchLayout, dialingParams);
                    showViewByDialingState();
                }

            }
        });

        showSmallViewByState();

    }

    private void initParams(WindowManager.LayoutParams params) {
        if (Build.VERSION.SDK_INT >= 27) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.x = 195;
        params.y = 32;

        params.gravity = Gravity.LEFT | Gravity.TOP;
    }

    /**
     * 拨号状态
     */
    private void showViewByDialingState() {
        CircleCharAvatarView icon = dialingTouchLayout.findViewById(R.id.icon);
        TextView phoneNum = dialingTouchLayout.findViewById(R.id.phone_num);
        final ImageView hangUp = dialingTouchLayout.findViewById(R.id.hang_up);
        ImageView minimum = dialingTouchLayout.findViewById(R.id.minimum);
        final TextView currentStatus = dialingTouchLayout.findViewById(R.id.current_status);
        final ImageView hangUpBg = dialingTouchLayout.findViewById(R.id.hang_up_bg);


        currentStatus.setText(mContext.getString(R.string.dialing));
        icon.setImageDrawable(mContext.getDrawable(R.drawable.head));
        if (callType == 1) {
            phoneNum.setText(mContext.getString(R.string.service_line));
        } else if (callType == 2) {
            phoneNum.setText(mContext.getString(R.string.rescue_line));
        }

        hangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus.setText(R.string.call_off);
                hangUpCall();
                hangUpBg.setVisibility(View.VISIBLE);
                hangUpBg.setOnClickListener(null);
                hangUpIBCall();
            }
        });

        minimum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeViewImmediate(dialingTouchLayout);
                if (smallToucherLayout == null) {
                    createSmallCallWindow();
                } else {
                    windowManager.addView(smallToucherLayout, smallParams);
                    showSmallViewByState();
                }
            }
        });
        showType = 1;

    }


    /**
     * 通话状态
     */
    private void showViewByPhoneState() {

        CircleCharAvatarView icon = phoneToucherLayout.findViewById(R.id.icon_contact);
        TextView name = phoneToucherLayout.findViewById(R.id.name);
        final ImageView hangUp = phoneToucherLayout.findViewById(R.id.hang_up);
        final ImageView minimum = phoneToucherLayout.findViewById(R.id.minimum);
        final TextView currentStatus = phoneToucherLayout.findViewById(R.id.call_last_time);
        final ImageView hangUpBg = phoneToucherLayout.findViewById(R.id.hang_up_bg);
        final TextView endStatus = phoneToucherLayout.findViewById(R.id.current_status);
        tvTime = currentStatus;

        icon.setImageDrawable(mContext.getDrawable(R.drawable.head));
        if (callType == 1) {
            name.setText(mContext.getString(R.string.service_line));
        } else if (callType == 2) {
            name.setText(mContext.getString(R.string.rescue_line));
        }

        hangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endStatus.setText(R.string.call_end);
                hangUpCall();
                hangUpBg.setOnClickListener(null);
                hangUpBg.setVisibility(View.VISIBLE);
                hangUpIBCall();
            }
        });

        minimum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeViewImmediate(phoneToucherLayout);
                if (smallToucherLayout == null) {
                    createSmallCallWindow();
                } else {
                    windowManager.addView(smallToucherLayout, smallParams);
                    showSmallViewByState();
                }
            }
        });

        showType = 2;
    }

    private void showSmallViewByState() {
        TextView mTvName = smallToucherLayout.findViewById(R.id.tv_name);
        TextView mTvState = smallToucherLayout.findViewById(R.id.tv_state);
        CircleCharAvatarView mIvHead = smallToucherLayout.findViewById(R.id.head);
        tvTime = mTvState;
        if (!isCalling) {
            mTvState.setText(mContext.getString(R.string.dialing));
        }
        mIvHead.setImageDrawable(mContext.getDrawable(R.drawable.head));
        if (callType == 1) {
            mTvName.setText(mContext.getString(R.string.service_line));
        } else if (callType == 2) {
            mTvName.setText(mContext.getString(R.string.rescue_line));
        }

        showType = 3;
    }

    private void setCallTime() {
        time = SystemClock.uptimeMillis();
        mExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long currentTime = SystemClock.uptimeMillis();
                final long callTime = currentTime - time;

                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (tvTime != null) {
                            tvTime.setText(LastTimeFormatUtils.countTime(callTime));
                        }
                    }
                });
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);

        isCalling = true;
    }

    private void hangUpCall() {
        if (callType == 1) {
            CarDataManager.getInstance().hangUpICall();
        } else if (callType == 2) {
            CarDataManager.getInstance().hangUpBCall();
        }
    }

    @Override
    public void onCarEvent(CarEvent event) {
        if (event.id == SDKConstants.TboxCallState.ID_TBOX_CALL_STATE) {
            int callState = (int) event.value;
            KLog.e(TAG, "UI" + callState);
            if (callState == SDKConstants.TboxCallState.ANSWER) {

                //先暂停播放铃声的焦点
//                AudioAuditionHelper.getInstance().stop();
//                AudioAuditionHelper.getInstance().requestAudioForCall();
                boolean isMic = XmMicManager.getInstance().requestMicFocus(onMicFocusChangeListener, XmMicManager.MIC_LEVEL_CALL, XmMicManager.FLAG_NONE);
                KLog.e(TAG, "has mic permission" + isMic);
                if (isMic) {
                    setCallTime();
                    if (showType == 1) {
                        //此时状态为通话中需要切换视图为通话中的视图
                        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                windowManager.removeViewImmediate(dialingTouchLayout);
                                createPhoneWindow();
                            }
                        });

                    }
                } else {
                    XMToast.showToast(this, R.string.call_faliue);
                    stopSelf();
                }

            } else if (callState == SDKConstants.TboxCallState.HANGUP_EXPIRE_FAIL) {
                hangUpIBCall();
            }
        }
    }

    private void hangUpIBCall() {

        if (isStop) {
            return;
        }

        if (showType == 2 || showType == 1) {
            ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (showType == 2) {
                            windowManager.removeViewImmediate(phoneToucherLayout);
                        } else if (showType == 1) {
                            windowManager.removeViewImmediate(dialingTouchLayout);
                        }
                        releaseAudioAndMic();
                        stopSelf();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        } else if (showType == 3) {
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    try {
                        windowManager.removeViewImmediate(smallToucherLayout);
                        releaseAudioAndMic();
                        stopSelf();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private void initAudioAudition() {
        AudioAuditionHelper.getInstance().init(getApplication(), ServiceConstants.RING_PATH, new AudioAuditionHelper.PlayCallback() {
            @Override
            public void onStart() {
                KLog.e(TAG, "PLAY RINGTONE");
            }

            @Override
            public void onStop() {
                KLog.e(TAG, "STOP RINGTONE");
            }

            @Override
            public void onError() {

            }
        }, callType == 1 ? AudioManager.STREAM_ICALL : AudioManager.STREAM_BCALL);
    }

    private void releaseAudioAndMic() {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocusForCall();
        }
//        AudioAuditionHelper.getInstance().release();
        XmMicManager.getInstance().abandonMicFocus(onMicFocusChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop = true;
        IBCallManager.getInstance().setIBCall(false);
        XmCarEventDispatcher.getInstance().unregisterEvent(this);
        XmWheelManager.getInstance().unregister(mCallWheelKeyListener);
        sendBroadcast(new Intent(CenterConstants.END_OF_IBCALL));
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = ServiceConstants.INCOMING_CALL)
    public void inComingCall(String event) {

        //拨号
        if (showType == 1) {
            windowManager.removeViewImmediate(dialingTouchLayout);
            if (smallToucherLayout == null) {
                createSmallCallWindow();
            } else {
                windowManager.addView(smallToucherLayout, smallParams);
                showSmallViewByState();
            }
        } else if (showType == 2) {//通话
            windowManager.removeViewImmediate(phoneToucherLayout);
            if (smallToucherLayout == null) {
                createSmallCallWindow();
            } else {
                windowManager.addView(smallToucherLayout, smallParams);
                showSmallViewByState();
            }
        }


    }

    @Subscriber(tag = ServiceConstants.BLUETOOTH_CALL)
    public void isInCall(boolean isCall) {

        if (isCall) {
            hangUpCall();
            hangUpIBCall();
        } else {

            if (showType == 3) {
                return;
            }

            windowManager.removeViewImmediate(smallToucherLayout);
            if (isCalling) {

                if (phoneToucherLayout == null) {
                    createPhoneWindow();
                } else {
                    windowManager.addView(phoneToucherLayout, phoneParams);
                    showViewByPhoneState();
                }

            } else {
                windowManager.addView(dialingTouchLayout, dialingParams);
                showViewByDialingState();
            }
        }

    }

    @Subscriber(tag = ServiceConstants.WHEEL_HANGUP_IBCALL)
    public void wheelForHangUp(String event) {
        if (IBCallManager.getInstance().isIBCall()) {
            hangUpCall();
            hangUpIBCall();
        }
    }


}
