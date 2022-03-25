package com.xiaoma.facerecognize.sdk;

/**
 * Created by kaka
 * on 19-3-27 下午3:35
 * <p>
 * desc: #a
 * </p>
 */
public enum RecognizeType {
    HeavyFatigueDriving(2),
    MiddleFatigueDriving(1),
    LightFatigueDriving(0),
    Inattention(3),
    Smoking(4),
    PhoneCall(5);


    private final int value;

    RecognizeType(int i) {
        value = i;
    }

    public static RecognizeType valueOf(int i){
        for (RecognizeType type : RecognizeType.values()) {
            if(type.value == i){
                return type;
            }
        }
        return null;
    }

}
