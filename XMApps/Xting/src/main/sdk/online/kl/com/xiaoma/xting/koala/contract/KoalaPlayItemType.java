package com.xiaoma.xting.koala.contract;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/3
 */
@IntDef({KoalaPlayItemType.DEFAULT, KoalaPlayItemType.LIVE_PLAYBACK,
        KoalaPlayItemType.LIVE_AUDITION_EDIT, KoalaPlayItemType.LIVE_AUDITION,
        KoalaPlayItemType.LIVING, KoalaPlayItemType.BROADCAST_LIVING, KoalaPlayItemType.BROADCAST_PLAYBACK})
@Retention(RetentionPolicy.SOURCE)
public @interface KoalaPlayItemType {

    int DEFAULT = 0;
    int LIVE_PLAYBACK = 1;
    int LIVE_AUDITION_EDIT = 2;
    int LIVE_AUDITION = 3;
    int LIVING = 4;
    int BROADCAST_LIVING = 5;
    int BROADCAST_PLAYBACK = 6;
}
