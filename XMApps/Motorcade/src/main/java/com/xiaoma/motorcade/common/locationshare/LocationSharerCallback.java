package com.xiaoma.motorcade.common.locationshare;


import com.xiaoma.motorcade.common.model.ShareLocationParam;

/**
 * Created by LKF on 2017/5/12 0012.
 */

public interface LocationSharerCallback {
    void onSendLocation(ILocationSharer sharer, ShareLocationParam param);

    void onReceiveLocation(ILocationSharer sharer, String fromId, ShareLocationParam param);

    void onReceiveLocationOut(ILocationSharer sharer, String fromId);
}
