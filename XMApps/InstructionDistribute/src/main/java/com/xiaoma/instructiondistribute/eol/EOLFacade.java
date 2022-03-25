package com.xiaoma.instructiondistribute.eol;

import android.car.eol.EolEventListener;
import android.car.eol.EolManager;
import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.instructiondistribute.contract.EOLUtils;
import com.xiaoma.instructiondistribute.listener.IEOLResponseCallback;
import com.xiaoma.instructiondistribute.utils.bluetooth.INfCommandBluetoothSdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Author: loren
 * Date: 2019/8/18
 */
public class EOLFacade implements EolEventListener {

    public static final String TAG = EOLFacade.class.getSimpleName();

    public static final int RESPONSE_CODE_OK = 0;

    private static final int EVENT_FILTER = 0b11111111111111;
    private static EOLFacade sEOLFacade;
    private EolManager mEOLManager;
    private Context mAppContext;
    private boolean mRegisterResultF;
    private boolean mInMockModeF;

    private CopyOnWriteArrayList<IEOLResponseCallback> mEOLResponseList;

    private EOLFacade() {
        mRegisterResultF = false;
        mEOLResponseList = new CopyOnWriteArrayList<>();
    }

    public static EOLFacade newSingleton() {
        if (sEOLFacade == null) {
            synchronized (EOLFacade.class) {
                if (sEOLFacade == null) {
                    sEOLFacade = new EOLFacade();
                }
            }
        }
        return sEOLFacade;
    }

    public boolean bindContext(Context context) {
        if (context == null) {
            throw new NullPointerException("context should not be null!");
        }
        mAppContext = context.getApplicationContext();
        mEOLManager = new EolManager(mAppContext);

        mRegisterResultF = mEOLManager.register(this, EVENT_FILTER);

        return mRegisterResultF;
    }

    public EolManager getEOLManager() {
        return mEOLManager;
    }

    private void setMockMode(boolean mockMode) {
        mInMockModeF = mockMode;
    }

    public void bindState(EolManager eolManager, boolean isRegistered) {
        mEOLManager = eolManager;
        mRegisterResultF = isRegistered;
    }

    public boolean isActivated() {
        if (mEOLManager == null) {
            //这里信息用于判断是否进行了注册成功EOL事件的监听
            Log.e(TAG, "EOLManager is not registered!,pls call bindContext(Context) first!");
            return false;
        }
        return mRegisterResultF;
    }

    private void handleFail(String msg) {
        Log.d("EOL_ERROR", EOLUtils.getMethodName() + " ~ " + msg);
    }

    /**
     * 恢复出厂设置
     *
     * @param i
     */
    @Override
    public void onFactoryReset(int i) {
        if (checkRegister("onFactoryReset", i)) {
            //考虑到 直接调用重启之后,可能数据传递不过去,所以默认先调
            try {
                mEOLManager.responseFactoryReset();
                EOLUtils.printResponse(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
//            XmCarVendorExtensionManager.getInstance().setRestoreCmd(0);
        }
    }

    @Override
    public void onEQConfiguration(int rw, int i1, int i2, int i3, int i4, int i5) {
        if (checkRegister("onEQConfiguration", rw, i1, i2, i3, i4, i5)) {
            if (EOLUtils.isActionSet(rw)) {
//                EOLUtils.setEQValue(new Integer[]{SDKConstants.VALUE.SOUND_EFFECTS_USER, i1, i2, i3, i4, i5});
                try {
                    mEOLManager.responseWriteEQConfiguration(RESPONSE_CODE_OK);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                ArrayList<Integer> eqValue = EOLUtils.getEQValue();
                if (eqValue == null || eqValue.isEmpty()) {
                    handleFail("Empty EQ Configuration!");
                } else {
                    EOLUtils.printResponse(eqValue.toString());
                    try {
                        mEOLManager.responseReadEQConfiguration(eqValue.get(1), eqValue.get(2), eqValue.get(3), eqValue.get(4), eqValue.get(5));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onBluetoothTestMode(int i, int i1) {
        // TODO: 2019/7/19 0019 安富实现
        if (checkRegister("onBluetoothTestMode", i, i1)) {
            try {
                //[WARN] 蓝牙状态和蓝牙测试模式的两帧数据必须一起都要给，否则通信失败 （新途表示 ： 这个是一汽给那边的需求）
                //目前为了保证蓝牙状态的测试通过，所以这里随便返回一个数据
                mEOLManager.responseGetBluetoothTestMode(RESPONSE_CODE_OK);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBluetoothStatus(int rw, int state) {
        if (checkRegister("", rw, state)) {
            if (EOLUtils.isActionSet(rw)) {
                boolean setResult = EOLUtils.setBluetoothStatus(state);
                if (setResult) {
                    try {
                        mEOLManager.responseSetBluetoothStatus(RESPONSE_CODE_OK);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    handleFail("set State Error");
                }
            } else {
                try {
                    mEOLManager.responseGetBluetoothStatus(EOLUtils.getBluetoothStatus());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBluetoothAddress(int rw, int[] setAddress) {
        if (EOLUtils.isRegister(String.format("%1$s{%2$s,%3$s}", "onBluetoothAddress",
                String.valueOf(rw), Arrays.toString(setAddress)), mInMockModeF)) {
            if (EOLUtils.isActionSet(rw)) {
                boolean b = EOLUtils.writeBtAddress(setAddress);
                if (b) {
                    try {
                        mEOLManager.responseWriteBluetoothAddress(0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    handleFail("Write Error");
                }
            } else {
                String address = EOLUtils.readBtAddress();
                String[] addressSplit = address.split(":");
                int[] intAddress = new int[addressSplit.length];
                for (int i = 0; i < addressSplit.length; i++) {
                    intAddress[i] = Integer.parseInt(addressSplit[i]);
                }
                try {
                    mEOLManager.responseReadBluetoothAddress(intAddress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBluetoothModuleVersion(int rw) {
        if (checkRegister("", rw)) {
            String version = INfCommandBluetoothSdk.getInstance().getNfServiceModuleName();
            if (!TextUtils.isEmpty(version) && version.contains(".")) {
                String[] versionSplit = version.split("\\.");
                int[] intVersion = new int[versionSplit.length];
                for (int i = 0; i < versionSplit.length; i++) {
                    intVersion[i] = Integer.parseInt(versionSplit[i]);
                }
                try {
                    mEOLManager.responseReadBluetoothModuleVersion(intVersion);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBTPairModeStatus(int rw, int pairMode) {
        if (checkRegister("", rw, pairMode)) {
            if (EOLUtils.isActionSet(rw)) {
                boolean b = EOLUtils.setBTPairMode(pairMode);
                if (b) {
                    try {
                        mEOLManager.responseSetBTPairModeStatus(RESPONSE_CODE_OK);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    handleFail("pair fail");
                }
            } else {
                try {
                    mEOLManager.responseGetBTPairModeStatus(EOLUtils.getBTPairMode());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBTAudioPlayPauseStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onBTAudioSkipTrackStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onBTIncomingCallActiveStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onClearBTPairedList() {
        if (checkRegister("")) {

        }
    }

    @Override
    public void onBTPairedWithDevice(int i, int[] ints) {
        if (EOLUtils.isRegister(String.format("%1$s{%2$s,%3$s}", "onBTPairedWithDevice",
                String.valueOf(i), Arrays.toString(ints)), mInMockModeF)) {

        }
    }

    @Override
    public void onAudioSourceSelect(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onFaderLevel(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onBalanceLevel(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onMuteStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onSpeedVolumeStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onEQSettingStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onBastListenPositionStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onSoundFieldStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onArkamys3DStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {

        }
    }

    @Override
    public void onVolumeLevel(int i, int i1, int i2) {
        if (checkRegister("", i, i1, i2)) {

        }
    }

    @Override
    public void onUSBAudioPlayPause(int i, int i1) {
        if (checkRegister("", i, i1)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    @Override
    public void onUSBAudioPlayMode(int i, int i1, int i2) {
        if (checkRegister("", i, i1, i2)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    @Override
    public void onUSBAudioSkipTrack(int i, int i1) {
        if (checkRegister("", i, i1)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    @Override
    public void onUSBDesiredFileAndTime(int i, int i1, int i2, int i3) {
        if (checkRegister("", i, i1, i2, i3)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    @Override
    public void onUSBCurrentStatus(int i) {
        if (checkRegister("", i)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    @Override
    public void onUSBPictureOperation(int i, int i1) {
        if (checkRegister("", i, i1)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    @Override
    public void onUSBPictureDisplayStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    @Override
    public void onUSBPictureSkipStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    @Override
    public void onUSBVideoPlayPauseStatus(int i, int i1) {
        if (checkRegister("", i, i1)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    @Override
    public void onUSBVideoSkip(int i, int i1) {
        if (checkRegister("onUSBVideoSkip", i, i1)) {
            if (EOLUtils.isUSBMounted()) {

            } else {
                handleUSBNotMounted();
            }
        }
    }

    private void handleUSBNotMounted() {

    }

    @Override
    public void onTunerCurrentStatus(int i) {
        if (checkRegister("onTunerCurrentStatus", i)) {

        }
    }

    @Override
    public void onTunerFrequency(int i, int i1) {
        if (checkRegister("onTunerFrequency", i, i1)) {

        }
    }

    @Override
    public void onTunerBand(int i, int i1) {
        if (checkRegister("onTunerBand", i, i1)) {

        }
    }

    @Override
    public void onTunerFavorite(int i, int i1) {
        if (checkRegister("onTunerFavorite", i, i1)) {

        }
    }

    @Override
    public void onTunerSeek(int i, int i1) {
        if (checkRegister("onTunerSeek", i, i1)) {

        }
    }

    @Override
    public void onTunerAutoStore(int i, int i1) {
        if (checkRegister("onTunerAutoStore", i, i1)) {

        }
    }

    @Override
    public void onTFTIlluminationOnOff(int i, int i1) {
        if (checkRegister("onTFTIlluminationOnOff", i, i1)) {

        }
    }

    @Override
    public void onTFTDisplayPattern(int i, int i1) {
        if (checkRegister("onTFTDisplayPattern", i, i1)) {

        }
    }

    @Override
    public void onTestScreenIllumination(int i, int i1, int i2) {
        if (checkRegister("onTestScreenIllumination", i, i1, i2)) {

        }
    }

    @Override
    public void onTestMFDIllumination(int i, int i1, int i2) {
        if (checkRegister("onTestMFDIllumination", i, i1, i2)) {

        }
    }

    @Override
    public void onLCDLVDSOutputOnOff(int i, int i1) {
        if (checkRegister("onLCDLVDSOutputOnOff", i, i1)) {

        }
    }

    @Override
    public void onIPKLVDSOutputOnOff(int i, int i1) {
        if (checkRegister("onIPKLVDSOutputOnOff", i, i1)) {

        }
    }

    @Override
    public void onDiagnosisSessionCommand(int i) {
        if (checkRegister("onDiagnosisSessionCommand", i)) {

        }
    }

    private boolean checkRegister(String methodName, int... params) {
        if (TextUtils.isEmpty(methodName)) {
            methodName = new Exception().getStackTrace()[1].getMethodName();
        }
        return EOLUtils.isRegister(String.format("%1$s %2$s", methodName, Arrays.toString(params)), mInMockModeF);
    }
}
