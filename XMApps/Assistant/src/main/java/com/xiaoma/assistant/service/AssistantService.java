package com.xiaoma.assistant.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.BluetoothPhoneManager;
import com.xiaoma.assistant.manager.api.ApiController;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.CarServiceConnManager;
import com.xiaoma.carlib.manager.CarServiceListener;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.openiflytek.OpenVoiceManager;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.utils.ContactsUtils;
import com.xiaoma.vrfactory.ivw.XmIvwManager;
import com.xiaoma.vrfactory.tts.XmTtsManager;
import com.xiaoma.wechat.manager.WeChatManager;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/26
 * Desc：语音助手服务
 */
public class AssistantService extends Service {
    private static final int ASSISTANT_NOTIFICATION_ID = 1345676548;
    private OnWheelKeyListener.Stub mKeyListener;

    private BroadcastReceiver showAssistant = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AssistantManager.getInstance().show(false);
            AssistantManager.getInstance().uploadWakeUp(AssistantConstants.WakeUpMethod.CLICK_METHOD);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.d(getString(R.string.voice_engine_init));
        ApiController.getInstance().init(getApplication(), AssistantConstants.LINKER_PORT);
        BluetoothPhoneManager.getInstance().init(getApplication());
        InterceptorManager.getInstance().init(getApplication());
        initIFlyNoise();
        AssistantManager.getInstance().init(this.getApplicationContext());
        XmWheelManager.getInstance().init(getApplication());
        OpenVoiceManager.getInstance().init(this.getApplicationContext());
        XmTtsManager.getInstance().init(this.getApplicationContext());
        if (!ConfigManager.ApkConfig.isCarPlatform()){
            XmIvwManager.getInstance().startWakeup();
        }else {
            CarServiceConnManager.getInstance(this.getApplicationContext()).addCallBack(new CarServiceListener() {
                @Override
                public void onCarServiceConnected(IBinder binder) {
                    XmIvwManager.getInstance().startWakeup();
                }

                @Override
                public void onCarServiceDisconnected() {
                    XmIvwManager.getInstance().startWakeup();
                }
            });
        }
        initShowAssistantDialog();
        //监听联系人数据库变化，当发生变化后重新从数据库中拉取联系人同步讯飞
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI,
                true, mObserver);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                initKeyEvent();
            }
        }, 2000);
        showNotification();
    }


    public void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationManager mgr = getSystemService(NotificationManager.class);
            if (mgr != null) {
                final String channelId = getPackageName();
                final NotificationChannel channel = new NotificationChannel(channelId, "assistant", NotificationManager.IMPORTANCE_DEFAULT);
                mgr.createNotificationChannel(channel);
                // 通知只设置小icon,不设置任何内容,小马SystemUI将隐藏该通知的显示
                mgr.notify(ASSISTANT_NOTIFICATION_ID,
                        new Notification.Builder(getApplicationContext(), channelId)
                                .setSmallIcon(R.drawable.icon_default_icon)
                                .build());
            }
        }
    }

    private void initKeyEvent() {
        //方控事件
        XmWheelManager.getInstance().register(mKeyListener = new OnWheelKeyListener.Stub() {
            @Override
            public String getPackageName() throws RemoteException {
                return "com.xiaoma.assistant";
            }

            @Override
            public boolean onKeyEvent(int keyAction, int keyCode) throws RemoteException {
                if (WheelKeyEvent.KEYCODE_WHEEL_VOICE == keyCode) {
                    switch (keyAction) {
                        case WheelKeyEvent.ACTION_CLICK:
                            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                                @Override
                                public void run() {
                                    AssistantManager.getInstance().show(false);
                                    AssistantManager.getInstance().uploadWakeUp(AssistantConstants.WakeUpMethod.HARDWARE_METHOD);
                                }
                            });
                            break;
                        case WheelKeyEvent.ACTION_PRESS:
                            if (WeChatManager.getInstance().isConversion()) {
                                WeChatManager.getInstance().onDownKeyEvent();
                            }
                            break;
                        case WheelKeyEvent.ACTION_RELEASE:
                            WeChatManager.getInstance().onUpKeyEvent();
                            break;
                    }
                }
                return true;
            }
        }, new int[]{WheelKeyEvent.KEYCODE_WHEEL_VOICE});
    }

    private void initShowAssistantDialog() {
        registerReceiver(showAssistant, new IntentFilter(VrConstants.Actions.SHOW_ASSSISTANT_DIALOG));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void initIFlyNoise() {
        XmCarFactory.init(this.getApplicationContext());
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                XmCarVendorExtensionManager.getInstance().setSrMode(SDKConstants.VALUE.SR_NOISE_CLEAN);
                KLog.d("set SR mode is " + XmCarVendorExtensionManager.getInstance().getSrMode());
            }
        }, 2000);

    }


    //监听联系人数据的监听对象
    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // 当联系人表发生变化时进行相应的操作, 因第三方可能是一个个插入，所以延迟2秒
            ThreadDispatcher.getDispatcher().remove(syncMailRunnable);
            ThreadDispatcher.getDispatcher().postDelayed(syncMailRunnable, 2000, Priority.LOW);
        }
    };

    private Runnable syncMailRunnable = new Runnable() {
        @Override
        public void run() {
            //数据库操作
            //LiteOrmDBManager.getInstance().saveAll(ContactsUtils.getPhoneContacts());
            ContactsUtils.upLoadContactInfos();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            XmWheelManager.getInstance().unregister(mKeyListener);
            unregisterReceiver(showAssistant);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //XmAudioManager.getInstance().release();
    }
}
