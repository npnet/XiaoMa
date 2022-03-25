package com.xiaoma.instructiondistribute.utils.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAvrcpController;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothMapClient;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.instructiondistribute.utils.DistributeConstants;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;


public class BluetoothController implements BluetoothControllerAgentInterface {
    private static String TAG = BluetoothController.class.getSimpleName();
    private Context context;
    private BluetoothAdapter bltAdapter;
    private BluetoothA2dpSink a2DpSink;
    private BluetoothHeadsetClient hfpClient;
    private BluetoothAvrcpController avrcp;
    private BluetoothPbapClient pbapClient;
    private BluetoothMapClient mapClient;
    private BluetoothProfile.ServiceListener bluetoothProfileListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP_SINK) {
                a2DpSink = (BluetoothA2dpSink) proxy;
            } else if (profile == BluetoothProfile.HEADSET_CLIENT) {
                hfpClient = (BluetoothHeadsetClient) proxy;
            } else if (profile == BluetoothProfile.AVRCP_CONTROLLER) {
                avrcp = (BluetoothAvrcpController) proxy;
            } else if (profile == BluetoothProfile.PBAP_CLIENT) {
                pbapClient = (BluetoothPbapClient) proxy;
            } else if (profile == BluetoothProfile.MAP_CLIENT) {
                mapClient = (BluetoothMapClient) proxy;
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothA2dp.A2DP_SINK) {
                a2DpSink = null;
            } else if (profile == BluetoothProfile.HEADSET_CLIENT) {
                hfpClient = null;
            } else if (profile == BluetoothProfile.AVRCP_CONTROLLER) {
                avrcp = null;
            } else if (profile == BluetoothProfile.PBAP_CLIENT) {
                pbapClient = null;
            } else if (profile == BluetoothProfile.MAP_CLIENT) {
                mapClient = null;
            }
        }
    };

    public BluetoothController(Context context) {
        this.context = context;
        bltAdapter = BluetoothAdapterUtils.getBluetoothAdapter(context);
    }

    @Override
    public void onPair(int type, BluetoothDevice device, String pairingKey) {
        switch (type) {
            case DistributeConstants.BT_DEVICE_PAIRING_VARIANT_PIN:
            case DistributeConstants.BT_DEVICE_PAIRING_VARIANT_PIN_16_DIGITS:
                byte[] pinBytes = BluetoothDevice.convertPinToBytes(pairingKey);
                if (pinBytes == null) {
                    return;
                }
                device.setPin(pinBytes);
                break;

            case DistributeConstants.BT_DEVICE_PAIRING_VARIANT_PASSKEY:
                int pass = Integer.parseInt(pairingKey);
                device.setPasskey(pass);
                break;

            case DistributeConstants.BT_DEVICE_PAIRING_VARIANT_PASSKEY_CONFIRMATION:
            case DistributeConstants.BT_DEVICE_PAIRING_VARIANT_CONSENT:
                device.setPairingConfirmation(true);
                break;

            case DistributeConstants.BT_DEVICE_PAIRING_VARIANT_DISPLAY_PASSKEY:
            case DistributeConstants.BT_DEVICE_PAIRING_VARIANT_DISPLAY_PIN:
                // Do nothing.
                break;
            case DistributeConstants.BT_DEVICE_PAIRING_VARIANT_OOB_CONSENT:
                device.setRemoteOutOfBandData();
                break;
            default:
                KLog.d("Incorrect pairing type received");
                break;
        }
    }

    @Override
    public void canBeDiscovered() {
        bltAdapter.setDiscoverableTimeout(1000);
        bltAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 1);
    }

    @Override
    public void unableDiscovered() {
        bltAdapter.setDiscoverableTimeout(1);
        bltAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE, 1);
    }

    @Override
    public void removeBond(BluetoothDevice device) {
        device.removeBond();
    }

    @Override
    public void connect(BluetoothDevice device) {
        identifyTypeAndConnect(device);
    }

    @Override
    public void initProfile(Context context) {
        if (hfpClient == null)
            bltAdapter.getProfileProxy(context, bluetoothProfileListener, BluetoothA2dp.HEADSET_CLIENT);
//        if (avrcp == null)
//            bltAdapter.getProfileProxy(context, bluetoothProfileListener, BluetoothA2dp.AVRCP_CONTROLLER);
        if (a2DpSink == null)
            bltAdapter.getProfileProxy(context, bluetoothProfileListener, BluetoothA2dp.A2DP_SINK);
        if (pbapClient == null)
            bltAdapter.getProfileProxy(context, bluetoothProfileListener, BluetoothPbapClient.PBAP_CLIENT);
        if (mapClient == null)
            bltAdapter.getProfileProxy(context, bluetoothProfileListener, BluetoothPbapClient.MAP_CLIENT);
    }

    private void identifyTypeAndConnect(BluetoothDevice device) {
        if (device == null) return;
        int deviceClass = device.getBluetoothClass().getDeviceClass();
        final int deviceClassMasked = deviceClass & 0x1F00;

        if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES) {
            //耳机
            hfpConnect(device);
        } else if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE) {
            //麦克风
        } else if (deviceClassMasked == BluetoothClass.Device.Major.COMPUTER) {
            //电脑
//            hfpConnect(device);
            a2DpSinkConnect(device);
        } else if (deviceClassMasked == BluetoothClass.Device.Major.PHONE) {
            //手机
            hfpConnect(device);
            a2DpSinkConnect(device);
            pbapClientConnect(device);
//            mapClientConnect(device);
//            headsetConnect(device);
        } else if (deviceClassMasked == BluetoothClass.Device.Major.HEALTH) {
            //健康类设备
        } else {
            //蓝牙：比如蓝牙音响。
            hfpConnect(device);
        }
    }

    private void pbapClientConnect(BluetoothDevice device) {
        if (pbapClient != null) {
            if (pbapClient.getPriority(device) > BluetoothProfile.PRIORITY_ON) {
                pbapClient.setPriority(device, BluetoothProfile.PRIORITY_ON);
            }
            boolean connect = pbapClient.connect(device);
            KLog.d("hzx", "pbapClient connect state: " + connect + ", device name: " + device.getName());
        }
    }

    private void a2DpSinkConnect(BluetoothDevice device) {
        if (a2DpSink != null) {
            if (a2DpSink.getPriority(device) > BluetoothProfile.PRIORITY_ON) {
                a2DpSink.setPriority(device, BluetoothProfile.PRIORITY_ON);
            }
            boolean connect = a2DpSink.connect(device);
            int connectionState = a2DpSink.getConnectionState(device);
            KLog.d("hzx", "a2DpSink connection,connectionState= " + connect + ", device name : " + device.getName());
        }
    }

    private void hfpConnect(BluetoothDevice device) {
        if (hfpClient != null) {
            if (hfpClient.getPriority(device) > BluetoothProfile.PRIORITY_ON) {
                hfpClient.setPriority(device, BluetoothProfile.PRIORITY_ON);
            }
            boolean connect = hfpClient.connect(device);
            int connectionState = hfpClient.getConnectionState(device);
            boolean isChecked = hfpClient.connectAudio(device);
            KLog.d("hzx", "hfpClient connect status: " + connect + ", device name : " + device.getName());
        }
    }

    private void mapClientConnect(BluetoothDevice device) {
        if (mapClient != null) {
            if (mapClient.getPriority(device) > BluetoothProfile.PRIORITY_ON) {
                mapClient.setPriority(device, BluetoothProfile.PRIORITY_ON);
            }
            boolean connect = mapClient.connect(device);
            KLog.d("hzx", "mapClient connect state: " + connect);
        }
    }

    @Override
    public void disconnect(BluetoothDevice device) {
        if (hfpClient != null) {
            hfpClient.disconnectAudio(device);
            hfpClient.disconnect(device);
            KLog.d("hzx","disconnect方法调用, hfp断开: " + device.getName());
        } else {
            KLog.d("hfpClient is null");
        }

        if (a2DpSink != null) {
            a2DpSink.disconnect(device);
            KLog.d("hzx","disconnect方法调用, a2dp断开: " + device.getName());
        } else {
            KLog.d("a2Dpsink is null");
        }
        if (pbapClient != null) {
            boolean disconnect = pbapClient.disconnect(device);
            KLog.d("hzx", "pbap disconnect: " + disconnect);
        }
        if (mapClient != null) {
            boolean disconnect = mapClient.disconnect(device);
            KLog.d("hzx", "mapClient disconnect: " + disconnect);
        }
       /* if (avrcp != null) {
            boolean disconnect = avrcp.disconnect(device);
            KLog.d("hzx", "avrcp controller disconnect: " + disconnect);
        }*/
    }

    /**
     *  断开目前已经连接的蓝牙设备
     * @param aimDevice 当前新加入准备连接的设备;
     * @return 是否断开了其他设备的连接
     */
    @Override
    public boolean disconnectOtherDevice(BluetoothDevice aimDevice) {
        boolean isDisconnectDevice = false;
        if (a2DpSink == null || hfpClient == null) return false;
        List<BluetoothDevice> connectedA2dpDevices = a2DpSink.getConnectedDevices();
        List<BluetoothDevice> connectedHfpDevices = hfpClient.getConnectedDevices();
        KLog.d("hzx", "a2DpSink size: " + connectedA2dpDevices.size() + ", hfpClient size: " + connectedHfpDevices.size());
        if (connectedA2dpDevices != null && connectedA2dpDevices.size() != 0) {
            for (BluetoothDevice device : connectedA2dpDevices) {
                if (aimDevice != null && TextUtils.equals(aimDevice.getAddress(), device.getAddress())) {
                    continue;
                }
                isDisconnectDevice = a2DpSink.disconnect(device);
                KLog.d("hzx", "a2dp 断开连接: " + device.getName() + ", 断开结果: " + isDisconnectDevice);
            }
        }
        if (connectedHfpDevices != null && connectedHfpDevices.size() != 0) {
            for (BluetoothDevice device : connectedHfpDevices) {
                if (aimDevice != null && TextUtils.equals(aimDevice.getAddress(), device.getAddress())) {
                    continue;
                }
                boolean disconnect = hfpClient.disconnect(device);
                KLog.d("hzx", "hfp 断开连接: " + device.getName() + ", 断开结果: " + disconnect);
                if (!isDisconnectDevice) {
                    isDisconnectDevice = disconnect;
                }
            }
        }
        if (pbapClient != null) {
            List<BluetoothDevice> connectedPbapClientDevices = pbapClient.getConnectedDevices();
            if (connectedPbapClientDevices != null && connectedPbapClientDevices.size() != 0)
                for (BluetoothDevice device : connectedPbapClientDevices) {
                    pbapClient.disconnect(device);
                    KLog.d("hzx", "pbap 断开连接: " + device.getName());
                }
        }

        if (mapClient != null) {
            List<BluetoothDevice> connectedPbapClientDevices = mapClient.getConnectedDevices();
            if (connectedPbapClientDevices != null && connectedPbapClientDevices.size() != 0) {
                for (BluetoothDevice device : connectedPbapClientDevices) {
                    mapClient.disconnect(device);
                    KLog.d("hzx", "map 断开连接: " + device.getName());
                }
            }
        }

        if (avrcp != null) {
            //新版android.jar编译不过
           /* List<BluetoothDevice> connectedArvcDevices = avrcp.getConnectedDevices();
            if (connectedArvcDevices != null && connectedA2dpDevices.size() != 0) {
                for (BluetoothDevice device : connectedA2dpDevices) {
                    avrcp.disconnect(device);
                }
            }*/
        }
        return isDisconnectDevice;
    }

    @Override
    public void onDestroy() {
        bltAdapter.closeProfileProxy(BluetoothProfile.A2DP_SINK, a2DpSink);
        bltAdapter.closeProfileProxy(BluetoothProfile.HEADSET_CLIENT, hfpClient);
        bltAdapter.closeProfileProxy(BluetoothProfile.AVRCP_CONTROLLER, avrcp);
        bltAdapter.closeProfileProxy(BluetoothProfile.PBAP_CLIENT, pbapClient);
        bltAdapter.closeProfileProxy(BluetoothProfile.MAP_CLIENT, mapClient);
    }

    @Override
    public void filterConnection(BluetoothDevice device, int profile) {
        if (ifDisconnectProfile(device)) {
            switch (profile) {
                case BluetoothProfile.PBAP_CLIENT:
                    if (pbapClient != null) {
                        pbapClient.disconnect(device);
                    }
                    break;
                case BluetoothProfile.MAP_CLIENT:
                    if (mapClient != null) {
                        mapClient.disconnect(device);
                    }
                    break;
            }
        }
    }

    private boolean ifDisconnectProfile(BluetoothDevice device) {

        List<BluetoothDevice> connectedDevices = new ArrayList<>();
        if (a2DpSink != null) {
            connectedDevices = a2DpSink.getConnectedDevices();
        }

        if (connectedDevices.size() == 0) {
            return false;
        }
        if (!connectedDevices.contains(device)) {
            return true;
        }
        return false;
    }

    /**
     * 目前的处理逻辑是,车机之前连接上蓝牙,不管连接了多少profile,不允许后面其他手机发起的连接请求,连接成功也会进行断开;
     *
     * @param device
     * @return 是否是允许设备
     */
    @Override
    public boolean filterConnection(BluetoothDevice device) {
        /*List<BluetoothDevice> a2DpSinkConnectedDevices = new ArrayList<>();
        if (a2DpSink != null) {
            a2DpSinkConnectedDevices = a2DpSink.getConnectedDevices();
        }
        List<BluetoothDevice> hfpConnectedDevices = new ArrayList<>();
        if (hfpClient != null) {
            hfpConnectedDevices = hfpClient.getConnectedDevices();
        }
        if (a2DpSinkConnectedDevices.size() == 0) { //a2Dp没有连接上的时候,断开其他连接上的hfp;
            if (hfpConnectedDevices.size() != 0) {
                for (BluetoothDevice btDevice : hfpConnectedDevices) {
                    if (TextUtils.equals(btDevice.getAddress(), device.getAddress())) continue;
                    boolean disconnect = hfpClient.disconnect(btDevice);
                    KLog.d("hzx", "filter 中断开连接: " + btDevice.getName() + ", disconnect result: " + disconnect);
                    if (mapClient != null) {
                        mapClient.disconnect(btDevice);
                    }
                    if (pbapClient != null) {
                        pbapClient.disconnect(btDevice);
                    }
                }
            }
        } else {
            //a2Dp连接上的时候,断开所有非a2Dp连接上的设备的其他设备,防止多连接(多连接1: 一个蓝牙profile连接上多台设备;多连接2: hfp连接上设备a, a2dp连接上设备b);
            if (a2DpSinkConnectedDevices.contains(device)) {
                for (BluetoothDevice btDevice : a2DpSinkConnectedDevices) {
                    if (TextUtils.equals(btDevice.getAddress(), device.getAddress())) {
                        continue;
                    }
                    a2DpSink.disconnect(btDevice);
                    if (mapClient != null) {
                        mapClient.disconnect(btDevice);
                    }
                    if (pbapClient != null) {
                        pbapClient.disconnect(btDevice);
                    }
                }
                if (hfpConnectedDevices.size() != 0) {
                    for (BluetoothDevice btDevice : hfpConnectedDevices) {
                        if (TextUtils.equals(btDevice.getAddress(), device.getAddress())) {
                            continue;
                        }
                        hfpClient.disconnect(btDevice);
                        if (mapClient != null) {
                            mapClient.disconnect(btDevice);
                        }
                        if (pbapClient != null) {
                            pbapClient.disconnect(btDevice);
                        }
                    }
                }
            } else {
                if (hfpConnectedDevices.size() != 0) {
                    BluetoothDevice bluetoothDevice = a2DpSinkConnectedDevices.get(0);
                    for (BluetoothDevice btDevice : hfpConnectedDevices) {
                        if (TextUtils.equals(bluetoothDevice.getAddress(), btDevice.getAddress())) {
                            continue;
                        }
                        hfpClient.disconnect(btDevice);
                        if (mapClient != null) {
                            mapClient.disconnect(btDevice);
                        }
                        if (pbapClient != null) {
                            pbapClient.disconnect(btDevice);
                        }
                    }
                }
                hfpClient.connect(a2DpSinkConnectedDevices.get(0));
            }

        }*/
        List<BluetoothDevice> a2DpSinkConnectedDevices = new ArrayList<>();
        if (a2DpSink != null) {
            a2DpSinkConnectedDevices = a2DpSink.getConnectedDevices();
        }
        List<BluetoothDevice> hfpConnectedDevices = new ArrayList<>();
        if (hfpClient != null) {
            hfpConnectedDevices = hfpClient.getConnectedDevices();
        }
        if (!hfpConnectedDevices.isEmpty()) {
            // 目前不存在多连接,可以这样写
            if (!hfpConnectedDevices.get(0).getAddress().equals(device.getAddress())) { // 当前连接的设备和已经连接的设备不属于同一设备
                if (a2DpSink != null) {
                    boolean disconnect = a2DpSink.disconnect(device);
                    KLog.d("hzx", "filterConnection中断开蓝牙a2dp: " + device.getName() + ", 断开结果: " + disconnect);
                }
                if (hfpClient != null) {
                    boolean disconnect = hfpClient.disconnect(device);
                    KLog.d("hzx", "filterConnection中断开蓝牙a2dp: " + device.getName() + ", 断开结果: " + disconnect);
                }
                return false;
            }
        }
        if (!a2DpSinkConnectedDevices.isEmpty()) {
            if (!a2DpSinkConnectedDevices.get(0).getAddress().equals(device.getAddress())) {
                if (a2DpSink != null) {
                    boolean disconnect = a2DpSink.disconnect(device);
                    KLog.d("hzx", "filterConnection中断开蓝牙a2dp: " + device.getName() + ", 断开结果: " + disconnect);
                }
                if (hfpClient != null) {
                    boolean disconnect = hfpClient.disconnect(device);
                    KLog.d("hzx", "filterConnection中断开蓝牙a2dp: " + device.getName() + ", 断开结果: " + disconnect);
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isBluetoothConnected(BluetoothDevice device) {
        if (a2DpSink != null && hfpClient != null) {
            List<BluetoothDevice> connectedA2dpSinkDevices = a2DpSink.getConnectedDevices();
            String name = null;
            for (BluetoothDevice a2dpDevice : connectedA2dpSinkDevices) {
                name = ", " + a2dpDevice.getName();
            }
            if (!TextUtils.isEmpty(name)) {
                KLog.d("hzx", "已连接的a2dp: " + name);
            }
            List<BluetoothDevice> connectedHfpClientDevices = hfpClient.getConnectedDevices();
            name = null;
            for (BluetoothDevice hfpDevice : connectedHfpClientDevices) {
                name = ", " + hfpDevice.getName();
            }
            KLog.d("hzx", "已连接的hfp: " + name);
            KLog.d(TAG, "A2DP_SINK device num: " + connectedA2dpSinkDevices.size() + ", HFP device num: " + connectedHfpClientDevices.size());
            if (connectedA2dpSinkDevices.contains(device) || connectedHfpClientDevices.contains(device)) {
                return true;
            } else {
                /*if (pbapClient != null) {
                    pbapClient.disconnect(device);
                }
                if (mapClient != null) {
                    mapClient.disconnect(device);
                }*/
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean isDeviceDisconnected(BluetoothDevice device) {
        if (a2DpSink != null && hfpClient != null) {
            List<BluetoothDevice> connectedA2dpSinkDevices = a2DpSink.getConnectedDevices();
            List<BluetoothDevice> connectedHfpClientDevices = hfpClient.getConnectedDevices();
            return !connectedA2dpSinkDevices.contains(device) && !connectedHfpClientDevices.contains(device);
        }
        return false;
    }
}


