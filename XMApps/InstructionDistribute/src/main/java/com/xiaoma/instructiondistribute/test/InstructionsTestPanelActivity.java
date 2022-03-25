package com.xiaoma.instructiondistribute.test;

import android.os.Bundle;
import android.view.View;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.instructiondistribute.distribute.DispatcherBean;
import com.xiaoma.instructiondistribute.distribute.InstructionDispatcher;
import com.xiaoma.instructiondistribute.distribute.ResultCallback;
import com.xiaoma.instructiondistribute.utils.DistributeConstants;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

import static com.xiaoma.instructiondistribute.utils.DistributeConstants.MUSIC;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.PORT_USB_AUDIO;
import static com.xiaoma.instructiondistribute.utils.DistributeConstants.XTING;

public class InstructionsTestPanelActivity extends TestPanelActivity {
    private final String TAG = InstructionsTestPanelActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InstructionDispatcher.getInstance().setContext(this);
        XmCarVendorExtensionManager.getInstance().init(this);
        EventBus.getDefault().register(this);
    }

    public void exitApp(View view) {
//        System.exit(1);
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        InstructionDispatcher.getInstance()
                .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_PICTURE_SHOW_TYPE, bundle));
    }

    @Override
    protected void setupItemData() {
        // setEQ
        ArrayList<Integer> eqValue = new ArrayList<>(5);
        eqValue.add(SDKConstants.VALUE.SOUND_EFFECTS_USER);
        eqValue.add(1);
        eqValue.add(2);
        eqValue.add(3);
        eqValue.add(4);
        eqValue.add(5);
        itemList.add(new Item("setEQConfiguration").setTask(new Task() {
            @Override
            protected void run() {
                // params DB1~DB5
                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList("eq", eqValue);
                InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_EQ, bundle));
                setResult("success");
            }
        }));

        // getEQ
        itemList.add(new Item("getEQConfiguration").setTask(new Task() {
            @Override
            protected void run() {
                // params DB1~DB5
                Bundle bundle = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_EQ, null));
                setResult("getEQConfiguration " + bundle.getIntegerArrayList("eq"));
            }
        }));
        itemList.add(new Item("getTunerCurrentStatus").setTask(new Task() {
            @Override
            protected void run() { // 获取当前电台状态
                // DB1~DB7
                //DB1:band information
                //DB2:Tuner signal strength （划线 去掉）
                //DB3:0x00
                //DB4~DB7:frequency information
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_GET_CURRENT_TUNER_STATE, null, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        int band = extra.getInt("band");
                                        int signal = extra.getInt("signal");
                                        int frequency = extra.getInt("frequency");
                                        setResult("result :band = " + band + ",signal = " + signal + ",frequency = " + frequency);
                                    }
                                });
            }
        }));
        itemList.add(new Item("setTunerFrequencyDB1").setTask(new Task() {
            @Override
            protected void run() { // 设置电台到指定频率
                // DB1~DB2
                Bundle bundle = new Bundle();
                bundle.putInt("frequency", 77);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_FREQUENCY, bundle, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("write tuner frequency db1 success");
                                    }
                                });
            }
        }));
        itemList.add(new Item("setTunerFrequencyDB2").setTask(new Task() {
            @Override
            protected void run() { // 设置电台到指定频率
                // DB1~DB2
                Bundle bundle = new Bundle();
                bundle.putInt("frequency", 150);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_FREQUENCY, bundle, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("write tuner frequency db2 success");
                                    }
                                });
            }
        }));
        itemList.add(new Item("setTunerBandFM").setTask(new Task() {
            @Override
            protected void run() { // 设置电台切换AM/FM
                //0x01: FM
                //0x02: AM
                Bundle bundle = new Bundle();
                bundle.putInt("band", 0x01);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_BAND, bundle, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("write tuner band FM success");
                                    }
                                });
            }
        }));
        itemList.add(new Item("setTunerBandAM").setTask(new Task() {
            @Override
            protected void run() { // 设置电台切换AM/FM
                //0x01: FM
                //0x02: AM
                Bundle bundle = new Bundle();
                bundle.putInt("band", 0x02);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_BAND, bundle, XTING),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("write tuner band AM success");
                                    }
                                });
            }
        }));
        int[] tunerFavorite = {0xFF, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12};
        for (int i = 0; i < tunerFavorite.length; i++) {
            int favorite = tunerFavorite[i];
            itemList.add(new Item("setTunerFavorite" + favorite).setTask(new Task() {
                @Override
                protected void run() { // 设置电台收藏
                    //0xFF: No Favorite
                    //0x01~0x12: Favorite 1~18
                    Bundle bundle = new Bundle();
                    bundle.putInt("favorite", favorite);
                    InstructionDispatcher.getInstance()
                            .handleInstructionByRequest(
                                    new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_FAVORITE, bundle, XTING),
                                    new ResultCallback() {
                                        @Override
                                        public void callback(Response response) {
                                            setResult("write tuner favorite = " + favorite + " success");
                                        }
                                    });
                }
            }));
        }
        int[] tunerSeek = {0x01, 0x02, 0x03, 0x04};
        for (int i = 0; i < tunerSeek.length; i++) {
            int seek = tunerSeek[i];
            itemList.add(new Item("setTunerSeek" + seek).setTask(new Task() {
                @Override
                protected void run() { // 设置电台搜索上一个/下一个电台 设置电台到上一个/下一个频率
                    //0x01: auto seek up
                    //0x02: auto seek down
                    //0x03: manually tune up
                    //0x04: manually tune down
                    Bundle bundle = new Bundle();
                    bundle.putInt("seek", seek);
                    InstructionDispatcher.getInstance()
                            .handleInstructionByRequest(
                                    new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_SEEK, bundle, XTING),
                                    new ResultCallback() {
                                        @Override
                                        public void callback(Response response) {
                                            setResult("write tuner seek = " + seek + " success");
                                        }
                                    });
                }
            }));
        }
        int[] tunerAutoStore = {0x01, 0x02};
        for (int i = 0; i < tunerAutoStore.length; i++) {
            int autoStore = tunerAutoStore[i];
            itemList.add(new Item("setTunerAutoStore" + autoStore).setTask(new Task() {
                @Override
                protected void run() { // 设置电台自动搜台(开始/停止)
                    //0x01: Stop
                    //0x02: Start
                    Bundle bundle = new Bundle();
                    bundle.putInt("autoStore", autoStore);
                    InstructionDispatcher.getInstance()
                            .handleInstructionByRequest(
                                    new DispatcherBean(DistributeConstants.PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_AUTO_STORE, bundle, XTING),
                                    new ResultCallback() {
                                        @Override
                                        public void callback(Response response) {
                                            setResult("write tuner auto store = " + autoStore + " success");
                                        }
                                    });
                }
            }));
        }
        int[] operations = {1, 2, 4};
        String[] operationsDesc = {"放大", "缩小", "右旋"};
        for (int i = 0; i < operations.length; i++) {
            String desc = operationsDesc[i];
            int type = operations[i];
            itemList.add(new Item("USBPictureOperation " + desc).setTask(new Task() {
                @Override
                protected void run() {
                    //0x01:放大 0x02: 缩小 0x03: 左旋(去掉) 0x04: 右旋
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", type);
                    InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_PICTURE_OPERATION, bundle));
                }
            }));
        }
        int[] showType = {1, 3};
        String[] typeDesc = {"列表", "全屏"};
        for (int i = 0; i < showType.length; i++) {
            int type = showType[i];
            String desc = typeDesc[i];
            itemList.add(new Item("setUSBPictureDisplayStatus " + desc).setTask(new Task() {
                @Override
                protected void run() {
                    //列表（普通）  0x02: 半屏 （划线 去掉） 0x03: 全屏
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", type);
                    InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_PICTURE_SHOW_TYPE, bundle));
                }
            }));
        }
        itemList.add(new Item("getUSBPictureDisplayStatus").setTask(new Task() {
            @Override
            protected void run() {
                //列表（普通）  0x02: 半屏 （划线 去掉） 0x03: 全屏
                InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_USB_PICTURE_SHOW_TYPE, new Bundle()));
            }
        }));

        int[] skipStatus = {1, 2};
        String[] skipDesc = {"Next pic", "Previous pic"};
        for (int i = 0; i < skipStatus.length; i++) {
            int type = skipStatus[i];
            String desc = skipDesc[i];
            itemList.add(new Item("setUSBPictureSkipStatus " + desc).setTask(new Task() {
                @Override
                protected void run() {
                    //0x01: Next
                    //0x02: Previous
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", type);
                    InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_PIC_PREVIOUS_NEXT, bundle));
                }
            }));
        }
        // 设置usb音乐播放暂停
        int[] usbvideo = {1, 2};
        String[] usbVideoDesc = {"Play", "Pause"};
        for (int i = 0; i < usbvideo.length; i++) {
            int status = usbvideo[i];
            String desc = usbVideoDesc[i];
            itemList.add(new Item("setUSBVideoPlayPauseStatus " + desc).setTask(new Task() {
                @Override
                protected void run() {
                    // 0x01: Play // 0x02: Pause
                    Bundle bundle = new Bundle();
                    bundle.putInt("status", status);
                    InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_VIDEO_PAUSE_PLAY, bundle));
                }
            }));
        }
        itemList.add(new Item("getUSBVideoPlayPauseStatus").setTask(new Task() {
            @Override
            protected void run() {
                //0x01: Play
                //0x02: Pause
                InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_USB_VIDEO_PAUSE_PLAY, new Bundle()));
            }
        }));

        int[] usbVideoSkip = {1, 2};
        String[] usbVideoSkipDesc = {"next track", "previous track"};
        for (int i = 0; i < usbVideoSkip.length; i++) {
            int status = usbVideoSkip[i];
            String desc = usbVideoSkipDesc[i];
            itemList.add(new Item("setUSBVideoSkip " + desc).setTask(new Task() {
                @Override
                protected void run() {
                    //0x01: Next track //0x02: Previous track
                    Bundle bundle = new Bundle();
                    bundle.putInt("status", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_VIDEO_PREVIOUS_NEXT, bundle));
                }
            }));
        }


        itemList.add(new Item("FactoryReset").setTask(new Task() {
            @Override
            protected void run() { // 恢复出厂设置
                // params 0x01
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_FACTORY_RESET, bundle));
                setResult("恢复出厂设置");
            }
        }));



        // BT test mode
        itemList.add(new Item("onBluetoothTestMode").setTask(new Task() {
            @Override
            protected void run() {
                // TODO: 2019/7/1 0001 params 0x01: Off / 0x02: On
            }
        }));

        // set bt status
        int[] bluetoothStatus = {0x01, 0x02};
        String[] bluetoothResult = {"蓝牙关闭:", "蓝牙打开:"};
        for (int i = 0; i < bluetoothStatus.length; i++) {
            int status = bluetoothStatus[i];
            itemList.add(new Item("setBluetoothStatus " + status).setTask(new Task() {
                @Override
                protected void run() {
                    // params 0x01: Off / 0x02: On
                    Bundle bundle = new Bundle();
                    bundle.putInt("isOn", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_BLUETOOTH, bundle));
                    setResult(bluetoothResult[status - 1] + result.getBoolean("result", false));
                }
            }));
        }

        // get bt status
        itemList.add(new Item("getBluetoothStatus").setTask(new Task() {
            @Override
            protected void run() {
                // params 0x01: Off / 0x02: On
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_BLUETOOTH, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));

        // set bt address
        itemList.add(new Item("writeBluetoothAddress").setTask(new Task() {
            @Override
            protected void run() {
                // TODO: 2019/7/1 0001 params DB1~DB6
                Bundle bundle = new Bundle();
                bundle.putIntArray("macAddress", new int[]{0XB4, 0XEF, 0X39, 0X9C, 0X56, 0XEB});
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_BLUETOOTH_ADDRESS, bundle));
                setResult("写入地址：" + result.getBoolean("result"));
            }
        }));

        // get bt address
        itemList.add(new Item("getBluetoothAddress").setTask(new Task() {
            @Override
            protected void run() {
                // TODO: 2019/7/1 0001 params DB1~DB6
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_BLUETOOTH_ADDRESS, bundle));
                setResult("mac地址：" + result.getString("result", " "));
            }
        }));

        // bt module version
        itemList.add(new Item("getBluetoothModuleVersion").setTask(new Task() {
            @Override
            protected void run() {
                // params DB1~DB8: Firmware Version
                Bundle bundle = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_BT_MODULE_VERSION, null));
                String version = bundle.getString("version");
                setResult("BluetoothModuleVersion is " + version);
            }
        }));

        // set bt pair mode status
        int[] btPairMode = {0x01, 0x02};
        String[] btPairDesc = {"蓝牙配对关闭:", "蓝牙配对打开:"};
        for (int i = 0; i < btPairMode.length; i++) {
            int mode = btPairMode[i];
            String desc = btPairDesc[i];
            itemList.add(new Item("setBTPairModeStatus " + desc).setTask(new Task() {
                @Override
                protected void run() { // 蓝牙配对模式
                    // params 0x01: Off / 0x02: On
                    Bundle bundle = new Bundle();
                    bundle.putInt("bt_pair_mode", mode);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_BT_PAIR_MODE, bundle));
                    setResult(desc + result.getBoolean("result", false));
                }
            }));
        }

        // get bt pair mode status
        itemList.add(new Item("getBTPairModeStatus").setTask(new Task() {
            @Override
            protected void run() { // 蓝牙配对模式
                // 0x01: Off / 0x02: On
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_BT_PAIR_MODE, null));
                setResult("BTPairModeStatus is " + result.getInt("bt_pair_mode"));
            }
        }));

        // 设置蓝牙音乐播放暂停
        int[] btAudioPlay = {0x01, 0x02};
        String[] btAudioPlayDesc = {"pause", "play"};
//        for (int i = 0; i < btAudioPlay.length; i++) {
//            final int status = btAudioPlay[i];
//            final String desc = btAudioPlayDesc[i];
        itemList.add(new Item("setBTAudioPlayPauseStatus pause").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Pause / 0x02: Play
                Bundle bundle = new Bundle();
                bundle.putInt("status", 1);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_BLUETOOTH_AUDIO, DistributeConstants.ACTION_SET_BLUETOOTH_AUDIO_PAUSE, bundle, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("set bt audio pause success");
                                    }
                                });
            }
        }));
        itemList.add(new Item("setBTAudioPlayPauseStatus play").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Pause / 0x02: Play
                Bundle bundle = new Bundle();
                bundle.putInt("status", 2);
                bundle.putInt("musicType", 1);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_BLUETOOTH_AUDIO, DistributeConstants.ACTION_SET_BLUETOOTH_AUDIO_PLAY, bundle, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("set bt audio play success");
                                    }
                                });
            }
        }));
//        }

        // 获取当前蓝牙音乐播放暂停状态
        itemList.add(new Item("getBTAudioPlayPauseStatus").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Pause / 0x02: Play
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_BLUETOOTH_AUDIO, DistributeConstants.ACTION_GET_BLUETOOTH_AUDIO_PLAY_PAUSE, null, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        boolean value = extra.getBoolean("status");
                                        setResult("BTAudioPlayPauseStatus is playing ? " + value);
                                    }
                                });
            }
        }));

        // 设置蓝牙音乐上一首下一首
        int[] btAudioSkip = {0x01, 0x02};
        String[] btAudioSkipDesc = {"Next track", "Previous track"};
//        for (int i = 0; i < btAudioSkip.length; i++) {
//            int skip = btAudioSkip[i];
//            String desc = btAudioSkipDesc[i];
        itemList.add(new Item("setBTAudioSkipTrackStatus next track").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Next track / 0x02: Previous track
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("skip", skip);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_BLUETOOTH_AUDIO, DistributeConstants.ACTION_SET_BLUETOOTH_AUDIO_NEXT, null, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("setBTAudioSkipTrackStatus next track success");
                                    }
                                });
            }
        }));
        itemList.add(new Item("setBTAudioSkipTrackStatus previous track").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Next track / 0x02: Previous track
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("skip", skip);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(
                                new DispatcherBean(DistributeConstants.PORT_BLUETOOTH_AUDIO, DistributeConstants.ACTION_SET_BLUETOOTH_AUDIO_PREVIOUS, null, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("setBTAudioSkipTrackStatus previous track success");
                                    }
                                });
            }
        }));
//        }

        // 设置接听挂断蓝牙电话
        int[] btCall = {0x01, 0x02};
        String[] btCallDesc = {"Hang up", "Answer"};
        for (int i = 0; i < btCall.length; i++) {
            int call = btCall[i];
            String desc = btCallDesc[i];
            itemList.add(new Item("setBTIncomingCallActiveStatus " + desc).setTask(new Task() {
                @Override
                protected void run() {
                    // params 0x01: Hang up / 0x02: Answer
                    Bundle bundle = new Bundle();
                    bundle.putInt("status", call);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_BT_CALL_ACTIVE_DEACTIVE, bundle));
                    setResult("setBTIncomingCallActiveStatus " + desc + result.getBoolean("result"));
                }
            }));
        }

        // 获取当前蓝牙电话状态
        itemList.add(new Item("getBTIncomingCallActiveStatus").setTask(new Task() {
            @Override
            protected void run() {
                // params 0x01: Hang up / 0x02: Answer
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_BT_CALL_ACTIVE_DEACTIVE, null));
                setResult("getBTIncomingCallActiveStatus " + btCallDesc[result.getInt("status") - 1]);
            }
        }));

        // 清除蓝牙配对列表
        itemList.add(new Item("ClearBTPairedList").setTask(new Task() {
            @Override
            protected void run() {
                // params 0x01: Clear
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_CLEAR_BT_PAIRED_LIST, null));
                setResult("ClearBTPairedList result is " + result.getBoolean("result"));
            }
        }));

        itemList.add(new Item("setBTPairedWithDevice").setTask(new Task() {
            @Override
            protected void run() {
                Bundle bundle = new Bundle();
                bundle.putString("mac", "38:53:9C:68:4B:2E");
                Bundle resultBundle = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_BT_PAIR_WITH_DEVICE, bundle));
                setResult("setBTPairedWithDevice " + resultBundle.getBoolean("result"));
            }
        }));
        itemList.add(new Item("getBTPairedWithDevice").setTask(new Task() {
            @Override
            protected void run() {
                // params DB1~DB6
                Bundle bundle = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_BT_PAIR_WITH_DEVICE, null));
                setResult("BTPairedWithDevice mac is " + bundle.getString("mac"));
            }
        }));
        itemList.add(new Item("onAudioSourceSelect").setTask(new Task() {
            @Override
            protected void run() {
                // TODO: 2019/7/1 0001 params
                //0xFF: Unknown mode
                //0x01: AM
                //0x02: FM
                //0x03: USB Audio
                //0x04: BT Audio
                //0x05: iDevice(iPod)
                //0x06: USB Video
                //0x07: Online Media（TBox）
                //0x08: Reserved
            }
        }));

        // 前后方向仅取值6,14
        int[] faderLevel = {0x06, 0x0E, 0XEE, 0XEF};
        for (int i = 0; i < faderLevel.length; i++) {
            int fader = faderLevel[i];
            itemList.add(new Item("setFaderLevel" + fader).setTask(new Task() {
                @Override
                protected void run() {
                    // R 0x01~0x0F: Fader level -7~+7
                    // W 0x01~0x0F: Fader level -7~+7 0xEE: Increase Fader by one level / 0xEF: Decrease Fader by one level
                    Bundle bundle = new Bundle();
                    bundle.putInt("fader", fader);
                    bundle.putInt("balance", 8);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_FADER_LEVEL, bundle));
                    setResult(bundle.getInt("fader", -1) + "");
                }
            }));
        }

        itemList.add(new Item("getFaderLevel").setTask(new Task() {
            @Override
            protected void run() {
                // R 0x01~0x0F: Fader level -7~+7
                // W 0x01~0x0F: Fader level -7~+7 0xEE: Increase Fader by one level / 0xEF: Decrease Fader by one level
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_FADER_LEVEL, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));

        int[] balanceLevel = {0x06, 0x0E, 0XEE, 0XEF};
        for (int i = 0; i < balanceLevel.length; i++) {
            int balance = balanceLevel[i];
            itemList.add(new Item("setBalanceLevel" + balance).setTask(new Task() {
                @Override
                protected void run() {
                    // R 0x01~0x0F: Fader level -7~+7
                    // W 0x01~0x0F: Fader level -7~+7 0xEE: Increase Fader by one level / 0xEF: Decrease Fader by one level
                    Bundle bundle = new Bundle();
                    bundle.putInt("balance", balance);
                    bundle.putInt("fader", 8);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_BALANCE_LEVEL, bundle));
                    setResult(bundle.getInt("balance", -1) + "");
                }
            }));
        }

        itemList.add(new Item("getBalanceLevel").setTask(new Task() {
            @Override
            protected void run() {
                // R 0x01~0x0F: Fader level -7~+7
                // W 0x01~0x0F: Fader level -7~+7 0xEE: Increase Fader by one level / 0xEF: Decrease Fader by one level
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_BALANCE_LEVEL, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));
        int[] muteStatus = {0x01, 0x02};
        String[] resultString = {"非静音设置:", "静音设置:"};
        for (int i = 0; i < muteStatus.length; i++) {
            int status = muteStatus[i];
            itemList.add(new Item("setMuteStatus" + status).setTask(new Task() {
                @Override
                protected void run() {
                    // params 0x01: Off / 0x02: On
                    Bundle bundle = new Bundle();
                    bundle.putInt("isOn", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_MUTE, bundle));
                    setResult(resultString[status - 1] + result.getBoolean("result", false) + "");
                }
            }));
        }
        itemList.add(new Item("getMuteStatus").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Off / 0x02: On
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_MUTE, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));

        int[] speedVolumeStatus = {0x01, 0x02, 0x03, 0x04};
        for (int i = 0; i < speedVolumeStatus.length; i++) {
            int status = speedVolumeStatus[i];
            itemList.add(new Item("setSpeedVolumeStatus" + status).setTask(new Task() {
                @Override
                protected void run() {
                    // 0x01: Off / 0x02: On
                    Bundle bundle = new Bundle();
                    bundle.putInt("speedVolumeStatus", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_SPEED_VOLUME, bundle));
                    setResult(bundle.getInt("speedVolumeStatus", -1) + "");
                }
            }));
        }

        itemList.add(new Item("getSpeedVolumeStatus").setTask(new Task() {
            @Override
            protected void run() {
                //  0x01: Off / 0x02: On
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_SPEED_VOLUME, bundle));
                int get = result.getInt("result", -1);
                setResult(get + "");
            }
        }));
        int[] eQSettingStatus = {0x01, 0x02, 0x03, 0x04, 0x05};
        for (int i = 0; i < eQSettingStatus.length; i++) {
            int status = eQSettingStatus[i];
            itemList.add(new Item("setEQSettingStatus" + status).setTask(new Task() {
                @Override
                protected void run() {
                    //0x01: Normal
                    //0x02: Classic
                    //0x03: Pop
                    //0x04: Jazz
                    //0x05: Custom
                    Bundle bundle = new Bundle();
                    bundle.putInt("eQSettingStatus", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_EQ_SETTING, bundle));
                    setResult(bundle.getInt("eQSettingStatus", -1) + "");
                }
            }));
        }
        itemList.add(new Item("getEQSettingStatus").setTask(new Task() {
            @Override
            protected void run() {
                // params 0x01: Off / 0x02: On
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_EQ_SETTING, bundle));
                int eqSettingStatus = result.getInt("result", -1);
                String eqStatus = "";
                if (eqSettingStatus == 1) {
                    eqStatus = "普通";
                } else if (eqSettingStatus == 2) {
                    eqStatus = "经典";
                } else if (eqSettingStatus == 3) {
                    eqStatus = "流行";
                } else if (eqSettingStatus == 4) {
                    eqStatus = "爵士";
                } else if (eqSettingStatus == 5) {
                    eqStatus = "用户自定义";
                }
                setResult("音效模式：" + eqStatus);
            }
        }));
        int[] positionStatus = {0x01, 0x02, 0x03, 0x04, 0x05};
        for (int i = 0; i < positionStatus.length; i++) {
            int status = positionStatus[i];
            itemList.add(new Item("setBastListenPositionStatus" + status).setTask(new Task() {
                @Override
                protected void run() {
                    //0x01: Center
                    //0x02: Driver
                    //0x03: FR passager
                    //0x04: RL passager
                    //0x05: RR passager
                    Bundle bundle = new Bundle();
                    bundle.putInt("status", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_BEST_POSITION, bundle));
                    setResult(bundle.getInt("status", -1) + "");
                }
            }));
        }
        itemList.add(new Item("getBastListenPositionStatus").setTask(new Task() {
            @Override
            protected void run() {
                //  params 最佳听音位
                //0x01: Center
                //0x02: Driver
                //0x03: FR passager
                //0x04: RL passager
                //0x05: RR passager
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_BEST_POSITION, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));
        int[] soundFieldStatus = {0x01, 0x02, 0x03};
        for (int i = 0; i < soundFieldStatus.length; i++) {
            int status = soundFieldStatus[i];
            itemList.add(new Item("setSoundFieldStatus" + status).setTask(new Task() {
                @Override
                protected void run() {
                    //0x01: 标准
                    //0x02: 歌剧院
                    //0x03: 音乐厅
                    Bundle bundle = new Bundle();
                    bundle.putInt("soundFieldStatus", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_SOUND_FIELD_STATUS, bundle));
                    setResult(bundle.getInt("soundFieldStatus", -1) + "");
                }
            }));
        }
        itemList.add(new Item("getSoundFieldStatus").setTask(new Task() {
            @Override
            protected void run() {
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_SOUND_FIELD_STATUS, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));
        int[] arkamys3DStatus = {0x01, 0x02};
        for (int i = 0; i < arkamys3DStatus.length; i++) {
            int status = arkamys3DStatus[i];
            itemList.add(new Item("setArkamys3DStatus" + status).setTask(new Task() {
                @Override
                protected void run() {
                    // 0001 params 0x01: Off / 0x02: On
                    Bundle bundle = new Bundle();
                    bundle.putInt("arkamys3DStatus", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_ARKAMYS_3D, bundle));
                    setResult(bundle.getInt("arkamys3DStatus", -1) + "");
                }
            }));
        }
        itemList.add(new Item("getArkamys3DStatus").setTask(new Task() {
            @Override
            protected void run() {
                // 0001 params 0x01: Off / 0x02: On
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_ARKAMYS_3D, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));
        int[] volumeSources = {0x01, 0x02, 0x03, 0x04, 0x05};
        String[] sources = {"车辆信息", "媒体", "通讯", "语音播报", "蓝牙音乐"};
        for (int i = 0; i < volumeSources.length; i++) {
            int source = volumeSources[i];
            for (int j = 1; j < 22; j++) {
                int finalJ = j;
                itemList.add(new Item("setVolume" + sources[i] + "+volume:" + j).setTask(new Task() {
                    @Override
                    protected void run() {
                        // R
                        //DB1: volume source
                        //0x01: 车辆信息
                        //0x02: 媒体
                        //0x03: 通讯
                        //0x04: TTS语音播报
                        //0x05: 蓝牙音乐
                        //
                        //DB2: volume level
                        //0x01~0x29: Volume level 0~40
                        // W
                        //DB1: volume source
                        //0x01: 车辆信息
                        //0x02: 媒体
                        //0x03: 通讯
                        //0x04: TTS语音播报
                        //0x05: 蓝牙音乐
                        //
                        //DB2: Volume level
                        //0x01~0x21: Volume level 0~32
                        //0xEE: Increase volume by one level
                        //0xEF: Decrease volume by one level
                        Bundle bundle = new Bundle();
                        bundle.putInt("volumeSource", source);
                        bundle.putInt("volumeLevel", finalJ);
                        Bundle result = InstructionDispatcher.getInstance()
                                .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_STREAM_VOLUME, bundle));
                        setResult(bundle.getInt("volumeLevel", -1) + "");
                    }
                }));
            }
            int[] volumeSources1 = {0xEE, 0xEF};
            for (int k = 0; k < volumeSources1.length; k++) {
                int volume = volumeSources1[k];
                itemList.add(new Item("setVolume" + sources[i] + "+volume:" + volume).setTask(new Task() {
                    @Override
                    protected void run() {
                        Bundle bundle = new Bundle();
                        bundle.putInt("volumeSource", source);
                        bundle.putInt("volumeLevel", volume);
                        Bundle result = InstructionDispatcher.getInstance()
                                .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_STREAM_VOLUME, bundle));
                        setResult(bundle.getInt("volumeLevel", -1) + "");
                    }
                }));
            }
        }
        for (int i = 0; i < volumeSources.length; i++) {
            int source = volumeSources[i];
            itemList.add(new Item("getVolume" + sources[i]).setTask(new Task() {
                @Override
                protected void run() {
                    Bundle bundle = new Bundle();
                    bundle.putInt("volumeSource", source);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_STREAM_VOLUME, bundle));
                    setResult(result.getInt("result", -1) + "");
                }
            }));
        }
        // 设置usb音乐播放暂停
        int[] usbAudio = {0x01, 0x02};
        String[] usbAudioDesc = {"Play", "Pause"};
        itemList.add(new Item("setUSBAudioPlayPause play").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Play / 0x02: Pause
                AudioCategoryBean bean = new AudioCategoryBean();
                bean.setAction(AudioConstants.PlayAction.DEFAULT);
                bean.setIndex(0);
                bean.setCategoryId(0);
                Bundle bundle = new Bundle();
                bundle.putParcelable(AudioConstants.BundleKey.EXTRA, bean);
                bundle.putInt(AudioConstants.BundleKey.MusicType, AudioConstants.MusicType.USB);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(new DispatcherBean(PORT_USB_AUDIO, AudioConstants.Action.PLAY, bundle, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("setBTAudioSkipTrackStatus play success");
                                    }
                                });
            }
        }));
        itemList.add(new Item("setUSBAudioPlayPause pause").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Play / 0x02: Pause
//                Bundle bundle = new Bundle();
//                bundle.putInt("status", status);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(new DispatcherBean(PORT_USB_AUDIO, AudioConstants.Action.Option.PAUSE, null, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("setUSBAudioPlayPause pause success");
                                    }
                                });
            }
        }));
        // 获取usb音乐播放暂停状态
        itemList.add(new Item("getUSBAudioPlayPause").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Play / 0x02: Pause
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(new DispatcherBean(PORT_USB_AUDIO, AudioConstants.Action.Option.PLAYER_STATUS, null, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle bundle = response.getExtra();
                                        boolean status = bundle.getBoolean("status");
                                        setResult("current USB Audio is playing : " + status);
                                    }
                                });
            }
        }));
        // 设置usb音乐播放模式 todo 接口需调整
        int[] usbAudioModeOne = {0x01, 0x02, 0x03, 0x04};
        int[] usbAudioModeTwo = {0x01, 0x02};
        String[] usbAudioModeDescOne = {"随机播放", "顺序播放", "单曲循环", "列表循环"};
        String[] usbAudioModeDescTwo = {"单曲循环", "列表循环"};
        for (int i = 0; i < usbAudioModeOne.length; i++) {
            int modeOne = usbAudioModeOne[i];
            String descOne = usbAudioModeDescOne[i];
//            for (int j = 0; j < usbAudioModeTwo.length; j++) {
//                int modeTwo = usbAudioModeTwo[j];
//                String descTwo = usbAudioModeDescTwo[j];
            itemList.add(new Item("setUSBAudioPlayMode" + descOne).setTask(new Task() {
                @Override
                protected void run() {
                    //VAL1:  0x01: 随机播放  0x02: 顺序播放  VAL2: 0x01: 单曲循环 0x02: 列表循环
                    Bundle bundle = new Bundle();
                    bundle.putInt("mode", modeOne);
                    InstructionDispatcher.getInstance()
                            .handleInstructionByRequest(new DispatcherBean(PORT_USB_AUDIO, AudioConstants.Action.Option.SWITCH_PLAY_MODE, bundle, MUSIC),
                                    new ResultCallback() {
                                        @Override
                                        public void callback(Response response) {
                                            setResult("setBTAudioSkipTrackStatus " + descOne + " success");
                                        }
                                    });
                }
            }));
//            }
        }
        // 获取usb音乐播放模式
        itemList.add(new Item("getUSBAudioPlayMode").setTask(new Task() {
            @Override
            protected void run() {
                //VAL1:  0x01: 随机播放  0x02: 顺序播放  VAL2: 0x01: 单曲循环 0x02: 列表循环
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(new DispatcherBean(PORT_USB_AUDIO, AudioConstants.Action.Option.CURRENT_PLAY_MODE, null, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle bundle = response.getExtra();
                                        int value1 = bundle.getInt("mode");
                                        setResult("current USBAudio Play Mode :" + usbAudioModeDescOne[value1 - 1]);
                                    }
                                });
            }
        }));
        // 设置usb音乐播放上一首下一首
        itemList.add(new Item("setUSBAudioSkipTrack next track").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Next track / 0x02: Previous track w
//                Bundle bundle = new Bundle();
//                bundle.putInt("status", skip);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(new DispatcherBean(PORT_USB_AUDIO, AudioConstants.Action.Option.NEXT, null, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("setUSBAudioSkipTrack next track success");
                                    }
                                });
            }
        }));
        itemList.add(new Item("setUSBAudioSkipTrack Previous track").setTask(new Task() {
            @Override
            protected void run() {
                // 0x01: Next track / 0x02: Previous track w
//                Bundle bundle = new Bundle();
//                bundle.putInt("status", skip);
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(new DispatcherBean(PORT_USB_AUDIO, AudioConstants.Action.Option.PREVIOUS, null, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        setResult("setUSBAudioSkipTrack previous track success");
                                    }
                                });
            }
        }));
        itemList.add(new Item("setUSBDesiredFileAndTime").setTask(new Task() {
            @Override
            protected void run() {
                //若设置越界，则跳到下一首进行播放
                Bundle bundle = new Bundle();
                bundle.putInt("seconds", 30000); // 快进到30秒
                bundle.putInt("type", 1); // 1 set  2 get
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(new DispatcherBean(PORT_USB_AUDIO, AudioConstants.Action.Option.SEEK, bundle, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        boolean result = extra.getBoolean("result");
                                        setResult("set current usb audio play position success ? " + result);
                                    }
                                });

            }
        }));

        // 设置音乐播放的分钟/秒钟
        itemList.add(new Item("getUSBCurrentStatus").setTask(new Task() {
            @Override
            protected void run() {
                //若设置越界，则跳到下一首进行播放
                Bundle bundle = new Bundle();
                bundle.getInt("type", 2); // 1 set  2 get
                InstructionDispatcher.getInstance()
                        .handleInstructionByRequest(new DispatcherBean(PORT_USB_AUDIO, AudioConstants.Action.Option.SEEK, bundle, MUSIC),
                                new ResultCallback() {
                                    @Override
                                    public void callback(Response response) {
                                        Bundle extra = response.getExtra();
                                        int milliSeconds = extra.getInt("seconds");
                                        int totalSeconds = milliSeconds / 1000;
                                        int minutes = totalSeconds / 60;
                                        int seconds = totalSeconds % 60;
                                        setResult("get current usb audio position: minutes is " + minutes + ",seconds is " + seconds);
                                    }
                                });
            }
        }));
// TODO: 2019/7/18 0018 释放
        //==========================================================================
//        int[] operations = {1, 2, 4};
//        String[] operationsDesc = {"放大", "缩小", "右旋"};
//        for (int i = 0; i < operations.length; i++) {
//            String desc = operationsDesc[i];
//            int type = operations[i];
//            itemList.add(new Item("USBPictureOperation " + desc).setTask(new Task() {
//                @Override
//                protected void run() {
//                    //0x01:放大 0x02: 缩小 0x03: 左旋(去掉) 0x04: 右旋
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("type", type);
//                    InstructionDispatcher.getInstance()
//                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_PICTURE_OPERATION, bundle));
//                }
//            }));
//        }
//        int[] showType = {1, 3};
//        String[] typeDesc = {"列表", "全屏"};
//        for (int i = 0; i < showType.length; i++) {
//            int type = showType[i];
//            String desc = typeDesc[i];
//            itemList.add(new Item("setUSBPictureDisplayStatus " + desc).setTask(new Task() {
//                @Override
//                protected void run() {
//                    //列表（普通）  0x02: 半屏 （划线 去掉） 0x03: 全屏
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("type", type);
//                    InstructionDispatcher.getInstance()
//                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_PICTURE_SHOW_TYPE, bundle));
//                }
//            }));
//        }
//        itemList.add(new Item("getUSBPictureDisplayStatus").setTask(new Task() {
//            @Override
//            protected void run() {
//                //列表（普通）  0x02: 半屏 （划线 去掉） 0x03: 全屏
//                InstructionDispatcher.getInstance()
//                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_USB_PICTURE_SHOW_TYPE, null));
//            }
//        }));
//
//        int[] skipStatus = {1, 2};
//        String[] skipDesc = {"Next pic", "Previous pic"};
//        for (int i = 0; i < skipStatus.length; i++) {
//            int type = skipStatus[i];
//            String desc = skipDesc[i];
//            itemList.add(new Item("setUSBPictureSkipStatus " + desc).setTask(new Task() {
//                @Override
//                protected void run() {
//                    //0x01: Next
//                    //0x02: Previous
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("type", type);
//                    InstructionDispatcher.getInstance()
//                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_PIC_PREVIOUS_NEXT, bundle));
//                }
//            }));
//        }
//        // 设置usb音乐播放暂停
//        int[] usbvideo = {1, 2};
//        String[] usbVideoDesc = {"Play", "Pause"};
//        for (int i = 0; i < usbvideo.length; i++) {
//            int status = usbvideo[i];
//            String desc = usbVideoDesc[i];
//            itemList.add(new Item("setUSBVideoPlayPauseStatus " + desc).setTask(new Task() {
//                @Override
//                protected void run() {
//                    // 0x01: Play // 0x02: Pause
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("status", status);
//                    InstructionDispatcher.getInstance()
//                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_VIDEO_PAUSE_PLAY, bundle));
//                }
//            }));
//        }
//        itemList.add(new Item("getUSBVideoPlayPauseStatus").setTask(new Task() {
//            @Override
//            protected void run() {
//                //0x01: Play
//                //0x02: Pause
//                InstructionDispatcher.getInstance()
//                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_USB_VIDEO_PAUSE_PLAY, new Bundle()));
//            }
//        }));
//
//        int[] usbVideoSkip = {1, 2};
//        String[] usbVideoSkipDesc = {"next track", "previous track"};
//        for (int i = 0; i < usbVideoSkip.length; i++) {
//            int status = usbVideoSkip[i];
//            String desc = usbVideoSkipDesc[i];
//            itemList.add(new Item("setUSBVideoSkip " + desc).setTask(new Task() {
//                @Override
//                protected void run() {
//                    //0x01: Next track //0x02: Previous track
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("status", status);
//                    Bundle result = InstructionDispatcher.getInstance()
//                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_USB_VIDEO_PREVIOUS_NEXT, bundle));
//                }
//            }));
//        }
        //==============================================================================
//        itemList.add(new Item("getTunerCurrentStatus").setTask(new Task() {
//            @Override
//            protected void run() { // 获取当前电台状态
//                // DB1~DB7
//                //DB1:band information
//                //DB2:Tuner signal strength （划线 去掉）
//                //DB3:0x00
//                //DB4~DB7:frequency information
//                InstructionDispatcher.getInstance()
//                        .handleInstructionByRequest(
//                                new DispatcherBean(PORT_EOL_XTING, CenterConstants.ACTION_GET_CURRENT_TUNER_STATE, null, XTING),
//                                new ResultCallback() {
//                                    @Override
//                                    public void callback(Response response) {
//                                        Bundle extra = response.getExtra();
//                                        int band = extra.getInt("band");
//                                        int signal = extra.getInt("signal");
//                                        int frequency = extra.getInt("frequency");
//                                        setResult("result :band = " + band + ",signal = " + signal + ",frequency = " + frequency);
//                                    }
//                                });
//            }
//        }));
//        itemList.add(new Item("setTunerFrequencyDB1").setTask(new Task() {
//            @Override
//            protected void run() { // 设置电台到指定频率
//                // DB1~DB2
//                Bundle bundle = new Bundle();
//                bundle.putInt("frequency", 77);
//                InstructionDispatcher.getInstance()
//                        .handleInstructionByRequest(
//                                new DispatcherBean(PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_FREQUENCY, bundle, XTING),
//                                new ResultCallback() {
//                                    @Override
//                                    public void callback(Response response) {
//                                        setResult("write tuner frequency db1 success");
//                                    }
//                                });
//            }
//        }));
//        itemList.add(new Item("setTunerFrequencyDB2").setTask(new Task() {
//            @Override
//            protected void run() { // 设置电台到指定频率
//                // DB1~DB2
//                Bundle bundle = new Bundle();
//                bundle.putInt("frequency", 150);
//                InstructionDispatcher.getInstance()
//                        .handleInstructionByRequest(
//                                new DispatcherBean(PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_FREQUENCY, bundle, XTING),
//                                new ResultCallback() {
//                                    @Override
//                                    public void callback(Response response) {
//                                        setResult("write tuner frequency db2 success");
//                                    }
//                                });
//            }
//        }));
//        itemList.add(new Item("setTunerBandFM").setTask(new Task() {
//            @Override
//            protected void run() { // 设置电台切换AM/FM
//                //0x01: FM
//                //0x02: AM
//                Bundle bundle = new Bundle();
//                bundle.putInt("band", 0x01);
//                InstructionDispatcher.getInstance()
//                        .handleInstructionByRequest(
//                                new DispatcherBean(PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_BAND, bundle, XTING),
//                                new ResultCallback() {
//                                    @Override
//                                    public void callback(Response response) {
//                                        setResult("write tuner band FM success");
//                                    }
//                                });
//            }
//        }));
//        itemList.add(new Item("setTunerBandAM").setTask(new Task() {
//            @Override
//            protected void run() { // 设置电台切换AM/FM
//                //0x01: FM
//                //0x02: AM
//                Bundle bundle = new Bundle();
//                bundle.putInt("band", 0x02);
//                InstructionDispatcher.getInstance()
//                        .handleInstructionByRequest(
//                                new DispatcherBean(PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_BAND, bundle, XTING),
//                                new ResultCallback() {
//                                    @Override
//                                    public void callback(Response response) {
//                                        setResult("write tuner band AM success");
//                                    }
//                                });
//            }
//        }));
//        int[] tunerFavorite = {0xFF, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12};
//        for (int i = 0; i < tunerFavorite.length; i++) {
//            int favorite = tunerFavorite[i];
//            itemList.add(new Item("setTunerFavorite" + favorite).setTask(new Task() {
//                @Override
//                protected void run() { // 设置电台收藏
//                    //0xFF: No Favorite
//                    //0x01~0x12: Favorite 1~18
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("favorite", favorite);
//                    InstructionDispatcher.getInstance()
//                            .handleInstructionByRequest(
//                                    new DispatcherBean(PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_FAVORITE, bundle, XTING),
//                                    new ResultCallback() {
//                                        @Override
//                                        public void callback(Response response) {
//                                            setResult("write tuner favorite = " + favorite + " success");
//                                        }
//                                    });
//                }
//            }));
//        }
//        int[] tunerSeek = {0x01, 0x02, 0x03, 0x04};
//        for (int i = 0; i < tunerSeek.length; i++) {
//            int seek = tunerSeek[i];
//            itemList.add(new Item("setTunerSeek" + seek).setTask(new Task() {
//                @Override
//                protected void run() { // 设置电台搜索上一个/下一个电台 设置电台到上一个/下一个频率
//                    //0x01: auto seek up
//                    //0x02: auto seek down
//                    //0x03: manually tune up
//                    //0x04: manually tune down
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("seek", seek);
//                    InstructionDispatcher.getInstance()
//                            .handleInstructionByRequest(
//                                    new DispatcherBean(PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_SEEK, bundle, XTING),
//                                    new ResultCallback() {
//                                        @Override
//                                        public void callback(Response response) {
//                                            setResult("write tuner seek = " + seek + " success");
//                                        }
//                                    });
//                }
//            }));
//        }
//        int[] tunerAutoStore = {0x01, 0x02};
//        for (int i = 0; i < tunerAutoStore.length; i++) {
//            int autoStore = tunerAutoStore[i];
//            itemList.add(new Item("setTunerAutoStore" + autoStore).setTask(new Task() {
//                @Override
//                protected void run() { // 设置电台自动搜台(开始/停止)
//                    //0x01: Stop
//                    //0x02: Start
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("autoStore", autoStore);
//                    InstructionDispatcher.getInstance()
//                            .handleInstructionByRequest(
//                                    new DispatcherBean(PORT_EOL_XTING, CenterConstants.ACTION_SET_TUNER_AUTO_STORE, bundle, XTING),
//                                    new ResultCallback() {
//                                        @Override
//                                        public void callback(Response response) {
//                                            setResult("write tuner auto store = " + autoStore + " success");
//                                        }
//                                    });
//                }
//            }));
//        }
        int[] tftIlluminationStatus = {0x01, 0x02};
        for (int i = 0; i < tftIlluminationStatus.length; i++) {
            int status = tftIlluminationStatus[i];
            itemList.add(new Item("setTFTIlluminationOnOff" + status).setTask(new Task() {
                @Override
                protected void run() {
                    // 0001 params 0x01: Off / 0x02: On
                    Bundle bundle = new Bundle();
                    bundle.putInt("tftIlluminationStatus", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_TFT_ILLUMINATION, bundle));
                    setResult(bundle.getInt("tftIlluminationStatus", -1) + "");
                }
            }));
        }
        itemList.add(new Item("getTFTIlluminationOnOff").setTask(new Task() {
            @Override
            protected void run() {
                // 0001 params 0x01: Off / 0x02: On
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_TFT_ILLUMINATION, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));
        int[] tftDisplayPattern = {0xFF, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F};
        for (int i = 0; i < tftDisplayPattern.length; i++) {
            int status = tftDisplayPattern[i];
            itemList.add(new Item("setTFTDisplayPattern" + status).setTask(new Task() {
                @Override
                protected void run() {
                    //0xFF: back to home page
                    //0x01: White Screen
                    //0x02: Black Screen
                    //0x03: Red Screen
                    //0x04: Green Screen
                    //0x05: Blue Screen
                    //0x06: 8 Color bar Screen
                    //0x07: Big chess Screen
                    //0x08: fish screen
                    //0x09: Gray black flicker  Screen
                    //0x0A: H Gray Scale Screen
                    //0x0B: Mid Grey Screen
                    //0x0C: V BW Screen
                    //0x0D: V Gray Scale Screen
                    //0x0E: reserved
                    //0x0F: reserved
                    Bundle bundle = new Bundle();
                    bundle.putInt("status", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_TFT_DISPLAY_PATTERN, bundle));
                    setResult(bundle.getInt("status", -1) + "");
                }
            }));
        }
        itemList.add(new Item("getTFTDisplayPattern").setTask(new Task() {
            @Override
            protected void run() {
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_TFT_DISPLAY_PATTERN, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));
        int[] testScreenIllumination = {0x01, 0x02, 0x03};
        int[] testScreenLevel = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A};
        for (int i = 0; i < testScreenIllumination.length; i++) {
            for (int j = 0; j < testScreenLevel.length; j++) {
                int status = testScreenIllumination[i];
                int level = testScreenLevel[j];
                itemList.add(new Item("setTestScreenIllumination" + status + "亮度：" + level).setTask(new Task() {
                    @Override
                    protected void run() {
                        // 0001 params 0x01: Off / 0x02: On
                        Bundle bundle = new Bundle();
                        bundle.putInt("testScreenIllumination", status);
                        bundle.putInt("level", level);
                        Bundle result = InstructionDispatcher.getInstance()
                                .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_TEST_SCREEN_ILLUMINATION, bundle));
                        setResult(bundle.getInt("level", -1) + "");
                    }
                }));
            }
        }
        itemList.add(new Item("getTestScreenIllumination").setTask(new Task() {
            @Override
            protected void run() {
                // 0001 params 0x01: Off / 0x02: On
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_TEST_SCREEN_ILLUMINATION, bundle));
                setResult("模式：" + result.getInt("mode", -1) + ",亮度值：" +
                        result.getInt("level", -1));
            }
        }));
        int[] testMFDIllumination = {0x01, 0x02, 0x03};
        int[] MFDIlluminationLevel = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A};
        for (int i = 0; i < testMFDIllumination.length; i++) {
            for (int j = 0; j < MFDIlluminationLevel.length; j++) {
                int mode = testMFDIllumination[i];
                int level = MFDIlluminationLevel[j];
                itemList.add(new Item("setTestMFDIllumination" + mode + " 按键亮度：" + level).setTask(new Task() {
                    @Override
                    protected void run() {
                        //DB1:
                        //0X01:Auto mode
                        //0x02:Day mode
                        //0x03:Night mode
                        //DB2:
                        //0x01~0x0A
                        Bundle bundle = new Bundle();
                        bundle.putInt("mode", mode);
                        bundle.putInt("level", level);
                        Bundle result = InstructionDispatcher.getInstance()
                                .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_TEST_MFD_ILLUMINATION, bundle));
                        setResult(bundle.getInt("level", -1) + "");
                    }
                }));
            }
        }
        itemList.add(new Item("setTestMFDIllumination").setTask(new Task() {
            @Override
            protected void run() {
                // 0001 params 0x01: Off / 0x02: On
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_TEST_MFD_ILLUMINATION, bundle));
                setResult("模式：" + result.getInt("mode", -1) + ",按键亮度值：" +
                        result.getInt("level", -1));
            }
        }));
        int[] LCDLVDSOutputOnOff = {0x01, 0x02};
        for (int i = 0; i < LCDLVDSOutputOnOff.length; i++) {
            int status = LCDLVDSOutputOnOff[i];
            itemList.add(new Item("setLCDLVDSOutputOnOff" + status).setTask(new Task() {
                @Override
                protected void run() {
                    //0x01: Off
                    //0x02: On
                    Bundle bundle = new Bundle();
                    bundle.putInt("status", status);
                    Bundle result = InstructionDispatcher.getInstance()
                            .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_SET_LCD_LVDS_OUTPUT, bundle));
                    setResult(bundle.getInt("status", -1) + "");

                }
            }));
        }
        itemList.add(new Item("getLCDLVDSOutputOnOff").setTask(new Task() {
            @Override
            protected void run() {
                //0x01: Off
                //0x02: On
                Bundle bundle = new Bundle();
                Bundle result = InstructionDispatcher.getInstance()
                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_LCD_LVDS_OUTPUT, bundle));
                setResult(result.getInt("result", -1) + "");
            }
        }));
        boolean lvdISOnT = true;
        itemList.add(new Item("setLVDOutputOnOff").setTask(new Task() {
            @Override
            protected void run() {
                //0x01: Off
                //0x02: On
//                Bundle bundle = new Bundle();
//                Bundle result = InstructionDispatcher.getInstance()
//                        .handleInstruction(new DispatcherBean(DistributeConstants.ACTION_GET_LCD_LVDS_OUTPUT, bundle));
//                setResult(result.getBoolean("result", false) + "");

//                if (lvdISOnT) {
//
//                }else{
//
//                }

            }
        }));
    }

    @Subscriber(tag = "usb_pic_operation")
    public void onUsbPicOperation(Bundle result) {
        int type = result.getInt("type");
        String[] descs = {"放大", "缩小", "", "右旋"};
        setResult("usb_pic_operation success ,type is :" + descs[type - 1]);
    }

    @Subscriber(tag = "usb_pic_show_type")
    public void onUsbPicShowType(Bundle data) {
        int result = data.getInt("result");
        String[] showTypes = {"列表", "半屏", "全屏"};
        setResult("usb_pic_show_type success show type is " + showTypes[result - 1]);
    }

    @Subscriber(tag = "usb_pic_next_or_previous")
    public void onUsbPicPreviousNext(Bundle result) {
        int type = result.getInt("type");
        String[] operation = {"next", "previous"};
        setResult("usb_pic_next_or_previous success,operation is " + operation[type - 1]);
    }

    @Subscriber(tag = "usb_video_play_pause")
    public void onUsbVideoPlayPause(Bundle result) {
        int status = result.getInt("status");
        String[] statusDesc = {"play", "pause"};
        setResult("usb_video_play_pause success operation is " + statusDesc[status - 1]);
    }

    @Subscriber(tag = "usb_video_previous_or_next")
    public void onUsbVideoPreviousOrNext(Bundle result) {
        int type = result.getInt("type");
        String[] operation = {"next", "previous"};
        setResult("usb_video_previous_or_next success ,operation is " + operation[type - 1]);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
