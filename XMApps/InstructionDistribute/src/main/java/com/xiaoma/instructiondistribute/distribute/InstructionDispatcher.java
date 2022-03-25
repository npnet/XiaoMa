package com.xiaoma.instructiondistribute.distribute;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.xiaoma.aidl.model.State;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarAudioManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.component.AppHolder;
import com.xiaoma.instructiondistribute.listener.ClientCallback;
import com.xiaoma.instructiondistribute.ui.DisplayActivity;
import com.xiaoma.instructiondistribute.ui.MainActivity;
import com.xiaoma.instructiondistribute.ui.PhotoActivity;
import com.xiaoma.instructiondistribute.utils.BluetoothHelper;
import com.xiaoma.instructiondistribute.utils.DistributeConstants;
import com.xiaoma.instructiondistribute.utils.bluetooth.BluetoothAdapterUtils;
import com.xiaoma.instructiondistribute.utils.bluetooth.BluetoothController;
import com.xiaoma.instructiondistribute.utils.bluetooth.INfCommandBluetoothSdk;
import com.xiaoma.instructiondistribute.xkan.common.constant.XkanConstants;
import com.xiaoma.instructiondistribute.xkan.main.ui.XkanMainActivity;
import com.xiaoma.instructiondistribute.xkan.video.ui.VideoPlayActivity;
import com.xiaoma.process.manager.XMApi;
import com.xiaoma.process.manager.XMBluetoothPhoneApiManager;

import org.simple.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.xiaoma.instructiondistribute.utils.DistributeConstants.DISPLAY_TYPE;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.WRITE_LCD_LVDS_OUTPUT;


public class InstructionDispatcher {
    private static boolean serviceConnected; // 服务是否已经连接
    private static InstructionDispatcher INSTANCE;
    private final String TAG = "EOL_Handle";
    public Map<String, Activity> destoryMap = new HashMap<>();
    private HashMap<String, SourceInfo> sourceInfoMap = new HashMap<>();
    private Context context;
    private boolean isInit;
    private int btPhoneState = 1;// 2:接听中 1：挂断
    private int mScreenShowType = -1;
    //    private boolean hasUsbAudio; // usb是否检测到 是否有音频信息 没有时  播放等操作全部返回错误信息 具体错误信息 等联调时确定
//
//    // usb扫描结束的广播 先启动本app 再插入u盘 避免出现没有收到广播的情况
//    BroadcastReceiver usbReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //扫描到的音频个数
//            int size = intent.getIntExtra(DistributeConstants.USB_AUDIO_SIZE, 0);
//            hasUsbAudio = size > 0;
////            rePlayUsb(size);
//        }
//    };
    private int mUSBPicShowType = -1;

    private InstructionDispatcher() {
    }

    public static InstructionDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (InstructionDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new InstructionDispatcher();
                }
            }
        }
        return INSTANCE;
    }

    private void initBtService() { // 蓝牙电话服务
        if (isInit) return;
        XMApi.getInstance().init(context);
        isInit = XMApi.getInstance().getXMBluetoothPhoneApiManager().bindService();
        if (isInit) {
            XMApi.getInstance().getXMBluetoothPhoneApiManager().addPhoneStateChangeListener((beanList, states) -> {
                if (states == null || states.length == 0) return;
                Log.d("Info", "BtPhone Status: " + states);
                handleBtPhoneState(states[0]);
            });
        }
        Log.d(TAG, "initBtService: bindresult = " + isInit);
    }

    private void handleBtPhoneState(State state) {
        if (state == null) return;
        switch (state.getValue()) {
            case 1: // 正在来电
            case 2: // 通话中
            case 4: // 保留
            case 5: // 正在拨号
                btPhoneState = 2;
                break;
            case 3: // 空闲
                btPhoneState = 1;
                break;
        }
    }

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
        initBtService();
        initINfCommandBtService();
    }

    private void initINfCommandBtService() {
        INfCommandBluetoothSdk.getInstance().init(context);
    }

    private boolean initCenter(Runnable runnable) {
        if (serviceConnected) {
            runnable.run();
            return false;
        }
        // 初始化
        Center.getInstance().init(context);
        Center.getInstance().runAfterConnected(() -> {
            serviceConnected = true;
            runnable.run();
        });
        return true;
    }

    /**
     * 本app处理 回传结果
     *
     * @param bean
     * @param callback
     * @param callback
     */
    public void handleInstruction(DispatcherBean bean, ResultCallback callback) {
        int action = bean.getAction();
        Bundle data = bean.getBundle();//接口参数
        switch (action) {
            case DistributeConstants.ACTION_GET_BT_MODULE_VERSION:
                getBtModuleVersion(callback);
                break;
            case DistributeConstants.ACTION_SET_BT_PAIR_WITH_DEVICE: // 跟指定的地址配对
                setBtPairWithDevice(data, callback);
                break;
        }
    }

    /**
     * 本app处理 直接返回结果
     *
     * @param bean
     */
    public Bundle handleInstruction(DispatcherBean bean) {
        if (context == null) {
            return null;
        }
        Bundle bundle = new Bundle(); // 回传结果
        int action = bean.getAction();
        Bundle data = bean.getBundle();//接口参数
        switch (action) {
            case DistributeConstants.ACTION_FACTORY_RESET:
                factoryReset();//恢复出厂设置
                break;
            case DistributeConstants.ACTION_SET_BLUETOOTH:
                bundle.putBoolean("result", setBluetoothStatus(data));//设置蓝牙状态
                break;
            case DistributeConstants.ACTION_GET_BLUETOOTH:
                bundle.putInt("result", getBluetoothStatus());//获取蓝牙开关状态
                break;
            case DistributeConstants.ACTION_SET_MUTE:
                bundle.putBoolean("result", setMuteStatus(data));//设置静音
                break;
            case DistributeConstants.ACTION_GET_MUTE:
                bundle.putInt("result", getMuteStatus());//获取是否静音
                break;
            case DistributeConstants.ACTION_SET_SPEED_VOLUME:
                setSpeedVolumeStatus(data);//设置速度音量补偿状态
                break;
            case DistributeConstants.ACTION_GET_SPEED_VOLUME:
                bundle.putInt("result", getSpeedVolumeStatus());//获取速度音量补偿状态
                break;
            case DistributeConstants.ACTION_SET_EQ_SETTING:
                setEQSettingStatus(data);//设置音效状态
                break;
            case DistributeConstants.ACTION_GET_EQ_SETTING:
                bundle.putInt("result", getEQSettingStatus());//获取音效状态
                break;
            case DistributeConstants.ACTION_SET_ARKAMYS_3D:
                setArkamys3DStatus(data);//设置Arkamys 3D状态
                break;
            case DistributeConstants.ACTION_GET_ARKAMYS_3D:
                bundle.putInt("result", getArkamys3DStatus());//获取Arkamys 3D状态
                break;
            case DistributeConstants.ACTION_SET_STREAM_VOLUME:
                setVolumeLevel(data);//设置某个音频下的音量
                break;
            case DistributeConstants.ACTION_GET_STREAM_VOLUME:
                bundle.putInt("result", getVolumeLevel(data));//获取某个音频下的音量
                break;
            case DistributeConstants.ACTION_SET_TFT_ILLUMINATION:
                setTFTIllumination(data);//on/off开启/关闭屏幕背光
                break;
            case DistributeConstants.ACTION_GET_TFT_ILLUMINATION:
                bundle.putInt("result", getTFTIllumination());//获取屏幕背光是否打开
                break;
            case DistributeConstants.ACTION_SET_TEST_SCREEN_ILLUMINATION:
                setTestScreenIllumination(data);//设置白天/黑夜模式
                break;
            case DistributeConstants.ACTION_GET_TEST_SCREEN_ILLUMINATION:
                bundle = getTestScreenIllumination();//获取白天/黑夜模式
                break;
            case DistributeConstants.ACTION_SET_TEST_MFD_ILLUMINATION:
                setTestMFDIllumination(data);//设置白天/黑夜模式
                break;
            case DistributeConstants.ACTION_GET_TEST_MFD_ILLUMINATION:
                bundle = getTestMFDIllumination();//获取白天/黑夜模式
                break;
            case DistributeConstants.ACTION_SET_FADER_LEVEL:
                setFaderLevel(data);//设置最佳听音位(前后方向)等级
                break;
            case DistributeConstants.ACTION_GET_FADER_LEVEL:
                bundle.putInt("result", getFaderLevel());//获取最佳听音位(前后方向)等级
                break;
            case DistributeConstants.ACTION_SET_BALANCE_LEVEL:
                setBalanceLevel(data);//设置最佳听音位(左右方向)等级
                break;
            case DistributeConstants.ACTION_GET_BALANCE_LEVEL:
                bundle.putInt("result", getBalanceLevel());//设置最佳听音位(左右方向)等级
                break;
            case DistributeConstants.ACTION_SET_BEST_POSITION:
                setBastListenPositionStatus(data);//设置最佳听音位为固定位置
                break;
            case DistributeConstants.ACTION_GET_BEST_POSITION:
                bundle.putInt("result", getBastListenPositionStatus());//获取最佳听音位为固定位置
                break;
            case DistributeConstants.ACTION_SET_TFT_DISPLAY_PATTERN:
                setTFTDisplayPattern(data);//设置指定屏幕展示模板
                break;
            case DistributeConstants.ACTION_GET_TFT_DISPLAY_PATTERN:
                bundle.putInt("result", getTFTDisplayPattern());//获取指定屏幕展示模板
                break;
            case DistributeConstants.ACTION_SET_LCD_LVDS_OUTPUT:
                setLCDLVDSOutput(data);//开启/关闭展示一张指定的图片
                break;
            case DistributeConstants.ACTION_GET_LCD_LVDS_OUTPUT:
                bundle.putInt("result", getLCDLVDSOutput());//获取当前是否正在展示固定的图片
                break;
            case DistributeConstants.ACTION_SET_SOUND_FIELD_STATUS:
                setSoundFieldStatus(data);//设置声场模式
                break;
            case DistributeConstants.ACTION_GET_SOUND_FIELD_STATUS:
                bundle.putInt("result", getSoundFieldStatus());//获取声场模式
                break;
            case DistributeConstants.ACTION_SET_EQ: // 设置当前音效对应的音效值
                bundle.putBoolean("result", setEQValue(data));
                break;
            case DistributeConstants.ACTION_GET_EQ: // 获取当前音效对应的音效值
                bundle.putIntegerArrayList("eq", getEQValue());
                break;
            case DistributeConstants.ACTION_SET_BT_PAIR_MODE:// 设置打开/关闭蓝牙配对模式
                bundle.putBoolean("result", setBTPairMode(data));
                break;
            case DistributeConstants.ACTION_GET_BT_PAIR_MODE:// 获取蓝牙配对模式打开/关闭状态
                bundle.putInt("bt_pair_mode", getBTPairMode());
                break;
            case DistributeConstants.ACTION_SET_BT_CALL_ACTIVE_DEACTIVE:// 接听挂断蓝牙电话
                bundle.putBoolean("result", setBtCallActiveOrDeactive(data));
                break;
            case DistributeConstants.ACTION_GET_BT_CALL_ACTIVE_DEACTIVE:// 当前蓝牙电话状态
                bundle.putInt("status", getBtCallStatus());
                break;
            case DistributeConstants.ACTION_CLEAR_BT_PAIRED_LIST: // 清除蓝牙配对列表
                bundle.putBoolean("result", clearBtPairedList());
                break;
            case DistributeConstants.ACTION_GET_BT_PAIR_WITH_DEVICE: // 获取当前连接的蓝牙的地址
                bundle.putString("mac", getBtPairedDeviceMacAddress());
                break;
            case DistributeConstants.ACTION_SET_USB_PICTURE_OPERATION:
                picOperation(data);
//                bundle.putBoolean("result", picOperation(data));
                break;
            case DistributeConstants.ACTION_SET_USB_PICTURE_SHOW_TYPE:
                mUSBPicShowType = data.getInt("type");
                setPicShowType(data);
//                bundle.putBoolean("result", setPicShowType(data));
                break;
            case DistributeConstants.ACTION_GET_USB_PICTURE_SHOW_TYPE:
                getPicShowType(data);
//                bundle.putInt("type", getPicShowType(data));
                break;
            case DistributeConstants.ACTION_SET_USB_PIC_PREVIOUS_NEXT:
                setUsbPicPreviousOrNext(data);
//                bundle.putBoolean("result", setUsbPicPreviousOrNext(data));
                break;
            case DistributeConstants.ACTION_SET_USB_VIDEO_PAUSE_PLAY:
                setUsbVideoPausePlay(data);
//                bundle.putBoolean("result", setUsbVideoPausePlay(data));
                break;
            case DistributeConstants.ACTION_GET_USB_VIDEO_PAUSE_PLAY:
                getUsbVideoPausePlay(data);
//                bundle.putInt("status", getUsbVideoPausePlay(data));
                break;
            case DistributeConstants.ACTION_SET_USB_VIDEO_PREVIOUS_NEXT:
                setUsbVideoPreviousNext(data);
//                bundle.putBoolean("result", setUsbVideoPreviousNext(data, null));
                break;
            case DistributeConstants.ACTION_SET_BLUETOOTH_ADDRESS:
                bundle.putBoolean("result", writeBtAddress(data));
                break;
            case DistributeConstants.ACTION_GET_BLUETOOTH_ADDRESS:
                bundle.putString("result", readBtAddress());
                break;

        }
        return bundle;
    }

    /**
     * 上/下一个视频
     *
     * @param data
     * @return
     */
    private void setUsbVideoPreviousNext(Bundle data) {
        data.putInt("action", DistributeConstants.ACTION_SET_USB_VIDEO_PREVIOUS_NEXT);
        launcherVideoActivityWithData(VideoPlayActivity.class, data);
    }

    /**
     * 获取usb视频播放/暂停状态
     *
     * @return 1 play 2 pause
     */
    private void getUsbVideoPausePlay(Bundle data) {
        data.putInt("action", DistributeConstants.ACTION_GET_USB_VIDEO_PAUSE_PLAY);
        launcherVideoActivityWithData(VideoPlayActivity.class, data);
    }

    public void setUsbVideoPausePlay(Bundle data) {
        data.putInt("action", DistributeConstants.ACTION_SET_USB_VIDEO_PAUSE_PLAY);
        launcherVideoActivityWithData(VideoPlayActivity.class, data);
    }

    private void setUsbPicPreviousOrNext(Bundle data) {
        data.putInt("action", DistributeConstants.ACTION_SET_USB_PIC_PREVIOUS_NEXT);
        launcherPicActivityWithData(XkanMainActivity.class, data);
    }

    /**
     * 图片相关操作 直接跳界面显示
     *
     * @param data
     * @return
     */
    private void picOperation(Bundle data) {
        data.putInt("action", DistributeConstants.ACTION_SET_USB_PICTURE_OPERATION);
        launcherPicActivityWithData(XkanMainActivity.class, data);
    }

    private void setPicShowType(Bundle data) {
        data.putInt("action", DistributeConstants.ACTION_SET_USB_PICTURE_SHOW_TYPE);
        launcherPicActivityWithData(XkanMainActivity.class, data);
    }

    @Deprecated
    private void getPicShowType(Bundle data) {


        if (mUSBPicShowType != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt("result", mUSBPicShowType);
            bundle.putInt("rw", 2);
            EventBus.getDefault().post(bundle, "usb_pic_show_type");
        } else {
            Intent intent = new Intent(AppHolder.getInstance().getAppContext(), PhotoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(PhotoActivity.ARG_ACTION, "");
            data.putInt("action", DistributeConstants.ACTION_GET_USB_PICTURE_SHOW_TYPE);
            launcherPicActivityWithData(XkanMainActivity.class, data);
        }
    }

    private void launcherPicActivityWithData(Class target, Bundle bundle) {
        context.startActivity(
                new Intent(context, target)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtras(bundle));
    }

    private void launcherVideoActivityWithData(Class target, Bundle bundle) {
        context.startActivity(
                new Intent(context, target)
                        .putExtra(XkanConstants.FROM_TYPE, XkanConstants.FROM_VIDEO)
                        .putExtra(XkanConstants.VIDEO_INDEX, 0)
                        .putExtras(bundle)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private boolean setEQValue(Bundle bundle) {
        ArrayList<Integer> eqValues = bundle.getIntegerArrayList("eq");
        Integer[] eqValueArr = {};
        eqValueArr = eqValues.toArray(eqValueArr);
        XmCarVendorExtensionManager.getInstance().setCustomSoundEffects(eqValueArr);
        return true;
    }

    private ArrayList<Integer> getEQValue() {
        int effectsMode = XmCarVendorExtensionManager.getInstance().getSoundEffectsMode();
        return (ArrayList<Integer>) XmCarVendorExtensionManager.getInstance().getCurrentSoundEffects(effectsMode);
    }

    /**
     * 读取
     *
     * @return
     */
    private String readBtAddress() {
        String path = Environment.getExternalStorageDirectory() + "/datax/bt_address.conf";
        File file = new File(path);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Log.d(TAG, "readBtAddress: " + sb.toString());
            br.close();
            inputStream.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写入
     *
     * @param data
     * @return
     */
    private boolean writeBtAddress(Bundle data) {
        int[] address = data.getIntArray("macAddress");
        String macAddress = String.valueOf(address[0]) + ":" + String.valueOf(address[1]) + ":" +
                String.valueOf(address[2]) + ":" + String.valueOf(address[3]) + ":" +
                String.valueOf(address[4]) + ":" + String.valueOf(address[5]);

        Log.d(TAG, "writeBtAddress: " + macAddress);
        String path = Environment.getExternalStorageDirectory() + "/datax/";
        String filePath = path + "bt_address.conf";
        File file = new File(filePath);
        boolean createFile = false;
        if (!file.exists()) {
            try {
                new File(path).mkdirs();
                createFile = file.createNewFile(); // 创建
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream outStream = new FileOutputStream(file, false);
            outStream.write(macAddress.getBytes());
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!createFile) return false;
        // 写文件
        return true;
    }

    // 验证 pad可用
    private String getBtPairedDeviceMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter(context);
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices == null || bondedDevices.isEmpty()) return "";
        String btAddress = bondedDevices.iterator().next().getAddress();
//        if (bluetoothAdapter.getState() != BluetoothAdapter.STATE_ON)
//            return "state is't on";
//        for (BluetoothDevice device : bondedDevices) {
//            if (isConnectedDevice(device)) {
//                btAddress = device.getAddress();
//                break;
//            }
//        }
        return btAddress;
    }

    // 验证 pad不可用 需调试
    private boolean isConnectedDevice(BluetoothDevice device) {
        BluetoothController controller = new BluetoothController(context);
        controller.initProfile(context);
        return controller.isBluetoothConnected(device);
    }

    /**
     * 直接用提供的mac地址配对蓝牙设备
     *
     * @param data
     */
    private void setBtPairWithDevice(Bundle data, ResultCallback callback) {
//        BluetoothA2dpSinkExt dpSinkExt = new BluetoothA2dpSinkExt(context);
//        dpSinkExt.init(new ServiceListener() {
//            @Override
//            public void onConnectedState(boolean st) {
//                setBtCanBeDiscovered(); // 打开配对
//                Bundle bundle = new Bundle();
//                if (st) {
//                    bundle.putBoolean("result", dpSinkExt.setBtPairWithDevice(data.getString("mac")));
//                }
//                callback.onResponse(bundle);
//            }
//        });
        setBtCanBeDiscovered(); // 打开配对
        Bundle bundle = new Bundle();
//
        bundle.putBoolean("result", BluetoothHelper.newSingleton().connectWithMac(data.getString("mac")));
        callback.onResponse(bundle);

    }

    /**
     * 获取蓝牙固件版本
     *
     * @param callback
     */
    private void getBtModuleVersion(ResultCallback callback) {
        Bundle bundle = new Bundle();
        bundle.putString("version", INfCommandBluetoothSdk.getInstance().getNfServiceModuleName());
        Log.d("Info", "getBtModuleVersion: " + INfCommandBluetoothSdk.getInstance().getNfServiceModuleName());
        callback.onResponse(bundle);
    }

    /**
     * @return if bt state is't state_on or no device was bounded return false
     */
    private boolean clearBtPairedList() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter(context);
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
            Log.d(TAG, "clearBtPairedList: bluethooth state is off");
            return false;
        } else if (bondedDevices.isEmpty()) {
            Log.d(TAG, "clearBtPairedList: paired list is empty");
            return false;
        }
        // 蓝牙是关闭状态 或者没有配对过
        Iterator<BluetoothDevice> iterator = bondedDevices.iterator();
        while (iterator.hasNext()) {
            BluetoothDevice device = iterator.next();
            device.removeBond(); // 移除
        }
        return true;
    }

    private boolean setBtCallActiveOrDeactive(Bundle data) {
        XMBluetoothPhoneApiManager phoneApiManager = XMApi.getInstance().getXMBluetoothPhoneApiManager();
        boolean bindService = phoneApiManager.bindService();
        if (!bindService) return false;
        boolean connected = phoneApiManager.isBluetoothConnected();
        boolean result = false;
        if (connected) {
            int status = data.getInt("status");
            switch (status) {
                case 1:
                    result = phoneApiManager.terminateCall();
                    break;
                case 2:
                    result = phoneApiManager.acceptCall();
                    break;
            }
        }
        return result;
    }

    private int getBtCallStatus() {
        return btPhoneState;
    }

    private boolean setBTPairMode(Bundle data) {
        int pairMode = data.getInt("bt_pair_mode");
        boolean result = false;
        switch (pairMode) {
            case 1: // off
                result = disableDiscovered();
                break;
            case 2: // on
                result = setBtCanBeDiscovered();
                break;
        }
        return result;
    }

    private boolean setBtCanBeDiscovered() {
        boolean result;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter(context);
        bluetoothAdapter.setDiscoverableTimeout(1000);
        result = bluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 1);
        return result;
    }

    private boolean setBtUnableDiscovered() {
        boolean result;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter(context);
        bluetoothAdapter.setDiscoverableTimeout(1);
        result = bluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE, 1);
        return result;
    }

    private boolean disableDiscovered() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter(context);
        bluetoothAdapter.setDiscoverableTimeout(1);
        return bluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_NONE, 1);
    }

    private int getBTPairMode() {
        int scanMode = BluetoothAdapterUtils.getBluetoothAdapter(context).getScanMode();
        switch (scanMode) {
            case BluetoothAdapter.SCAN_MODE_NONE:
                scanMode = 1;//需要确定
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                scanMode = 1;
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                scanMode = 2;
                break;
        }
        return scanMode;
    }

    private boolean setBluetoothStatus(Bundle bundle) {
        BluetoothAdapter adapter;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            final BluetoothManager bluetoothManager = (BluetoothManager)
                    context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        int isOn = bundle.getInt("isOn");
        if (isOn == 0x02) {
            return adapter.enable();
        } else {
            return adapter.disable();
        }
    }

    private int getBluetoothStatus() {
        BluetoothAdapter adapter;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            final BluetoothManager bluetoothManager = (BluetoothManager)
                    context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        if (adapter.isEnabled()) {
            return 0x02;
        }
        return 0x01;
    }

    private boolean setMuteStatus(Bundle bundle) {
        int isOn = bundle.getInt("isOn");
        if (isOn == 0x02) {
            return XmCarAudioManager.getInstance().setCarMasterMute(true);//TODO 3.6jar包之后再开放
        } else {
            return XmCarAudioManager.getInstance().setCarMasterMute(false);//TODO 3.6jar包之后再开放
        }
    }

    private int getMuteStatus() {
        if (XmCarAudioManager.getInstance().isMute()) {
            return 0x02;
        }
        return 0x01;
    }

    private int getSpeedVolumeStatus() {
        return XmCarVendorExtensionManager.getInstance().getCarSpeedVolumeCompensate() + 1;
    }

    private void setSpeedVolumeStatus(Bundle bundle) {
        int speedVolumeStatus = bundle.getInt("speedVolumeStatus");
        XmCarVendorExtensionManager.getInstance().setCarSpeedVolumeCompensate(speedVolumeStatus - 1);
    }

    private void factoryReset() {
        XmCarVendorExtensionManager.getInstance().setRestoreCmd(0);
    }

    private int getEQSettingStatus() {
        int mode = XmCarVendorExtensionManager.getInstance().getSoundEffectsMode();
        int status = -1;
        switch (mode) {
            case SDKConstants.VALUE.SOUND_EFFECTS_STANDARD:
                status = 0x01;
                break;
            case SDKConstants.VALUE.SOUND_EFFECTS_CLASSIC:
                status = 0x02;
                break;
            case SDKConstants.VALUE.SOUND_EFFECTS_POP:
                status = 0x03;
                break;
            case SDKConstants.VALUE.SOUND_EFFECTS_JAZZ:
                status = 0x04;
                break;
            case SDKConstants.VALUE.SOUND_EFFECTS_USER:
                status = 0x05;
                break;
        }
        return status;
    }

    private void setEQSettingStatus(Bundle bundle) {
        int eQSettingStatus = bundle.getInt("eQSettingStatus");
        int mode = SDKConstants.VALUE.SOUND_EFFECTS_STANDARD;
        switch (eQSettingStatus) {
            case 1:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_STANDARD;
                break;
            case 2:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_CLASSIC;
                break;
            case 3:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_POP;
                break;
            case 4:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_JAZZ;
                break;
            case 5:
                mode = SDKConstants.VALUE.SOUND_EFFECTS_USER;
                break;
        }
        XmCarVendorExtensionManager.getInstance().setSoundEffectsMode(mode);
    }

    private int getArkamys3DStatus() {
        return XmCarVendorExtensionManager.getInstance().getArkamys3D() + 1;
    }

    private void setArkamys3DStatus(Bundle bundle) {
        int arkamys3DStatus = bundle.getInt("arkamys3DStatus");
        XmCarVendorExtensionManager.getInstance().setArkamys3D(arkamys3DStatus - 1);
    }

    private void setVolumeLevel(Bundle bundle) {
        int volumeSource = bundle.getInt("volumeSource");
        int volumeLevel = bundle.getInt("volumeLevel");
        int currentVolume = -1;
        if (volumeSource != 0x01) {
            currentVolume = XmCarAudioManager.getInstance().getStreamVolume(volumeSource - 2);
        }
        if (volumeLevel == 0xEE) {
            XmCarAudioManager.getInstance().setStreamVolume(volumeSource - 2, ++currentVolume);
        } else if (volumeLevel == 0xEF) {
            XmCarAudioManager.getInstance().setStreamVolume(volumeSource - 2, --currentVolume);
        } else {
            XmCarAudioManager.getInstance().setStreamVolume(volumeSource - 2, volumeLevel);
        }
    }

    private int getVolumeLevel(Bundle bundle) {
        int volumeSource = bundle.getInt("volumeSource");
        if (volumeSource != 0x01) {
            return XmCarAudioManager.getInstance().getStreamVolume(volumeSource - 2);
        }
        return -1;
    }

    private int getTFTIllumination() {
        if (XmCarVendorExtensionManager.getInstance().getScreenStatus()) {
            return 0x02;
        }
        return 0x01;
    }

    private void setTFTIllumination(Bundle bundle) {
        int tftIlluminationStatus = bundle.getInt("tftIlluminationStatus");
        if (tftIlluminationStatus == 0x02) {
            XmCarVendorExtensionManager.getInstance().turnOnScreen();
        } else {
            XmCarVendorExtensionManager.getInstance().closeScreen();
        }
    }

    private Bundle getTestScreenIllumination() {
        Bundle bundle = new Bundle();
        bundle.putInt("mode", XmCarVendorExtensionManager.getInstance().getDisplayMode() + 1);
        bundle.putInt("level", XmCarVendorExtensionManager.getInstance().getDisplayLevel());
        return bundle;
    }

    private void setTestScreenIllumination(Bundle bundle) {
        int testScreenIllumination = bundle.getInt("testScreenIllumination");
        int level = bundle.getInt("level");
        XmCarVendorExtensionManager.getInstance().setDisplayMode(testScreenIllumination - 1);
        if (testScreenIllumination == 0x01) {
            XmCarVendorExtensionManager.getInstance().setAutoDisplayLevel(level);
        } else if (testScreenIllumination == 0x02) {
            XmCarVendorExtensionManager.getInstance().setDayDisplayLevel(level);
        } else if (testScreenIllumination == 0x03) {
            XmCarVendorExtensionManager.getInstance().setNightDisplayLevel(level);
        }
    }

    private Bundle getTestMFDIllumination() {
        Bundle bundle = new Bundle();
        bundle.putInt("mode", XmCarVendorExtensionManager.getInstance().getDisplayMode() + 1);
        bundle.putInt("level", XmCarVendorExtensionManager.getInstance().getKeyBoardLevel());
        return bundle;
    }

    private void setTestMFDIllumination(Bundle bundle) {
        int mode = bundle.getInt("mode");
        int level = bundle.getInt("level");
        XmCarVendorExtensionManager.getInstance().setDisplayMode(mode - 1);
        if (mode == 0x01) {
            XmCarVendorExtensionManager.getInstance().setKeyBoardLevel(level);
        } else if (mode == 0x02) {
            XmCarVendorExtensionManager.getInstance().setKeyBoardLevel(level);
        } else if (mode == 0x03) {
            XmCarVendorExtensionManager.getInstance().setKeyBoardLevel(level);
        }
    }

    /**
     * 设置方控背光
     */
    private void setWheelControlIllumination(Bundle bundle) {
        int testScreenIllumination = bundle.getInt("testScreenIllumination");
        // TODO: 2019/7/12 调用设置方控背光
        if ((testScreenIllumination) == 0x01) {
            //auto
        } else if (testScreenIllumination == 0x02) {
            //day mode
        } else if (testScreenIllumination == 0x03) {
            //night mode
        }
    }

    private int getCurWheelControlIllumination() {
        // TODO: 2019/7/12 返回方控背光模式 ,白天or 黑夜模式
        return 0;
    }

    private int getFaderLevel() {
        return XmCarVendorExtensionManager.getInstance().getSoundEffectPositionAtAnyPoint().y;
    }

    private void setFaderLevel(Bundle bundle) {
        int fader = bundle.getInt("fader");
        int balance = XmCarVendorExtensionManager.getInstance().getSoundEffectPositionAtAnyPoint().x;
        int currentFader = XmCarVendorExtensionManager.getInstance().getSoundEffectPositionAtAnyPoint().y;
        if (fader == 0xEE) {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(balance, ++currentFader);
        } else if (fader == 0xEF) {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(balance, --currentFader);
        } else {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(balance, fader);
        }
    }

    private int getBalanceLevel() {
        return XmCarVendorExtensionManager.getInstance().getSoundEffectPositionAtAnyPoint().x;
    }

    private void setBalanceLevel(Bundle bundle) {
        int balance = bundle.getInt("balance");
        int fader = XmCarVendorExtensionManager.getInstance().getSoundEffectPositionAtAnyPoint().y;
        int currentBalance = XmCarVendorExtensionManager.getInstance().getSoundEffectPositionAtAnyPoint().x;
        if (balance == 0xEE) {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(++currentBalance, fader);
        } else if (balance == 0xEF) {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(--currentBalance, fader);
        } else {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(balance, fader);
        }
    }

    private int getBastListenPositionStatus() {
        Point point = XmCarVendorExtensionManager.getInstance().getSoundEffectPositionAtAnyPoint();
        if (point.x == 8 && point.y == 8) {
            return 0x01;
        } else if (point.x == 4 && point.y == 12) {
            return 0x02;
        } else if (point.x == 12 && point.y == 12) {
            return 0x03;
        } else if (point.x == 4 && point.y == 4) {
            return 0x04;
        } else if (point.x == 12 && point.y == 4) {
            return 0x05;
        }
        return -1;
    }

    private void setBastListenPositionStatus(Bundle bundle) {
        int status = bundle.getInt("status");
        if (status == 0x01) {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(8, 8);
        } else if (status == 0x02) {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(4, 12);
        } else if (status == 0x03) {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(12, 12);
        } else if (status == 0x04) {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(4, 4);
        } else if (status == 0x05) {
            XmCarVendorExtensionManager.getInstance().setSoundEffectPositionAtAnyPoint(12, 4);
        }

    }

    private int getTFTDisplayPattern() {
        Activity activity = destoryMap.get("DisplayActivity");
        if (activity == null) {
            return -1;
        } else {
//            int type = activity.getIntent().getIntExtra(DISPLAY_TYPE, -1);
//            int state = -1;
//            switch (type) {
//                case DistributeConstants.BACK_TO_HOME_PAGE:
//                    state = 0xff;
//                    break;
//                case DistributeConstants.TEST_WHITE_SCREEN:
//                    state = 0x01;
//                    break;
//                case DistributeConstants.TEST_BLACK_SCREEN:
//                    state = 0x02;
//                    break;
//                case DistributeConstants.TEST_RED_SCREEN:
//                    state = 0x03;
//                    break;
//                case DistributeConstants.TEST_GREEN_SCREEN:
//                    state = 0x04;
//                    break;
//                case DistributeConstants.TEST_BLUE_SCREEN:
//                    state = 0x05;
//                    break;
//                case DistributeConstants.EGIHT_COLOR_BAR_SCREEN:
//                    state = 0x06;
//                    break;
//                case DistributeConstants.BIG_CHESS_SCREEN:
//                    state = 0x07;
//                    break;
//                case DistributeConstants.FISH_SCREEN:
//                    state = 0x08;
//                    break;
//                case DistributeConstants.GRAY_BLACK_SCREEN:
//                    state = 0x09;
//                    break;
//                case DistributeConstants.H_GRAY_SCALE_SCREEN:
//                    state = 0x0A;
//                    break;
//                case DistributeConstants.MID_GRAY_SCREEN:
//                    state = 0x0B;
//                    break;
//                case DistributeConstants.V_BW_SCREEN:
//                    state = 0x0C;
//                    break;
//                case DistributeConstants.V_GRAY_SCREEN:
//                    state = 0x0D;
//                    break;
//                case DistributeConstants.RESERVED_E:
//                    state = 0x0E;
//                    break;
//                case DistributeConstants.RESERVED_F:
//                    state = 0x0F;
//                    break;
//            }
            return mScreenShowType;
        }
    }

    private void setTFTDisplayPattern(Bundle bundle) {
        int state = bundle.getInt("status");
        int type = 0;
        mScreenShowType = state;
        switch (state) {
            case 0xff:
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                return;
            case 0x01:
                type = DistributeConstants.TEST_WHITE_SCREEN;
                break;
            case 0x02:
                type = DistributeConstants.TEST_BLACK_SCREEN;
                break;
            case 0x03:
                type = DistributeConstants.TEST_RED_SCREEN;
                break;
            case 0x04:
                type = DistributeConstants.TEST_GREEN_SCREEN;
                break;
            case 0x05:
                type = DistributeConstants.TEST_BLUE_SCREEN;
                break;
            case 0x06:
                type = DistributeConstants.EGIHT_COLOR_BAR_SCREEN;
                break;
            case 0x07:
                type = DistributeConstants.BIG_CHESS_SCREEN;
                break;
            case 0x08:
                type = DistributeConstants.FISH_SCREEN;
                break;
            case 0x09:
                type = DistributeConstants.GRAY_BLACK_SCREEN;
                break;
            case 0x0A:
                type = DistributeConstants.H_GRAY_SCALE_SCREEN;
                break;
            case 0x0B:
                type = DistributeConstants.MID_GRAY_SCREEN;
                break;
            case 0x0C:
                type = DistributeConstants.V_BW_SCREEN;
                break;
            case 0x0D:
                type = DistributeConstants.V_GRAY_SCREEN;
                break;
            case 0x0E:
                type = DistributeConstants.RESERVED_E;
                return;
            case 0x0F:
                type = DistributeConstants.RESERVED_F;
                return;
        }
        startDisplay(type);
    }

    private int getLCDLVDSOutput() {
        Activity activity = destoryMap.get("DisplayActivity");
        if (activity == null || activity.getIntent().getIntExtra(DISPLAY_TYPE, -1) != WRITE_LCD_LVDS_OUTPUT) {
            return 0x01;
        } else {
            return 0x02;
        }
    }

    private void setLCDLVDSOutput(Bundle bundle) {
        int status = bundle.getInt("status");
        if (status == 0x02) {
            startDisplay(WRITE_LCD_LVDS_OUTPUT);
        } else if (status == 0x01) {
            Activity activity = destoryMap.get("DisplayActivity");
            if (activity != null) {
                destoryMap.get("DisplayActivity").finish();
            }
        }
    }

    private void startDisplay(int type) {
        Intent intent = new Intent(context, DisplayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DISPLAY_TYPE, type);
        context.startActivity(intent);
    }

    private int getSoundFieldStatus() {
        //1-标准，2-歌剧院，3-音乐厅
        int soundFieldStatus = XmCarVendorExtensionManager.getInstance().getSoundFieldMode();
        if (soundFieldStatus == SDKConstants.VALUE.STANDARD) {
            return 0x01;
        } else if (soundFieldStatus == SDKConstants.VALUE.CINEMA) {
            return 0x02;
        } else if (soundFieldStatus == SDKConstants.VALUE.ODEUM) {
            return 0x03;
        }
        return -1;
    }

    private void setSoundFieldStatus(Bundle bundle) {
        int soundFieldStatus = bundle.getInt("soundFieldStatus");
        //1-标准，2-歌剧院，3-音乐厅
        if (soundFieldStatus == 0x01) {
            XmCarVendorExtensionManager.getInstance().setSoundFieldMode(SDKConstants.VALUE.STANDARD);
        } else if (soundFieldStatus == 0x02) {
            XmCarVendorExtensionManager.getInstance().setSoundFieldMode(SDKConstants.VALUE.CINEMA);
        } else if (soundFieldStatus == 0x03) {
            XmCarVendorExtensionManager.getInstance().setSoundFieldMode(SDKConstants.VALUE.ODEUM);
        }
    }

    public void handleInstructionByConnect(DispatcherBean bean, ResultCallback callback) {
        // 初始化
        initCenter(() -> justifyRemoteClientAlive(getSourceInfo(bean.getRemoteApp(), bean.getPort()), () ->
                connect(bean.getPort(), bean.getAction(), bean.getRemoteApp(), bean.getBundle(), callback)));
    }

    public void handleInstructionByRequest(DispatcherBean bean, ResultCallback callback) {
        initCenter(() -> justifyRemoteClientAlive(getSourceInfo(bean.getRemoteApp(), bean.getPort()), () ->
                request(bean.getPort(), bean.getAction(), bean.getRemoteApp(), bean.getBundle(), callback)));
    }

    public void handleInstructionBySend(DispatcherBean bean) {
        initCenter(() -> justifyRemoteClientAlive(getSourceInfo(bean.getRemoteApp(), bean.getPort()), () ->
                send(bean.getPort(), bean.getAction(), bean.getRemoteApp(), bean.getBundle())));
    }


    /**
     * 发起连接 需要返回操作结果
     *
     * @param port     同一个应用 不同业务
     * @param action   不同action 业务相同事件不同
     * @param bundle   要传输的数据
     * @param callback 业务执行结果 会在该回调中返回
     */
    private void connect(int port, int action, String remoteAppPkg, Bundle bundle, IClientCallback callback) {
        int connectStatus = Linker.getInstance().connect(getRequest(port, action, remoteAppPkg, bundle), callback);
        Log.d(TAG, "connect: port=" + port + " status=" + connectStatus);
    }

    private void request(int port, int action, String remoteAppPkg, Bundle bundle, IClientCallback callback) {
        int connectStatus = Linker.getInstance().request(getRequest(port, action, remoteAppPkg, bundle), callback);
        Log.d(TAG, "request: port=" + port + " status=" + connectStatus);
    }

    /**
     * 发起连接 不需要返回结果
     *
     * @param port
     * @param action
     * @param remoteAppPkg
     * @param bundle
     */
    private void send(int port, int action, String remoteAppPkg, Bundle bundle) {
        int connectStatus = Linker.getInstance().send(getRequest(port, action, remoteAppPkg, bundle));
        Log.d(TAG, "send: port=" + port + " status=" + connectStatus);
    }

    /**
     * 获取对应的Request
     *
     * @param port
     * @param action
     * @param bundle
     * @return
     */
    private Request getRequest(int port, int action, String remoteAppPkg, Bundle bundle) {
        return new Request(getSourceInfo(context.getPackageName(), port), new RequestHead(getSourceInfo(remoteAppPkg, port), action), bundle);
    }

    /**
     * 获取SourceInfo
     *
     * @param app
     * @param port
     * @return
     */
    private SourceInfo getSourceInfo(String app, int port) {
        String id = app + "/" + port;
        if (!sourceInfoMap.containsKey(id)) {
            sourceInfoMap.put(id, new SourceInfo(app, port));
        }
        return sourceInfoMap.get(id);
    }

    private void justifyRemoteClientAlive(SourceInfo remoteInfo, ClientCallback callback) {
        // 判断远程客户端是否活跃
        boolean clientAlive = Center.getInstance().isClientAlive(remoteInfo);
        if (clientAlive) {
            Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * 远程客户端活跃, 直接发起连接请求");
            // 远程客户端活跃, 直接发起连接请求
            callback.onClientAlive();
        } else {
            Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * 远程客户端不活跃时等待活跃再开始发起连接");
            // 远程客户端不活跃时等待活跃再开始发起连接
            StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                @Override
                public void onClientIn(SourceInfo source) {
                    if (source.equals(remoteInfo)) {
                        callback.onClientAlive();
                        StateManager.getInstance().removeCallback(this);
                    }
                }
            });
        }
    }
}
