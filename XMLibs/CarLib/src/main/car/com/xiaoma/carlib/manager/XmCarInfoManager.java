package com.xiaoma.carlib.manager;

import android.car.Car;
import android.car.CarInfoManager;
import android.car.CarNotConnectedException;
import android.content.Context;
import android.os.IBinder;

import com.xiaoma.carlib.utils.LogUtils;

/**
 * Created by zhushi.
 * Date: 2019/1/3
 */
public class XmCarInfoManager extends BaseCarManager<CarInfoManager> implements ICarWholeInfo {
    private static final String TAG = XmCarInfoManager.class.getSimpleName();
    private static XmCarInfoManager instance;
    private static final String SERVICE_NAME = Car.INFO_SERVICE;

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
        String vin = "";
        //3.1版本没有这个方法先注释了
        /*
        try {
            vin = getManager().getVIN();

        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
        LogUtils.e(TAG, "VinCode=" + vin);
        */
        return vin;
    }


    @Override
    public String getCarModel() {
        String carModel = "";
        try {
            carModel = getManager().getModel();

        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
        LogUtils.e(TAG, "carModel=" + carModel);
        return carModel;
    }

    @Override
    public void onCarServiceConnected(IBinder binder) {
        super.onCarServiceConnected();
        //服务连接 注册callBack
    }

    @Override
    public void onCarServiceDisconnected() {
        super.onCarServiceDisconnected();
        //服务断开
    }

}
