package com.xiaoma.facerecognize.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.facerecognize.sdk.RecognizeFactory;
import com.xiaoma.facerecognize.sdk.RecognizeListener;
import com.xiaoma.facerecognize.sdk.RecognizeType;
import com.xiaoma.facerecognize.ui.AbsRecognizeDialog;
import com.xiaoma.facerecognize.ui.NavDialogActivity;
import com.xiaoma.facerecognize.ui.TipsDialogActivity;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import static com.xiaoma.center.logic.CenterConstants.BLUETOOTH_PHONE;

/**
 * Created by kaka
 * on 19-4-18 下午2:47
 * <p>
 * desc: #a
 * </p>
 */
public class RecognizeManager {

    private static final String TAG = RecognizeManager.class.getSimpleName();
    private Queue<RecognizeType> recognizeQueue = new PriorityBlockingQueue<>();
    private boolean onCall;
    private boolean needReConnect;
    private boolean isShowing;
    private Application mApplication;

    public static RecognizeManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final RecognizeManager instance = new RecognizeManager();
    }

    public void init(final Application application) {
        mApplication = application;
        RecognizeFactory.getSDK().init(application);
        RecognizeFactory.getSDK().registerRecognizeListener(new RecognizeListener() {
            @Override
            public synchronized void onRecognize(final RecognizeType type) {
                //防止同一个警报多次触发
                if (!recognizeQueue.contains(type)) {
                    recognizeQueue.add(type);
                }

                if (!onCall && !isShowing) {
                    handleRecognize(mApplication);
                }
            }
        });
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                int result = connectPhoneState(application);
                Log.d(TAG, "RecognizeManager init phone state linker: result" + result);
            }
        });

        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onClientIn(SourceInfo source) {
                if (BLUETOOTH_PHONE.equals(source.getLocation()) && needReConnect) {
                    Log.d(TAG, "onClientIn: BlePhoneCall, try reconnect");
                    connectPhoneState(mApplication);
                }
            }

            @Override
            public void onClientOut(SourceInfo source) {
                if (BLUETOOTH_PHONE.equals(source.getLocation())) {
                    Log.d(TAG, "onClientOut: BlePhoneCall");
                    needReConnect = true;
                }
            }
        });

        application.registerActivityLifecycleCallbacks(new LifecycleCallbackImpl() {
            @Override
            public void onActivityStarted(Activity activity) {
                if (activity instanceof AbsRecognizeDialog) {
                    isShowing = true;
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity instanceof AbsRecognizeDialog) {
                    isShowing = false;
                }
            }
        });
    }

    private int connectPhoneState(Application application) {
        return Linker.builder()
                .remote(BLUETOOTH_PHONE, CenterConstants.BLUETOOTH_PHONE_PORT)
                .action(CenterConstants.BluetoothPhoneThirdAction.REGISTER_PHONE_STATE_CALLBACK)
                .defaultLocal(application)
                .request(new IClientCallback.Stub() {
                    @Override
                    public void callback(Response response) {
                        Bundle extra = response.getExtra();
                        extra.setClassLoader(ContactBean.class.getClassLoader());
                        int[] audioStatus = extra.getIntArray(CenterConstants.BluetoothPhoneThirdBundleKey.STATE_ARRAY);
                        onCall = checkOnCall(audioStatus);
                        Log.d(TAG, "phone state change: " + onCall);
                    }
                });
    }

    private boolean checkOnCall(int[] status) {
        if (status == null) return false;
        if (status.length == 0) return false;
        if (status.length != 2) return false;
        return status[0] != State.IDLE.getValue() || status[1] != State.IDLE.getValue();
    }

    public synchronized void handleRecognize(Context context) {
        RecognizeType type = recognizeQueue.poll();
        if (type != null) {
            if (type == RecognizeType.HeavyFatigueDriving) {
                NavDialogActivity.newRecognize(context);
            } else {
                TipsDialogActivity.newRecognize(context, type);
            }
        }
    }
}
