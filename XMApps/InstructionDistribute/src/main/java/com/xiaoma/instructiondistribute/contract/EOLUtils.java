package com.xiaoma.instructiondistribute.contract;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.instructiondistribute.utils.bluetooth.BluetoothAdapterUtils;
import com.xiaoma.instructiondistribute.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

//import android.car.eol.EolCmd;
//import com.xiaoma.instructiondistribute.eol.EOLFacade;

/**
 * Author: loren
 * Date: 2019/8/18
 */
public class EOLUtils {

    public static final String TAG = "EOL_JUDGE";

    public static final int STATE_ON = 0x02;
    public static final int STATE_OFF = 0x01;
    private static final int REQ_WRITE = 2;

    private EOLUtils() {
        throw new UnsupportedOperationException("not allowed!");
    }

    public static boolean isRegister(String methodInfo, boolean isMock) {
        if (isMock) {
            Log.i(TAG, String.format("【%1$s】 mock OK", methodInfo));
            return true;
        } else {
            boolean activated = true;
            if (activated) {
                Log.i(TAG, String.format("【%1$s】 register OK", methodInfo));
            } else {
                Log.e(TAG, String.format("【%1$s】 register Fail", methodInfo));
            }
            return activated;
        }
    }

    public static void printResponse(String response) {
        Log.w(TAG, String.format("Response [%1$s] %2$s", getMethodName(), response));
    }

    public static boolean isActionSet(int rw) {
        return rw == REQ_WRITE;
    }

    // TODO: 2019/8/18 用于判断USB是否插入
    public static boolean isUSBMounted() {
        UsbStatus status = UsbMediaDataManager.getInstance().getCurrentUsbStatus();
        if (status == UsbStatus.MOUNTED) {
            return true;
        }
        return false;
    }

    public static String convertMacAddr(int[] mac) {
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

    public static int[] convertMacAddr(String mac) {
        if (TextUtils.isEmpty(mac) || !mac.contains(":")) return new int[0];
        String[] split = mac.split(":");
        int[] macArr = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            macArr[i] = Integer.parseInt(split[i], 16);
        }
        return macArr;
    }

    public static String getMethodName() {
        return new Exception().getStackTrace()[1].getMethodName();
    }

    public static ArrayList<Integer> getEQValue() {
        int effectsMode = XmCarVendorExtensionManager.getInstance().getSoundEffectsMode();
        return (ArrayList<Integer>) XmCarVendorExtensionManager.getInstance().getCurrentSoundEffects(effectsMode);
    }

    public static void setEQValue(Integer[] soundEffects) {
        XmCarVendorExtensionManager.getInstance().setCustomSoundEffects(soundEffects);
    }

    public static boolean setBluetoothStatus(int setState) {
        BluetoothAdapter adapter;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            final BluetoothManager bluetoothManager = (BluetoothManager)
                    AppHolder.getInstance().getAppContext().getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        if (setState == STATE_ON) {
            return adapter.enable();
        } else {
            return adapter.disable();
        }
    }

    public static int getBluetoothStatus() {
        BluetoothAdapter adapter;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            final BluetoothManager bluetoothManager = (BluetoothManager)
                    AppHolder.getInstance().getAppContext().getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        if (adapter.isEnabled()) {
            return STATE_ON;
        }
        return STATE_OFF;
    }

    public static String readBtAddress() {
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

    public static boolean writeBtAddress(int[] address) {
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

    public static int getBTPairMode() {
        int scanMode = BluetoothAdapterUtils
                .getBluetoothAdapter(AppHolder.getInstance().getAppContext()).getScanMode();
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

    public static boolean setBTPairMode(int pairMode) {
        boolean result = false;
        switch (pairMode) {
            case STATE_OFF: // off
                result = disableDiscovered();
                break;
            case STATE_ON: // on
                result = setBtCanBeDiscovered();
                break;
        }
        return result;
    }

    private static boolean disableDiscovered() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter(AppHolder.getInstance().getAppContext());
        bluetoothAdapter.setDiscoverableTimeout(1);
        return bluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_NONE, 1);
    }

    private static boolean setBtCanBeDiscovered() {
        boolean result;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter(AppHolder.getInstance().getAppContext());
        bluetoothAdapter.setDiscoverableTimeout(1000);
        result = bluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 1);
        return result;
    }
}
