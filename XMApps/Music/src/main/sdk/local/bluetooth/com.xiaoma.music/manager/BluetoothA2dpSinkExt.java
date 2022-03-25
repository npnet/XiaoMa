package com.xiaoma.music.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.nforetek.bt.aidl.INfCommandA2dp;
import com.nforetek.bt.aidl.UiCallbackAvrcp;
import com.nforetek.bt.aidl.UiCommand;
import com.nforetek.bt.res.NfDef;
import com.xiaoma.utils.log.KLog;

import java.lang.reflect.Method;

/**
 * Created by hamm on 19-5-10.
 */

public class BluetoothA2dpSinkExt implements A2dpSinkExtInterface {
    private String TAG = "Nf_BluetoothA2dpSinkExt";

    private INfCommandA2dp mCommandA2dp;
    private Context mContext;
    private ServiceListener mServiceListener;
    private UiCommand mUiCommand;
    private A2dpPlaybackStateCallback mPlaybackStateCallback;


    private void notifyServiceConnected(boolean state) {
        if (mServiceListener != null) {
            mServiceListener.onConnectedState(state);
        }
    }

    public BluetoothA2dpSinkExt(Context context) {
        mContext = context;
    }

    public void init(ServiceListener listener) {
        Log.v(TAG, "bindA2dpService");
        mServiceListener = listener;
        Intent intent = new Intent();
        //intent.setPackage(NfDef.PACKAGE_NAME);
        //intent.setAction(NfDef.CLASS_SERVICE_A2DP);
        intent.setComponent(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_A2DP));
        mContext.bindService(intent, this.mConnection, Context.BIND_AUTO_CREATE);
        Intent intent2 = new Intent("com.nforetek.bt.START_UI_SERVICE");
        intent2.setPackage("com.nforetek.bt.demo.service");
        mContext.bindService(intent2, this.mUiConnection, Context.BIND_AUTO_CREATE);
    }

    public void deInit() {
        Log.i(TAG, "start unbind service");
        mServiceListener = null;
        mContext.unbindService(mConnection);
        mContext.unbindService(mUiConnection);
        Log.i(TAG, "end unbind service");
    }

    private ServiceConnection mUiConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "ready onServiceConnected");

            Log.v(TAG, "Piggy Check className : " + className);

            if (className.getPackageName().equals("com.nforetek.bt.demo.service")) {
                Log.i(TAG, "ComponentName(com.nforetek.bt.demo.service");
                mUiCommand = UiCommand.Stub.asInterface(service);
                if (mUiCommand == null) {
                    Log.i(TAG, "mUiCommand is null !!");
                    return;
                }
                dumpClassMethod(mUiCommand.getClass());
                try {
                    mUiCommand.registerAvrcpCallback(mAvrcpCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                notifyServiceConnected(true);
                    try {
//                        mUiCommand.reqAvrcp13GetElementAttributesPlaying();
//                        mUiCommand.reqAvrcpUpdateSongStatus();
                        if (mUiCommand.isAvrcpConnected()) {
                            KLog.d("hzx","Avrcp connected");
                            mUiCommand.reqAvrcp13GetElementAttributesPlaying();
                            mUiCommand.reqAvrcpUpdateSongStatus();
                            mUiCommand.reqAvrcp13GetCapabilitiesSupportEvent();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

            }

            Log.i(TAG, "end onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "ready onServiceDisconnected: " + className);
            if (className.getPackageName().equals("com.nforetek.bt.demo.service")) {
                mUiCommand = null;
            }
            Log.i(TAG, "end onServiceDisconnected");
            notifyServiceConnected(false);
        }
    };

    private UiCallbackAvrcp mAvrcpCallback = new UiCallbackAvrcp.Stub() {

        @Override
        public void onAvrcpServiceReady() throws RemoteException {

        }

        @Override
        public void onAvrcpStateChanged(String s, int i, int i1) throws RemoteException {

        }

        @Override
        public void retAvrcp13CapabilitiesSupportEvent(byte[] bytes) throws RemoteException {

        }

        @Override
        public void retAvrcp13PlayerSettingAttributesList(byte[] bytes) throws RemoteException {

        }

        @Override
        public void retAvrcp13PlayerSettingValuesList(byte b, byte[] bytes) throws RemoteException {

        }

        @Override
        public void retAvrcp13PlayerSettingCurrentValues(byte[] bytes, byte[] bytes1) throws RemoteException {

        }

        @Override
        public void retAvrcp13SetPlayerSettingValueSuccess() throws RemoteException {

        }

        @Override
        public void retAvrcp13ElementAttributesPlaying(int[] ints, String[] strings) throws RemoteException {

        }

        @Override
        public void retAvrcp13PlayStatus(long l, long l1, byte b) throws RemoteException {

        }

        @Override
        public void onAvrcp13RegisterEventWatcherSuccess(byte b) throws RemoteException {

        }

        @Override
        public void onAvrcp13RegisterEventWatcherFail(byte b) throws RemoteException {

        }

        @Override
        public void onAvrcp13EventPlaybackStatusChanged(byte b) throws RemoteException {

        }

        @Override
        public void onAvrcp13EventTrackChanged(long l) throws RemoteException {

        }

        @Override
        public void onAvrcp13EventTrackReachedEnd() throws RemoteException {

        }

        @Override
        public void onAvrcp13EventTrackReachedStart() throws RemoteException {

        }

        @Override
        public void onAvrcp13EventPlaybackPosChanged(long songPos) throws RemoteException {
            if (mPlaybackStateCallback != null) {
                long songPosition;
                if (songPos >= 0) {
                    songPosition = songPos;
                } else {
                    songPosition = 0;
                }
                // TODO: 2019/6/4 0004 确认下这个songPos是否能直接用
//                mA2dpPlayingProgress = (int) ((songPosition * 100 / songLength));
                mPlaybackStateCallback.onProgressChange(songPosition);
            }
        }

        @Override
        public void onAvrcp13EventBatteryStatusChanged(byte b) throws RemoteException {

        }

        @Override
        public void onAvrcp13EventSystemStatusChanged(byte b) throws RemoteException {

        }

        @Override
        public void onAvrcp13EventPlayerSettingChanged(byte[] bytes, byte[] bytes1) throws RemoteException {

        }

        @Override
        public void onAvrcp14EventNowPlayingContentChanged() throws RemoteException {

        }

        @Override
        public void onAvrcp14EventAvailablePlayerChanged() throws RemoteException {

        }

        @Override
        public void onAvrcp14EventAddressedPlayerChanged(int i, int i1) throws RemoteException {

        }

        @Override
        public void onAvrcp14EventUidsChanged(int i) throws RemoteException {

        }

        @Override
        public void onAvrcp14EventVolumeChanged(byte b) throws RemoteException {

        }

        @Override
        public void retAvrcp14SetAddressedPlayerSuccess() throws RemoteException {

        }

        @Override
        public void retAvrcp14SetBrowsedPlayerSuccess(String[] strings, int i, long l) throws RemoteException {

        }

        @Override
        public void retAvrcp14FolderItems(int i, long l) throws RemoteException {

        }

        @Override
        public void retAvrcp14MediaItems(int i, long l) throws RemoteException {

        }

        @Override
        public void retAvrcp14ChangePathSuccess(long l) throws RemoteException {

        }

        @Override
        public void retAvrcp14ItemAttributes(int[] ints, String[] strings) throws RemoteException {

        }

        @Override
        public void retAvrcp14PlaySelectedItemSuccess() throws RemoteException {

        }

        @Override
        public void retAvrcp14SearchResult(int i, long l) throws RemoteException {

        }

        @Override
        public void retAvrcp14AddToNowPlayingSuccess() throws RemoteException {

        }

        @Override
        public void retAvrcp14SetAbsoluteVolumeSuccess(byte b) throws RemoteException {

        }

        @Override
        public void onAvrcpErrorResponse(int i, int i1, byte b) throws RemoteException {

        }

        @Override
        public void retAvrcpUpdateSongStatus(String s, String s1, String s2) throws RemoteException {

        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "ready onServiceConnected");

            Log.v(TAG, "Piggy Check className : " + className);

            Log.i(TAG, "IBinder service: " + service.hashCode());
            try {
                Log.v(TAG, "Piggy Check service : " + service.getInterfaceDescriptor());
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }

            if (className.equals(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_A2DP))) {
                Log.i(TAG, "ComponentName(" + NfDef.CLASS_SERVICE_A2DP + ")");
                mCommandA2dp = INfCommandA2dp.Stub.asInterface(service);
                if (mCommandA2dp == null) {
                    Log.i(TAG, "mCommandA2dp is null !!");
                    return;
                }
                dumpClassMethod(mCommandA2dp.getClass());
                notifyServiceConnected(true);
            }

            Log.i(TAG, "end onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "ready onServiceDisconnected: " + className);
            if (className.equals(new ComponentName(NfDef.PACKAGE_NAME, NfDef.CLASS_SERVICE_A2DP))) {
                mCommandA2dp = null;
            }
            Log.i(TAG, "end onServiceDisconnected");
            notifyServiceConnected(false);
        }
    };

    /*
     *  For UI command API
     *
     */
    private void dumpClassMethod(Class c) {
        for (Method method : c.getDeclaredMethods()) {
            Log.i(TAG, "Method name: " + method.getName());
        }
    }

    @Override
    public boolean isConnected() {
        Log.v(TAG, "isA2dpConnected()");
        if (mCommandA2dp == null) {
            return false;
        }
        try {
            return mCommandA2dp.isA2dpConnected();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getConnectedAddress() {
        Log.v(TAG, "getA2dpConnectedAddress()");
        if (mCommandA2dp == null) {
            return null;
        }
        try {
            return mCommandA2dp.getA2dpConnectedAddress();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void pauseRender() {
        Log.v(TAG, "pauseA2dpRender()");
        if (mCommandA2dp == null) {
            return;
        }
        try {
            mCommandA2dp.pauseA2dpRender();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startRender() {
        Log.v(TAG, "startA2dpRender()");
        if (mCommandA2dp == null) {
            return;
        }
        try {
            mCommandA2dp.startA2dpRender();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setLocalVolume(float vol) {
        Log.v(TAG, "setA2dpLocalVolume() " + vol);
        if (mCommandA2dp == null) {
            return false;
        }
        try {
            return mCommandA2dp.setA2dpLocalVolume(vol);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setStreamType(int type) {
        Log.v(TAG, "setA2dpStreamType() " + type);
        if (mCommandA2dp == null) {
            return false;
        }
        try {
            return mCommandA2dp.setA2dpStreamType(type);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getStreamType() {
        Log.v(TAG, "getA2dpStreamType()");
        if (mCommandA2dp == null) {
            return -1;
        }
        try {
            return mCommandA2dp.getA2dpStreamType();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setPlaybackStateCallback(A2dpPlaybackStateCallback callback) {
        mPlaybackStateCallback = callback;
    }
}
