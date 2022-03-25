package com.xiaoma.ad.models;

import java.io.Serializable;

/**
 * 广告事件上报bean
 *
 * @author KY
 * @date 2018/9/13
 */
public class AdEvent implements Serializable{

    private static final long serialVersionUID = -3532184000529242177L;

    /**
     * 开机闪屏的上报类型ID固定为1
     */
    private int trackTypeId = 1;
    private long id;

    public AdEvent(long id) {
        this.id = id;
    }
}
