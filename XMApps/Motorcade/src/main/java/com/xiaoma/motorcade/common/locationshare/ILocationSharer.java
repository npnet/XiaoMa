package com.xiaoma.motorcade.common.locationshare;

/**
 * Created by LKF on 2017/5/9 0009.
 */

public interface ILocationSharer {
    void start();

    void stop();

    String getChatToId();

    void addCallback(LocationSharerCallback listener);

    void removeCallback(LocationSharerCallback listener);

}
