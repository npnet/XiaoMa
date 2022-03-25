package com.xiaoma.assistant.utils;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/07/30
 * @Describe:
 */
public class NumTransUtils {


    public static String getMinValue(String val1, String val2) {
        double ret1 = string2Double(val1);
        double ret2 = string2Double(val2);
        if (ret1 == -Integer.MAX_VALUE) {
            return val1;
        }
        if (ret2 == -Integer.MAX_VALUE) {
            return val2;
        }
        return ret1 <= ret2 ? val1 : val2;
    }

    public static String getMaxValue(String val1, String val2) {
        double ret1 = string2Double(val1);
        double ret2 = string2Double(val2);
        if (ret1 == -Integer.MAX_VALUE) {
            return val2;
        }
        if (ret2 == -Integer.MAX_VALUE) {
            return val1;
        }
        return ret1 > ret2 ? val1 : val2;
    }

    private static double string2Double(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return -Integer.MAX_VALUE;
        }
    }
}
