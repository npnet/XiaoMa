package com.xiaoma.bluetooth.phone.common.CommonInterface;

import com.xiaoma.aidl.model.ContactBean;

import java.util.List;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/7/29
 * @Desc:
 */
public interface PullContactbookCallback {
    void onPullContactFinished(List<ContactBean> list);

    void onPullMissCallLog(List<ContactBean> list);

    void onPullReceivedCallLog(List<ContactBean> list);

    void onPullDialedCallLog(List<ContactBean> list);

    void onPullCalllog(List<ContactBean> list);

    void onPullCallHistory(List<ContactBean> list);

    void onPullFailed();

    void onUnauthoried();

    void onTimeOut();

    void unInitialized();
}
