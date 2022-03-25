package com.xiaoma.instructiondistribute.ui;

import android.car.eol.EolCmd;
import android.car.eol.EolEventListener;
import android.car.eol.EolManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.instructiondistribute.R;
import com.xiaoma.instructiondistribute.contract.AudioSourceType;
import com.xiaoma.instructiondistribute.contract.EOLUtils;
import com.xiaoma.instructiondistribute.distribute.DispatcherBean;
import com.xiaoma.instructiondistribute.distribute.InstructionDispatcher;
import com.xiaoma.instructiondistribute.distribute.ResultCallback;
import com.xiaoma.instructiondistribute.eol.EOLFacade;
import com.xiaoma.instructiondistribute.listener.IAudioSourceTypeListener;
import com.xiaoma.instructiondistribute.utils.BluetoothHelper;
import com.xiaoma.instructiondistribute.utils.DistributeConstants;
import com.xiaoma.instructiondistribute.utils.EOLAudioFocusManager;
import com.xiaoma.instructiondistribute.utils.OpenAppUtils;
import com.xiaoma.instructiondistribute.xkan.ijkplayer.NiceVideoPlayer;
import com.xiaoma.instructiondistribute.xkan.ijkplayer.NiceVideoPlayerManager;
import com.xiaoma.instructiondistribute.xkan.service.XkanService;
import com.xiaoma.instructiondistribute.xkan.video.ui.VideoPlayActivity;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.ui.toast.XMToast;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Arrays;

import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ACTION_FULL_SCREEN;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ACTION_FULL_SCREEN_NEXT;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ACTION_FULL_SCREEN_PRE;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ACTION_GET_SHOW_TYPE;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ACTION_LIST;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ACTION_ROTATE_LEFT;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ACTION_ROTATE_RIGHT;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ACTION_ZOOM_IN;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ACTION_ZOOM_OUT;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.ARG_RESULT;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.STATE_FULL_SCREEN;
import static com.xiaoma.instructiondistribute.ui.PhotoActivity.STATE_LIST;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_CLEAR_BT_PAIRED_LIST;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_FACTORY_RESET;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_ARKAMYS_3D;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_BALANCE_LEVEL;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_BEST_POSITION;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_BLUETOOTH;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_BLUETOOTH_ADDRESS;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_BT_PAIR_MODE;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_EQ;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_EQ_SETTING;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_FADER_LEVEL;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_LCD_LVDS_OUTPUT;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_MUTE;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_SOUND_FIELD_STATUS;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_SPEED_VOLUME;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_STREAM_VOLUME;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_TEST_MFD_ILLUMINATION;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_TEST_SCREEN_ILLUMINATION;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_TFT_DISPLAY_PATTERN;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_GET_TFT_ILLUMINATION;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_ARKAMYS_3D;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_BALANCE_LEVEL;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_BEST_POSITION;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_BLUETOOTH;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_BLUETOOTH_ADDRESS;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_BT_PAIR_MODE;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_EQ;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_EQ_SETTING;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_FADER_LEVEL;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_LCD_LVDS_OUTPUT;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_MUTE;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_SOUND_FIELD_STATUS;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_SPEED_VOLUME;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_STREAM_VOLUME;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_TEST_MFD_ILLUMINATION;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_TEST_SCREEN_ILLUMINATION;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_TFT_DISPLAY_PATTERN;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.ACTION_SET_TFT_ILLUMINATION;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.MUSIC;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.PORT_USB_AUDIO;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.XTING;

public class MainActivity extends BaseActivity {
    public static final String TAG_ERROR = "EOL_E";
    private final String TAG = "EOL_XM";
    private EolManager eolManager;
    private boolean registerResult;

    private AudioSourceType mSourceType = AudioSourceType.UNKNOWN_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        eolManager = new EolManager(this);
        Log.d(TAG, "onCreate: 注册EolManager start");
//
        registerResult = eolManager.register(new EolEventListenerImpl(), 0b11111111111111);
        Log.d(TAG, "onCreate: 注册EolManager end");
        Log.d(TAG, "onCreate: 注册结果 result= " + registerResult);
        EOLFacade.newSingleton().bindState(eolManager, registerResult);
        InstructionDispatcher.getInstance().setContext(this);
        XmCarVendorExtensionManager.getInstance().init(this);
        BluetoothHelper.newSingleton().init(this);

        startService(new Intent(this, XkanService.class));
        EOLAudioFocusManager.newSingleton(this).setOnAudioSourceTypeListener(new IAudioSourceTypeListener() {
            @Override
            public void onAudioSourceTypeGranted(AudioSourceType type) {
                Log.d("APP", "onAudioSourceTypeGranted: " + type.name());
                mSourceType = type;
                XMToast.showToast(getApplicationContext(), type.name(), false);
            }
        });
    }

    public void jumpToTestPage(View view) {
//        startActivity(new Intent(this, InstructionsTestPanelActivity.class));
    }

    private void startPhoto(String action) {
        if (!EOLUtils.isUSBMounted()) {
            XMToast.showToast(MainActivity.this, "未检测到可识别的USB设备");
        } else {
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra(PhotoActivity.ARG_ACTION, action);
            startActivity(intent);
        }
    }


    /**
     * @param val1
     * @param val2
     * @return
     */
    private int getUsbPlayMode(int val1, int val2) {
        if (val1 != 0) {
            return val1;
        } else {
            return (val2 + 2);
        }
    }

    private Bundle handleCommand(String key1, String key2, int value1, int value2, int action) {
        Bundle bundle = new Bundle();
        bundle.putInt(key1, value1);
        bundle.putInt(key2, value2);
        return InstructionDispatcher.getInstance().handleInstruction(new
                DispatcherBean(action, bundle));
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscriber(tag = "photo.event")
    public void onUsbPicOperation(Bundle result) {
        String action = result.getString(PhotoActivity.ARG_ACTION);
        boolean feedback = result.getBoolean(ARG_RESULT);

        Log.d(TAG, "onUsbPicOperation: " + action + " ~ " + feedback);
        XMToast.showToast(this, "USB_Photo " + action + " ~ " + feedback);
        switch (action) {
            case ACTION_ZOOM_IN:
                if (feedback) {
                    if (eolManager != null) {
                        try {
                            eolManager.responseSetUSBPictureOperation(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //操作失败
                }
                break;
            case ACTION_ZOOM_OUT:
                if (feedback) {
                    if (eolManager != null) {
                        try {
                            eolManager.responseSetUSBPictureOperation(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //操作失败
                }
                break;
            case ACTION_ROTATE_LEFT:
                if (feedback) {
                    if (eolManager != null) {
                        try {
                            eolManager.responseSetUSBPictureOperation(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //操作失败
                }
                break;
            case ACTION_ROTATE_RIGHT:
                if (feedback) {
                    if (eolManager != null) {
                        try {
                            eolManager.responseSetUSBPictureOperation(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //操作失败
                }
                break;
            case ACTION_LIST:
                if (feedback) {
                    if (eolManager != null) {
                        try {
                            eolManager.responseSetUSBPictureOperation(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //操作失败
                }
                break;
            case ACTION_FULL_SCREEN:
                if (feedback) {
                    if (eolManager != null) {
                        try {
                            eolManager.responseSetUSBPictureOperation(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //操作失败
                }
                break;
            case ACTION_FULL_SCREEN_PRE:
                if (feedback) {
                    if (eolManager != null) {
                        try {
                            eolManager.responseSetUSBPictureOperation(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //操作失败
                }
                break;
            case ACTION_FULL_SCREEN_NEXT:
                if (feedback) {
                    if (eolManager != null) {
                        try {
                            eolManager.responseSetUSBPictureOperation(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //操作失败
                }
                break;
        }
    }

    @Subscriber(tag = "eol_pic_show")
    public void onUSBPicShowType(String state) {
        //返回值 0x01 列表 , 0x03 全屏
        try {
            XMToast.showToast(this, "show Type " + state);
            if (STATE_FULL_SCREEN.equals(state)) {
                eolManager.responseGetUSBPictureDisplayStatus(3);
            } else if (STATE_LIST.equals(state)) {
                eolManager.responseGetUSBPictureDisplayStatus(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscriber(tag = VideoPlayActivity.TAG_OPEARTE_USB_VIDEO_NEXT_PRE)
    public void onUSBVideoPreAndNextOperate(boolean result) {
        XMToast.showToast(this, "USB Pre & Next ==>" + result);
        if (eolManager != null) {
            try {
                eolManager.responseSetUSBVideoSkip(0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscriber(tag = "usb_pic_show_type")
    public void onUsbPicShowType(Bundle data) {
        int result = data.getInt("result");
        int rw = data.getInt("rw");
        if (eolManager != null) {
            XMToast.showToast(MainActivity.this, String.format("RW=%1$s,result=%2$s", String.valueOf(rw), String.valueOf(result)));
            try {
                if (rw == 1)
                    eolManager.responseSetUSBPictureDisplayStatus(0);
                else
                    eolManager.responseGetUSBPictureDisplayStatus(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Subscriber(tag = "usb_video_play_pause")
    public void onUsbVideoPlayPause(Bundle result) {
        int status = result.getInt("status");
        int rw = result.getInt("rw");
        XMToast.showToast(this, "USB_Video " + status + " ~ " + rw);
        if (eolManager != null) {
            try {
                if (rw == 1) {
                    eolManager.responseSetUSBVideoPlayPauseStatus(0);
                } else {
                    eolManager.responseGetUSBVideoPlayPauseStatus(status);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Subscriber(tag = "usb_video_previous_or_next")
    public void onUsbVideoPreviousOrNext(Bundle result) {
        int type = result.getInt("type");
        if (eolManager != null) {
            try {
                eolManager.responseGetUSBVideoTrack(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回当前指令是get 还是set
     *
     * @param rw
     * @return
     */
    private boolean isSet(int rw) {
        return rw == EolCmd.SID.REQUEST_WRITE;
    }

    private String getMacIntArr2String(int[] mac) {
        if (mac == null || mac.length == 0) return "";
        StringBuffer macBuf = new StringBuffer();
        if (mac.length > 6) {
            String toHexStr;
            for (int i = 0; i < 6; i++) {
                toHexStr = Integer.toHexString(mac[i] & 0xFF);
                if (toHexStr.length() == 1) {
                    macBuf.append("0");
                }
                macBuf.append(toHexStr).append(":");
            }
            macBuf.deleteCharAt(macBuf.length() - 1);
        }
        return macBuf.toString().toUpperCase();
    }

    private int[] getMacString2IntArr(String mac) {
        if (TextUtils.isEmpty(mac) || !mac.contains(":")) return new int[0];
        String[] split = mac.split(":");
        int[] macArr = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            macArr[i] = Integer.parseInt(split[i], 16);
        }
        return macArr;
    }

    public void startPhotoPage(View view) {
        startPhoto(ACTION_FULL_SCREEN);
    }

    public void startVideoPage(View view) {
        // 切换上一个/下一个USB视频
        Bundle bundle = new Bundle();
        bundle.putInt("status", 1);
        InstructionDispatcher.getInstance()
                .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_VIDEO_PREVIOUS_NEXT, bundle));
    }

    public void setUSBAudioPlayMode(View view) {
//        Bundle bundle = new Bundle();
////        int mode = getUsbPlayMode(val1, val2);
////        XMToast.showToast(MainActivity.this,
////                String.format("mode = %1$s,var1 = %2$s, var2 = %3$s",
////                        String.valueOf(mode), String.valueOf(val1), String.valueOf(val2)));
//        int action;
//
//        action = AudioConstants.Action.Option.SWITCH_PLAY_MODE;
////        bundle.putInt("mode", mode);
//
//        DispatcherBean bean = new DispatcherBean(PORT_USB_AUDIO, action, bundle, MUSIC);
//        InstructionDispatcher.getInstance().handleInstructionByRequest(bean, new ResultCallback() {
//            @Override
//            public void callback(Response response) {
//                try {
//                    if (isSet(rw)) {
//                        eolManager.responseSetUSBAudioPlayMode(0);
//                    } else {
//                        Bundle bundle = response.getExtra();
//                        int playMode = bundle.getInt("mode");
//
//                        if (playMode <= 2) {
//                            XMToast.showToast(MainActivity.this,
//                                    String.format("playMode Get 0 = %1$s", String.valueOf(playMode)));
//                            eolManager.responseGetUSBAudioPlayMode(playMode, 0);
//                        } else {
//                            XMToast.showToast(MainActivity.this,
//                                    String.format("playMode Get 1= %1$s", String.valueOf(playMode)));
//                            eolManager.responseGetUSBAudioPlayMode(0, playMode - 2);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public void setUSBPlayMode(View view) {
        Bundle bundle = new Bundle();
        int mode = getUsbPlayMode(2, 0);
        int action;
        action = AudioConstants.Action.Option.SWITCH_PLAY_MODE;
        bundle.putInt("mode", mode);

        DispatcherBean bean = new DispatcherBean(PORT_USB_AUDIO, action, bundle, MUSIC);
        InstructionDispatcher.getInstance().handleInstructionByRequest(bean, new ResultCallback() {
            @Override
            public void callback(Response response) {
                XMToast.showToast(MainActivity.this, "Set Play Mode Ok");
                try {
                    eolManager.responseSetUSBAudioPlayMode(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class EolEventListenerImpl implements EolEventListener {

        private boolean checkRegister(int... params) {

            return EOLUtils.isRegister(String.format("%1$s %2$s", EOLUtils.getMethodName(), Arrays.toString(params)), false);
        }

        @Override
        public void onFactoryReset(int state) {
            // app里未实现，需要自己实现
            if (checkRegister(state)) {
                try {
                    eolManager.responseFactoryReset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handleCommand("", "", 0, 0, ACTION_FACTORY_RESET);
            }
        }

        @Override
        public void onEQConfiguration(int rw, int i1, int i2, int i3, int i4, int i5) {
            if (checkRegister(rw, i1, i2, i3, i4, i5)) {
                int setOrGet;
                Bundle bundle = new Bundle();
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_EQ;
                } else {
                    setOrGet = ACTION_SET_EQ;

                    ArrayList<Integer> eqValue = new ArrayList<>(6);
                    eqValue.add(SDKConstants.VALUE.SOUND_EFFECTS_USER);
                    eqValue.add(i1);
                    eqValue.add(i2);
                    eqValue.add(i3);
                    eqValue.add(i4);
                    eqValue.add(i5);

                    bundle.putIntegerArrayList("eq", eqValue);
                }
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(setOrGet, bundle));

                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        ArrayList<Integer> list = result.getIntegerArrayList("eq");
                        if (list.isEmpty()) {
                            dispatchError("onEQConfiguration [Get] size is Empty!");
                        } else {
                            Log.d(TAG, "responseReadEQConfiguration" + list.toString());
                            eolManager.responseReadEQConfiguration(list.get(1), list.get(2), list.get(3), list.get(4), list.get(5));
                        }
                    } else {
                        eolManager.responseWriteEQConfiguration(0);//set返回什么 成功返回0即可，不成功不需要返回
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void dispatchError(String msg) {
            Log.e(TAG_ERROR, "[Error] " + msg);
            XMToast.showToast(MainActivity.this, msg);
        }

        @Override
        public void onBluetoothTestMode(int rw, int mode) {
            // TODO: 2019/7/19 0019 接口还没有提供
            if (checkRegister(rw, mode)) {
                try {
                    //[WARN] 蓝牙状态和蓝牙测试模式的两帧数据必须一起都要给，否则通信失败 （新途表示 ： 这个是一汽给那边的需求）
                    //目前为了保证蓝牙状态的测试通过，所以这里随便返回一个数据
                    eolManager.responseGetBluetoothTestMode(0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onBluetoothStatus(int rw, int state) {
            if (checkRegister(rw, state)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_BLUETOOTH;
                } else {
                    setOrGet = ACTION_SET_BLUETOOTH;
                }
                Bundle bundle = handleCommand("isOn", "", state, 0, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetBluetoothStatus(bundle.getInt("result"));
                    } else {
                        eolManager.responseSetBluetoothStatus(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onBluetoothAddress(int rw, int[] address) {
            if (EOLUtils.isRegister(String.format("%1$s{%2$s,%3$s}", "onBluetoothAddress",
                    String.valueOf(rw), Arrays.toString(address)), false)) {
                int setOrGet;
                Bundle bundle = new Bundle();
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_BLUETOOTH_ADDRESS;
                } else {
                    setOrGet = ACTION_SET_BLUETOOTH_ADDRESS;
                    bundle.putIntArray("macAddress", address);
                }
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(setOrGet, bundle));
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        String addresses = result.getString("result");
                        String[] addressSplit = addresses.split(":");
                        int[] intAddress = new int[addressSplit.length];
                        for (int i = 0; i < addressSplit.length; i++) {
                            intAddress[i] = Integer.parseInt(addressSplit[i]);
                        }
                        eolManager.responseReadBluetoothAddress(intAddress);
                    } else {
                        eolManager.responseWriteBluetoothAddress(0);//set 时返回什么需确定
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onBluetoothModuleVersion(int rw) {
            if (checkRegister(rw)) {
                InstructionDispatcher.getInstance()
                        .handleInstruction(
                                new DispatcherBean(DistributeConstants.ACTION_GET_BT_MODULE_VERSION, null),
                                new ResultCallback() {
                                    @Override
                                    public void onResponse(Bundle data) {
                                        String version = data.getString("version");
                                        if (!TextUtils.isEmpty(version) && version.contains(".")) {
                                            String[] versionSplit = version.split("\\.");
                                            int[] intVersion = new int[8];//这里需要返回8位，不足补充0
                                            for (int i = 0; i < versionSplit.length; i++) {
                                                intVersion[i] = Integer.parseInt(versionSplit[i]);
                                            }
                                            try {
                                                eolManager.responseReadBluetoothModuleVersion(intVersion);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
            }
        }

        @Override
        public void onBTPairModeStatus(int rw, int state) {
            if (checkRegister(rw, state)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_BT_PAIR_MODE;
                } else {
                    setOrGet = ACTION_SET_BT_PAIR_MODE;
                }
                Bundle result = handleCommand("bt_pair_mode", "", state, 0, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetBTPairModeStatus(result.getInt("bt_pair_mode"));
                    } else {
                        eolManager.responseSetBTPairModeStatus(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onBTAudioPlayPauseStatus(int rw, int state) {
            if (checkRegister(rw, state)) {
                Bundle bundle = new Bundle();
                int action;
                if (isSet(rw)) {
                    if (state == 1) // pause
                        action = DistributeConstants.ACTION_SET_BLUETOOTH_AUDIO_PAUSE;
                    else { // play
                        action = DistributeConstants.ACTION_SET_BLUETOOTH_AUDIO_PLAY;
                        bundle.putInt("musicType", AudioConstants.MusicType.BLUE);
                    }
                } else {
                    action = DistributeConstants.ACTION_GET_BLUETOOTH_AUDIO_PLAY_PAUSE;
                }
                DispatcherBean bean = new DispatcherBean(DistributeConstants.PORT_BLUETOOTH_AUDIO, action, bundle, MUSIC);
                InstructionDispatcher.getInstance().handleInstructionByRequest(bean, new ResultCallback() {
                    @Override
                    public void callback(Response response) {
                        try {
                            if (isSet(rw)) {
                                eolManager.responseSetBTAudioPlayPauseStatus(0);
                            } else {
                                Bundle extra = response.getExtra();
                                boolean isPlaying = extra.getBoolean("status");
                                int state = isPlaying ? 2 : 1;
                                eolManager.responseGetBTAudioPlayPauseStatus(state);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onBTAudioSkipTrackStatus(int rw, int state) {
            if (checkRegister(rw, state)) {
                int action;
                if (isSet(rw)) {
                    if (state == 1) // next track
                        action = DistributeConstants.ACTION_SET_BLUETOOTH_AUDIO_NEXT;
                    else { // previous track
                        action = DistributeConstants.ACTION_SET_BLUETOOTH_AUDIO_PREVIOUS;
                    }
                } else {
                    Log.d(TAG, "onBTAudioSkipTrackStatus: 没有get");
                    return;
                }
                DispatcherBean bean = new DispatcherBean(DistributeConstants.PORT_BLUETOOTH_AUDIO, action, null, MUSIC);
                InstructionDispatcher.getInstance().handleInstructionByRequest(bean, new ResultCallback() {
                    @Override
                    public void callback(Response response) {
                        try {
                            if (isSet(rw)) {
                                eolManager.responseSetBTAudioSkipTrackStatus(0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onBTIncomingCallActiveStatus(int rw, int active) {
            if (checkRegister(rw, active)) {
                Bundle bundle = new Bundle();
                int action;
                if (isSet(rw)) {
                    action = DistributeConstants.ACTION_SET_BT_CALL_ACTIVE_DEACTIVE;
                    bundle.putInt("status", active);
                } else {
                    action = DistributeConstants.ACTION_GET_BT_CALL_ACTIVE_DEACTIVE;
                }
                DispatcherBean bean = new DispatcherBean(action, bundle);
                Bundle resultBundle = InstructionDispatcher.getInstance().handleInstruction(bean);
                try {
                    if (isSet(rw)) {
                        boolean success = resultBundle.getBoolean("result");
                        if (success) {
                            eolManager.responseSetBTIncomingCallActiveStatus(0);
                        } else
                            Log.e(TAG, "onBTIncomingCallActiveStatus: Error");
//                        eolManager.responseSetBTIncomingCallActiveStatus(DistributeConstants.ERROR_CODE_EOL); // 传个错误值
                    } else {
                        int status = resultBundle.getInt("status");
                        eolManager.responseGetBTIncomingCallActiveStatus(status);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onClearBTPairedList() {
            if (checkRegister()) {
                handleCommand("", "", 0, 0, ACTION_CLEAR_BT_PAIRED_LIST);
                try {
                    eolManager.responseClearBTPairedList(0); // result返回什么
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onBTPairedWithDevice(int rw, int[] mac) {
            if (EOLUtils.isRegister(String.format("%1$s{%2$s,%3$s}", "onBTPairedWithDevice",
                    String.valueOf(rw), Arrays.toString(mac)), false)) {
                Bundle bundle = new Bundle();
                int action;
                if (isSet(rw)) {
                    bundle.putString("mac", getMacIntArr2String(mac));
                    action = DistributeConstants.ACTION_SET_BT_PAIR_WITH_DEVICE;
                    InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(action, bundle), new ResultCallback() {
                                @Override
                                public void onResponse(Bundle data) {
                                    try {
                                        if (data.getBoolean("result")) {
                                            eolManager.responseSetBTPairedWithDevice(0);
                                        } else {
                                            Log.d(TAG, "onBTPairedWithDevice: Error");
                                        }
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                } else {
                    action = DistributeConstants.ACTION_GET_BT_PAIR_WITH_DEVICE;
                    Bundle resultBundle = InstructionDispatcher.getInstance().handleInstruction(new DispatcherBean(action, bundle));
                    int[] macArr = getMacString2IntArr(resultBundle.getString("mac"));
                    try {
                        eolManager.responseGetBTPairedWithDevice(macArr);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onAudioSourceSelect(int rw, int i1) {
            if (checkRegister(rw, i1)) {
                try {
                    if (!isSet(rw)) {
                        eolManager.responseGetAudioSourceSelect(mSourceType.getSerialNo());
                    } else {
                        EOLAudioFocusManager.newSingleton(MainActivity.this)
                                .requestAudioFocus(AudioSourceType.convert2AudioSourceType(i1));
                        eolManager.responseSetAudioSourceSelect(0);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();

                }
            }
        }

        @Override
        public void onFaderLevel(int rw, int source) {
            if (checkRegister(rw, source)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_FADER_LEVEL;
                } else {
                    setOrGet = ACTION_SET_FADER_LEVEL;
                }
                Bundle result = handleCommand("fader", "balance", source, 8, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetFaderLevel(result.getInt("result"));
                    } else {
                        eolManager.responseSetFaderLevel(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onBalanceLevel(int rw, int level) {
            if (checkRegister(rw, level)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_BALANCE_LEVEL;
                } else {
                    setOrGet = ACTION_SET_BALANCE_LEVEL;
                }
                Bundle result = handleCommand("fader", "balance", 8, level, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetBalanceLevel(result.getInt("result"));
                    } else {
                        eolManager.responseSetBalanceLevel(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onMuteStatus(int rw, int state) {
            if (checkRegister(rw, state)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_MUTE;
                } else {
                    setOrGet = ACTION_SET_MUTE;
                }
                Bundle result = handleCommand("isOn", "", state, 0, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetMuteStatus(result.getInt("result"));
                    } else {
                        eolManager.responseSetMuteStatus(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSpeedVolumeStatus(int rw, int state) {
            if (checkRegister(rw, state)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_SPEED_VOLUME;
                } else {
                    setOrGet = ACTION_SET_SPEED_VOLUME;
                }
                Bundle result = handleCommand("speedVolumeStatus", "", state, 0, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetSpeedVolumeStatus(result.getInt("result"));
                    } else {
                        eolManager.responseSetSpeedVolumeStatus(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onEQSettingStatus(int rw, int state) {
            // 音效状态
            if (checkRegister(rw, state)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_EQ_SETTING;
                } else {
                    setOrGet = ACTION_SET_EQ_SETTING;
                }
                Bundle result = handleCommand("eQSettingStatus", "", state, 0, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetEQSettingStatus(result.getInt("result"));
                    } else {
                        eolManager.responseSetEQSettingStatus(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onBastListenPositionStatus(int rw, int state) {
            if (checkRegister(rw, state)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_BEST_POSITION;
                } else {
                    setOrGet = ACTION_SET_BEST_POSITION;
                }
                Bundle result = handleCommand("status", "", state, 0, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetBastListenPositionStatus(result.getInt("result"));
                    } else {
                        eolManager.responseSetBastListenPositionStatus(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onSoundFieldStatus(int rw, int state) {
            if (checkRegister(rw, state)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_SOUND_FIELD_STATUS;
                } else {
                    setOrGet = ACTION_SET_SOUND_FIELD_STATUS;
                }
                Bundle result = handleCommand("soundFieldStatus", "", state, 0, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetSoundFieldStatus(result.getInt("result"));
                    } else {
                        eolManager.responseSetSoundFieldStatus(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onArkamys3DStatus(int rw, int state) {
            if (checkRegister(rw, state)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_ARKAMYS_3D;
                } else {
                    setOrGet = ACTION_SET_ARKAMYS_3D;
                }
                Bundle result = handleCommand("arkamys3DStatus", "", state, 0, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetArkamys3DStatus(result.getInt("result"));
                    } else {
                        eolManager.responseSetArkamys3DStatus(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onVolumeLevel(int rw, int source, int level) {
            // 获取某个音频通道音量
            if (checkRegister(rw, source, level)) {
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_STREAM_VOLUME;
                } else {
                    setOrGet = ACTION_SET_STREAM_VOLUME;
                }

                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        Bundle result = handleCommand("volumeSource", "volumeLevel", 2, level, setOrGet);//默认获取媒体，因为新途没有传递具体的值
                        eolManager.responseGetVolumeLevel(source, result.getInt("result"));
                    } else {
                        Bundle result = handleCommand("volumeSource", "volumeLevel", source, level, setOrGet);
                        eolManager.responseSetVolumeLevel(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean handleUSBMound() {
            if (!EOLUtils.isUSBMounted()) {
                XMToast.showToast(MainActivity.this, "No Valid USB found");
                return false;
            }

            return true;
        }

        private boolean jumpToUSBMusic() {
            if (OpenAppUtils.openApp(MainActivity.this, CenterConstants.MUSIC)) {
                //只要求后台播放,不需要界面
//                NodeUtils.jumpTo(MainActivity.this, CenterConstants.MUSIC, "com.xiaoma.music.player.ui.PlayerActivity",
//                        NodeConst.MUSIC.PLAYER_ACTIVITY
//                                + "/" + NodeConst.MUSIC.PLAYER_FRAGMENT
//                                + "/" + NodeConst.MUSIC.OPEN_PLAY_LYRIC);

                return true;
            } else {
                XMToast.showToast(MainActivity.this, "请先安装音乐");
                return false;
            }
        }

        @Override
        public void onUSBAudioPlayPause(int rw, int state) {
            if (checkRegister(rw, state)) {
                if (!handleUSBMound()) return;
                if (!jumpToUSBMusic()) {
                    return;
                }


                Bundle bundle = new Bundle();
                int action;
                if (isSet(rw)) {
                    if (state == 1) { // play
                        action = AudioConstants.Action.PLAY;
                        AudioCategoryBean bean = new AudioCategoryBean();
                        bean.setAction(AudioConstants.PlayAction.DEFAULT);
                        bean.setIndex(0);
                        bean.setCategoryId(0);
                        bundle.putParcelable(AudioConstants.BundleKey.EXTRA, bean);
                        bundle.putInt(AudioConstants.BundleKey.MusicType, AudioConstants.MusicType.USB);
                    } else { // pause
                        XMToast.showToast(MainActivity.this, "Go To Pause");
                        action = AudioConstants.Action.Option.PAUSE;
                    }
                } else {
                    action = AudioConstants.Action.Option.PLAYER_STATUS;
                }
                DispatcherBean bean = new DispatcherBean(PORT_USB_AUDIO, action, bundle, MUSIC);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(bean, new ResultCallback() {
                            @Override
                            public void callback(Response response) {
                                try {
                                    if (isSet(rw)) {
                                        eolManager.responseSetBTAudioPlayPauseStatus(0);
                                    } else {
                                        Bundle bundle = response.getExtra();
                                        boolean status = bundle.getBoolean("status");
                                        int state = status ? 1 : 2;
                                        eolManager.responseGetBTAudioPlayPauseStatus(state);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }

        @Override
        public void onUSBAudioPlayMode(int rw, int val1, int val2) {
            // 设置/获取USB音乐播放模式
            if (checkRegister(rw, val1, val2)) {
                if (!handleUSBMound()) return;
                if (!jumpToUSBMusic()) {
                    return;
                }
                Bundle bundle = new Bundle();
                int mode = getUsbPlayMode(val1, val2);
                XMToast.showToast(MainActivity.this,
                        String.format("mode = %1$s,var1 = %2$s, var2 = %3$s",
                                String.valueOf(mode), String.valueOf(val1), String.valueOf(val2)));
                int action;
                if (isSet(rw)) {
                    action = AudioConstants.Action.Option.SWITCH_PLAY_MODE;
                    bundle.putInt("mode", mode);
                } else {
                    action = AudioConstants.Action.Option.CURRENT_PLAY_MODE;
                }
                DispatcherBean bean = new DispatcherBean(PORT_USB_AUDIO, action, bundle, MUSIC);
                InstructionDispatcher.getInstance().handleInstructionByRequest(bean, new ResultCallback() {
                    @Override
                    public void callback(Response response) {
                        try {
                            if (isSet(rw)) {
                                eolManager.responseSetUSBAudioPlayMode(0);
                            } else {
                                Bundle bundle = response.getExtra();
                                int playMode = bundle.getInt("mode");

                                if (playMode <= 2) {
                                    XMToast.showToast(MainActivity.this,
                                            String.format("playMode Get 0 = %1$s", String.valueOf(playMode)));
                                    eolManager.responseGetUSBAudioPlayMode(playMode, 0);
                                } else {
                                    XMToast.showToast(MainActivity.this,
                                            String.format("playMode Get 1= %1$s", String.valueOf(playMode)));
                                    eolManager.responseGetUSBAudioPlayMode(0, playMode - 2);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onUSBAudioSkipTrack(int rw, int state) {
            // 播放上一首/下一首USB音乐
            if (checkRegister(rw, state)) {
                if (!handleUSBMound()) return;
                if (!jumpToUSBMusic()) {
                    return;
                }
                int action;
                if (isSet(rw)) {
                    if (state == 1) { // next track
                        action = AudioConstants.Action.Option.NEXT;
                    } else { // previous track
                        action = AudioConstants.Action.Option.PREVIOUS;
                    }
                } else {
                    Log.d(TAG, "onUSBAudioSkipTrack: 没有get");
                    return;
                }
                DispatcherBean bean = new DispatcherBean(PORT_USB_AUDIO, action, null, MUSIC);
                InstructionDispatcher.getInstance().handleInstructionByRequest(bean, new ResultCallback() {
                    @Override
                    public void callback(Response response) {
                        try {
                            if (isSet(rw)) {
                                eolManager.responseSetUSBAudioSkipTrack(0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onUSBDesiredFileAndTime(int rw, int number, int minutes, int second) {
            // 设置/获取USB音乐 到 指定分钟/秒钟
            if (checkRegister(rw, number, minutes, second)) {
                if (!handleUSBMound()) return;
                if (!jumpToUSBMusic()) {
                    return;
                }
                Bundle bundle = new Bundle();
                int action;
                if (isSet(rw)) {
                    action = AudioConstants.Action.Option.SEEK;
                    //USB 方需要毫秒单位
                    bundle.putLong("seconds", (minutes * 60 + second) * 1000); // 快进到*秒
                    bundle.putInt("type", 1); // 1 set  2 get
                } else {
                    Log.d(TAG, "onUSBAudioSkipTrack: 没有get");
                    return;
                }
                DispatcherBean bean = new DispatcherBean(PORT_USB_AUDIO, action, bundle, MUSIC);
                InstructionDispatcher.getInstance().handleInstructionByRequest(bean, new ResultCallback() {
                    @Override
                    public void callback(Response response) {
                        try {
                            if (isSet(rw)) {
                                eolManager.responseSetUSBDesiredFileAndTime(DistributeConstants.UNCERTAIN_CODE_EOL); // todo 联调确认回传什么值
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onUSBCurrentStatus(int rw) {
            if (checkRegister(rw)) {
                if (!handleUSBMound()) return;
                if (!jumpToUSBMusic()) {
                    return;
                }
                Bundle bundle = new Bundle();
                int action;
                if (!isSet(rw)) {
                    action = AudioConstants.Action.Option.SEEK;
                    bundle.getInt("type", 2); // 1 set  2 get
                } else {
                    Log.d(TAG, "onUSBAudioSkipTrack: 没有set");
                    return;
                }
                DispatcherBean bean = new DispatcherBean(PORT_USB_AUDIO, action, bundle, MUSIC);
                InstructionDispatcher.getInstance().handleInstructionByRequest(bean, new ResultCallback() {
                    @Override
                    public void callback(Response response) {
                        try {
                            if (!isSet(rw)) {
                                Bundle extra = response.getExtra();
                                long milliSeconds = extra.getLong("seconds");
                                int totalSeconds = (int) (milliSeconds / 1000);
                                int minutes = totalSeconds / 60;
                                int seconds = totalSeconds % 60;
                                eolManager.responseGetUSBCurrentStatus(DistributeConstants.UNCERTAIN_CODE_EOL, minutes, seconds); // todo 联调确认number回传什么值
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }

        @Override
        public void onUSBPictureOperation(int rw, int operation) {
            // 操作USB图片
            if (checkRegister(rw, operation)) {
                if (!handleUSBMound()) return;
                String action = ACTION_ZOOM_IN;
                if (operation == 1) {
                    XMToast.showToast(MainActivity.this, "USB PiC 放大");
                } else if (operation == 2) {
                    action = ACTION_ZOOM_OUT;
                    XMToast.showToast(MainActivity.this, "USB PiC 缩小");
                } else if (operation == 4) {
                    action = ACTION_ROTATE_RIGHT;
                    XMToast.showToast(MainActivity.this, "USB PiC 右旋");
                }
                startPhoto(action);
            }
        }

        @Override
        public void onUSBPictureDisplayStatus(int rw, int state) {
            // 设置/获取当前USB图片处于全屏还是列表的状态
            if (checkRegister(rw, state)) {
                if (!handleUSBMound()) return;
                if (isSet(rw)) {
                    String stateMsg = ACTION_LIST;
                    if (state == 0x03) {
                        stateMsg = ACTION_FULL_SCREEN;
                    }
                    XMToast.showToast(MainActivity.this, "USB PIC Display : " + stateMsg);
                    startPhoto(stateMsg);
                } else {
                    startPhoto(ACTION_GET_SHOW_TYPE);

//                    action = DistributeConstants.ACTION_GET_USB_PICTURE_SHOW_TYPE;
//                    DispatcherBean bean = new DispatcherBean(action, bundle);
//                    InstructionDispatcher.getInstance().handleInstruction(bean);
                }

            }
        }

        @Override
        public void onUSBPictureSkipStatus(int rw, int state) {
            // 切换上一张/下一张USB图片
            if (checkRegister(rw, state)) {
                if (!handleUSBMound()) return;
                String action = ACTION_FULL_SCREEN_PRE;
                if (state == 0x01) {
                    action = ACTION_FULL_SCREEN_NEXT;
                }
                startPhoto(action);
            }
        }

        private boolean handleVideoPlay(boolean play) {
            NiceVideoPlayer currentNiceVideoPlayer = NiceVideoPlayerManager.instance().getCurrentNiceVideoPlayer();
            if (currentNiceVideoPlayer == null || !currentNiceVideoPlayer.isPlaying()) {
                if (play) {
                    return true;
                } else {
                    XMToast.showToast(MainActivity.this, "请先播放USB视频");
                    return false;
                }
            }
            return true;
        }

        @Override
        public void onUSBVideoPlayPauseStatus(int rw, int state) {
            // 设置/获取USB视频播放状态
            if (checkRegister(rw, state)) {
                if (!handleUSBMound()) return;
                XMToast.showToast(MainActivity.this, "USB_VIDEO RECEIVE ： " + state);
                int action = -1;
                Bundle bundle = new Bundle();
                if (isSet(rw)) {
                    if (!handleVideoPlay(state == 1)) return;
                    action = DistributeConstants.ACTION_SET_USB_VIDEO_PAUSE_PLAY;
                    bundle.putInt("status", state);
                } else {
                    NiceVideoPlayer currentNiceVideoPlayer = NiceVideoPlayerManager.instance().getCurrentNiceVideoPlayer();
                    if (currentNiceVideoPlayer == null) {
                        XMToast.showToast(MainActivity.this, "Pls Start USB Video First!");
                        return;
                    }
                    if (currentNiceVideoPlayer.isPlaying() || currentNiceVideoPlayer.isBufferingPlaying()) {
                        if (eolManager != null) {
                            try {
                                eolManager.responseGetUSBVideoPlayPauseStatus(1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (eolManager != null) {
                            try {
                                eolManager.responseGetUSBVideoPlayPauseStatus(2);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if (action > 0) {
                    InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(action, bundle));
                }
            }
        }

        @Override
        public void onUSBVideoSkip(int rw, int state) {
            if (checkRegister(rw, state)) {
                if (!handleUSBMound()) return;
                NiceVideoPlayer currentNiceVideoPlayer = NiceVideoPlayerManager.instance().getCurrentNiceVideoPlayer();
                XMToast.showToast(MainActivity.this, "onUSBSkip " + rw + " ~ " + state + " ~ " + currentNiceVideoPlayer);
                if (currentNiceVideoPlayer == null) {
                    XMToast.showToast(MainActivity.this, "SKip : Pls start USB Video First !");
                } else {
                    // 切换上一个/下一个USB视频
                    Bundle bundle = new Bundle();
                    bundle.putInt("status", state);
                    InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_VIDEO_PREVIOUS_NEXT, bundle));
//                }
                }
            }

        }

        @Override
        public void onTunerCurrentStatus(int rw) {

        }

        @Override
        public void onTunerFrequency(int rw, int freq) {
            // 设置电台到指定频率
            if (checkRegister(rw, freq)) {
                Bundle bundle = new Bundle();
                bundle.putInt("frequency", freq);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_FREQUENCY, bundle, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        if (extra != null) {
                                            boolean result = extra.getBoolean("result");
                                            try {
                                                if (result) {
                                                    eolManager.responseSetTunerFrequency(0);
                                                } else {
                                                    Log.e(TAG, "onTunerFrequency: Error");
                                                }

                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                });
            }

        }

        @Override
        public void onTunerBand(int rw, int band) {
            // 设置电台切换AM/FM
            if (checkRegister(rw, band)) {
                Bundle bundle = new Bundle();
                bundle.putInt("band", band);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_BAND, bundle, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        try {
                                            eolManager.responseSetTunerBand(0);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
            }

        }

        @Override
        public void onTunerFavorite(int rw, int state) {
            // 电台收藏
            if (checkRegister(rw, state)) {
                Bundle bundle = new Bundle();
                bundle.putInt("favorite", state);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_FAVORITE, bundle, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        if (extra != null) {
                                            boolean result = extra.getBoolean("result");
                                            try {
                                                if (result) {
                                                    eolManager.responseSetTunerFrequency(0);
                                                } else {
                                                    Log.e(TAG, "onTunerFavorite:Error ");
                                                }
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
//                                    setResult("write tuner favorite = " + favorite + " success");
                                    }
                                });
            }

        }

        @Override
        public void onTunerSeek(int rw, int state) {
            // 设置电台搜索上一个/下一个电台
            // 设置电台到上一个/下一个频率
            //0x01: auto seek up
            //0x02: auto seek down
            //0x03: manually tune up
            //0x04: manually tune down
            if (checkRegister(rw, state)) {
                Bundle bundle = new Bundle();
                bundle.putInt("seek", state);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_SEEK, bundle, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        if (extra != null) {
                                            boolean result = extra.getBoolean("result");
                                            try {
                                                if (result) {
                                                    eolManager.responseSetTunerFrequency(0);
                                                } else {
                                                    Log.e(TAG, "onTunerSeek : Error");
                                                }
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
//                                    setResult("write tuner seek = " + seek + " success");
                                    }
                                });
            }

        }

        @Override
        public void onTunerAutoStore(int rw, int state) {
            // 设置电台自动搜索，开始/停止
            if (checkRegister(rw, state)) {
                Log.d(TAG, "onTunerAutoStore: ");
                Bundle bundle = new Bundle();
                bundle.putInt("autoStore", state);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_AUTO_STORE, bundle, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        if (extra != null) {
                                            boolean result = extra.getBoolean("result");
                                            try {
                                                if (result) {
                                                    eolManager.responseSetTunerFrequency(0);
                                                } else {
                                                    Log.e(TAG, "onTunerAutoStore : Error");
                                                }
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
//                                    setResult("write tuner seek = " + seek + " success");
                                    }
                                });
            }


        }

        @Override
        public void onTFTIlluminationOnOff(int rw, int state) {
            // 屏幕背光打开/关闭 state: off/on
            if (checkRegister(rw, state)) {
                Log.d(TAG, "onTFTIlluminationOnOff: ");
                int setOrGet;
                if (rw == EolCmd.SID.REQUEST_READ) {
                    setOrGet = ACTION_GET_TFT_ILLUMINATION;
                } else {
                    setOrGet = ACTION_SET_TFT_ILLUMINATION;
                }
                Bundle result = handleCommand("tftIlluminationStatus", "", state, 0, setOrGet);
                try {
                    if (rw == EolCmd.SID.REQUEST_READ) {
                        eolManager.responseGetTFTIlluminationOnOff(result.getInt("result"));
                    } else {
                        eolManager.responseSetTFTIlluminationOnOff(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onTFTDisplayPattern(int rw, int state) {
            if (!checkRegister(rw, state)) return;
            Log.d(TAG, "onTFTDisplayPattern: ");
            int setOrGet;
            if (rw == EolCmd.SID.REQUEST_READ) {
                setOrGet = ACTION_GET_TFT_DISPLAY_PATTERN;
            } else {
                setOrGet = ACTION_SET_TFT_DISPLAY_PATTERN;
            }
            Bundle result = handleCommand("status", "", state, 0, setOrGet);
            try {
                if (rw == EolCmd.SID.REQUEST_READ) {
                    eolManager.responseGetTFTDisplayPattern(result.getInt("result"));
                } else {
                    eolManager.responseSetTFTDisplayPattern(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTestScreenIllumination(int rw, int mode, int state) {
            if (!checkRegister(rw, mode, state)) return;
            Log.d(TAG, "onTestScreenIllumination: ");
            int setOrGet;
            if (rw == EolCmd.SID.REQUEST_READ) {
                setOrGet = ACTION_GET_TEST_SCREEN_ILLUMINATION;
            } else {
                setOrGet = ACTION_SET_TEST_SCREEN_ILLUMINATION;
            }
            Bundle result = handleCommand("testScreenIllumination", "level", mode, state, setOrGet);
            try {
                if (rw == EolCmd.SID.REQUEST_READ) {
                    eolManager.responseGetTestScreenIllumination(result.getInt("mode"), result.getInt("level"));
                } else {
                    eolManager.responseSetTestScreenIllumination(0);//set返回什么
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTestMFDIllumination(int rw, int mode, int state) {
            if (!checkRegister(rw, mode, state)) return;
            // 按键亮度
            Log.d(TAG, "onTestMFDIllumination: ");
            int setOrGet;
            if (rw == EolCmd.SID.REQUEST_READ) {
                setOrGet = ACTION_GET_TEST_MFD_ILLUMINATION;
            } else {
                setOrGet = ACTION_SET_TEST_MFD_ILLUMINATION;
            }
            Bundle result = handleCommand("mode", "level", mode, state, setOrGet);
            try {
                if (rw == EolCmd.SID.REQUEST_READ) {
                    eolManager.responseGetTestMFDIllumination(result.getInt("mode"), result.getInt("level"));
                } else {
                    eolManager.responseSetTestMFDIllumination(0);//set返回什么
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLCDLVDSOutputOnOff(int rw, int state) {
            if (!checkRegister(rw, state)) return;
            Log.d(TAG, "onLCDLVDSOutputOnOff: ");
            int setOrGet;
            if (rw == EolCmd.SID.REQUEST_READ) {
                setOrGet = ACTION_GET_LCD_LVDS_OUTPUT;
            } else {
                setOrGet = ACTION_SET_LCD_LVDS_OUTPUT;
            }
            Bundle result = handleCommand("status", "", state, 0, setOrGet);
            try {
                if (rw == EolCmd.SID.REQUEST_READ) {
                    eolManager.responseGetLCDLVDSOutputOnOff(result.getInt("result"));
                } else {
                    eolManager.responseSetLCDLVDSOutputOnOff(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onIPKLVDSOutputOnOff(int rw, int state) {
            if (!checkRegister(rw, state)) return;
            Log.d(TAG, "onIPKLVDSOutputOnOff: ");
//            try {
//                eolManager.responseSetIPKLVDSOutputOnOff(0);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
            if (isSet(rw)) {
                int action;
                if (state == 0x01) {
                    action = CenterConstants.EOLContract.Action.LVDS_HIDE;
                } else {
                    action = CenterConstants.EOLContract.Action.LVDS_SHOW;
                }

                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(CenterConstants.IPCPort.DualScreen.EOL, action, null, CenterConstants.IPCPort.DualScreen.PKG),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        if (extra != null) {
                                            boolean result = extra.getBoolean(CenterConstants.EOLContract.Key.EXTRA);
                                            try {
                                                if (result) {
                                                    eolManager.responseSetIPKLVDSOutputOnOff(0);

                                                }
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
//                                    setResult("write tuner seek = " + seek + " success");
                                    }
                                });
            } else {
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(CenterConstants.IPCPort.DualScreen.EOL, CenterConstants.EOLContract.Action.LVDS_STATE, null, CenterConstants.IPCPort.DualScreen.PKG),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        if (extra != null) {
                                            boolean result = extra.getBoolean(CenterConstants.EOLContract.Key.EXTRA);
                                            try {
                                                eolManager.responseGetIPKLVDSOutputOnOff(result ? 0x02 : 0x01);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
            }
        }

        @Override
        public void onDiagnosisSessionCommand(int i) {

        }
    }
}
