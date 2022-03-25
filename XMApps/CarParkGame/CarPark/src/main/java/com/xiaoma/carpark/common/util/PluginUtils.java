package com.xiaoma.carpark.common.util;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/12/12 0012.
 */

public class PluginUtils {
    private static final int ONE_ZERO_TWO_FOUR = 1024;

    /**
     * 将字节转换为MB
     *
     * @param byteSize
     * @return
     */
    public static String byteToM(long byteSize) {
        BigDecimal filesize = new BigDecimal(byteSize);
        BigDecimal megabyte = new BigDecimal(ONE_ZERO_TWO_FOUR * ONE_ZERO_TWO_FOUR);
        float returnValue = filesize.divide(megabyte, 1, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(ONE_ZERO_TWO_FOUR);
        returnValue = filesize.divide(kilobyte, 1, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }


}
