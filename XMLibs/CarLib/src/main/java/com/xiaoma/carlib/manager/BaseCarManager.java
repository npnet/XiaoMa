package com.xiaoma.carlib.manager;


import android.content.Context;
import android.util.Log;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;

/**
 * @author: iSun
 * @date: 2018/10/23 0023
 */
public abstract class BaseCarManager<T> implements CarServiceListener {
    private static final String TAG = BaseCarManager.class.getSimpleName();
    private String serviceName;
    private T manager;
    protected Context mContext;
    public Integer DEFAULT_INT = new Integer(SDKConstants.DEFAULT_INT);
    public String DEFAULT_STRING = new String(SDKConstants.DEFAULT_STRING);
    public Float DEFAULT_FLOAT = new Float(SDKConstants.DEFAULT_FLOAT);
    public Boolean DEFAULT_BOOLEAN = false;


    public BaseCarManager(String serviceName) {
        this.serviceName = serviceName;
    }

    public void init(Context context) {
        this.mContext = context;
        CarServiceConnManager.getInstance(mContext).addCallBack(this);
    }

    public synchronized T getManager() {
        if (manager == null) {
            Object managerForName = CarServiceConnManager.getInstance(mContext).getManagerForName(serviceName);
            if (managerForName != null) {
                manager = (T) managerForName;
            } else if (CarServiceConnManager.getInstance(mContext).isOnceConnected()) {
                //曾经连接成功过 但是carManager已经为空 再次发起一次连接
                CarServiceConnManager.getInstance(mContext).reInit();
            }
            if (managerForName == null) {
                //此处仅作为问题排查处理
                boolean onceConnected = CarServiceConnManager.getInstance(mContext).isOnceConnected();
                boolean isConnection = CarServiceConnManager.getInstance(mContext).isConnection();
                Log.e(TAG, "manager is null.onceConnected:" + onceConnected + "  isConnection:" + isConnection);
            }
        }
        return manager;
    }

    public <E> E getDefaultValue(Class<E> propertyClass) {
        if (propertyClass == null) {
            return null;
        }
        if (Integer.class.equals(propertyClass)) {
            return propertyClass.cast(DEFAULT_INT);
        } else if (String.class.equals(propertyClass)) {
            return propertyClass.cast(DEFAULT_STRING);
        } else if (Float.class.equals(propertyClass)) {
            return propertyClass.cast(DEFAULT_FLOAT);
        } else if (Boolean.class.equals(propertyClass)) {
            return propertyClass.cast(DEFAULT_BOOLEAN);
        }
        return null;
    }

    public void onCarServiceDisconnected() {
        manager = null;
    }

    public void onCarServiceConnected() {
        manager = getManager();
    }

    void setRobAction(int action) {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(action);
    }
}

