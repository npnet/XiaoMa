package com.xiaoma.assistant.utils;

import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/09
 * @Describe: 单位换算
 */

public class UnitConverUtils {


    public static float toG(Unit curtUnit, float curSize) {
        float result = 0;
        switch (curtUnit) {
            case G:
                result = curSize;
                break;
            case M:
                result = curSize / 1024;
                break;
            case KB:
                result = curSize / 1024 / 1024;
                break;
        }
        return result;
    }

    public static float toM(Unit curtUnit, float curSize) {
        float result = 0;
        switch (curtUnit) {
            case G:
                result = curSize * 1024;
                break;
            case M:
                result = curSize;
                break;
            case KB:
                result = curSize / 1024;
                break;
        }
        return result;
    }

    public static float toKb(Unit curtUnit, float curSize) {
        float result = 0;
        switch (curtUnit) {
            case G:
                result = curSize * 1024 * 1024;
                break;
            case M:
                result = curSize * 1024;
                break;
            case KB:
                result = curSize;
                break;
        }
        return result;
    }

    public static float toG(Unit curtUnit, String curSizeStr) {
        float curSize = 0;
        try {
            curSize = Float.parseFloat(curSizeStr);
        } catch (Exception e) {
        }
        return toG(curtUnit, curSize);
    }

    public static float toM(Unit curtUnit, String curSizeStr) {
        float curSize = 0;
        try {
            curSize = Float.parseFloat(curSizeStr);
        } catch (Exception e) {
        }
        return toM(curtUnit, curSize);
    }

    public static float toKb(Unit curtUnit, String curSizeStr) {
        float curSize = 0;
        try {
            curSize = Float.parseFloat(curSizeStr);
        } catch (Exception e) {

        }
        return toKb(curtUnit, curSize);
    }

    /**
     * 转成就近的一个单位值 （e.g. M --> G）
     *
     * @param curUnit
     * @param curSizeStr
     * @return
     */
    public static String toNear(Unit curUnit, String curSizeStr) {
        float size;
        String result = null;
        switch (curUnit) {
            case G:
                size = toG(curUnit, curSizeStr);//G
                if (size < 1) { //G -->M
                    size = toM(Unit.G, size);//m
                    //                    if (size < 1) { //M --> KB
                    //                        result = toTwoDigits(toKb(Unit.M, size)) + "KB";
                    //                    } else {
                    result = toTwoDigits(size) + "M";
                    //                    }
                } else {
                    result = toTwoDigits(size) + "G";
                }
                break;
            case M:
                size = toM(curUnit, curSizeStr); //M
                if (size > 1000) {
                    result = toTwoDigits(toG(Unit.M, size)) + "G";
                } else {
                    //                    if (size < 1) { // M --> KB
                    //                        result = toTwoDigits(toKb(Unit.M, size)) + "KB";
                    //                    } else { // M-->M
                    result = toTwoDigits(size) + "M";
                    //                    }
                }
                break;
            case KB:
                size = toM(curUnit, curSizeStr);//M
                if (size > 1000) { //M -->G
                    size = toG(Unit.M, size); //G
                    result = toTwoDigits(size) + "G";
                } else {
                    result = toTwoDigits(size) + "M";
                }
                break;

        }
        return result;
    }

    /**
     * 转化为两位数
     *
     * @param originNum
     * @return
     */
    private static String toTwoDigits(float originNum) {
        String result = "0.00";
        DecimalFormat df = new DecimalFormat("#0.0");
        try {
            result = df.format(originNum);
        } catch (Exception e) {
        }
        return result;
    }

    public static String formatIntValue(int intValue) {
        if (intValue > 1000) {
            return NumberUtils.formatDouble(intValue * 1.0f / 1000) + "K";
        }
        return String.valueOf(intValue);
    }

    public enum Unit {
        G, M, KB
    }

    public static String moreThanToConvert(String metaData) {
        if (TextUtils.isEmpty(metaData)) return "";
        String result = "";
        try {
            float data = Float.parseFloat(metaData);
            if (data >= 1000) {
                //新需求 : 舍去小数点
                DecimalFormat format = new DecimalFormat("#0");
                format.setRoundingMode(RoundingMode.FLOOR);//舍弃四舍五入
                result = format.format(data / 1000f) + "K";
            } else {
                result = metaData;
            }
        } catch (Exception e) {
            result = metaData;
        }
        return result;
    }

    // String --> Float
    public static float exchange(String price) {
        float result = 0;
        try {
            result = Float.parseFloat(price);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String moreThanToConvert(long value) {
        String result = "";
        try {
            if (value >= 10000) {
                DecimalFormat format = new DecimalFormat("#0.0");
                format.setRoundingMode(RoundingMode.FLOOR);//舍弃四舍五入
                result = format.format(value / 10000f) + "W";
            } else {
                result = value + "";
            }
        } catch (Exception e) {
            result = value + "";
        }
        return result;
    }

    /**
     * 0-9999显示xxxx人听过
     * 10000-9999万显示xxxx万听过
     * 10000万以上显示xxxx亿听过
     */
    public static String bigNumFormat(long value) {
        if (value <= 9999) return value+"";
        if (value > 9999 && value <100000000)
            return value / 10000 + "万";
        else
            return value / 100000000L + "亿";
    }

}
