package com.xiaoma.club.common.util;

/**
 * Created by LKF on 2019-1-9 0009.
 */
public class ArrayUtil {
    public static boolean contains(int[] arr, int n) {
        if (arr != null && arr.length > 0) {
            for (final int e : arr) {
                if (e == n) {
                    return true;
                }
            }
        }
        return false;
    }
}
