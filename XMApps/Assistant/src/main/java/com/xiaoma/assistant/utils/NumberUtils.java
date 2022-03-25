package com.xiaoma.assistant.utils;

import java.text.DecimalFormat;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/28
 */
public class NumberUtils {

    private static DecimalFormat sDecialFormat = new DecimalFormat("#0.00");

    private NumberUtils() {
        throw new UnsupportedOperationException("not allowed!");
    }

    public static String formatDouble(double doubleValue) {
        return sDecialFormat.format(doubleValue);
    }
}
