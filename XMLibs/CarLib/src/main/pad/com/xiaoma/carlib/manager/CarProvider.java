package com.xiaoma.carlib.manager;

import android.content.Context;


public class CarProvider {
    private static final String TAG = "CarProperty";
    private final static int MSG_INPUT_SOURCE_CHANGED = 0x01;
    private static CarProvider instance;
    private boolean isInited = false;
    public static final  int ID_SONG_RECOGNITION = 0;

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
        return true;
    }
    public void stopCar(){

    }
    public void setBooleanProperty(int prop, int area, boolean val) {

    }

    public boolean getBooleanProperty(int prop) {

        return false;
    }



}
