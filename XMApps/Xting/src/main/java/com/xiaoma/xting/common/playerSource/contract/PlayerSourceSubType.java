package com.xiaoma.xting.common.playerSource.contract;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
public interface PlayerSourceSubType {

    //本地电台
    int YQ_RADIO_FM = -1;
    int YQ_RADIO_AM = -2;

    // Himalayan 数据类型
    int RADIO = 1; //Schedule & Radio 收藏都要使用这种
    int SCHEDULE = 2;
    int TRACK = 3;

    // Koala 数据类型
    int KOALA_PGC_RADIO = 4;
    int KOALA_ALBUM = 5;
}
