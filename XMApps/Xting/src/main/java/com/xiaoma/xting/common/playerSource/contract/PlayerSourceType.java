package com.xiaoma.xting.common.playerSource.contract;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
@IntDef({PlayerSourceType.DEFAULT,
        PlayerSourceType.HIMALAYAN, PlayerSourceType.KOALA,
        PlayerSourceType.RADIO_XM, PlayerSourceType.RADIO_YQ})
@Retention(RetentionPolicy.SOURCE)
public @interface PlayerSourceType {

    int DEFAULT = 0;
    int HIMALAYAN = 1;//喜马拉雅电台
    int KOALA = 2; //考拉电台
    int RADIO_XM = 3; //小马自定义喜马拉雅数据
    int RADIO_YQ = 4; //本地电台
}
