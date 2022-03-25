package com.xiaoma.personal.account.vm;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ItemFlag.CAR_COIN,
        ItemFlag.TASK_CENTER,
        ItemFlag.ORDER,
        ItemFlag.CAR_INFO,
        ItemFlag.ACCOUNT,
        ItemFlag.FEED_BACK,
        ItemFlag.NEW_GUIDE,
        ItemFlag.CODE_MANAGER})
@Retention(RetentionPolicy.SOURCE)
public @interface ItemFlag {
    int CAR_COIN = 0;
    int TASK_CENTER = 1;
    int ORDER = 2;
    int CAR_INFO = 3;
    int ACCOUNT = 4;
    int FEED_BACK = 5;
    int NEW_GUIDE = 6;
    int CODE_MANAGER = 7;
}
