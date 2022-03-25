package com.qiming.fawcard.synthesize.base.util;

import com.qiming.fawcard.synthesize.base.constant.QMConstant;

import java.math.BigDecimal;

public class MathUtils {

    // 这个类不能实例化
    private MathUtils() {

    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }

        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");

        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * String四舍五入转Double
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double roundString2Double(String v, int scale) {
        try {
            if (scale < 0) {
                throw new IllegalArgumentException("The scale must be a positive integer or zero");
            }
            BigDecimal b = new BigDecimal(v);
            BigDecimal one = new BigDecimal("1");
            return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            return QMConstant.EXCEPTION_DATA;
        }
    }

    /**
     * String四舍五入转int类型
     *
     * @param val
     * @return
     */
    public static int roundString2Int(String val) {
        try {
            BigDecimal b = new BigDecimal(val);
            BigDecimal one = new BigDecimal("1");
            double halfUpNum = b.divide(one, 0, BigDecimal.ROUND_HALF_UP).doubleValue();
            return (int) halfUpNum;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return QMConstant.EXCEPTION_DATA;
        }
    }
}
