// WheelKeyListeners.aidl
package com.xiaoma.carlib.wheelcontrol;

// Declare any non-default types here with import statements
import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;

interface WheelKeyListeners {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    /*void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);*/

    void register(OnWheelKeyListener listener,in int[] keyCodes);

    void unregister(OnWheelKeyListener listener);
}
