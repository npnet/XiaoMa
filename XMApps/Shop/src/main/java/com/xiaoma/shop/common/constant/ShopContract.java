package com.xiaoma.shop.common.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/16
 */
public interface ShopContract {

    @IntDef({Pay.DEFAULT, Pay.COIN, Pay.RMB, Pay.COIN_AND_RMB})
    @Retention(RetentionPolicy.SOURCE)
    @interface Pay {
        int DEFAULT = 0;
        int COIN = 1; //只能用车币
        int RMB = 2; //只能用RMB
        int COIN_AND_RMB = 3; //用车币 或者 RMB
    }

    @IntDef({PriceType.CASH, PriceType.SCORE,PriceType.NONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PriceType {
        int CASH = 0;
        int SCORE = 1;
        int NONE = -1;
    }
}
