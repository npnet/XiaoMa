// OnWheelKeyListener.aidl
package com.xiaoma.carlib.wheelcontrol;

// Declare any non-default types here with import statements

interface OnWheelKeyListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    /*void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);*/

    boolean onKeyEvent(int keyAction, int keyCode);

    String getPackageName();
}