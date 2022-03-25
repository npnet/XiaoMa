package com.xiaoma.carlib.manager;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarVendorExtensionManager;
import android.car.hardware.property.CarPropertyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


public class CarProvider {
    private static final String TAG = "CarProperty";
    private final static int MSG_INPUT_SOURCE_CHANGED = 0x01;
    private static CarProvider instance;
    private boolean isInited = false;
    private Car car;
    private CarPropertyManager propertyManager = null;
    public static final  int ID_SONG_RECOGNITION = CarVendorExtensionManager.ID_SONG_RECOGNITION;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_INPUT_SOURCE_CHANGED) {
                Log.i(TAG, "handleMessage _ " +msg.toString());
            }
        }
    };
    private CarPropertyManager.CarPropertyEventListener listener = new CarPropertyManager.CarPropertyEventListener() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            switch (carPropertyValue.getPropertyId()) {
                case CarVendorExtensionManager.ID_SONG_RECOGNITION:
               /*     handler.obtainMessage(
                            MSG_INPUT_SOURCE_CHANGED,
                            1,
                            1   //carPropertyValue.getValue()
                    ).sendToTarget();*/
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onErrorEvent(int i, int i1) {
            Log.i(TAG, "onErrorEvent _ " + i + " - " + i1);
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            try {
                propertyManager = (CarPropertyManager) car.getCarManager("property");
                propertyManager.registerListener(listener, CarVendorExtensionManager.ID_SONG_RECOGNITION, 1);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
        }
    };

    private CarProvider() {
    }

    public static CarProvider getInstance() {
        if (instance == null) {
            synchronized (CarProvider.class) {
                if (instance == null) {
                    instance = new CarProvider();
                }
            }
        }
        return instance;
    }

    public boolean init(Context context) {
        if (context == null) {
            return false;
        }
        if (isInited) {
            return true;
        }
        car = Car.createCar(context, serviceConnection);
        car.connect();
        isInited = true;
        return true;
    }
    public void stopCar(){
        if (car!=null){
            car.disconnect();
        }
    }
    public void setBooleanProperty(int prop, int area, boolean val) {
        if (propertyManager != null) {
            try {
                Log.d("youthyjj: ", "at " + new Throwable().getStackTrace()[0]
                        + "\n * set bool: " + val);
                propertyManager.setBooleanProperty(prop, area, val);

                Log.d("youthyjj: ", "at " + new Throwable().getStackTrace()[0]
                        + "\n * get bool: " + getBooleanProperty(prop));
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getBooleanProperty(int prop) {
        boolean value = false;
        if (propertyManager != null) {
            try {
                value = propertyManager.getBooleanProperty(prop, 0);
                Log.d("youthyjj: ", "at " + new Throwable().getStackTrace()[0]
                        + "\n * no exception");
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return value;
    }



}
