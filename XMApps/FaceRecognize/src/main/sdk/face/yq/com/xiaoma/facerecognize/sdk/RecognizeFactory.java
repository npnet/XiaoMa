package com.xiaoma.facerecognize.sdk;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.xiaoma.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author KY
 * @date 3/27/2019
 */
public class RecognizeFactory {
    public static final String TAG = RecognizeFactory.class.getSimpleName();
    private static Random random = new Random();
    private static List<RecognizeListener> faultListeners = new ArrayList<>();

    private RecognizeFactory() throws Exception {
        throw new Exception();
    }

    private static Sensor mSensor;
    private static boolean isDark;
    private static IFaceRecognize mFault = new IFaceRecognize() {
        @Override
        public void init(Context context) {
            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (mSensor != null)
                sensorManager.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (event.values[0] <= 10) {
                            if (!isDark) {
                                Log.d(TAG, "mock recognize！");
                                dispatchListener(generateRecognize());
                            }
                            isDark = true;
                        } else {
                            isDark = false;
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                }, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void registerRecognizeListener(RecognizeListener faultListener) {
            faultListeners.add(faultListener);
        }

        @Override
        public void removeRecognizeListener(RecognizeListener faultListener) {
            faultListeners.remove(faultListener);
        }

        @Override
        public void mockHandleRecognize(RecognizeType recognizeType) {
            //do nothing
        }
    };

    /**
     * 供测试用，手动分发识别结果
     * @param type
     */
    public static void dispatchListener(RecognizeType type) {
        if (CollectionUtil.isListEmpty(faultListeners)) return;
        for (RecognizeListener faultListener : faultListeners) {
            faultListener.onRecognize(type);
        }
    }

    private static RecognizeType generateRecognize() {
        return RecognizeType.valueOf(random.nextInt(5));
    }

    public static IFaceRecognize getSDK() {
//        return mFault;
        return RecognizeSDK.getInstance();
    }
}
