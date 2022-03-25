package com.xiaoma.carlib.wheelcontrol;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LKF on 2019-5-6 0006.
 * 方控管理Client
 */
@SuppressLint("LogNotTimber")
public class XmWheelManager implements WheelKeyListeners {
    private static final String TAG = "XmWheelManager";
    private static XmWheelManager sInstance;
    private WeakReference<Context> mContextRef;
    private WheelKeyListeners mWheelKeyListeners;
    private ServiceConnection mConn;

    public static XmWheelManager getInstance() {
        if (sInstance != null) {
            return sInstance;
        } else {
            synchronized (XmWheelManager.class) {
                if (sInstance == null) {
                    sInstance = new XmWheelManager();
                }
                return sInstance;
            }
        }
    }

    public void init(Context context) {
        Log.i(TAG, String.format("init( context: %s )", context));
        Context app = context.getApplicationContext();
        unbindWheelService();
        bindWheelService(app, new WheelServiceConnection());
        mContextRef = new WeakReference<>(app);
    }

    public void release() {
        unbindWheelService();
        mWheelKeyListeners = null;
        mContextRef = null;
    }

    /**
     * 注册一个方控按键监听器
     *
     * @param listener 监听器
     * @param keyCodes 需要监听的按键{@link WheelKeyEvent}
     */
    @Override
    public void register(final OnWheelKeyListener listener, final int[] keyCodes) {
        if (!isInit()) {
            Log.e(TAG, "register: Did not init !!!");
            return;
        }
        doUnregister(listener);
        Log.i(TAG, String.format("register( listener: %s, keyCodes: %s )", listener, Arrays.toString(keyCodes)));
        Context context = mContextRef.get();
        if (context == null)
            return;
        WheelKeyListeners keyListeners = mWheelKeyListeners;
        if (keyListeners == null
                || !keyListeners.asBinder().isBinderAlive()
                || mConn == null) {
            unbindWheelService();
            bindWheelService(context, new WheelServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    super.onServiceConnected(name, service);
                    register(listener, keyCodes);
                }
            });
        } else {
            try {
                keyListeners.register(listener, keyCodes);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注销一个方控按键监听器
     */
    @Override
    public void unregister(OnWheelKeyListener listener) {
        if (!isInit()) {
            Log.e(TAG, "unregister: Did not init !!!");
            return;
        }
        Log.i(TAG, String.format("unregister( listener: %s )", listener));
        doUnregister(listener);
    }

    private boolean isInit() {
        return mContextRef != null;
    }

    private void doUnregister(OnWheelKeyListener listener) {
        try {
            WheelKeyListeners listeners = mWheelKeyListeners;
            if (listeners != null) {
                listeners.unregister(listener);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindWheelService(Context context, ServiceConnection conn) {
        if (conn == null)
            return;
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(WheelConstant.ACTION_WHEEL_SERVICE)
                .setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        List<ResolveInfo> infoList = pm.queryIntentServices(intent, 0);
        if (infoList != null && !infoList.isEmpty()) {
            ServiceInfo serviceInfo = infoList.get(0).serviceInfo;
            if (serviceInfo != null) {
                intent.setComponent(new ComponentName(serviceInfo.packageName, serviceInfo.name));
            }
        }
        boolean rlt = false;
        try {
            rlt = context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mConn = conn;
        Log.i(TAG, String.format("bindWheelService(){ infoList: %s, rlt: %s}", infoList, rlt));
    }

    private void unbindWheelService() {
        ServiceConnection conn = mConn;
        if (conn == null)
            return;
        WeakReference<Context> ref = mContextRef;
        if (ref == null)
            return;
        Context context = ref.get();
        if (context == null)
            return;
        try {
            context.unbindService(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "unbindWheelService()");
    }

    @Override
    public IBinder asBinder() {
        WheelKeyListeners listeners = mWheelKeyListeners;
        return listeners != null ? listeners.asBinder() : null;
    }

    private class WheelServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, String.format("onServiceConnected( name: %s, service: %s )", name, service));
            mWheelKeyListeners = WheelKeyListeners.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, String.format("onServiceDisconnected( name: %s )", name));
            mWheelKeyListeners = null;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.i(TAG, String.format("onBindingDied( name: %s )", name));
            mWheelKeyListeners = null;
            Context context = mContextRef.get();
            if (context != null) {
                context.unbindService(this);
                bindWheelService(context, new WheelServiceConnection());
            }
        }

    }
}
