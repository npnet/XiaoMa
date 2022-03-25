package com.xiaoma.login.sdk;

//import android.car.hardware.vendor.RmbLeKey;

/**
 * Created by kaka
 * on 19-5-19 下午3:38
 * <p>
 * desc: #a
 * </p>
 *
 * @see
 */
public enum CarKey {
    NO_KEY(0),
    NM_KEY_1(1),
    NM_KEY_2(2),
    BLE_KEY_1(3),
    BLE_KEY_2(4),
    BLE_KEY_3(5),
    BLE_KEY_4(6),
    ;

    private int value;

    CarKey(int value) {
        this.value = value;

    }

    public int getValue() {
        return value;
    }

    public String getStr() {
        return String.valueOf(value);
    }

    public static CarKey valueOf(int value) {
        for (CarKey carKey : CarKey.values()) {
            if (value == carKey.value) {
                return carKey;
            }
        }
        return null;
    }

    public static CarKey strOf(String str) {
        int value;
        try {
            value = Integer.valueOf(str);
        } catch (NumberFormatException e) {
            value = 0;
        }
        return valueOf(value);
    }

    public boolean isBle() {
        return this == BLE_KEY_1
                || this == BLE_KEY_2
                || this == BLE_KEY_3
                || this == BLE_KEY_4;
    }

    public static boolean isBle(String KeyId) {
        int value;
        try {
            value = Integer.valueOf(KeyId);
        } catch (NumberFormatException e) {
            value = 0;
        }
        CarKey carKey = valueOf(value);
        return carKey != null && carKey.isBle();
    }

    public static boolean isBle(int KeyId) {
        CarKey carKey = valueOf(KeyId);
        return carKey == BLE_KEY_1
                || carKey == BLE_KEY_2
                || carKey == BLE_KEY_3
                || carKey == BLE_KEY_4;
    }
}
