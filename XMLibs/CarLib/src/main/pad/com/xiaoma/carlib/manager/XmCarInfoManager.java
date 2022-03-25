package com.xiaoma.carlib.manager;


import android.os.IBinder;

/**
 * Created by zhushi.
 * Date: 2019/1/3
 */
public class XmCarInfoManager extends BaseCarManager implements ICarWholeInfo {
    private static final String TAG = XmCarInfoManager.class.getSimpleName();
    private static XmCarInfoManager instance;
    private static final String SERVICE_NAME = "";

    public static XmCarInfoManager getInstance() {
        if (instance == null) {
            synchronized (XmCarInfoManager.class) {
                if (instance == null) {
                    instance = new XmCarInfoManager();
                }
            }
        }

        return instance;
    }

    public XmCarInfoManager() {
        super(SERVICE_NAME);
    }


    @Override
    public String getVinCode() {
        return null;
    }

    @Override
    public String getCarModel() {
        return null;
    }

    @Override
    public void onCarServiceConnected(IBinder binder) {

    }
}
