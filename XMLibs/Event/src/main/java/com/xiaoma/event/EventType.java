package com.xiaoma.event;

/**
 * 上报事件类型
 *
 * @author zs
 * @date 2018/9/12 0012.
 */
public enum EventType {

    /*位置压缩批量上报*/
    LOCATION_COMPRESS(1),

    /*操作日志压缩批量上报*/
    EVENT_COMPRESS(2),

    /*流量统计压缩批量上报*/
    TRAFFIC_COMPRESS(3);

    private int value;

    EventType(int value) {
        this.value = value;
    }

}
