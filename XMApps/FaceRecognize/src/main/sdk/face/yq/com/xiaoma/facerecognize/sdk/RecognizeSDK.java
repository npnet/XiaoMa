package com.xiaoma.facerecognize.sdk;

import android.car.hardware.CarVendorExtensionManager;
import android.car.hardware.vendor.TiredRemindWarning;
import android.content.Context;
import android.util.Log;

import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;

import java.util.concurrent.CopyOnWriteArraySet;


/**
 * Created by kaka
 * on 19-5-23 下午5:50
 * <p>
 * desc: #a
 * </p>
 */
public class RecognizeSDK implements IFaceRecognize {

    private static RecognizeSDK mRecognizeSDK;
    private CopyOnWriteArraySet<RecognizeListener> recognizeListeners = new CopyOnWriteArraySet<>();
    private int lastTiredSignal = -1;

    public static RecognizeSDK getInstance() {
        if (mRecognizeSDK == null) {
            synchronized (RecognizeSDK.class) {
                if (mRecognizeSDK == null) {
                    mRecognizeSDK = new RecognizeSDK();
                }
            }
        }

        return mRecognizeSDK;
    }

    @Override
    public void init(Context context) {
        XmCarVendorExtensionManager.getInstance().init(context);
        XmCarVendorExtensionManager.getInstance().addValueChangeListener(new XmCarVendorExtensionManager.ValueChangeListener() {
            @Override
            public void onChange(int id, Object value) {
                if (id == CarVendorExtensionManager.ID_TIRED_REMIND_WARNING) {
                    int value1 = (int) value;
                    Log.d("facecheck", "onChange : 疲劳， 等级：" + value1);
                    if (lastTiredSignal == value1) return;
                    lastTiredSignal = value1;
                    switch ((int) value) {
                        case TiredRemindWarning.LV1:
                            handleRecognize(RecognizeType.LightFatigueDriving);
                            break;
                        case TiredRemindWarning.LV2:
                            handleRecognize(RecognizeType.MiddleFatigueDriving);
                            break;
                        case TiredRemindWarning.LV3:
                            handleRecognize(RecognizeType.HeavyFatigueDriving);
                            break;
                    }
                } else if (id == CarVendorExtensionManager.ID_SMOKING_WARNING) {
                    if ((int) value == 1) {
                        Log.d("facecheck", "onChange : 抽烟");
                        handleRecognize(RecognizeType.Smoking);
                    }
                } else if (id == CarVendorExtensionManager.ID_PHONE_WARNING) {
                    if ((int) value == 1) {
                        Log.d("facecheck", "onChange : 打电话");
                        handleRecognize(RecognizeType.PhoneCall);
                    }
                } else if (id == CarVendorExtensionManager.ID_DISTRACTION_WARNING) {
                    if ((int) value == 1) {
                        Log.d("facecheck", "onChange : 注意力分散");
                        handleRecognize(RecognizeType.Inattention);
                    }
                }
            }
        });
    }

    private void handleRecognize(RecognizeType recognizeType) {
        for (RecognizeListener recognizeListener : recognizeListeners) {
            if (recognizeListener != null) {
                recognizeListener.onRecognize(recognizeType);
            }
        }
    }

    @Override
    public void mockHandleRecognize(RecognizeType recognizeType) {
        for (RecognizeListener recognizeListener : recognizeListeners) {
            if (recognizeListener != null) {
                recognizeListener.onRecognize(recognizeType);
            }
        }
    }

    @Override
    public void registerRecognizeListener(RecognizeListener faultListener) {
        recognizeListeners.add(faultListener);
    }

    @Override
    public void removeRecognizeListener(RecognizeListener faultListener) {
        recognizeListeners.remove(faultListener);
    }
}
