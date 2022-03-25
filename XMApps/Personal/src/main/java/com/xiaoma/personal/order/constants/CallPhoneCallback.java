package com.xiaoma.personal.order.constants;

import com.xiaoma.center.logic.model.Response;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/20 0020 20:04
 *       desc：电话拨打失败回调
 * </pre>
 */
public interface CallPhoneCallback {
    void failed(Response response);
}
