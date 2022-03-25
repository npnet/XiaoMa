package com.xiaoma.dualscreen.manager;

import android.content.Context;
import android.os.RemoteException;

import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lai
 * @Data 2019/5/17
 * @DES 方控
 */
public class WheelOperateDualManager {
    private static WheelOperateDualManager manager;
    private OnWheelKeyListener carLibListener;
    private List<OnWheelOperatePhoneListener> mWheelOperateListenerList = new ArrayList<>();
    private final int[] KEYCODE = new int[]{WheelKeyEvent.KEYCODE_WHEEL_UP, WheelKeyEvent.KEYCODE_WHEEL_DOWN,
            WheelKeyEvent.KEYCODE_WHEEL_BACK, WheelKeyEvent.KEYCODE_WHEEL_OK};
    private Context mContext;

    public static WheelOperateDualManager getInstance(Context context) {
        if (manager == null) {
            manager = new WheelOperateDualManager(context);
        }
        return manager;
    }

    public WheelOperateDualManager(Context context) {
        mContext = context;
    }

    public void registerCarLibListener() {
        if (carLibListener == null) {
            carLibListener = new OnWheelKeyListener.Stub() {
                @Override
                public boolean onKeyEvent(int keyAction, int keyCode) {
                    for (int i = 0; i < mWheelOperateListenerList.size(); i++) {
                        OnWheelOperatePhoneListener listener = mWheelOperateListenerList.get(i);
                        if (WheelKeyEvent.KEYCODE_WHEEL_UP == keyCode) {
                            KLog.d("onKeyEvent : UP");
                            listener.keyUp();
                        } else if (WheelKeyEvent.KEYCODE_WHEEL_DOWN == keyCode) {
                            KLog.d("onKeyEvent : DOWN");
                            listener.keyDown();
                        } else if (WheelKeyEvent.KEYCODE_WHEEL_BACK == keyCode) {
                            KLog.d("onKeyEvent : BACK");
                            listener.keyCancel();
                        } else if (WheelKeyEvent.KEYCODE_WHEEL_OK == keyCode) {
                            KLog.d("onKeyEvent : OK");
                            listener.keyOk();
                        }
                    }
                    return true;
                }

                @Override
                public String getPackageName() throws RemoteException {
                    return mContext.getPackageName();
                }
            };
        }
        XmWheelManager.getInstance().register(carLibListener, KEYCODE);

    }

    public void setWheelOperatePhoneListener(OnWheelOperatePhoneListener listener) {
        if (!this.mWheelOperateListenerList.contains(listener)) {
            this.mWheelOperateListenerList.add(listener);
        }
    }

    public void removeWheelOperatePhoneListener(OnWheelOperatePhoneListener listener) {
        if (this.mWheelOperateListenerList.contains(listener)) {
            this.mWheelOperateListenerList.remove(listener);
        }
    }

    public void removeAllWheelOperatePhoneListener() {
        this.mWheelOperateListenerList.clear();
    }

    public interface OnWheelOperatePhoneListener {
        void keyUp();

        void keyDown();

        void keyCancel();

        void keyOk();
    }

}
