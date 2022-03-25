package com.xiaoma.shop.common.util;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/21
 */
public class PriceUtils {

    private PriceUtils() {

    }

    public static boolean isFree(double originalPrice, double discountPrice, boolean andOperationF) {
        if (andOperationF) return originalPrice == 0 && discountPrice == 0;
        return originalPrice == 0 || discountPrice == 0;
    }

    public static String formatPrice(double price) {
        if (price == 0) return null;
        return UnitConverUtils.moreThanToConvert(String.valueOf(price));
    }

    public static String formatPrice(int price) {
        if (price == 0) return null;
        return UnitConverUtils.moreThanToConvert(String.valueOf(price));
    }
}
