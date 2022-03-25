package com.xiaoma.carlib.manager;

import android.content.Context;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: iSun
 * @date: 2018/10/25 0025
 */
public class XmEngineManager {
    public String TAG = XmEngineManager.class.getSimpleName();
    private static XmEngineManager instance;
    private final Context context;
    private List<EngineCallBack> callBacks = new ArrayList<>();
    private volatile boolean engineStatus = true;

    public static XmEngineManager getInstance(Context context) {
        if (instance == null) {
            synchronized (XmEngineManager.class) {
                if (instance == null) {
                    instance = new XmEngineManager(context);
                }
            }
        }
        return instance;
    }

    private XmEngineManager(Context context) {
        this.context = context;
    }


    public void registerCallBack(EngineCallBack callBack) {
        if (callBack != null) {
            callBacks.add(callBack);
        }
    }

    public void unregisterCallBack(EngineCallBack callBack) {
        if (callBack != null) {
            callBacks.remove(callBack);
        }
    }

    private synchronized void setEngineStatus(boolean status) {
        if (status != engineStatus) {
            engineStatus = status;
            notifyCallBack();
        }
    }

    public boolean getEngineStatus() {
        // TODO: 2018/11/29 0029 获取发动机状态及回调 新增
        int engineState = XmCarFactory.getCarVendorExtensionManager().getEngineState();
        return engineState == SDKConstants.VALUE.ENGINESTATE_OFF ? false : true;
    }

    private synchronized void notifyCallBack() {
        Iterator<EngineCallBack> iterator = callBacks.iterator();
        while (iterator.hasNext()) {
            EngineCallBack next = iterator.next();
            if (engineStatus) {
                next.engineON();
            } else {
                next.engineOFF();
            }
        }
    }

    public interface EngineCallBack {
        public void engineON();

        public void engineOFF();
    }
}
