package com.xiaoma.xting.common.playerSource.contract;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/4
 */
@IntDef({PlayerOperate.DEFAULT, PlayerOperate.SUCCESS,
        PlayerOperate.FAIL, PlayerOperate.UNSUPPORTED})
@Retention(RetentionPolicy.SOURCE)
public @interface PlayerOperate {

    int DEFAULT = 0; //默认情况 比如 本来在播放,调用播放
    int SUCCESS = 1; // 操作成功
    int FAIL = 2;//操作失败
    int UNSUPPORTED = 3; // 操作不支持
}
